package fr.abes.cbs.notices.sequences;

import fr.abes.cbs.notices.SousZone;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_DEBUT;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_FIN;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_LACUNES;
import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
    protected List<SousZone> listeSousZonesDebut = new ArrayList<>();
    protected List<SousZone> listeSousZonesFin = new ArrayList<>();

    public void addSubTag(String label, String value, String typeSequence) {
        switch(typeSequence) {
            case "959":
                if (EnumUtils.isValidEnum(SEQ_ECOLL_LACUNES.class, label)) {
                    this.listeSousZonesDebut.add(new SousZone<SEQ_ECOLL_LACUNES>(SEQ_ECOLL_LACUNES.valueOf(label), value));
                }
                break;
            case "955":
            case "956":
            case "957":
                if (EnumUtils.isValidEnum(SEQ_ECOLL_DEBUT.class, label)) {
                    this.listeSousZonesDebut.add(new SousZone<SEQ_ECOLL_DEBUT>(SEQ_ECOLL_DEBUT.valueOf(label), value));
                }
                else {
                    this.listeSousZonesFin.add(new SousZone<SEQ_ECOLL_FIN>(SEQ_ECOLL_FIN.valueOf(label), value));
                }
                break;
            default:

        }
    }

    @Override
    public String toString() {
        StringBuilder sequenceStr = new StringBuilder();
        for (SousZone ssZone : listeSousZonesDebut) {
            sequenceStr.append(ssZone.getLabelSousZone()).append(ssZone.getValeur());
        }
        for (SousZone ssZoneFin : listeSousZonesFin) {
            sequenceStr.append(ssZoneFin.getLabelSousZone()).append(ssZoneFin.getValeur());
        }
        return sequenceStr.toString();
    }
}
