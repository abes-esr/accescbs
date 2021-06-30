package fr.abes.cbs.notices;

import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import jdk.jshell.spi.ExecutionControl;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class Biblio extends Notice {
    public Biblio() {
        super();
    }

    public Biblio(String biblioUnm) throws ZoneException {
        super();
        createBiblioFromUnm(biblioUnm);
    }

    /**
     * Constructeur permettant de déterminer le constructeur à appeler en fonction du format de la notice reçue (Unimarc ou XML)
     *
     * @param notice
     * @param format
     * @throws ExecutionControl.NotImplementedException
     */
    public Biblio(String notice, FORMATS format) throws ZoneException {
        switch (format) {
            case UNM:
                createBiblioFromUnm(notice);
                break;
            case XML:
                createBiblioFromXml(notice);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + format);
        }
    }

    /**
     * Construction de l'objet notice bibliographique à partir d'une chaine de caractère représentant la notice en unimarc de catalogage issu du CBS
     *
     * @param biblioUnm
     */
    public void createBiblioFromUnm(String biblioUnm) throws ZoneException {
        Pattern pattern;
        Matcher matcher;

        //suppression des caractères du protocole pica3 présent en préfixe et suffixe de la notice
        String biblioToCreate = biblioUnm.substring(biblioUnm.indexOf(Constants.STR_1F) + 1, biblioUnm.indexOf(Constants.STR_1E));
        String[] tabBiblio = biblioToCreate.split("\\r");

        Pattern standarZoneRegexPattern = Pattern.compile("[0-8][\\d][\\w]\\s.*[$].*");
        Pattern standarZoneSansSousZonePattern = Pattern.compile("[0-8][\\d]{2}\\s[^$].*");
        String zonePrecedente = "";
        //variable de gestion de l'index en cas de zone répétée
        Integer indexZone = 0;
        for (String lineZone : tabBiblio) {
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

    /**
     * Méthode permettant de créer un objet à partir d'une notice formattée en XML (ne fonctionne pas sur les notices issues de la base XML qui sont en unimarc d'export)
     *
     * @param noticeXml
     */
    public void createBiblioFromXml(String noticeXml) {
        createNoticeFromXml(noticeXml);
    }

    @Override
    public String getNumEx() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Impossible de récupérer l'exemplaire d'une notice bibliographique");
    }


    @Override
    public TYPE_NOTICE getType() {
        return TYPE_NOTICE.BIBLIOGRAPHIQUE;
    }

    /**
     * Détermine si une notice de thèse est un support papier ou électronique
     *
     * @return true si support papier / false dans le cas contraire
     */
    public Boolean isTheseElectronique() {
        return (this.findZones("008").get(0).findSubLabel("$a").startsWith("O")
                && this.findZones("105").get(0).findSubLabel("$b").startsWith("m"));
    }

}
