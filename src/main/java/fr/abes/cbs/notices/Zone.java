package fr.abes.cbs.notices;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Table;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.zones.ZoneSpecification;
import fr.abes.cbs.zones.ZonesSpecifications;
import fr.abes.cbs.zones.enumZones.EnumZones;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@NoArgsConstructor
public class Zone {

    protected String label;

    protected boolean isProtected;

    protected String valeur;

    protected char[] indicateurs;

    protected TYPE_NOTICE typeNotice;

    String specifications = "";

    List<String> specificationList = Arrays.asList(specifications.split(""));

    Table<Integer, String, String> subLabelTable = HashBasedTable.create();

    public Zone(String label, TYPE_NOTICE typeNotice, Table<Integer, String, String> subLabelTable) throws ZoneException {
        this.label = label;
        this.typeNotice = typeNotice;
        setSubLabelTable(subLabelTable);
        initSpecifications();
    }

    public Zone(String label, TYPE_NOTICE typeNotice, Table<Integer, String, String> subLabelTable, char[] indicateurs) throws ZoneException {
        this.label = label;
        this.typeNotice = typeNotice;
        setSubLabelTable(subLabelTable);
        this.indicateurs = indicateurs;
        initSpecifications();
    }

    public Zone(String label, TYPE_NOTICE typeNotice, String value) throws ZoneException {
        this.label = label;
        this.typeNotice = typeNotice;
        this.valeur = value;
        initSpecifications();
    }

    public Zone(String label, TYPE_NOTICE typeNotice, char[] indicateurs) throws ZoneException {
        this.label = label;
        this.typeNotice = typeNotice;
        this.indicateurs = indicateurs;
        initSpecifications();
    }

    public Zone(String label, TYPE_NOTICE typeNotice, char[] indicateurs, String value) throws ZoneException {
        this.label = label;
        this.typeNotice = typeNotice;
        this.indicateurs = indicateurs;
        this.valeur = value;
        initSpecifications();
    }

    public Zone(String label, TYPE_NOTICE typeNotice) throws ZoneException {
        this.label = label;
        this.typeNotice = typeNotice;
        initSpecifications();
    }

    /**
     * Ajoute les sous-zones d'une table à les zone en utilisant la méthode addSubLabel
     *
     * @param subLabelTable
     */
    private void setSubLabelTable(Table<Integer, String, String> subLabelTable) throws ZoneException {
        AtomicReference<ZoneException> ex = new AtomicReference<>(new ZoneException(null));
        subLabelTable.rowMap().forEach((integer, stringStringMap) -> {
            Map.Entry<String, String> singleSubLabel = stringStringMap.entrySet().stream().findFirst().orElse(null);
            assert singleSubLabel != null;
            try {
                this.addSubLabel(singleSubLabel.getKey(), singleSubLabel.getValue());
            } catch (ZoneException e) {
                ex.set(e);
            }
        });
        if (ex.get().getMessage() != null) {
            throw ex.get();
        }
    }

    /**
     * Permet l'ajout d'une sous zone dans une zone à la suite les unes des autres
     *
     * @param subLabel
     * @param value
     * @throws Exception
     */
    public void addSubLabel(String subLabel, String value) throws ZoneException {
        subLabel = getStandardSubLabel(subLabel);
        if (isInSpecification(subLabel)) {
            subLabelTable.put(getMaxIndex(subLabelTable), subLabel, value);
        } else {
            throw new ZoneException("La sous-zone " + subLabel + " n'est pas reconnue dans la zone " + this.getLabelForOutput() + ". Valeur = " + value);
        }
    }

    /**
     * Standardise le code de la sous-zone avec un $
     *
     * @param subLabel
     * @return
     */
    private String getStandardSubLabel(String subLabel) {
        if (!subLabel.startsWith("$")) {
            subLabel = "$" + subLabel;
        }
        return subLabel;
    }

    /**
     * Supprime toute les sous-zones subLabel de la zone
     *
     * @param subLabel
     */
    public void deleteSubLabel(String subLabel) {
        subLabel = getStandardSubLabel(subLabel);
        subLabelTable.columnMap().remove(subLabel);
    }

    /**
     * Retourne les sous-zones et valeurs de la zone
     *
     * @return
     */
    public ListMultimap<String, String> getSubLabelList() {
        ListMultimap<String, String> sublabels = ArrayListMultimap.create();
        subLabelTable.rowMap().forEach((integer, stringStringMap) -> {
            stringStringMap.forEach((s, s2) -> {
                sublabels.put(s, s2);
            });
        });
        return sublabels;
    }

