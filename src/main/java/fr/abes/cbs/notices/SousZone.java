package fr.abes.cbs.notices;

import fr.abes.cbs.zones.enumSousZones.ZonesTransliterees;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;

public class SousZone<K extends Enum<K>> implements Comparable<SousZone> {
    @Getter
    private Enum<K> labelSousZone;
    @Setter @Getter
    private String valeur;

    public SousZone(Enum<K> labelSousZone, String valeur) {
        this.labelSousZone = labelSousZone;
        this.valeur = valeur;
    }

    /**
     * Méthode de tri des éléments de la liste
     * @param o
     * @return
     */
    @Override
    public int compareTo(SousZone o) {
        if (EnumUtils.isValidEnum(ZonesTransliterees.class, o.getLabelSousZone().name())) {
            return EnumUtils.getEnumList(ZonesTransliterees.class).indexOf(ZonesTransliterees.valueOf(o.getLabelSousZone().name()));
        }
        return this.getLabelSousZone().compareTo((K) o.getLabelSousZone());
    }

    @Override
    public String toString() {
        return "SousZone{" +
                "labelSousZone=" + labelSousZone +
                ", valeur='" + valeur + '\'' +
                '}';
    }
}
