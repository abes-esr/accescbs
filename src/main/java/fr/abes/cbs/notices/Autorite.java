package fr.abes.cbs.notices;

import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Autorite extends Notice {
    @Override
    public String getNumEx() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Impossible de récupérer l'exemplaire d'une notice autorité");
    }

    @Override
    public TYPE_NOTICE getType() {
        return TYPE_NOTICE.AUTORITE;
    }

    public Autorite(String notice) throws ZoneException {
        super();
        createAutoriteFromUnimarc(notice);
    }

    public Autorite(String notice, FORMATS format) throws ZoneException {
        switch (format) {
            case UNM:
                createAutoriteFromUnimarc(notice);
                break;
            case XML:
                createAutoriteFromXml(notice);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + format);
        }
    }

    private void createAutoriteFromXml(String noticeXml) {
        try {
            Document doc = DocumentHelper.parseText(noticeXml);
            List<Node> listeZone = doc.selectNodes("//record/*");

            for (int i = 0; i < listeZone.size(); i++) {
                Node zone = listeZone.get(i);
                //cas ou la zone XML est de type datafield
                if ("datafield".equals(zone.getName())) {
                    generateDataField(zone);
                } else if ("controlfield".equals(zone.getName())) {
                    generateControlField(zone);
                } else {
                    //cas leader
                    generateLeader(zone);
                }

            }
            this.addZone("00A", "$0", "0");
            this.addZone("00U", "$0", "utf8");
        } catch (Exception ex) {
            log.error("Error converting Xml to Marc ", ex);
        }
    }

    private void generateLeader(Node zone) {

    }

    private void generateControlField(Node zone) throws ParseException, ZoneException {
        String labelZone = ((Element) zone).attributeValue("tag");
        switch (labelZone) {
            case "001":
                Zone zone003 = new Zone("003", getType(), zone.getStringValue());
                this.addZone(zone003);
                break;
            case "004":
                SimpleDateFormat dateFormatXml4 = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat dateFormatSudoc4 = new SimpleDateFormat("dd-MM-yy");
                Date dateXml = dateFormatXml4.parse(zone.getStringValue());
                this.addZone("004", dateFormatSudoc4.format(dateXml));
                break;
            case "005":
                SimpleDateFormat dateFormatXml5 = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
                SimpleDateFormat dateFormatSudoc5 = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");
                dateXml = dateFormatXml5.parse(zone.getStringValue());
                this.addZone("005", dateFormatSudoc5.format(dateXml));
                break;
            case "006":
                StringBuilder zone004String = new StringBuilder(findZone("004", 0).getValeur());
                StringBuilder zone005String = new StringBuilder(findZone("005", 0).getValeur());
                StringBuilder zone006String = new StringBuilder(zone.getStringValue());
                zone006String.append(':');
                zone004String.insert(0, zone006String);
                zone005String.insert(0, zone006String);
                this.deleteZone("005");
                this.deleteZone("004");
                this.addZone("004", zone004String.toString());
                this.addZone("005", zone005String.toString());
                this.addZone("006", zone004String.toString());
                break;
            case "008":
                this.addZone("008", "$a", zone.getStringValue());
                break;
            default:

        }
    }

    private void generateDataField(Node zone) throws ZoneException {
        String zoneId = ((Element) zone).attributeValue("tag");
        //on ne traite pas les zones 100, 152, 801 et 9xx du format d'export
        if (!(zoneId.equals("100") || zoneId.equals("152") || zoneId.equals("801") || Integer.parseInt(zoneId) >= 900)) {
            //récupération du nombre de zones déjà existantes dans la notice avec ce label
            int indexZone = this.findZones(zoneId).size();
            char[] indicateurs = getIndicateurs(zone);
            List<Node> listeSousZones = zone.selectNodes("*");
            traiterSousZonesXml(zoneId, indicateurs, listeSousZones, indexZone);
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
                if (!generate120(zoneId, listeSousZones.get(k), indicateurs)) {
                    //on ne traite pas les sous zones $7 du format d'export
                    if (!codeId.equals("7")) {
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
        }
    }

    /**
     * Méthode de génération de la zone 120 : suppression du dernier caractère en $a
     * @return false si la zone n'a pas été générée
     * @param zoneId
     * @param node
     * @param indicateurs
     */
    private boolean generate120(String zoneId, Node node, char[] indicateurs) throws ZoneException {
        if (zoneId.equals("120")) {
            this.addZone("120", "$a", node.getStringValue().substring(0,1), indicateurs);
            return true;
        }
        return false;
    }

    public void createAutoriteFromUnimarc(String autoriteUnm) throws ZoneException {
        Pattern pattern;
        Matcher matcher;

        //suppression des caractères du protocole pica3 présent en préfixe et suffixe de la notice
        String autoriteToCreate = autoriteUnm.substring(autoriteUnm.indexOf(Constants.STR_1F) + 1, autoriteUnm.indexOf(Constants.STR_1E));
        String[] tabAutorite = autoriteToCreate.split("\\r");

        Pattern standarZoneRegexPattern = Pattern.compile("[0-8][\\d][\\w]\\s.*[$].*");
        Pattern standarZoneSansSousZonePattern = Pattern.compile("[0-8][\\d]{2}\\s[^$].*");
        String zonePrecedente = "";
        //variable de gestion de l'index en cas de zone répétée
        int indexZone = 0;
        for (String lineZone : tabAutorite) {
            Matcher standardZoneRegexFinded = standarZoneRegexPattern.matcher(lineZone);
            Matcher standardZoneSansSousZoneFinded = standarZoneSansSousZonePattern.matcher(lineZone);
            String labelZone = "";
            char[] indicateurs = new char[2];
            boolean firstSousZone = true;
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
                    for (int i = 0; i <= 50; i++) {
                        if (matcher.group("szv" + i) != null) {
                            if (firstSousZone) {
                                if (indicateurs.length > 0) {
                                    this.addZone(labelZone, matcher.group("szk" + i), matcher.group("szv" + i), indicateurs);
                                } else {
                                    this.addZone(labelZone, matcher.group("szk" + i), matcher.group("szv" + i));
                                }
                                firstSousZone = false;
                            } else {
                                this.addSousZone(labelZone, matcher.group("szk" + i), matcher.group("szv" + i), indexZone);
                            }

                        }
                    }
                    zonePrecedente = labelZone;
                }
            } else if (standardZoneSansSousZoneFinded.find()) {
                genererZonesSansSousZone(lineZone, labelZone);
            }
        }
    }

}
