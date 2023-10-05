package fr.abes.cbs.notices;

import com.google.common.collect.*;
import fr.abes.cbs.exception.NoticeException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.sequences.SequenceEtatColl;
import fr.abes.cbs.notices.sequences.SequencePrimaire;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import fr.abes.cbs.zones.ZonesSpecifications;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;

import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Notice implements INotice {
    @Getter
    protected ListMultimap<String, Zone> listeZones;

    public Notice() {
        initListeZones();
    }

    public Notice(List<Zone> listeZones) {
        initListeZones();
        for (Zone zone : listeZones) {
            this.listeZones.put(zone.getLabel(), zone);
        }
    }

    public Notice(ListMultimap<String, Zone> listeZones) {
        this.listeZones = listeZones;
    }

    protected void initListeZones() {
        this.listeZones = MultimapBuilder.treeKeys(new Comparator<String>() {
            public int compare(String zone1, String zone2) {
                return ZonesSpecifications.getZoneSpecification(zone1, getType()).order.compareTo(ZonesSpecifications.getZoneSpecification(zone2, getType()).order);
            }
        }).linkedListValues().build();
    }

    protected void createNoticeFromXml(String noticeXml) throws DocumentException, ZoneException {
        Document doc = DocumentHelper.parseText(noticeXml);
        List<Node> listeZone = doc.selectNodes("//record/*");

        for (int i = 0; i < listeZone.size(); i++) {
            Node zone = listeZone.get(i);
            //cas ou la zone XML est de type datafield
            if ("datafield".equals(zone.getName())) {
                String zoneId = ((Element) zone).attributeValue("tag");
                //récupération du nombre de zones déjà existantes dans la notice avec ce label
                int indexZone = this.findZones(zoneId).size();
                char[] indicateurs = getIndicateurs(zone);
                List<Node> listeSousZones = zone.selectNodes("*");
                traiterSousZonesXml(zoneId, indicateurs, listeSousZones, indexZone);
            }

        }
    }

    /**
     * Méthode de traitement des sous zones d'une zone formattée en xml
     *
     * @param zoneId         : Objet XML correspondant à la zone
     * @param indicateurs    : tableau contenant les 2 indicateurs
     * @param listeSousZones : liste de nodes contenant l'ensemble des sous zones de la zone
     * @param indexZone      : index ou placer la zone dans la liste multimap
     */
    protected void traiterSousZonesXml(String zoneId, char[] indicateurs, List<Node> listeSousZones, Integer indexZone) throws ZoneException {
        boolean isFirst = true;
        for (int k = 0; k < listeSousZones.size(); k++) {
            if (Constants.SUBFIELD.equals(listeSousZones.get(k).getName())) {
                String codeId = ((Element) listeSousZones.get(k)).attributeValue("code");
                if (listeSousZones.get(k).getStringValue() != null) {
                    //cas de la première sous zone de la zone, permettant d'ajouter les indicateurs
                    if (isFirst) {
                        //cas des zones exx
                        if (zoneId.matches("e\\d\\d")) {
                            this.addZone(zoneId, Constants.DOLLAR + codeId, listeSousZones.get(k).getStringValue());
                        } else {
                            this.addZone(zoneId, Constants.DOLLAR + codeId, listeSousZones.get(k).getStringValue(), indicateurs);
                        }
                        isFirst = false;
                    } else {
                        this.addSousZone(zoneId, Constants.DOLLAR + codeId, listeSousZones.get(k).getStringValue(), indexZone);
                    }
                }
            }
        }
    }

    /**
     * Méthode de récupération des indicateurs (attributs ind1 et ind2 du noeud en cours) de la zone XML
     *
     * @param zone
     * @return tableau de caractères contenant les 2 indicateurs
     */
    public static char[] getIndicateurs(Node zone) {
        String ind1id = ((Element) zone).attributeValue("ind1");
        String ind2id = ((Element) zone).attributeValue("ind2");

        String ind1 = null;
        String ind2 = null;
        if (ind1id != null) {
            ind1 = ind1id;
        }
        if (ind2id != null) {
            ind2 = ind2id;
        }
        if ((" ").equals(ind1)) {
            ind1 = Constants.DIEZ;
        }
        if ((" ").equals(ind2)) {
            ind2 = Constants.DIEZ;
        }
        return (ind1 + ind2).toCharArray();
    }

    /**
     * Méthode d'ajout d'une zone à la notice
     *
     * @param zone     intitulé de la zone à ajouter
     * @param sousZone intitulé de la première sous zone à ajouter
     * @param valeur   valeur à affecter à la sous zone
     */
    @Override
    public void addZone(String zone, String sousZone, String valeur) throws ZoneException {
        valeur = Utilitaire.deleteExpensionFromValue(valeur);
        Zone buffer = new Zone(zone, getType());
        buffer.addSubLabel(sousZone, valeur);
        listeZones.put(zone, buffer);
    }

    /**
     * Méthode d'ajout d'une zone à la notice
     *
     * @param zone        intitulé de la zone à ajouter
     * @param sousZone    intitulé de la première sous zone à ajouter
     * @param valeur      valeur à affecter à la sous zone
     * @param indicateurs tableau des indicateurs de la zone
     */
    @Override
    public void addZone(String zone, String sousZone, String valeur, char[] indicateurs) throws ZoneException {
        valeur = Utilitaire.deleteExpensionFromValue(valeur);
        Zone buffer = new Zone(zone, getType(), indicateurs);
        buffer.addSubLabel(sousZone, valeur);
        listeZones.put(zone, buffer);
    }

    /**
     * Méthode d'ajout d'une zone dans le cas zone + valeur
     *
     * @param zone   intitulé de la zone à créer
     * @param valeur valeur à affecter à la zone
     */
    @Override
    public void addZone(String zone, String valeur) throws ZoneException {
        valeur = Utilitaire.deleteExpensionFromValue(valeur);
        this.listeZones.put(zone, new Zone(zone, getType(), valeur));
    }

    /**
     * Méthode d'ajout d'une zone à partir d'un objet zone déjà constitué
     *
     * @param zone
     */
    @Override
    public void addZone(Zone zone) {
        if (zone != null) {
            this.listeZones.put(zone.getLabel(), zone);
        }
    }

    /**
     * Méthode d'ajout d'une zone d'état de collection à la notice
     *
     * @param zone        : intitulé de la zone d'état de collection : 955, 956, 957 ou 959
     * @param sousZone    : sous zone à ajouter (la zone ne peut pas être créée sans sous zone)
     * @param valeur      : valeur à affecter à la sous zone
     * @param indicateurs : indicateurs
     */
    @Override
    public void addZoneEtatCollection(String zone, String sousZone, String valeur, char[] indicateurs) throws ZoneException {
        ZoneEtatColl buffer = new ZoneEtatColl(zone, indicateurs);
        //new tag being created, the subtag is necessarily part of a primary sequence
        SequencePrimaire sequencePrimaire = new SequencePrimaire(sousZone + valeur, zone);
        SequenceEtatColl sequenceEtatColl = new SequenceEtatColl(sequencePrimaire);
        buffer.addSequence(sequenceEtatColl);
        listeZones.put(zone, buffer);
    }

    /**
     * Retourne une liste de zones dont le label est passé en paramètre
     *
     * @param label label de la zone à récupérer
     * @return liste des zones correspondant au label (plusieurs si zone répétable)
     */
    @Override
    public List<Zone> findZones(String label) {
        List<Zone> zonesfinded = new ArrayList<>();
        for (Zone localZone : this.listeZones.values()) {
            if (localZone.getLabel().equals(label)) {
                zonesfinded.add(localZone);
            }
        }
        return zonesfinded;
    }

    /**
     * Récupération de la nième zone dont le label est passé en paramètre
     *
     * @param label label de zone
     * @param index index de la nième zone répétée dans la notice
     * @return la zone placée à l'index fourni. Si l'index fourni est supérieur au nombre de zones trouvées, on retourne la première zone
     */
    @Override
    public Zone findZone(String label, Integer index) {
        List<Zone> zonesFinded = new ArrayList<>();
        for (Zone localZone : this.listeZones.values()) {
            if (localZone.getLabel().equals(label)) {
                zonesFinded.add(localZone);
            }
        }
        if (zonesFinded.size() == 0)
            return null;
        if (index < zonesFinded.size()) {
            return zonesFinded.get(index);
        }
        return zonesFinded.get(0);
    }

    /**
     * Méthode permettant de trouver une zone en spécifiant une sous zone contenant une valeur correspondant au pattern
     *
     * @param zone     zone sur laquelle chercher
     * @param sousZone sous zone dans laquelle chercher
     * @param pattern  valeur valeur devant être contenue dans la sous zone
     * @return liste des zones correspondant à la recherche
     */
    @Override
    public List<Zone> findZoneWithPattern(String zone, String sousZone, String pattern) {
        List<Zone> zonesFinded = new ArrayList<>();
        String strTarget = Normalizer.normalize(pattern, Normalizer.Form.NFD);

        for (Zone localZone : this.findZones(zone)) {
            localZone.getSubLabelTable().column(sousZone)
                    .forEach((integer, s) -> {
                        String strSource = Normalizer.normalize(s, Normalizer.Form.NFD);
                        if (strSource.contains(strTarget)) {
                            zonesFinded.add(localZone);
                        }
                    });
        }
        return zonesFinded;
    }

    /**
     * Remplacer la valeur d'une sous zone contenant une valeur donnée par une nouvelle valeur
     *
     * @param zone       zone sur laquelle effectuer le remplacement
     * @param sousZone   sous zone sur laquelle effectuer le remplacement
     * @param valeurInit valeur devant être contenue dans la sous zone
     * @param newValeur  nouvelle valeur à affecter (remplace l'intégralité de la valeur de la sous zone)
     */
    @Override
    public void replaceSousZoneWithValue(String zone, String sousZone, String valeurInit, String newValeur) {
        String strTarget = Normalizer.normalize(valeurInit, Normalizer.Form.NFD);
        for (Zone localZone : this.findZones(zone)) {

            localZone.getSubLabelTable().column(sousZone)
                    .replaceAll((integer, s) -> {
                        String strSource = Normalizer.normalize(s, Normalizer.Form.NFD);
                        if (strSource.contains(strTarget)) {
                            return newValeur;
                        }
                        return s;
                    });
        }
    }

    /**
     * Méthode d'ajout d'une sous zone dans une zone. En cas de zone répétée, toutes les zones seront concernées par l'ajout
     *
     * @param zone     zone dans laquelle rajouter la sous zone
     * @param sousZone sous zone à ajouter
     * @param valeur   valeur à affecter à la sous zone
     */
    @Override
    public void addSousZone(String zone, String sousZone, String valeur) throws ZoneException {
        valeur = Utilitaire.deleteExpensionFromValue(valeur);
        if (Utilitaire.isCorectParameter(zone) && Utilitaire.isCorectParameter(sousZone) && Utilitaire.isCorectParameter(valeur)) {
            ListMultimap<String, Zone> zonesToBeAdded = MultimapBuilder.treeKeys().linkedListValues().build();
            for (Zone localZone : this.findZones(zone)) {
                localZone.addSubLabel(sousZone, valeur);
                zonesToBeAdded.put(zone, localZone);
            }
            deleteZone(zone); //supprimer au préalable les zone modifiées pour les réinsérer
            this.listeZones.putAll(zonesToBeAdded);
        }
    }

    /**
     * Méthode d'ajout d'une sous zone dans une zone. En cas de zone répétée, uniquement la zone située à l'index en paramètre sera concernée
     *
     * @param zone
     * @param sousZone
     * @param valeur   valeur à placer dans la sous zone
     * @param index    de la zone en cours sur laquelle créer la sous zone (pour zones répétées uniquement)
     */
    @Override
    public void addSousZone(String zone, String sousZone, String valeur, Integer index) throws ZoneException {
        valeur = Utilitaire.deleteExpensionFromValue(valeur);
        if (Utilitaire.isCorectParameter(zone) && Utilitaire.isCorectParameter(sousZone) && Utilitaire.isCorectParameter(valeur)) {
            Zone localZone = this.findZone(zone, index);
            if (localZone != null) {
                localZone.addSubLabel(sousZone, valeur);
                deleteZone(zone, index); //supprimer au préalable la zone modifiée pour la réinsérer
                this.listeZones.put(localZone.getLabel(), localZone); //réajouter la zone modifiée finale dans la liste membre
            }
        }
    }

    /**
     * Ajout d'une sous zone dans une zone. La sous zone sera placée après une sous zone contenant une valeur précise
     *
     * @param zone
     * @param sousZone
     * @param valeur         valeur à placer dans la sous zone
     * @param sousZoneBefore sous zone précédente
     * @param valeurBefore   valeur de la sous zone précédente, après laquelle sera placée la nouvelle sous zone
     */
    @Override
    public void addSousZone(String zone, String sousZone, String valeur, String sousZoneBefore, String valeurBefore) throws ZoneException {
        valeurBefore = Utilitaire.deleteExpensionFromValue(valeurBefore);
        AtomicReference<ZoneException> ex = new AtomicReference<>(new ZoneException(null));
        if (Utilitaire.isCorectParameter(zone) && Utilitaire.isCorectParameter(sousZone) && Utilitaire.isCorectParameter(valeur) && Utilitaire.isCorectParameter(sousZoneBefore) && Utilitaire.isCorectParameter(valeurBefore)) {
            ListMultimap<String, Zone> zonesToBeAdded = MultimapBuilder.treeKeys().linkedListValues().build();
            for (Zone localZone : this.findZones(zone)) {

                String finalValeurBefore = valeurBefore;
                localZone.getSubLabelTable().rowMap()
                        .forEach((integer, stringStringMap) -> {
                            if (stringStringMap.get(sousZoneBefore) != null && stringStringMap.get(sousZoneBefore).contains(finalValeurBefore)) {
                                try {
                                    localZone.addSubLabel(sousZone, valeur);
                                } catch (ZoneException e) {
                                    ex.set(e);
                                }
                            }
                        });
                if (ex.get().getMessage() != null) {
                    throw ex.get();
                }
                zonesToBeAdded.put(zone, localZone);
            }
            deleteZone(zone); //supprimer au préalable les zone modifiées pour les réinsérer
            this.listeZones.putAll(zonesToBeAdded); //réajouter les zone modifiées finales dans la liste membre
        }
    }

    /**
     * Méthode de suppression d'une zone à l'index donnée
     *
     * @param zone
     * @param index index de la zone à supprimer en cas de zone répétée. Si null, suppression de toutes les zones répétées concernées
     */
    @Override
    public void deleteZone(String zone, Integer index) {
        Iterator<Zone> it = this.listeZones.values().iterator();
        Integer i = 0;
        while (it.hasNext()) {
            Zone zonetodelete = it.next();
            if ((zonetodelete.getLabel().equals(zone) && ((index == null) || (i == index)))) {
                it.remove();
            }
            if (zonetodelete.getLabel().equals(zone)) {
                i++;
            }
        }
    }

    /**
     * Méthode de suppression d'une zone de la notice (en cas de zones répétées, toutes les zones sont supprimées)
     *
     * @param zone
     */
    @Override
    public void deleteZone(String zone) {
        deleteZone(zone, null);
    }

    @Override
    public void deleteZoneWithValue(String zone, String sousZone, String valeur) {

        Iterator<Zone> it = this.listeZones.values().iterator();
        while (it.hasNext()) {
            Zone zoneToDelete = it.next();
            if (zoneToDelete.getLabel().equals(zone) && zoneToDelete.findSubLabel(sousZone).contains(valeur)) {
                it.remove();
            }
        }

    }

    /**
     * Méthode de suppression d'une sous zone dans une zone
     * en cas de zone répétée, toutes les sous zones qui se trouvent dans l'une des zones seront supprimées
     *
     * @param zone
     * @param sousZone
     */
    @Override
    public void deleteSousZone(String zone, String sousZone) {
        Iterator<Zone> it = this.listeZones.values().iterator();
        while (it.hasNext()) {
            Zone zoneToTreat = it.next();
            if (zoneToTreat.getLabel().equals(zone)) {
                zoneToTreat.deleteSubLabel(sousZone);
                if (zoneToTreat.getSubLabelTable().isEmpty()) {
                    it.remove();
                }
            }
        }
    }


    /**
     * Méthode de remplacement de la valeur d'une sous zone dans toutes les zones concernées
     *
     * @param zone     zone dans laquelle remplacer la sous zone
     * @param sousZone sous zone à remplacer
     * @param valeur   nouvelle valeur à affecter à la sous zone
     */
    @Override
    public void replaceSousZone(String zone, String sousZone, String valeur) {
        valeur = Utilitaire.deleteExpensionFromValue(valeur);
        for (Zone localzone : this.findZones(zone)) {
            localzone.editSubLabel(sousZone, valeur);
        }
    }

    @Override
    public String toString() {
        boolean zoneProtege = false;
        StringBuilder notice = new StringBuilder();
        for (Zone zoneATraiter : listeZones.values()) {
            //on traite différemment l'affichage des zones classiques et des zones d'état de collection à cause des zones protégées
            if (zoneATraiter.getClass().isInstance(ZoneEtatColl.class)) {
                ZoneEtatColl zoneEnCours = (ZoneEtatColl) zoneATraiter;
                notice.append(zoneEnCours);
            } else {
                zoneProtege = genererZoneProtegee(zoneProtege, notice, zoneATraiter);
                notice.append(zoneATraiter);
            }

        }
        return notice.toString();
    }

    /**
     * Génère STR_1B + P en début d'une zone protégée, ou STR_1B en début d'une zone suivant une zone protégée
     *
     * @param zoneProtege
     * @param notice
     * @param zoneEnCours
     * @return
     */
    private boolean genererZoneProtegee(boolean zoneProtege, StringBuilder notice, Zone zoneEnCours) {
        if (zoneProtege) {
            notice.append(Constants.STR_1B).append("D");
            zoneProtege = false;
        }
        if (zoneEnCours.isProtected) {
            notice.append(Constants.STR_1B).append("P");
            zoneProtege = true;
        }
        return zoneProtege;
    }

    /**
     * Méthode de génération d'une zone sans sous zone (uniquement zone + valeur) à partir d'une chaine Unimarc
     *
     * @param lineZone
     * @param labelZone
     */
    protected void genererZonesSansSousZone(String lineZone, String labelZone) throws ZoneException {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(Constants.zoneSansSousZoneRegex);
        matcher = pattern.matcher(lineZone);
        while (matcher.find()) {
            if (matcher.group("zStaName") != null) {
                labelZone = matcher.group("zStaName");
            }
            if (matcher.group("zVal") != null) {
                this.addZone(labelZone, matcher.group("zVal"));
            }
        }
    }
}