    /**
     * Remplace toutes les valeurs des sous-zones subLabel par value
     *
     * @param subLabel
     * @param value
     */
    public void editSubLabel(String subLabel, String value) {
        if (isInSpecification(subLabel)) {
            subLabelTable.columnMap().get(subLabel).replaceAll((integer, s) -> s = value);
        }
    }

    /**
     * Retourne la valeur de la première sous-zone subLabel
     *
     * @param subLabel
     * @return
     */
    public String findSubLabel(String subLabel) {
        subLabel = getStandardSubLabel(subLabel);
        if (subLabelTable.columnMap().get(subLabel) == null) {
            return null;
        }
        return subLabelTable.columnMap().get(subLabel).values().stream().findFirst().orElse(null);
    }

    /**
     * Ecrit la zone selon la spécification
     *
     * @return
     */
    public String toString() {
        Table<Integer, String, String> tableWithoutDollar = removeDollar();
        StringBuilder zone = new StringBuilder(getLabelForOutput()).append(" ");
        if (this.getIndicateurs() != null) {
            zone.append(this.getIndicateurs());
        }
        if (this.getValeur() != null) {
            zone.append(this.getValeur());
        } else {
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
        }
        zone.append(Constants.STR_0D);
        return zone.toString();
    }

    /**
     * Crée une table sans les $ dans les noms des sous-zones
     *
     * @return
     */
    protected Table<Integer, String, String> removeDollar() {
        Table<Integer, String, String> tableWithoutDollar = HashBasedTable.create();

        subLabelTable.rowMap().forEach((integer, stringStringMap) -> {
            stringStringMap.forEach((s, s2) -> {
                if (s.startsWith("$")) {
                    s = s.substring(1);
                }
                Map<String, String> stringStringMapWithoutDollar = new HashMap<>();
                stringStringMapWithoutDollar.put(s, s2);
                tableWithoutDollar.row(integer).putAll(stringStringMapWithoutDollar);
            });
        });
        return tableWithoutDollar;
    }

    /**
     * Récupération du label de la zone au format String
     *
     * @return l'intitulé de la zone prêt à être écrit dans la notice au format String
     */
    public String getLabelForOutput() {
        return (label.startsWith("B")
                || label.startsWith("Z")
                || label.startsWith("P")
                || label.matches("[A]\\d\\d\\d")) ? label.substring(1) : label;
    }

    /**
     * Ajoute à zone les sous-zones suivants une séquence
     *
     * @param zone
     * @param sequence
     * @param tableWithoutDollar
     */
    protected void addFollowingSequenceToString(StringBuilder zone, List<String> sequence, Table<Integer, String, String> tableWithoutDollar) {
        for (Map<String, String> value : tableWithoutDollar.rowMap().values()) {
            Map.Entry<String, String> valueEntryMap = value.entrySet().stream().findFirst().orElse(null);
            if (sequence.contains(valueEntryMap.getKey())) {
                zone.append("$").append(valueEntryMap.getKey()).append(valueEntryMap.getValue());
            }
        }
    }

    /**
     * Ajoute à zone les sous-zones suivant l'ordre d'ajout
     *
     * @param zone
     * @param labelSpecification
     * @param tableWithoutDollar
     */
    protected void addNormalToString(StringBuilder zone, String labelSpecification, Table<Integer, String, String> tableWithoutDollar) {
        if (tableWithoutDollar.columnMap().get(labelSpecification) != null) {
            for (String value : tableWithoutDollar.columnMap().get(labelSpecification).values()) {
                zone.append("$").append(labelSpecification).append(value);
            }
        }
    }

    /**
     * Retourne l'index max + 1 de la table
     *
     * @param table
     * @return
     */
    private Integer getMaxIndex(Table<Integer, String, String> table) {
        if (table.isEmpty()) {
            return 0;
        } else {
            return table.rowMap().keySet().stream().max(Integer::compareTo).orElse(0) + 1;
        }
    }

    /**
     * Initialisation des membres de la classe
     */
    private void initSpecifications() throws ZoneException {
        try {
            ZoneSpecification zoneSpecification = ZonesSpecifications.getZoneSpecification(this.getLabel(), this.typeNotice);
            this.specifications = zoneSpecification.sublabelSpecification;
            this.isProtected = zoneSpecification.isProtected;
            this.specificationList = Arrays.asList(specifications.split(""));
        } catch (NullPointerException ex) {
            throw new ZoneException("zone " + this.getLabelForOutput() + " inconnue pour le type de notice " + typeNotice);
        }
    }

    /**
     * Vérifie que la sous-zone est dans les spécifications
     *
     * @param subLabel
     * @return
     */
    private boolean isInSpecification(String subLabel) {
        return specifications.contains(subLabel.substring(1));
    }


}
