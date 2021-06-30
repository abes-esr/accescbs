package fr.abes.cbs.notices.sequences;

import fr.abes.cbs.notices.SousZone;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_DEBUT;
import fr.abes.cbs.zones.enumSousZones.SEQ_ECOLL_FIN;
import org.apache.commons.lang3.EnumUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceParallele extends Sequence {
    public final static String SEPARATEUR = "$5 ";

    public SequenceParallele(String sequence) {
        int sizeDebut = EnumUtils.getEnumList(SEQ_ECOLL_DEBUT.class).size();
        int sizeFin = EnumUtils.getEnumList(SEQ_ECOLL_FIN.class).size();
        Pattern pattern = Pattern.compile(Constants.SOUS_ZONE_REGEXP_SEQUENCE);
        Matcher matcher = pattern.matcher(sequence);
        if (matcher.find()) {
            for (int i = 0; i < (sizeDebut + sizeFin); i++) {
                String labelSousZone = matcher.group("szk" + i);
                String valeurZone = matcher.group("szv" + i);
                if (EnumUtils.isValidEnum(SEQ_ECOLL_DEBUT.class, labelSousZone)) {
                    this.listeSousZonesDebut.add(new SousZone<SEQ_ECOLL_DEBUT>(SEQ_ECOLL_DEBUT.valueOf(labelSousZone), valeurZone));
                } else {
                    if (EnumUtils.isValidEnum(SEQ_ECOLL_FIN.class, labelSousZone)) {
                        this.listeSousZonesFin.add(new SousZone<SEQ_ECOLL_FIN>(SEQ_ECOLL_FIN.valueOf(labelSousZone), valeurZone));
                    }
                }
            }
        }
    }

}
