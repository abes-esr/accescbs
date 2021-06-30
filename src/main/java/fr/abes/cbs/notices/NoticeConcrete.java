package fr.abes.cbs.notices;

import fr.abes.cbs.models.ExemplaireFromXml;
import fr.abes.cbs.utilitaire.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
@NoArgsConstructor
public class NoticeConcrete extends Notice implements INotice {
    private Biblio noticeBiblio;
    private DonneeLocale noticeLocale;
    private List<Exemplaire> exemplaires = new ArrayList<>();

    public NoticeConcrete(String noticeXml) {
        StringBuilder biblio = new StringBuilder("<record>");
        StringBuilder locale = new StringBuilder("<record>");
        StringBuilder exemplairesXML = new StringBuilder("<record>");
        Matcher matcher;
        try {
            Document doc = DocumentHelper.parseText(noticeXml);
            List<Node> listeZone = doc.selectNodes("//record/*");

            Pattern zoneBiblioPattern = Pattern.compile(Constants.ZONE_BIBLIO_NAME_REGEX);
            Pattern zoneLocalePattern = Pattern.compile(Constants.ZONE_LOCALE_NAME_REGEX);
            Pattern zoneExemplairePattern = Pattern.compile(Constants.ZONE_EXEMPLAIRE_NAME_REGEX);

            for (int i = 0; i < listeZone.size(); i++) {
                Node zone = listeZone.get(i);
                matcher = zoneBiblioPattern.matcher(((Element) zone).attributeValue("tag"));
                if (matcher.find()) {
                    biblio.append(zone.asXML());
                } else {
                    matcher = zoneLocalePattern.matcher(((Element) zone).attributeValue("tag"));
                    if (matcher.find()) {
                        locale.append(zone.asXML());
                    } else {
                        matcher = zoneExemplairePattern.matcher(((Element) zone).attributeValue("tag"));
                        if (matcher.find()) {
                            exemplairesXML.append(zone.asXML());
                        }
                    }
                }
            }
            biblio.append("</record>");
            locale.append("</record>");
            exemplairesXML.append("</record>");
            noticeBiblio = new Biblio(biblio.toString(), FORMATS.XML);
            noticeLocale = new DonneeLocale(locale.toString(), FORMATS.XML);

            List<ExemplaireFromXml> exemplairesFromXml = getListeExemplaireFromXml(exemplairesXML.toString());
            for (ExemplaireFromXml exemplaireFromXml : exemplairesFromXml) {
                String exemplaire = exemplaireXMLBuilder(exemplaireFromXml);
                this.exemplaires.add(new Exemplaire(exemplaire, FORMATS.XML));
            }

        } catch (Exception e) {
            log.error("Impossible de construire la notice, erreur XML", e);
        }
    }

    private String exemplaireXMLBuilder(ExemplaireFromXml exemplaireFromXml) {
        return "<record>" +
                "<datafield tag=\"930\" ind1=\" \" ind2=\" \">" +
                "<subfield code=\"b\">" + exemplaireFromXml.rcr + "</subfield>" +
                "<subfield code=\"j\">g</subfield>" +
                "</datafield>" +
                "</record>";
    }

    public NoticeConcrete(Biblio biblio, DonneeLocale donneeLocale, List<Exemplaire> exemplaires) {
        this.noticeBiblio = biblio;
        this.noticeLocale = donneeLocale;
        this.exemplaires = exemplaires;
    }

    public static List<Exemplaire> listeExemplaireUnimarc(String listeExemplaire) throws Exception {

        List<Exemplaire> exemplaires = new ArrayList<>();

        final String regex = "\\x1f(e\\d{2}.+?)\\x1e";
        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(listeExemplaire);

        while (matcher.find()) {
            exemplaires.add(new Exemplaire(matcher.group(0)));
        }

        return exemplaires;
    }

    @Override
    public String getNumEx() {
        return (this.getExemplaires().size() == 0) ? null : String.format("%02d", this.getExemplaires().size());
    }

    @Override
    public TYPE_NOTICE getType() {
        return TYPE_NOTICE.CONCRETE;
    }

    @Override
    public String toString() {
        StringBuilder noticeToReturn = new StringBuilder();
        noticeToReturn.append(noticeBiblio.toString())
                .append("\r\n\r\n")
                .append(noticeLocale.toString())
                .append("\r\n\r\n");
        for (Exemplaire exemp : exemplaires) {
            noticeToReturn.append(exemp.toString()).append("\r\n");
        }
        return noticeToReturn.toString();
    }

    public List<ExemplaireFromXml> getListeExemplaireFromXml(String notices) {
        List<ExemplaireFromXml> listeExemp = new ArrayList<>();
        try {
            Matcher matcher;

            Document doc = DocumentHelper.parseText(notices);
            List<Node> listeZone = doc.selectNodes("//record/*");
            Pattern zoneExemplairePattern = Pattern.compile(Constants.ZONE_DEBUT_EXEMPLAIRE_XML_REGEX);

            for (int i = 0; i < listeZone.size(); i++) {
                Node zone = listeZone.get(i);
                matcher = zoneExemplairePattern.matcher(((Element) zone).attributeValue("tag"));
                if (matcher.find()) {
                    listeExemp.add(new ExemplaireFromXml(zone.selectNodes("*").get(0).getStringValue()));
                }
            }
        } catch (Exception e) {
            log.error("Impossible de construire les exemplaires, erreur XML");
        }
        return listeExemp;
    }
}
