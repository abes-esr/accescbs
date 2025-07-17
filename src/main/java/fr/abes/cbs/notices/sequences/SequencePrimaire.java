package fr.abes.cbs.notices.sequences;

import fr.abes.cbs.notices.SousZone;
import fr.abes.cbs.utilitaire.EnumUtils;
import fr.abes.cbs.utilitaire.Utilitaire;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_DEBUT;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_FIN;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_LACUNES;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequencePrimaire extends Sequence {

    public SequencePrimaire(String sequence, String typeSequence) {
        int sizeDebut = EnumUtils.getEnumList(SEQ_ECOLL_DEBUT.class).size();
        int sizeFin = EnumUtils.getEnumList(SEQ_ECOLL_FIN.class).size();
        Pattern pattern = Pattern.compile(Utilitaire.setListeSousZoneRegex(sizeDebut + sizeFin));
        Matcher matcher = pattern.matcher(sequence);
        if (matcher.find()) {
            for (int i=0;i<(sizeDebut + sizeFin);i++) {
                String labelSousZone = matcher.group("szk" + i);
                String valeurZone = matcher.group("szv" + i);
                if (("959").equals(typeSequence)) {
                    if (EnumUtils.isValidEnum(SEQ_ECOLL_LACUNES.class, labelSousZone)) {
                        this.listeSousZonesDebut.add(new SousZone<>(SEQ_ECOLL_LACUNES.valueOf(labelSousZone), valeurZone));
                    }
                }
                else {
                    if (EnumUtils.isValidEnum(SEQ_ECOLL_DEBUT.class, labelSousZone)) {
                        this.listeSousZonesDebut.add(new SousZone<>(SEQ_ECOLL_DEBUT.valueOf(labelSousZone), valeurZone));
                    } else {
                        if (EnumUtils.isValidEnum(SEQ_ECOLL_FIN.class, labelSousZone)) {
                            this.listeSousZonesFin.add(new SousZone<>(SEQ_ECOLL_FIN.valueOf(labelSousZone), valeurZone));
                        }
                    }
                }
            }
        }
    }
}
