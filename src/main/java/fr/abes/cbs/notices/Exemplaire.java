package fr.abes.cbs.notices;

import com.google.common.collect.ListMultimap;
import fr.abes.cbs.exception.NoticeException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.sequences.SequenceEtatColl;
import fr.abes.cbs.notices.sequences.SequenceParallele;
import fr.abes.cbs.notices.sequences.SequencePrimaire;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import fr.abes.cbs.zones.enumSousZones.Zone_955;
import fr.abes.cbs.zones.enumSousZones.Zone_956;
import fr.abes.cbs.zones.enumSousZones.Zone_957;
import fr.abes.cbs.zones.enumSousZones.Zone_959;
import org.dom4j.DocumentException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Exemplaire extends Notice {
    public Exemplaire() {
        super();
    }

    public Exemplaire(ListMultimap<String, Zone> listeZones) {
        this.listeZones = listeZones;
    }

    public Exemplaire(List<Zone> listeZones) {
        super();
        for (Zone zone : listeZones) {
            this.listeZones.put(zone.getLabel(), zone);
        }
    }

    public Exemplaire(String notice) throws ZoneException {
        super();
        createExemplaireFromUnimarc(notice);
    }

    public Exemplaire(String notice, FORMATS format) throws ZoneException, NoticeException, DocumentException {
        super();
        switch (format) {
            case UNM:
                createExemplaireFromUnimarc(notice);
                break;
            case XML:
                createExemplaireFromXml(notice);
                break;
            default:
                throw new NoticeException("Format non pris en charge dans la création de notice");
        }
    }

    private void createExemplaireFromXml(String noticeXml) throws ZoneException, DocumentException {
        createNoticeFromXml(noticeXml);
    }

    /**
     * Constructeur d'un objet Exemplaire à partir d'une chaine issue du Sudoc
     *
     * @param exemplaire chaine de l'exemplaire préfixée de STR_1F et suffixée de STR_1E
     */
    public void createExemplaireFromUnimarc(String exemplaire) throws ZoneException {
        Pattern pattern;
        Matcher matcher;

        //suppression du caractère STR_1F et STR_1E de la chaine
        String exempToCreate = exemplaire.substring(exemplaire.indexOf(Constants.STR_1F) + 1, exemplaire.indexOf(Constants.STR_1E));
        String[] tabZonesExemp = exempToCreate.split("\\r");
        //Regex qui identifie les zones non systemes,
        //elles peuvent commencer par un E majuscule ou minuscule excepté le A, et contenir une serie de chiffres.
        Pattern standardZoneRegexPattern = Pattern.compile("^[^A][E/i]?\\d*");

        //Regex identifiant les zones systèmes A97, A98, A99
        Pattern systemZonesRegexPattern = Pattern.compile("^[A][9][789]\\d*");

        //Regex identifiant la zone système A97
        Pattern systemZone97RegexPattern = Pattern.compile("^[A][9][7]\\d*");

        //Regex identifiant la zone système A98
        Pattern systemZone98RegexPattern = Pattern.compile("^[A][9][8]\\d*");

        //Regex identifiant les zones 955 / 956 / 957 / 959
        Pattern standardZoneEtatCollRegexPattern = Pattern.compile("^[9][5][5679]\\d*");
        String zonePrecedente = "";
        Integer indexZone = 0;

        for (String lineZone : tabZonesExemp) {
            Matcher standardZoneRegexFinded = standardZoneRegexPattern.matcher(lineZone);
            Matcher systemZonesRegexFinded = systemZonesRegexPattern.matcher(lineZone);
            Matcher systemZones97RegexFinded = systemZone97RegexPattern.matcher(lineZone);
            Matcher standardZoneEtatCollRegexFinded = standardZoneEtatCollRegexPattern.matcher(lineZone);
            Matcher systemZones98RegexFinded = systemZone98RegexPattern.matcher(lineZone);

            String labelZone = "";
            char[] indicateurs = new char[2];
            boolean firstSousZone = true;
            if (standardZoneEtatCollRegexFinded.find()) {
                genererEtatCollection(lineZone);
            } else {
                if (standardZoneRegexFinded.find()) {
                    pattern = Pattern.compile(Utilitaire.setStandardZoneRegex(Constants.NB_SOUS_ZONES_REGEXP_STANDARD));
                    matcher = pattern.matcher(lineZone);

                    while (matcher.find()) {
                        if (matcher.group("zStaName") != null) {
                            labelZone = matcher.group("zStaName");
                            indexZone = ((labelZone.equals(zonePrecedente)) ? indexZone + 1 : 0);
                        }
                        if (matcher.group("zStaHash") != null) {
                            indicateurs = matcher.group("zStaHash").toCharArray();
                        }
                        for (int i = 0; i <= Constants.NB_SOUS_ZONES_REGEXP_STANDARD; i++) {
                            if (matcher.group("szv" + i) != null) {
                                if (firstSousZone) {
                                    if (indicateurs.length > 0) {
                                        this.addZone(labelZone, matcher.group("szk" + i), matcher.group("szv" + i), indicateurs);
                                    } else {
                                        this.addZone(labelZone, matcher.group("szk" + i), matcher.group("szv" + i));
                                    }
                                    firstSousZone = false;
                                } else {
                                    this.addSousZone(labelZone, matcher.group("szk" + i), matcher.group("szv" + i),  indexZone);
                                }
                            }
                        }
                        zonePrecedente = labelZone;
                    }
                } else if (systemZonesRegexFinded.find()) {
                    if (systemZones97RegexFinded.find()) {
                        genererZoneA97(lineZone, labelZone);
                    } else if (systemZones98RegexFinded.find()) {
                        genererZoneA98(lineZone, labelZone);
                    } else {
                        genererZoneA99(lineZone, labelZone);
                    }
                }
            }
        }
    }

    /**
     * Méthode permettant de générer une zone A99 d'exemplaire
     * @param lineZone ligne de la notice contenant la zone
     * @param labelZone intitulé de la zone
     */
    private void genererZoneA99(String lineZone, String labelZone) throws ZoneException {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(Constants.systemZone99Regex);
        matcher = pattern.matcher(lineZone);

        while (matcher.find()) {
            if (matcher.group("zSysName") != null) {
                labelZone = matcher.group("zSysName");
            }
            if (matcher.group("zSysv0") != null) {
                this.addZone(labelZone, matcher.group("zSysv0"));
            }
        }
    }

    /**
     * Méthode permettant de générer une zone A98 d'exemplaire
     * @param lineZone ligne de la notice contenant la zone
     * @param labelZone intitulé de la zone
     */
    private void genererZoneA98(String lineZone, String labelZone) throws ZoneException {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(Constants.systemZone98Regex);
        matcher = pattern.matcher(lineZone);

        while (matcher.find()) {
            if (matcher.group("zSysName") != null) {
                labelZone = matcher.group("zSysName");
            }
            if (matcher.group("zSysv0") != null && matcher.group("zSysv1") != null) {
                this.addZone(labelZone, matcher.group("zSysv0") + matcher.group("zSysv1"));
            }
        }
    }

    /**
     * Méthode permettant de générer une zone A97 d'exemplaire
     * @param lineZone ligne de la notice contenant la zone
     * @param labelZone intitulé de la zone
     */
    private void genererZoneA97(String lineZone, String labelZone) throws ZoneException {
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(Constants.systemZone97Regex);
        matcher = pattern.matcher(lineZone);

        while (matcher.find()) {
            if (matcher.group("zSysName") != null) {
                labelZone = matcher.group("zSysName");
            }
            if (matcher.group("zSysv0") != null && matcher.group("zSysv1") != null) {
                this.addZone(labelZone,matcher.group("zSysv0") + " " + matcher.group("zSysv1"));
            }
        }
    }

    /**
     * Méthode de génération d'une zone d'état de collection : 955, 956, 957, 959
     * @param lineZone
     */
    private void genererEtatCollection(String lineZone) throws ZoneException {
        String labelZone = "";
        String valeurZone = "";
        ZoneEtatColl zoneEtatColl = new ZoneEtatColl();
        char[] indicateurs = new char[2];
        Pattern pattern;
        Matcher matcher;
        String sequenceParaStr;
        SequenceParallele sequencePara;
        String sequencePrimaireStr;
        SequencePrimaire sequencePrimaire;
        SequenceEtatColl sequenceEtatColl;
        pattern = Pattern.compile(Constants.standardZoneSequence);
        matcher = pattern.matcher(lineZone);
        while (matcher.find()) {
            if (matcher.group("zStaName") != null) {
                labelZone = matcher.group("zStaName");
            }
            if (matcher.group("zStaHash") != null) {
                indicateurs = matcher.group("zStaHash").toCharArray();
            }
            zoneEtatColl = new ZoneEtatColl(labelZone, indicateurs);
            if (matcher.group("szv") != null) {
                valeurZone = matcher.group("szv");
            }
            //on récupère toutes les séquences de l'état de collection séparées par un $0
            String[] tabSequence = valeurZone.split("[$]0\\s");
            for (String sequence : tabSequence) {
                sequenceParaStr = "";
                sequencePara = null;
                //on récupère la sous séquence de numérotation parallèle (séquence située après le $5 )
                String[] tabNumerotationPara = sequence.split("[$]5\\s");
                if (tabNumerotationPara.length == 2) {
                    sequenceParaStr = tabNumerotationPara[1];
                    sequencePrimaireStr = tabNumerotationPara[0];
                }
                else {
                    sequencePrimaireStr = tabNumerotationPara[0];
                }
                //si on a trouvé de séquence de numérotation parallèle
                if (!sequenceParaStr.isEmpty()) {
                    sequencePara = new SequenceParallele(sequenceParaStr);
                }
                sequencePrimaire = new SequencePrimaire(sequencePrimaireStr, labelZone);
                //ajout de la séquence primaire et de la séquence de numérotation parallèle dans une nouvelle séquence d'état de collection
                sequenceEtatColl = new SequenceEtatColl(sequencePrimaire, sequencePara);
                zoneEtatColl.addSequence(sequenceEtatColl);
            }

            zoneEtatColl.setLabel(labelZone);
            zoneEtatColl.setIndicateurs(indicateurs);
        }
        //traitement des zones restantes situées après les séquences
        int nbSousZones;
        switch (zoneEtatColl.getLabel()) {
            case "955":
                pattern = Pattern.compile(Utilitaire.setEtatCollZoneRestantes(Zone_955.values()));
                nbSousZones = Zone_955.values().length;
                break;
            case "956":
                pattern = Pattern.compile(Utilitaire.setEtatCollZoneRestantes(Zone_956.values()));
                nbSousZones = Zone_956.values().length;
                break;
            case "957":
                pattern = Pattern.compile(Utilitaire.setEtatCollZoneRestantes(Zone_957.values()));
                nbSousZones = Zone_957.values().length;
                break;
            default:
                pattern = Pattern.compile(Utilitaire.setEtatCollZoneRestantes(Zone_959.values()));
                nbSousZones = Zone_959.values().length;
        }

        matcher = pattern.matcher(lineZone);
        if (matcher.find()) {
            for (int i = 0; i< nbSousZones ; i++) {
                if (matcher.group("szk" + i) != null) {
                    zoneEtatColl.addSubLabel(matcher.group("szk" + i), matcher.group("szv" + i));
                }
            }
        }
        this.addZone(zoneEtatColl);
    }

    public void creerNoticeFromUnimarc(String notice) throws ZoneException {
        this.listeZones = new Exemplaire(notice).listeZones;
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
     * Récupération du numéro d'un exemplaire à partir du label de la première zone
     *
     * @return numero de l'exemplaire au format xy (x/y entre 0 et 9)
     */
    public String getNumEx() {
        return this.listeZones.values().iterator().next().getLabel().toString().substring(1);
    }

    @Override
    public TYPE_NOTICE getType() {
        return TYPE_NOTICE.EXEMPLAIRE;
    }
}
