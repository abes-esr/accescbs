package fr.abes.cbs.utilitaire;

import fr.abes.cbs.notices.TYPE_NOTICE;
import fr.abes.cbs.zones.enumZones.EnumZones;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class Utilitaire {
    /***
     * Constructeur privé permet de rendre la classe non instantiable
     */
    private Utilitaire(){}


    public static String setStandardZoneRegex(int nombreSousZones){
        StringBuilder regex = new StringBuilder("^(\\x1b[PD])?(?<zStaName>[^A]\\w{2,3})(?<zStaSpace>\\s*)(?<zStaHash>[\\d]{0,2}[#]*[\\d]{0,1})");
        for (int i=0; i<=nombreSousZones; i++){
            regex.append(" *((?<szk").append(i).append(">[$]\\w)(?<szv").append(i).append(">[^$]*))?");
        }

        return regex.toString();
    }

    /**
     * Méthode permettant de construire une regexp permettant de matcher uniquement les sous zones d'une zone
     * @param nombreSousZones nombre maximum de sous zones pouvant matcher
     * @return la regexp
     */
    public static String setListeSousZoneRegex(int nombreSousZones) {
        StringBuilder regex = new StringBuilder("^");
        for (int i=0; i<=nombreSousZones; i++){
            regex.append("((?<szk").append(i).append(">[$][a-z\\d])(?<szv").append(i).append(">[^$]*))? *");
        }

        return regex.substring(0, regex.length()-2);
    }

    /**
     * Méthode permettant de définir une regex correspondant à une liste de sous zones situées uniquement après n séquences dans une zone
     * @param sousZones sous zone à traiter
     * @return la regexp correspondante
     */
    public static String setEtatCollZoneRestantes(Enum[] sousZones) {
        StringBuilder regex = new StringBuilder();
        for (int i=0; i< sousZones.length;i++) {
            regex.append("((?<szk").append(i).append(">[$][").append(sousZones[i]).append("])(?<szv").append(i).append(">[^$]*))?");
        }
        regex.append("$");
        return regex.toString();
    }


	/**
	 * Récupère dans ligne entre tag et tagfin
	 * @param ligne String sur laquelle appliquer recupEntre
	 * @param tag tag de début
	 * @param tagfin tag de fin
	 * @return la string contenue entre tag et tagfin
	 */
	public static String recupEntre(final String ligne, final String tag, final String tagfin) {
        int posd = ligne.indexOf(tag);
        if (posd < 0) {
            return "";
        }
        if (tagfin.isEmpty()) {
            return ligne.substring(posd);
        }
        int posf = ligne.indexOf(tagfin, posd + 1);
        if (posf <= 0) {
            return "";
        }

        return ligne.substring(posd, posd + posf - posd).substring(tag.length());
    }

	/**
	 * retourne le nombre de résultats d'une commande CHE
	 * 
	 * @param resu
	 *            chaine de résulat de la commande CHE
	 * @return nombre de notices retourné par commande CHE
	 */
	public static Integer getNbNoticesFromChe(String resu) {
		// un seul résultat
		String nbResults = Utilitaire.recupEntre(resu, Constants.STR_1D + "VSZ", Constants.STR_1D);
		if (resu.indexOf("LPP") != 0
				&& "1".equals(nbResults)) {
			return 1;
		}
		// plusieurs résultats
		if (resu.contains(Constants.STR_1D + "VSZ")) {
			return Integer.parseInt(nbResults);
		}
		// pas de résultats
		return 0;
	}
    /**
     * Verifie que les données sont en UTF-8 et les convertit en ISO8859_1
     * @param str String a vérifier
     * @return String convertie
     */
    public static String cv(final String str) {
    	String resu = null;
        try {
            //pour verifier encodage
            CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
            boolean ok = encoder.canEncode(str);
            if (!ok) {
                log.info("ko:" + str);
            }
            byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
            resu = new String(utf8, "ISO8859_1");
        } catch (UnsupportedEncodingException e) {
            log.error("Error:" + e.getMessage(), e);
        }
        return resu;
    }

    /**
     * Convertit une notice native (pica) en XMLMARC
     * @param notice Notice pica
     * @return La notice en XML
     */
    public static String xmlFormat(final String notice) {
        String[] lstTags = notice.split(Constants.STR_0D);
        if (lstTags.length == 0) {
            return "<error>pas de tags</error>";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("<collection xmlns=\"http://www.loc.gov/MARC21/slim\"><record>");
        boolean iscontrol;

        for (String tag : lstTags) {
            tag=tag.replaceAll(Constants.DOUBLEDOLLAR, Constants.ONETWODOLLAR);
            if (tag.length() >= 4) {
                if ("   ".equals(tag.substring(1, 1 + 3))) {
                    ret.append("<leader>" + tag + "</leader>");
                } else if (" ".equals(tag.substring(3, 3 + 1))) {
                    if ("001".equals(tag.substring(0, 0 + 3))) {
                        ret.append("<controlfield  tag=\"" + tag.substring(0, 0 + 3) + "\" >" + tag.substring(4).replaceAll("$a", "").replaceAll("$b", "") + "</controlfield>");
                    } else {
                        if ("00".equals(tag.substring(0, 2))) {
                            iscontrol = true;
                            ret.append("<controlfield  tag=\"" + tag.substring(0, 3) + "\" >");
                        } else {
                            iscontrol = false;
                            //pour les zones sans indicateur on test si ind1=$
                            if (Constants.DOLLAR.equals(tag.substring(4, 4 + 1)) || "A".equals(tag.substring(0, 1))) {
                                ret.append(Constants.DATAFIELD + tag.substring(0, 3) + "\" >");
                            } else {
                                ret.append(Constants.DATAFIELD + tag.substring(0, 3) + Constants.IND1 + tag.substring(4, 4 + 1).replaceAll(Constants.DIEZ, "") + Constants.IND2 + tag.substring(5, 5 + 1).replaceAll(Constants.DIEZ, "") + "\">");
                            }
                        }
                        String[] lstdollar = tag.split("\\$");

                        if ((lstdollar.length >= 1) &&(!iscontrol)) {
                            int i = 0;
                            for (String dollar : lstdollar) {
                                if (i > 0) {
                                    ret.append("<subfield code=\"" + dollar.charAt(0) + "\">");
                                    ret.append(suppInutile(dollar.substring(1).replaceAll(Constants.ONETWODOLLAR,Constants.DOUBLEDOLLAR)));
                                    ret.append("</subfield>");
                                }
                                i++;
                            }
                        } else {
                            ret.append(tag.substring(4));
                        }
                        if (iscontrol) {
                            ret.append("</controlfield>");
                        } else {
                            ret.append("</datafield>");
                        }
                    }
                }
            }
        }
        ret.append("</record></collection>");
        return ret.toString().replaceAll(Constants.STR_1B, "#27#");
    }

    /**
     * Supprime des info inutiles
     * @param in String a nettoyer
     * @return La string nettoyée
     */
    private static String suppInutile(final String in) {
        String out;
        if (in.length() == 0) {
            return in;
        }
        if (in.substring(0, 1).equals(Constants.CR_152_S)) {
           out = in.replaceFirst(Constants.CR_152_S, "").replaceFirst(Constants.CR_156_S, "");
        } else {
            out = in;
        }

        return out.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("'", "&apos;").replace("\"", "&quot;");
    }

    /**
     * Retourne une liste de resultats courts en XML
     * @param resultatsTable Table des résultats
     * @return Résultats de la recherche au format XML
     */
    public static String getRecordSetAsXml(List<List<String>> resultatsTable) {
        StringBuilder result = new StringBuilder();
        Iterator<List<String>> lignes = resultatsTable.iterator();
        result.append("<RecordsList>");
        while (lignes.hasNext()) {
            result.append("<Record>");
            // extrait (une référence sur) l’élément suivant
            List<String> ligne = lignes.next();
            Iterator<String> colonnes = ligne.iterator();
            int i = 0;
            while (colonnes.hasNext()) {
                String colonne = colonnes.next();
                result.append("<Col").append(i).append(">");
                result.append(colonne);
                result.append("</Col").append(i).append(">");
                i++;
            }
            result.append("</Record>");
        }
        result.append("</RecordsList>");
        return result.toString();
    }

    /**
     * Comme au dessus mais depuis jusque
     * @param from Index du 1er résultat à récuperer
     * @param len Index du dernier résultat à récuperer
     * @return Résultats au format XML entre from et len
     */
    public static String getRecordSetAsXmlFromFor(int from, int len, List<List<String>> resultatsTable) {
        StringBuilder result = new StringBuilder();
        Iterator<List<String>> lignes = resultatsTable.iterator();
        int j = 0;
        int from2 = from;
        if (from2 == 1) {
            from2 = 0;
        }

        result.append("<RecordsList>");
        while (lignes.hasNext()) {
            List<String> ligne = lignes.next();
            if ((j >= from2) && (j < (from2 + len)) && (j < resultatsTable.size())) {
                result.append("<Record>");
                // extrait (une référence sur) l’élément suivant

                Iterator<String> colonnes = ligne.iterator();
                int i = 0;
                while (colonnes.hasNext()) {

                    String colonne = colonnes.next();
                    result.append("<Col").append(i).append(">");
                    result.append(colonne);
                    result.append("</Col").append(i).append(">");
                    i++;
                }
                result.append("</Record>");
                j++;
            }
        }
        result.append("</RecordsList>");
        return result.toString();
    }

    /**
     * Recup des tags marcs
     * @param notice Notice pica
     * @param tag Tag à récuperer
     * @param stag Sous tag
     * @return Valeur du tag/sous-tag
     */
    public static String getTag(String notice, String tag, String stag) {
        String trv = recupEntre(notice, tag + " ", Constants.STR_0D);
        String trv1 = recupEntre(trv, stag, Constants.DOLLAR);
        if (trv1.isEmpty()) {
            trv1 = recupEntre(trv + Constants.STR_0D, stag, Constants.STR_0D);
        }
        return trv1;
    }

    /**
     * Convertit une notice en format natif vers du format XML pour une notive en edit
     * parametre: notice en edit sous forme native
     * @param notice Notice pica en mode édition
     * @return La notice au format XML
     */
    public static String xmlFormatEdit(final String notice) {
        String[] lstTags = notice.split(Constants.STR_0D);
        if (lstTags.length == 0) {
            return "<error>pas de tags</error>";
        }
        StringBuilder ret = new StringBuilder();

        ret.append("<collection xmlns=\"http://www.loc.gov/MARC21/slim\"><record>");
        String tag;
        StringBuilder mrq; 
        boolean iscontrol;
        for (String tagin : lstTags) 
        {
            tag = tagin;
            tag=tag.replaceAll(Constants.DOUBLEDOLLAR, Constants.ONETWODOLLAR);
            mrq = new StringBuilder();
            if (tag.length() >= 4) {
                if (tag.substring(0, 1).equals(Constants.STR_1B)) {
                	mrq.append(tag.charAt(1));
                    tag = tag.replaceAll(Constants.STR_1B + mrq, "");
                }
                if (tag.substring(0, 1).equals(Constants.STR_1B)) {
                	mrq.append(tag.charAt(1));
                    tag = tag.replaceAll(Constants.STR_1B + tag.substring(1, 1 + 1), "");
                }

                if (("   ").equals(tag.substring(1, 1 + 3))) {
                    ret.append("<leader>" + tag + "</leader>");
                } else if ((" ").equals(tag.substring(3, 3 + 1))) {

                    //pour les autorites on ne met rien en contole fiel car il y a des dollars dans las zones 00
                    iscontrol = false;

                    if (!("").equals(mrq.toString())) {//cbd 18122014 tag.indexOf("$")<0 
                        if ((("00").equals(tag.substring(0, 2))) || tag.indexOf('$')<0 || (("$").equals(tag.substring(4, 4 + 1))) || (("A").equals(tag.substring(0, 1)))) {
                            ret.append("<datafield tag=\"" + tag.substring(0, 3) + "\" act=\"" + mrq + "\" >");
                        } else {
                            ret.append("<datafield tag=\"" + tag.substring(0, 3) + "\" ind1=\"" + tag.charAt(4) + "\" ind2=\"" + tag.charAt(5) + "\" act=\"" + mrq + "\" >");
                        }
                    } else {
                        if ((("00").equals(tag.substring(0, 2)))  || !tag.contains("$") || (("$").equals(tag.substring(4, 4 + 1))) || (("A").equals(tag.substring(0, 1))) || (("E").equals(tag.substring(0, 1)))) {
                            ret.append("<datafield  tag=\"" + tag.substring(0, 3) + "\" >");
                        } else {
                            ret.append("<datafield  tag=\"" + tag.substring(0, 3) + "\" ind1=\"" + tag.charAt(4) + "\" ind2=\"" + tag.charAt(5) + "\">");
                        }
                    }
                    
                    String[] lstdollar = tag.split("\\$");
                    int i = 0;
                    for (String dollar : lstdollar) //foreach (String dollar in lstdollar)
                    {

                        if (i > 0) {
                            ret.append("<subfield code=\"" + dollar.charAt(0) + "\">");
                            ret.append(dollar.substring(1).replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("'", "&apos;").replace("\"", "&quot;").replaceAll("1dollar2","\\$\\$"));
                            ret.append("</subfield>");
                        }
                        i++;
                    }

                    if (iscontrol) {
                        if (lstdollar.length == 1) {
                            ret.append(tag.substring(4));
                        }
                        ret.append("</controlfield>");
                    } else {
                        if (lstdollar.length == 1) {
                            ret.append(tag.substring(4));
                        }
                        ret.append("</datafield>");
                    }


                }

            }

        }
        ret.append("</record></collection>");
        return ret.toString().replaceAll(Constants.STR_1B, "#27#");

    }

    /**
     * Pour la présentation padd les infos..
     * @param inn String à raccourcir
     * @param lg Taille souhaitée
     * @return La string inn raccourcie à la longueur lg
     */
    public static String format(String inn, int lg) {
        String ret = inn;
        if (inn.length() > lg) {
            ret = inn.substring(0, lg - 3) + "...";
        } else if (inn.length() < lg) {
            ret = inn;
        }

        return ret;
    }

    /**
     * Convertit une notice en edit du format XML vers du natif
     * @param noticeXml Notice au format XML
     * @return La notice en pica
     */
    public static String xml2MarcEdit(final String noticeXml) {
        try {
            DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
            // création d'un constructeur de documents
            DocumentBuilder constructeur = fabrique.newDocumentBuilder();
            // lecture du contenu d'un fichier XML avec DOM
            StringReader reader = new StringReader(noticeXml);
            InputSource inputSource = new InputSource(reader);
            Document doc = constructeur.parse(inputSource);
            reader.close();
            XPathFactory xfabrique = XPathFactory.newInstance();
            XPath xpath = xfabrique.newXPath();
            StringBuilder notice = new StringBuilder();
            XPathExpression exp = xpath.compile("//record/*");
            org.w3c.dom.NodeList lstnodes = (org.w3c.dom.NodeList) exp.evaluate(doc, XPathConstants.NODESET);
            String ttmp = "";
            for (int i = 0; i < lstnodes.getLength(); i++) {
                Node tag = lstnodes.item(i);
                if ("leader".equals(tag.getNodeName()) || "header".equals(tag.getNodeName())) {
                    if (tag.getNodeValue().length() >= 10) {
                        ttmp = tag.getNodeValue().substring(9, 9 + 1);
                    }
                    switch(ttmp){
	                    case "b": ttmp = "Tb";
	                    	break;
				        case "f": ttmp = "Tu";
				          	break;
				        case "h": ttmp = "Tq";
						  	break;
				        case "j": ttmp = "Td";
							break;
				        case "c": ttmp = "Tg";
					        break;
				        case "e": ttmp = "Ta";
				            break;
				        case "d": ttmp = "Tm";
				            break;
				        default:  ttmp = "Tp";
                        	break;
                    }
                    notice.append("008" + " " + "$a" + ttmp + "6" + Constants.STR_0D); //6 pour cree, 5 validation standard
                }
                String actStr = "";
                if ("controlfield".equals(tag.getNodeName())) {
                    Node tagid = tag.getAttributes().getNamedItem("tag");
                    Node act = tag.getAttributes().getNamedItem("act");

                    if (act != null) {
                        if (act.getNodeValue().length() == 1) {
                            actStr = Constants.STR_1B + act.getNodeValue();
                        } else if (act.getNodeValue().length() == 2) {
                            actStr = Constants.STR_1B + act.getNodeValue().substring(0, 1);
                            actStr += Constants.STR_1B + act.getNodeValue().substring(1, 1 + 1);
                        }
                    }
                    Node ind1id = tag.getAttributes().getNamedItem("ind1");
                    Node ind2id = tag.getAttributes().getNamedItem("ind2");

                    String ind1 = "";
                    String ind2 = "";
                    if (ind1id != null) {
                        ind1 = ind1id.getNodeValue();
                    }
                    if (ind2id != null) {
                        ind2 = ind2id.getNodeValue();
                    }
                    notice.append(actStr + tagid.getNodeValue() + " " + ind1 + ind2);
                    org.w3c.dom.NodeList lstdollar = tag.getChildNodes();

                    boolean estpasse = false;
                    for (int k = 0; k < lstdollar.getLength(); k++)
                    {
                        if (Constants.SUBFIELD.equals(lstdollar.item(k).getNodeName())) {
                            estpasse = true;
                            Node codeid = lstdollar.item(k).getAttributes().getNamedItem("code");
                            if (lstdollar.item(k).getFirstChild()!=null){
                                notice.append(Constants.DOLLAR + codeid.getNodeValue() + lstdollar.item(k).getFirstChild().getNodeValue().replaceAll("#27#", Constants.STR_1B));
                            } else {
                                 notice.append(Constants.DOLLAR + codeid.getNodeValue());
                            }
                        }

                    }
                    if (!estpasse && tag.getFirstChild()!=null){
                            notice.append(tag.getFirstChild().getNodeValue().replaceAll("#27#", Constants.STR_1B));
                    }
                    notice.append(Constants.STR_0D);

                } else if ("datafield".equals(tag.getNodeName())) {
                    Node tagid = tag.getAttributes().getNamedItem("tag");
                    Node act = tag.getAttributes().getNamedItem("act");
                    Node ind1id = tag.getAttributes().getNamedItem("ind1");
                    Node ind2id = tag.getAttributes().getNamedItem("ind2");
                    if (act != null) {
                        if (act.getNodeValue().length() == 1) {
                            actStr = Constants.STR_1B + act.getNodeValue();
                        } else if (act.getNodeValue().length() == 2) {
                            actStr = Constants.STR_1B + act.getNodeValue().charAt(0);
                            actStr += Constants.STR_1B + act.getNodeValue().charAt(1);
                        }
                    }
                    String ind1 = "";
                    String ind2 = "";
                    if (ind1id != null) {
                        ind1 = ind1id.getNodeValue();
                    }
                    if (ind2id != null) {
                        ind2 = ind2id.getNodeValue();
                    }
                    //pour la zone de d exemplaire exx on supprime les indicateurs
                    if ("e".equalsIgnoreCase(tagid.getNodeValue().substring(0, 1))  || "A".equals(tagid.getNodeValue().substring(0, 1))) {
                        ind1 = "";
                        ind2 = "";
                    }
                    if ("E".equals(tagid.getNodeValue().substring(0, 1))) {
                        notice.append(Constants.STR_1F + Constants.STR_1E + "VTXT0T" + tagid.getNodeValue() + tag.getNodeValue());
                    } else {
                        notice.append(actStr + tagid.getNodeValue() + " " + ind1 + ind2);
                        org.w3c.dom.NodeList lstdollar = tag.getChildNodes();
                        boolean estpasse = false;
                        for (int k = 0; k < lstdollar.getLength(); k++)
                        {
                            org.w3c.dom.Node lenode = lstdollar.item(k);
                            if (Constants.SUBFIELD.equals(lenode.getNodeName())) {
                                estpasse = true;
                                 org.w3c.dom.Node codeid =  lenode.getAttributes().getNamedItem("code");
                                  if ((codeid != null) && (lenode.getFirstChild() != null)) {
                                    notice.append(Constants.DOLLAR + codeid.getNodeValue() + lenode.getFirstChild().getNodeValue().replaceAll("#27#", Constants.STR_1B));
                                }
                            }
                        }

                        if (!estpasse && tag.getFirstChild()!=null){
                                notice.append(tag.getFirstChild().getNodeValue().replaceAll("#27#", Constants.STR_1B));
                        }
                    }
                    notice.append(Constants.STR_0D);
                }
            }
            return notice.toString();

        } catch (Exception ex) {
        	log.error("",ex);
            return null;
        }
    }


    /**
     * Convertit une notice qui n'est pas en edit de XML vers natif
     * @param noticeXml Notice au format XML
     * @return La notice en pica
     */
    public static String xml2Marc(final String noticeXml) {
         try {
            DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
            DocumentBuilder constructeur = fabrique.newDocumentBuilder();
            // lecture du contenu d'un fichier XML avec DOM
            StringReader reader = new StringReader(noticeXml);
            InputSource inputSource = new InputSource(reader);
            Document doc = constructeur.parse(inputSource);
            reader.close();
            XPathFactory xfabrique = XPathFactory.newInstance();
            XPath xpath = xfabrique.newXPath();
            XPathExpression exp = xpath.compile("//record/*");
            StringBuilder notice = new StringBuilder();
            org.w3c.dom.NodeList lstnodes = (org.w3c.dom.NodeList) exp.evaluate(doc, XPathConstants.NODESET);
            String ttmp = "";
            for (int i = 0; i < lstnodes.getLength(); i++) {
                Node tag = lstnodes.item(i);
                if ("leader".equals(tag.getNodeName()) || "header".equals(tag.getNodeName())) {
                    if (tag.getNodeValue().length() >= 10) {
                        ttmp = tag.getNodeValue().substring(9, 9 + 1);
                    }
                    switch(ttmp){
	                    case "b": ttmp = "Tb";
	                              break;
	                    case "f": ttmp = "Tu";
	                    		  break;
	                    case "h": ttmp = "Tq";
	          		  			  break;
	                    case "j": ttmp = "Td";
			  			          break;
	                    case "c": ttmp = "Tg";
				                  break;
	                    case "e": ttmp = "Ta";
		                          break;
	                    case "d": ttmp = "Tm";
	                              break;
	                    default:  ttmp = "Tp";
	                              break;
                    }
                    //6 pour cree, 5 validation standard
                    notice.append("008").append(" ").append("$a").append(ttmp).append("6").append(Constants.STR_0D);
                }
                String actStr = "";
                if ("controlfield".equals(tag.getNodeName())) {
                    Node tagid = tag.getAttributes().getNamedItem("tag");
                    Node act = tag.getAttributes().getNamedItem("act");

                    if (act != null) {
                        if (act.getNodeValue().length() == 1) {
                            actStr = Constants.STR_1B + act.getNodeValue();
                        } else if (act.getNodeValue().length() == 2) {
                            actStr = Constants.STR_1B + act.getNodeValue().charAt(0);
                            actStr += Constants.STR_1B + act.getNodeValue().charAt(1);
                        }
                    }
                    if ("001".equals(tagid.getNodeValue()) || "008".equals(tagid.getNodeValue())) {
                        if (!actStr.isEmpty()) {
                            notice.append(actStr).append(tagid.getNodeValue()).append(" ").append(tag.getFirstChild().getNodeValue()).append(Constants.STR_0D);
                        } else {
                            notice.append(tagid.getNodeValue()).append(" ").append(tag.getFirstChild().getNodeValue()).append(Constants.STR_0D);
                        }
                    }
                }

                if ("datafield".equals(tag.getNodeName()))
                {

                    Node tagid = tag.getAttributes().getNamedItem("tag");
                    Node act = tag.getAttributes().getNamedItem("act");
                    Node ind1id = tag.getAttributes().getNamedItem("ind1");
                    Node ind2id = tag.getAttributes().getNamedItem("ind2");
                    if (act != null) {
                        if (act.getNodeValue().length() == 1) {
                            actStr = Constants.STR_1B + act.getNodeValue();
                        } else if (act.getNodeValue().length() == 2) {
                            actStr = Constants.STR_1B + act.getNodeValue().substring(0, 1);
                            actStr += Constants.STR_1B + act.getNodeValue().substring(1, 1 + 1);
                        }
                    }
                    String ind1 = "";
                    String ind2 = "";
                    if (ind1id != null) {
                        ind1 = ind1id.getNodeValue();
                    }
                    if (ind2id != null) {
                        ind2 = ind2id.getNodeValue();
                    }
                    if (ind1.isEmpty() || " ".equals(ind1)) {
                        ind1 = Constants.DIEZ;
                    }
                    if (ind2.isEmpty() || " ".equals(ind2)) {
                        ind2 = Constants.DIEZ;
                    }

                    //pour la zone de d exemplaire exx on supprime les indicateurs
                    if ("e".equalsIgnoreCase(tagid.getNodeValue().substring(0, 1)) || "A".equals(tagid.getNodeValue().substring(0, 1))) {
                        ind1 = "";
                        ind2 = "";
                    }
                    notice.append(actStr).append(tagid.getNodeValue()).append(" ").append(ind1).append(ind2);
                    org.w3c.dom.NodeList lstdollar = tag.getChildNodes();

                    //ici
                    boolean estpasse = false;
                    for (int k = 0; k < lstdollar.getLength(); k++)
                    {
                        if (Constants.SUBFIELD.equals(lstdollar.item(k).getNodeName())) {
                            estpasse = true;
                            Node codeid = lstdollar.item(k).getAttributes().getNamedItem("code");
                            if (lstdollar.item(k).getFirstChild()!=null){
                                notice.append(Constants.DOLLAR).append(codeid.getNodeValue()).append(lstdollar.item(k).getFirstChild().getNodeValue().replaceAll("#27#", Constants.STR_1B));
                            }
                        }
                    }

                    if (!estpasse && tag.getFirstChild()!=null){
                        notice.append(tag.getFirstChild().getNodeValue().replaceAll("#27#", Constants.STR_1B));
                    }
                    notice.append(Constants.STR_0D);
                }
            }
            return notice.toString();
        } catch (Exception ex) {
        	log.error("Error converting Xml to Marc ",ex);
        	return null;
        }
    }

    /**     * Récupère une zone / sous zone dans une notice
     * en cas de zone ou de sous zone répétable, ne renvoie que la première trouvée
     * @param notice : chaine de la notice dans laquelle chercher
     * @param tag : zone à chercher
     * @param subTag : sous zone à chercher (avec le $)
     * @return chaine correspondant à la sous zone, null si pas de sous zone trouvée
     */
    public static String getZone(String notice, String tag, String subTag) {
        String ligne = recupEntre(recupNoticeBib(notice), tag, Constants.STR_0D);
        //si pas de sous zone en paramètre, retour zone entière
        if (subTag.isEmpty()){
            return ligne.substring(1);
        }
        //si sous zone seule
        if (ligne.indexOf(subTag) == ligne.lastIndexOf('$')) {
            return ligne.substring(ligne.indexOf(subTag)+subTag.length());
        }
        //si sous zone répétée, on retourne uniquement la première instance
        if (ligne.indexOf(subTag) != ligne.lastIndexOf(subTag)) {
            ligne = ligne.substring(ligne.indexOf(subTag)+subTag.length());
            return ligne.substring(0, ligne.indexOf('$'));
        }
        //récupération portion de la ligne à partir de la sous zone recherchée
        ligne = ligne.substring(ligne.indexOf(subTag)+subTag.length());
        //retourne ligne jusqu'au prochain $
        return ligne.substring(0, ligne.indexOf('$'));
    }

    /**
     * Récupère une zone / sous zone dans une notice contenant une chaine passée en paramètre
     * en cas de zone / sous zone répétable, parcours toutes les répétitions, et retourne la première qui contient la valeur
     * si pas de sous zone en paramètre, on cherche dans toutes les sous zones de la zone
     * @param notice : chaine de la notice dans laquelle chercher
     * @param tag : zone à chercher
     * @param subTag : sous zone à chercher (avec le $)
     * @param value : chaine à rechercher dans la sous zone
     * @return true si la chaine est trouvée, false sinon
     */
    public static boolean getZoneWithValue(String notice, String tag, String subTag, String value) {
         return getZone(notice, tag, subTag).contains(value);
    }

    /**
     * @param notice Notice pica
     * @return la notice sans les informations autour
     */
    public static String recupNoticeBib(final String notice) {
       //si notice sans exemplaire, chaine de fin différente
        if (notice.contains(Constants.STR_1E + Constants.VMC)){
            return recupEntre(notice, Constants.VTXTBIB, Constants.STR_1E + Constants.VMC);
        } else {
            return recupEntre(notice, Constants.VTXTBIB, Constants.STR_1E + Constants.VTXTE);
        }
    }

    /**
     * Ajoute une zone à une notice bibliographique
     * @param notice notice biblio à modifier (résultat de la commande mod)
     * @param tag intitulé de la zone à ajouter
     * @param subTag intitulé de la sous zone à ajouter (avec le $)
     * @param valeur valeur de la zone/sous zone à insérer
     * @return notice modifiée, prête à être validée
     */
    public static String ajoutZoneBiblio(final String notice, final String tag, final String subTag, final String valeur) {
        return (new StringBuilder().append(recupEntre(notice, Constants.STR_1F, Constants.STR_0D + Constants.STR_0D + Constants.STR_1E))
        		.append(Constants.SEP_CHAMP).append(tag).append(" ").append(subTag)
        		.append(valeur).append(Constants.SEP_CHAMP)).toString();
    }

    /**
     * Ajout une sous zone dans une notice d'exemplaire
     * @param exemp : notice d'exemplaire, préfixée de STR_1F et suffixée de STR_1E
     * @param tag : zone concernée par l'ajout
     * @param subTag : sous zone à ajouter
     * @param valeur : valeur de la sous-zone
     * @return : l'exemplaire modifié
     */
    public static String ajoutSousZoneExemp(final String exemp, final String tag, final String subTag, final String valeur) {
        List<String> zonesExemp = cutExemp(exemp);
        Iterator itZones = zonesExemp.iterator();
        while (itZones.hasNext()) {
            String zone = (String)itZones.next();
            if (getLabelZone(zone).equals(tag)) {
                List<String> sousZones = cutZone(zone);

            }
        }
        return "";
    }

    /**
     * Découpe un exemplaire en une liste chainée de ses zones
     * @param exemp : exemplaire à découper
     * @return : liste chainée des zones de l'exemplaire
     */
    private static List<String> cutExemp(String exemp) {
        //on supprime le premier et le dernier caractère (STR_1F et STR_1E) pour traitement
        String newExemp = exemp.substring(1 , exemp.length() - 1);
        List<String> zonesExemp = new LinkedList<>();
        //on découpe l'exemplaire en plusieurs zones
        String[] tabLignesExemp = newExemp.split(Constants.STR_0D);
        //on constitue une liste chainée des zones de l'exemplaire
        for (int i=0;i<tabLignesExemp.length;i++) {
            if (tabLignesExemp[i].length() >=3 ) {
                zonesExemp.add(tabLignesExemp[i]);
            }
        }
        return zonesExemp;
    }

    /**
     * découpe une zone en une liste chainée contenant ses sous zones
     * @param zone : zone à découper
     * @return : liste chainée des sous zones composant la zone
     */
    private static List<String> cutZone(String zone) {
        List<String> sousZones = new LinkedList<>();
        String[] tabSousZones = zone.split("$");
        for (int i=0;i<tabSousZones.length;i++) {
            sousZones.add(tabSousZones[i]);
        }
        return sousZones;
    }

    /**
     * méthode de suppresion d'une zone / sous zone d'une notice d'exemplaire
     * @param exemp : exemplaire à modifier, il doit être borné par STR_1F et STR_1E
     * @param tag : zone à supprimer
     * @return : chaine modifiée de l'exemplaire amputé de la zone / sous zone,  il sera borné par STR_1F et STR_1E
     */
    public static String suppZoneExemp(final String exemp, final String tag) {
        List<String>zonesExemp = cutExemp(exemp);
        StringBuilder exempModifie = new StringBuilder();
        Iterator values = zonesExemp.listIterator();
        while (values.hasNext()) {
            String zone = (String)values.next();
            if (!getLabelZone(zone).equals(tag)) {
                exempModifie.append(zone).append(Constants.STR_0D);
            }
        }
        //on remet le premier caractère
        exempModifie.insert(0, Constants.STR_1F);
        return exempModifie.toString();

    }

    /**
     * Modifie une zone / sous zone à une notice bibliographique. on part du principe que la zone / sous zone existe.
     * la méthode ne fait rien si ce n'est pas le cas
     * @param notice : notice biblio à modifier (résultat de la commande mod)
     * @param tag : intitulé de la zone à modifier
     * @param subTag : intitulé de la sous zone à modifier (avec le $)
     * @param valeur : nouvelle valeur de la zone / sous zone à modifier
     * @return  notice modifiée, prête à être validée
     */
    public static String modifZoneBiblio(final String notice, final String tag, final String subTag, final String valeur) {
        //on stocke la notice dans un tableau où chaque indice contient 1 zone
        String[] resuEdit = recupEntre(notice, Constants.STR_1F, Constants.STR_0D + Constants.STR_0D + Constants.STR_1E).split(Constants.STR_0D);
        for (int i = 1; i < resuEdit.length; i++) {
            if (resuEdit[i].length() > 0 && resuEdit[i].substring(0,3).equals(tag)) {
            	//zone trouvée
                String[] xx = resuEdit[i].split("\\$");
                for (int j = 1; j < xx.length; j++) {
                 //sous zone trouvée
                 if (xx[j].substring(0, 1).equals(subTag.substring(1,2))) {
                     xx[j]=subTag.substring(1,2)+valeur;
                     break;
                 }
                }
                StringBuilder zoneamodif = new StringBuilder();
                for (String xx1 : xx) {
                    zoneamodif.append(xx1).append(Constants.DOLLAR);
                }
                resuEdit[i]=zoneamodif.substring(0,zoneamodif.length()-1);
            }
        }
        StringBuilder newNotice = new StringBuilder();
        for (String resuEdit1 : resuEdit) {
            newNotice.append(resuEdit1).append(Constants.STR_0D);
        }
        return newNotice.toString();
    }


    /**
     * Méthode de suppression d'une zone / sous zone dans une notice biblio
     * @param notice : notice à modifier
     * @param tag : zone à supprimer : si zone répétée, toutes les instances sont concernées
     * @param subTag : sous zone à supprimer : si sous zone vide, on supprime toute la zone !
     * @return : chaine de la notice modifiée, prête à être validée
     */
    public static String suppZoneBiblio(final String notice, final String tag, final String subTag) {
        //on stocke la notice dans un tableau où chaque indice contient 1 zone
        String[] resuEdit = recupEntre(notice, Constants.STR_1F, Constants.STR_0D + Constants.STR_0D + Constants.STR_1E).split(Constants.STR_0D);
        for (int i = 0; i < resuEdit.length; i++) {
        	//zone trouvée
            if (resuEdit[i].substring(0,3).equals(tag)) {
                if (subTag.isEmpty()) {
                    resuEdit[i] = "";
                } else {
                    String[] xx = resuEdit[i].split("\\$");
                    String[] newTabSousZone = new String[xx.length];
                    int cpt = 0;
                    for (String xx1 : xx) {
                        if (xx1.substring(0, 1).equals(subTag.substring(1,2))) {
                            //sous zone trouvée
                            //on ne fait rien
                            continue;
                        }
                        newTabSousZone[cpt] = xx1;
                        cpt++;
                    }
                    //si une seule sous zone dans la zone, on supprime la zone entière
                    if (newTabSousZone.length <= 2) {
                        resuEdit[i] = "";
                    }
                    else {
                    	StringBuilder zoneamodif = new StringBuilder();
                        for (String newTabSousZone1 : newTabSousZone) {
                            if (newTabSousZone1 != null)
                                zoneamodif.append(newTabSousZone1).append(Constants.DOLLAR);
                        }
                        resuEdit[i]=zoneamodif.substring(0,zoneamodif.length()-1);
                    }
                }
            }
        }
        StringBuilder newNotice = new StringBuilder();
        for (String resuEdit1 : resuEdit) {
            if (!resuEdit1.isEmpty()) {
                newNotice.append(resuEdit1).append(Constants.STR_0D);
            }
        }
        return newNotice.toString();
    }

    /**
     * Supprime une zone d'une notice biblio dont la sous zone subtag contient la chaine pattern
     * @param notice notice sur laquelle supprimer la zone
     * @param tag zone à supprimer
     * @param subTag sous zone dans laquelle chercher la chaine validant la suppression
     * @param pattern chaine à rechercher dans la sous zone pour valider la suppression
     * @return notice modifiée
     */
    public static String suppZoneBiblioWithPattern(final String notice, final String tag, final String subTag, String pattern) {
        //on stocke la notice dans un tableau où chaque indice contient 1 zone
        String[] resuEdit = recupEntre(notice, Constants.STR_1F, Constants.STR_0D + Constants.STR_0D + Constants.STR_1E).split(Constants.STR_0D);
        for (int i = 0; i < resuEdit.length; i++) {
        	//zone trouvée
            if (resuEdit[i].substring(0,3).equals(tag)) {
                if (subTag.isEmpty()) {
                    resuEdit[i] = "";
                } else {
                    Boolean zoneASupp = false;
                    String[] xx = resuEdit[i].split("(?<![$])[$](?![$])");
                    String[] newTabSousZone = new String[xx.length];
                    int cpt = 0;
                    for (String xx1 : xx) {
                        if (xx1.substring(0, 1).equals(subTag.substring(1,2))) {
                            //sous zone trouvée
                            if (xx1.contains(pattern)) {
                                // pattern trouvé
                                //on indique qu'il s'agit de la zone à supprimer
                                zoneASupp = true;
                                continue;
                            }
                        }
                        newTabSousZone[cpt] = xx1;
                        cpt++;
                    }
                    StringBuilder zoneamodif = new StringBuilder();
                    for (String newTabSousZone1 : newTabSousZone) {
                        zoneamodif.append(newTabSousZone1).append(Constants.DOLLAR);
                    }
                    resuEdit[i] = zoneASupp ? "" : zoneamodif.substring(0,zoneamodif.length()-1);
                }
            }
        }
        StringBuilder newNotice = new StringBuilder();
        for (String resuEdit1 : resuEdit) {
            if (!resuEdit1.isEmpty()) {
                newNotice.append(resuEdit1).append(Constants.STR_0D);
            }
        }
        return newNotice.toString();
    }

    /**
     * Renvoie le numéro du prochain exemplaire à créer d'une notice passée en paramêtre
     * <p>Va renseigner NbExPpnEncours et NvNumEx</p>
     * @param notice notice dont on veut connaître le numéro du prochain exemplaire
     * @return NvNumEx le numéro du prochain exemplaire sous la forme e<i>XX</i>
     */
    public static String numExemplaire(final String notice) {
        //num du prochain exemplaire
        StringBuilder nvNumEx = new StringBuilder().append("e");
        int posLastEx;
        if (notice.contains("<BR>e01")) {
            //la notice a déjà un exemplaire
            posLastEx = notice.lastIndexOf("<BR>e");
            //num de l'exemplaire en cours
            int nbExPPnEncours = Integer.parseInt(notice.substring(posLastEx + 5, posLastEx + 7)) +1;
            if (String.valueOf(nbExPPnEncours).length() == 1) {
                nvNumEx.append("0").append(nbExPPnEncours);
            } else {
                nvNumEx.append(nbExPPnEncours);
            }
        } else {
            //la notice n'a pas encore d'exemplaire
            nvNumEx.append("01");
        }
        return nvNumEx.toString();
    }

    /**
     * Renvoie le numéro de l'exemplaire correspondant à l'EPN passé en paramètre
     * Retourne une chaine de la forme exx (xx = numéro d'exemplaire)
     * @param notice notice à parcourir
     * @param epn numéro d'epn à trouver dans la notice pour trouver le numéro d'exemplaire
     * @return : numEx : numéro d'exemplaire correspondant
     *
     */
    public static String epnToExemplaire(String notice, String epn) {
        // on détermine la position de l'EPN passé en paramètre dans la notice
        int posEpn = notice.indexOf(epn);
        int indexExemp = 0;
        Pattern pattern = Pattern.compile("<(B|b)(R|r)>e([0-9]{1}[0-9]{1})(\\s)\\$a");
        Matcher matcher = pattern.matcher(notice);
        // on cherche dans le résultat, la dernière occurence trouvée avant l'epn
        while (matcher.find()) {
            if (matcher.start() < posEpn) {
                indexExemp = matcher.start();
            }
            else break;
        }
        return "e"+notice.substring(indexExemp+5, indexExemp+7);
    }
    
    /**
   * Renvoi le texte du message retour du CBS suite à l'envoi d'une commande
   * @param resCommande le résultat de l'exécution d'une commande avec tous les codes
   * @return le texte du message renvoyé par le CBS suite à l'exécution d'une commande
   */
  public static String messageCommande(final String resCommande) {
      return resCommande.substring(2, resCommande.indexOf(Constants.STR_1D + "V"));
  }

    /**
     * renvoie le label d'une zone passée en paramètre (en prenant en compte les zones XXX, EXXX, LXXX, exx)
     * @param zone zone à analyser
     * @return label de la zone
     */
  public static String getLabelZone(final String zone) {
      if (zone.substring(0,3).matches("\\d{3}")) {
          return zone.substring(0,3);
      }
      if (zone.substring(0,3).matches("e\\d{2}")){
          return zone.substring(0,3);
      }
      if (zone.substring(0,4).matches("E\\d{3}")){
          return zone.substring(0,4);
      }
      if (zone.substring(0,4).matches("L\\d{3}")){
          return zone.substring(0,4);
      }
      return "";
  }

    public static Boolean isZoneProtegee(String zone) {
        Pattern pattern1 = Pattern.compile("\\x1bP\\p{Digit}\\p{Digit}\\p{Digit}");
        Pattern pattern2 = Pattern.compile("\\x1bD\\x1bP\\p{Digit}\\p{Digit}\\p{Digit}");
        Matcher matcher1 = pattern1.matcher(zone);
        Matcher matcher2 = pattern2.matcher(zone);
        return matcher1.find() || matcher2.find();
    }

    /**
     * Déterminer si un paramètre passé en paramètre est non vide et non null
     * @param parameter
     * @return
     */
    public static boolean isCorectParameter(String parameter){
        if (parameter == null || parameter.isEmpty()){
            return false;
        }else {
            return true;
        }
    }

    public static String getStringFromZone(String zone, TYPE_NOTICE type) {
        List<EnumZones> listeZones = EnumUtils.getEnumList(EnumZones.class);
        Stream<EnumZones> zonesProteges = listeZones.stream().filter(p -> p.toString().startsWith("P") && p.toString().contains(zone));

        if (zonesProteges.toArray().length != 0){
            return "P" + zone;
        }
        if (zone.matches("9\\d\\d")) {
            return "Z" + zone;
        }
        if (zone.matches("\\d\\d\\w")) {
            if (type.equals(TYPE_NOTICE.BIBLIOGRAPHIQUE)) {
                return "B" + zone;
            }
            else {
                return "A" + zone;
            }
        } else {
            return zone;
        }
    }

    public static String deleteExpensionFromValue(String value) {
        if (value.indexOf(Constants.STR_1B+"I@") != -1) {
            return value.substring(0, value.indexOf(Constants.STR_1B+"I@"));
        }
        return value;
    }
}
