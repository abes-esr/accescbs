package fr.abes.cbs.notices;

import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.utilitaire.Constants;
import jdk.jshell.spi.ExecutionControl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonneeLocale extends Notice {

    public DonneeLocale(String notice) throws ZoneException {
        createLocaleFromUnimarc(notice);
    }

    public DonneeLocale(String notice, FORMATS format) throws ZoneException {
        switch (format) {
            case UNM:createLocaleFromUnimarc(notice);
                break;
            case XML:createLocaleFromXml(notice);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + format);
        }
    }

    private void createLocaleFromXml(String noticeXml) {
        createNoticeFromXml(noticeXml);
    }

    /**
     * Constructeur d'un objet DonnéesLocales à partir d'une chaine issue du Sudoc
     * @param donneesLocales
     */
    public void createLocaleFromUnimarc(String donneesLocales) throws ZoneException {
        Pattern pattern;
        Matcher matcher;

        String donneesLocalesToCreate = donneesLocales.substring(donneesLocales.indexOf(Constants.STR_1F) + 1, donneesLocales.indexOf(Constants.STR_1E));
        String[] tabZonesDonneesLocales = donneesLocalesToCreate.split("\\r");

        Pattern standardZoneRegexPattern = Pattern.compile("^[L]?[0-9][0-9][0-9]");
        Pattern systemZonesRegexPattern = Pattern.compile("L005");

        for (String linezone : tabZonesDonneesLocales) {
            Matcher standardZoneRegexFinded = standardZoneRegexPattern.matcher(linezone);
            Matcher systemZonesRegexFinded = systemZonesRegexPattern.matcher(linezone);

            String labelzone = "";
            char[] indicateurs = new char[2];
            boolean firstSousZone = true;
            if (systemZonesRegexFinded.find()){
                pattern = Pattern.compile(Constants.systemZoneL005Regex);
                matcher = pattern.matcher(linezone);

                while (matcher.find()) {
                    if (matcher.group("zSysName") != null) {
                        labelzone = matcher.group("zSysName");
                    }
                    if (matcher.group("zSysv0") != null && matcher.group("zSysv1") != null) {
                        this.addZone(labelzone, matcher.group("zSysv0") + " " + matcher.group("zSysv1"));
                    }
                }
            }
            else if (standardZoneRegexFinded.find()) {
                pattern = Pattern.compile(Constants.donneesLocalesRegex);
                matcher = pattern.matcher(linezone);

                while (matcher.find()) {
                    if (matcher.group("zStaName") != null) {
                        labelzone = matcher.group("zStaName");
                    }
                    if (matcher.group("zStaHash") != null) {
                        indicateurs = matcher.group("zStaHash").toCharArray();
                    }
                    for (int i = 0; i <= 50; i++) {
                        if (matcher.group("szv" + i) != null) {
                            if (firstSousZone) {
                                if (indicateurs.length > 0) {
                                    this.addZone(labelzone, matcher.group("szk" + i), matcher.group("szv" + i), indicateurs);
                                } else {
                                    this.addZone(labelzone, matcher.group("szk" + i), matcher.group("szv" + i));
                                }
                                firstSousZone = false;
                            } else {
                                this.addSousZone(labelzone, matcher.group("szk" + i), matcher.group("szv" + i));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getNumEx() throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("Impossible de récupérer l'exemplaire d'une donnée locale");
    }

    @Override
    public TYPE_NOTICE getType() {
        return TYPE_NOTICE.LOCALE;
    }
}
