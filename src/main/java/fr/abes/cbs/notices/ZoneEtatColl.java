package fr.abes.cbs.notices;

import com.google.common.collect.Table;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.sequences.SequenceEtatColl;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.EnumUtils;
import fr.abes.cbs.zones.enumSousZones.Zone_955;
import fr.abes.cbs.zones.enumSousZones.Zone_956;
import fr.abes.cbs.zones.enumSousZones.Zone_957;
import fr.abes.cbs.zones.enumSousZones.Zone_959;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ZoneEtatColl extends Zone {
    private List<SequenceEtatColl> listeSequences = new ArrayList<>();

    public ZoneEtatColl(String zone, char[] indicateurs) throws ZoneException {
        super(zone, TYPE_NOTICE.EXEMPLAIRE, indicateurs);
    }

    public ZoneEtatColl(String zone, char[] indicateurs, SequenceEtatColl sequence) {
        this.label = zone;
        this.indicateurs = indicateurs;
        this.listeSequences.add(sequence);
    }

    public void addSequence(SequenceEtatColl sequence) {
        this.listeSequences.add(sequence);
    }

    public void addSousZone(String labelSubTag, String value, int indexSequence) throws ZoneException {
        switch (this.label) {
            case "959":
                if (EnumUtils.isValidEnum(Zone_959.class, labelSubTag)) {
                    this.addSubLabel(labelSubTag, value);
                } else {
                    this.listeSequences.get(indexSequence).getSequencePrimaire().addSubTag(labelSubTag, value, this.label);
                }
                break;
            case "955":
                if (EnumUtils.isValidEnum(Zone_955.class, labelSubTag)) {
                    this.addSubLabel(labelSubTag, value);
                } else {
                    this.listeSequences.get(indexSequence).getSequencePrimaire().addSubTag(labelSubTag, value, this.label);
                }
                break;
            case "956":
                if (EnumUtils.isValidEnum(Zone_956.class, labelSubTag)) {
                    this.addSubLabel(labelSubTag, value);
                } else {
                    this.listeSequences.get(indexSequence).getSequencePrimaire().addSubTag(labelSubTag, value, this.label);
                }
                break;
            case "957":
                if (EnumUtils.isValidEnum(Zone_957.class, labelSubTag)) {
                    this.addSubLabel(labelSubTag, value);
                } else {
                    this.listeSequences.get(indexSequence).getSequencePrimaire().addSubTag(labelSubTag, value, this.label);
                }
                break;
        }

    }

    @Override
    public String toString() {
        StringBuilder zone = new StringBuilder();
        zone.append(this.getLabelForOutput()).append(" ");
        if (this.getIndicateurs() != null) {
            zone.append(this.getIndicateurs());
        }
        for (SequenceEtatColl sequence : this.listeSequences) {
            zone.append(sequence.toString()).append(SequenceEtatColl.SEPARATEUR);
        }
        //suppression du dernier séparateur ajouté
        if (this.listeSequences.size() != 0) {
            zone.replace(zone.lastIndexOf(SequenceEtatColl.SEPARATEUR), zone.length(), "");
        }
        //ajout des sous zones restantes hors séquence
        Table<Integer, String, String> tableWithoutDollar = removeDollar();
        for (int i = 0; i < specificationList.size(); i++) {
            String subLabelSpecification = specificationList.get(i);
            List<String> sequence = new ArrayList<>();
            if ("[".equals(subLabelSpecification)) {
                for (int j = i + 1; !"]".equals(specificationList.get(j)); j++) {
                    sequence.add(specificationList.get(j));
                    subLabelSpecification = specificationList.get(j);
                    i = j;
                }
            }
            if (sequence.isEmpty()) {
                addNormalToString(zone, subLabelSpecification, tableWithoutDollar);
            } else {
                addFollowingSequenceToString(zone, sequence, tableWithoutDollar);
            }
        }
        zone.append(Constants.STR_0D);
        return zone.toString();
    }
}
