package fr.abes.cbs.notices;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.abes.cbs.utilitaire.Constants;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

public class BiblioTest {
    @DisplayName("Test création notice biblio à partir d'une chaine")
    @Test
    void biblioFromStringBasique() throws Exception {
        String biblioStr = Constants.STR_1F +
                "000 $01\r" +
                "002 $aFRBNF455216030000003$2FRBNF\r" +
                "003 http://www.sudoc.fr/230362532\r" +
                "004 4994:25-09-18\r" +
                "005 1999:25-09-18 11:25:00.000\r" +
                "006 4994:25-09-18\r" +
                "008 $aAax3\r" +
                "010 ##$A978-2-7470-9101-5$bbr$d2,50 EUR\r" +
                "021 ##$aFR$bDLE-20180608-38992\r" +
                "033 ##$ahttp://catalogue.bnf.fr/ark:/12148/cb45521603c$2BNF$d20180904\r" +
                "035 ##$aFRBNF455216030000003$zFRBNF45521603$d20180911\r" +
                "073 #0$a9782747091015\r" +
                "100 0#$a2018\r" +
                "101 0#$afre\r" +
                "102 ##$aFR\r" +
                "104 ##$aa$by$cy$dba$e0$ffre\r" +
                "105 ##$a|$a|$a|$a|$bz$c0$d0$e|$fa$g|\r" +
                "106 ##$ar\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cn\r" +
                "200 1#$a@Petit Ours brun cuisine avec papa$fMarie Aubinais$g[illustrations originales], Danièle Bour\r" +
                "214 #3$aCastres$bla bas$clui$atoulouse$cmoi$dC 2009\r" +
                "215 ##$a1 vol. (non paginé [15] p.)$cill. en coul.$d15 cm\r" +
                "225 1#$a@Petit Ours brun\r" +
                "330 ##$aPetit Ours Brun aide son papa à faire le diner. Préparer les tomates, casser les oeufs... Petit Ours Brun est un vrai petit cuistot !$2éditeur\r" +
                "461 ##$t@Petit Ours brun\r" +
                "686 ##$a809$2Cadre de classement de la Bibliographie nationale française\r" +
                "700 #1$3086100106Aubinais, Marie$4070\r" +
                "702 #1$3026744813Bour, Danièle (1939-....)$4440\r" +
                "801 #0$aFR$bFR-751131015$c20180608$gAFNOR$2intermrc\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.getListeZones().size()).isEqualTo(30);
        assertThat(biblio.findZones("200").get(0).getSubLabelList().size()).isEqualTo(3);
        biblio.getListeZones().values();
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                Constants.STR_1B + "P000 $01\r" +
                Constants.STR_1B + "D002 $aFRBNF455216030000003$2FRBNF\r" +
                "003 http://www.sudoc.fr/230362532\r" +
                "004 4994:25-09-18\r" +
                "005 1999:25-09-18 11:25:00.000\r" +
                "006 4994:25-09-18\r" +
                "008 $aAax3\r" +
                "010 ##$A978-2-7470-9101-5$bbr$d2,50 EUR\r" +
                "021 ##$aFR$bDLE-20180608-38992\r" +
                "033 ##$ahttp://catalogue.bnf.fr/ark:/12148/cb45521603c$2BNF$d20180904\r" +
                "035 ##$aFRBNF455216030000003$zFRBNF45521603$d20180911\r" +
                "073 #0$a9782747091015\r" +
                "100 0#$a2018\r" +
                "101 0#$afre\r" +
                "102 ##$aFR\r" +
                "104 ##$aa$by$cy$dba$e0$ffre\r" +
                "105 ##$a|$a|$a|$a|$bz$c0$d0$e|$fa$g|\r" +
                "106 ##$ar\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cn\r" +
                "200 1#$a@Petit Ours brun cuisine avec papa$fMarie Aubinais$g[illustrations originales], Danièle Bour\r" +
                "214 #3$aCastres$bla bas$clui$atoulouse$cmoi$dC 2009\r" +
                "215 ##$a1 vol. (non paginé [15] p.)$cill. en coul.$d15 cm\r" +
                "225 1#$a@Petit Ours brun\r" +
                "330 ##$aPetit Ours Brun aide son papa à faire le diner. Préparer les tomates, casser les oeufs... Petit Ours Brun est un vrai petit cuistot !$2éditeur\r" +
                "461 ##$t@Petit Ours brun\r" +
                "686 ##$a809$2Cadre de classement de la Bibliographie nationale française\r" +
                "700 #1$3086100106Aubinais, Marie$4070\r" +
                "702 #1$3026744813Bour, Danièle (1939-....)$4440\r" +
                "801 #0$aFR$bFR-751131015$c20180608$gAFNOR$2intermrc\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromStringWithRepeatedTag() throws Exception {
        String biblioStr = Constants.STR_1F +
                "000 $01\r" +
                "002 $aFRBNF455216030000003$2FRBNF\r" +
                "003 http://www.sudoc.fr/230362532\r" +
                "004 4994:25-09-18\r" +
                "005 1999:25-09-18 11:25:00.000\r" +
                "006 4994:25-09-18\r" +
                "008 $aAax3\r" +
                "010 ##$A978-2-7470-9101-5$bbr$d2,50 EUR\r" +
                "021 ##$aFR$bDLE-20180608-38992\r" +
                "033 ##$ahttp://catalogue.bnf.fr/ark:/12148/cb45521603c$2BNF$d20180904\r" +
                "035 ##$aFRBNF455216030000003$zFRBNF45521603$d20180911\r" +
                "073 #0$a9782747091015\r" +
                "100 0#$a2018\r" +
                "101 0#$afre\r" +
                "102 ##$aFR\r" +
                "104 ##$aa$by$cy$dba$e0$ffre\r" +
                "105 ##$a|$a|$a|$a|$bz$c0$d0$e|$fa$g|\r" +
                "106 ##$ar\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cn\r" +
                "200 1#$a@Petit Ours brun cuisine avec papa$fMarie Aubinais$g[illustrations originales], Danièle Bour\r" +
                "215 ##$a1 vol. (non paginé [15] p.)$cill. en coul.$d15 cm\r" +
                "215 ##$a2 vol.$ctest\r" +
                "225 1#$a@Petit Ours brun\r" +
                "330 ##$aPetit Ours Brun aide son papa à faire le diner. Préparer les tomates, casser les oeufs... Petit Ours Brun est un vrai petit cuistot !$2éditeur\r" +
                "461 ##$t@Petit Ours brun\r" +
                "686 ##$a809$2Cadre de classement de la Bibliographie nationale française\r" +
                "700 #1$3086100106Aubinais, Marie$4070\r" +
                "702 #1$3026744813Bour, Danièle (1939-....)$4440\r" +
                "801 #0$aFR$bFR-751131015$c20180608$gAFNOR$2intermrc\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.getListeZones().size()).isEqualTo(30);
        assertThat(biblio.findZones("200").get(0).getSubLabelList().size()).isEqualTo(3);
        assertThat(biblio.findZones("215").size()).isEqualTo(2);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                Constants.STR_1B + "P000 $01\r" +
                Constants.STR_1B + "D002 $aFRBNF455216030000003$2FRBNF\r" +
                "003 http://www.sudoc.fr/230362532\r" +
                "004 4994:25-09-18\r" +
                "005 1999:25-09-18 11:25:00.000\r" +
                "006 4994:25-09-18\r" +
                "008 $aAax3\r" +
                "010 ##$A978-2-7470-9101-5$bbr$d2,50 EUR\r" +
                "021 ##$aFR$bDLE-20180608-38992\r" +
                "033 ##$ahttp://catalogue.bnf.fr/ark:/12148/cb45521603c$2BNF$d20180904\r" +
                "035 ##$aFRBNF455216030000003$zFRBNF45521603$d20180911\r" +
                "073 #0$a9782747091015\r" +
                "100 0#$a2018\r" +
                "101 0#$afre\r" +
                "102 ##$aFR\r" +
                "104 ##$aa$by$cy$dba$e0$ffre\r" +
                "105 ##$a|$a|$a|$a|$bz$c0$d0$e|$fa$g|\r" +
                "106 ##$ar\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cn\r" +
                "200 1#$a@Petit Ours brun cuisine avec papa$fMarie Aubinais$g[illustrations originales], Danièle Bour\r" +
                "215 ##$a1 vol. (non paginé [15] p.)$cill. en coul.$d15 cm\r" +
                "215 ##$a2 vol.$ctest\r" +
                "225 1#$a@Petit Ours brun\r" +
                "330 ##$aPetit Ours Brun aide son papa à faire le diner. Préparer les tomates, casser les oeufs... Petit Ours Brun est un vrai petit cuistot !$2éditeur\r" +
                "461 ##$t@Petit Ours brun\r" +
                "686 ##$a809$2Cadre de classement de la Bibliographie nationale française\r" +
                "700 #1$3086100106Aubinais, Marie$4070\r" +
                "702 #1$3026744813Bour, Danièle (1939-....)$4440\r" +
                "801 #0$aFR$bFR-751131015$c20180608$gAFNOR$2intermrc\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromStringWithRepeatedSubTag() throws Exception {
        String biblioStr = Constants.STR_1F +
                "003 231924682\r" +
                "004 341720001:21-11-19\r" +
                "005 341720001:21-11-19 14:08:09.000\r" +
                "006 341720001:21-11-19\r" +
                "008 $aAax\r" +
                "00A $00\r" +
                "00U $0utf8\r" +
                "100 0#$a2019\r" +
                "101 ##$afre\r" +
                "200 0#$a@Petit ours brun à la ferme$aOTto\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.getListeZones().size()).isEqualTo(10);
        assertThat(biblio.findZones("200").get(0).getSubLabelList().size()).isEqualTo(2);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "003 231924682\r" +
                "004 341720001:21-11-19\r" +
                "005 341720001:21-11-19 14:08:09.000\r" +
                "006 341720001:21-11-19\r" +
                "008 $aAax\r" +
                Constants.STR_1B + "P00A $00\r" +
                Constants.STR_1B + "D" + Constants.STR_1B + "P00U $0utf8\r" +
                Constants.STR_1B + "D100 0#$a2019\r" +
                "101 ##$afre\r" +
                "200 0#$a@Petit ours brun à la ferme$aOTto\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromStringWithRepeatedRameau() throws Exception {
        String biblioStr = Constants.STR_1F +
                "003 231924682\r" +
                "004 341720001:21-11-19\r" +
                "005 341720001:21-11-19 14:08:09.000\r" +
                "006 341720001:21-11-19\r" +
                "008 $aAax\r" +
                "100 0#$a2019\r" +
                "101 ##$afre\r" +
                "200 0#$a@Petit ours brun à la ferme$aOTto\r" +
                "606 ##$3027466957Droits de l'homme$3027890384Compétence territoriale$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$3050605976Droit des peuples à disposer d'eux-mêmes$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$302934400XCitoyenneté$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$302747562XJustice distributive$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$3027233103Émigration et immigration$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$3034228942$3027253139$2rameau\r" +
                "606 ##$3029677440$3027253139$2rameau\r"
                + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.getListeZones().size()).isEqualTo(15);
        assertThat(biblio.findZones("606").get(0).getSubLabelList().size()).isEqualTo(4);
        assertThat(biblio.findZones("606").get(5).getSubLabelList().size()).isEqualTo(3);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "003 231924682\r" +
                "004 341720001:21-11-19\r" +
                "005 341720001:21-11-19 14:08:09.000\r" +
                "006 341720001:21-11-19\r" +
                "008 $aAax\r" +
                "100 0#$a2019\r" +
                "101 ##$afre\r" +
                "200 0#$a@Petit ours brun à la ferme$aOTto\r" +
                "606 ##$3027466957Droits de l'homme$3027890384Compétence territoriale$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$3050605976Droit des peuples à disposer d'eux-mêmes$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$302934400XCitoyenneté$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$302747562XJustice distributive$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$3027233103Émigration et immigration$3027253139Thèses et écrits académiques$2rameau\r" +
                "606 ##$3034228942$3027253139$2rameau\r" +
                "606 ##$3029677440$3027253139$2rameau\r"
                + Constants.STR_1E);
    }

    @Test
    void biblioFromXmlTest() throws Exception {
        Scanner scanner = new Scanner(BiblioTest.class.getResourceAsStream("/noticeXML.xml"), StandardCharsets.UTF_8).useDelimiter("\\r");
        StringBuilder notice = new StringBuilder();
        while (scanner.hasNext()) {
            notice.append(scanner.next());
        }
        Biblio biblio = new Biblio(notice.toString(), FORMATS.XML);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "002 ##$aSTAR10007$2STAR\r" +
                "008 ##$aOax3\r" +
                "029 ##$aFR$b2010ECAP0020\r" +
                "033 ##$ahttp://www.theses.fr/2010ECAP0020\r" +
                "035 ##$aSTAR10007\r" +
                "100 0#$a2010\r" +
                "101 0#$afre$dfre$deng\r" +
                "102 ##$aFR\r" +
                "104 ##$ak$by$cy$dba$e0$ffre\r" +
                "105 ##$bm$ba$c0$d0$fy$gy\r" +
                "135 ##$ad$br\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cc\r" +
                "183 ##$P01$aceb\r" +
                "200 1#$a@Contribution à l'élaboration d'un outil de simulation de procédés de transformation physico-chimique de matières premières issues des agro ressources$eapplication aux procédés de transformation de biopolymères par extrusion réactive$fMarie-Amélie De ville d'avray$gsous la direction de Arsène Isambert\r" +
                "219 #1$d2010\r" +
                "230 ##$aDonnées textuelles\r" +
                "304 ##$aTitre provenant de l'écran-titre\r" +
                "310 ##$aThèse confidentielle jusqu'au 05 juillet 2015\r" +
                "314 ##$aEcole(s) Doctorale(s) : Sciences pour l'ingénieur\r" +
                "314 ##$aPartenaire(s) de recherche : Laboratoire Génie des Procédés et Matériaux (Laboratoire), Laboratoire de Genie des Procédés et Matériaux (Laboratoire)\r" +
                "314 ##$aAutre(s) contribution(s) : Gilles Trystram (Président du jury) ; Arsène Isambert, Hélène Ducatel, Stéphane Brochot (Membre(s) du jury) ; Yann Le gorrec, Xuân Meyer (Rapporteur(s))\r" +
                "328 #0$bThèse de doctorat$cGénie des procédés$eEcole centrale Paris$d2010\r" +
                "606 ##$3034228942$3027253139$2rameau\r" +
                "606 ##$3029677440$3027253139$2rameau\r" + Constants.STR_1E);
    }

    @Test
    void isTheseElectroniqueTest() throws Exception {
        Biblio notice = new Biblio();
        notice.addZone("008", "$a", "Oa");
        notice.addZone("105", "$b", "m");
        assertThat(notice.isTheseElectronique()).isTrue();
        notice = new Biblio();
        notice.addZone("008", "$a", "Aa");
        notice.addZone("105", "$b", "m");
        assertThat(notice.isTheseElectronique()).isFalse();
        notice = new Biblio();
        notice.addZone("008", "$a", "Oa");
        notice.addZone("105", "$b", "test");
        assertThat(notice.isTheseElectronique()).isFalse();
    }

    @Test
    void biblioFromStringWithProtectedTag() throws Exception {
        String biblioStr = Constants.STR_1F +
                "003 231924682\r" +
                "004 341720001:21-11-19\r" +
                "005 341720001:21-11-19 14:08:09.000\r" +
                "006 341720001:21-11-19\r" +
                "008 $aAax\r" +
                Constants.STR_1B + "P00A $00\r" +
                Constants.STR_1B + "D00U $0utf8\r" +
                "100 0#$a2019\r" +
                "101 ##$afre\r" +
                "200 0#$a@Petit ours brun à la ferme$aOTto\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.getListeZones().size()).isEqualTo(10);
        assertThat(biblio.findZones("200").get(0).getSubLabelList().size()).isEqualTo(2);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "003 231924682\r" +
                "004 341720001:21-11-19\r" +
                "005 341720001:21-11-19 14:08:09.000\r" +
                "006 341720001:21-11-19\r" +
                "008 $aAax\r" +
                Constants.STR_1B + "P00A $00\r" +
                Constants.STR_1B + "D" + Constants.STR_1B + "P00U $0utf8\r" +
                Constants.STR_1B + "D100 0#$a2019\r" +
                "101 ##$afre\r" +
                "200 0#$a@Petit ours brun à la ferme$aOTto\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromStringWithTransliteration() throws Exception {
        String biblioStr = Constants.STR_1F +
                "200 #1$601$7ba$aKazantzakīs$fNikos$f1883-1957\r" +
                "200 #1$601$7ga$aΚαζαντζακης$fΝικος$f1883-1957\r" +
                Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "200 #1$601$7ba$aKazantzakīs$fNikos$f1883-1957\r" +
                "200 #1$601$7ga$aΚαζαντζακης$fΝικος$f1883-1957\r" +
                Constants.STR_1E);
    }

    @Test
    void biblioRareSubLabel() throws Exception {
        String biblioStr = Constants.STR_1F +
                Constants.STR_1B + "P000 $04,41,51,74,86,88,102,130,159,173\r" +
                Constants.STR_1B + "D" +"003 http://www.sudoc.fr/134381246\r" +
                "004 693872101:22-06-09\r" +
                "005 1999:26-02-20 03:27:27.000\r" +
                "006 693872101:22-06-09\r" +
                "008 $aAax3\r" +
                Constants.STR_1B + "P00A $00\r" +
                Constants.STR_1B + "D" +"010 ##$A978-9973-70409-2$bbr.$d19 EUR\r" +
                "010 ##$a9973-70409-6$bbr.\r" +
                "034 $aOCoLC$0494531729\r" +
                "072 #1$a6194039974556$bbr.$d19 EUR\r" +
                "100 0#$a2009$d2009\r" +
                "101 0#$afre\r" +
                "102 ##$aTN\r" +
                "104 ##$am$by$cy$dba$e0$ffre\r" +
                "105 ##$aa$ah$ac$ba$c0$d0$e1$fy$gd\r" +
                "106 ##$ar\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cn\r" +
                "200 1#$aLes @Africains$hI$iLes intellectuels carthaginois$ele génie africain et l'éducation classique$ela vie littéraire à Carthage$fPaul Monceaux$gprésentation, commentaires et indices Leïla Ladjimi Sebaï\r" +
                "210 ##$aCarthage$cCartaginoiseries$dDL 2009\r" +
                "215 ##$a1 vol. (165 p.)$cill., couv. ill. en coul.$d21 cm\r" +
                "320 ##$aBibliogr. p. 163. Notes bibliogr. Glossaire. Index\r" +
                "517 ##$aLes @intellectuels carthaginois\r" +
                "517 ##$aLe @génie africain et l'éducation classique$ela vie littéraire à Carthage\r" +
                "606 ##$3027236986Littérature latine$3027218597Afrique du Nord$2rameau\r" +
                "606 ##$305352313XCivilisation -- Carthage (ville ancienne)$2rameau\r" +
                "606 ##$3028219961Civilisation -- Afrique du Nord$3027789888Antiquité$2rameau\r" +
                "606 ##$3027255670Vie intellectuelle$3027357899Carthage (ville ancienne)$2rameau\r" +
                "607 ##$3027374769Afrique du Nord -- Jusqu'à 647$2rameau\r" +
                "676 ##$a870.9$v22\r" +
                "700 #1$3035738340Monceaux, Paul (1859-1941)$4070\r" +
                "702 #1$3030350964Ladjimi Sebaï, Leïla$4340\r" +
                "801 #0$bAUXAM$gAACR2\r" +
                "801 #1$bAUXAM$gAACR2\r" +
                "830 ##$aABES NE PAS SUPPRIMER Réforme Rameau retournement 607 juillet 2019\r" +
                "830 ##$aABES NE PAS SUPPRIMER Réforme Rameau Tg préconstruites avec subd. aux lieux 2019-06-14\r" +
                Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.toString()).isEqualTo(biblioStr);
    }

    @Test
    void biblioFromString214() throws Exception {
        String biblioStr = Constants.STR_1F + "214 #4$dC 2000\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F + "214 #4$dC 2000\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromString214Test2() throws Exception {
        String biblioStr = Constants.STR_1F + "214 #4$dc1990$dC 2000\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr, FORMATS.UNM);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F + "214 #4$dc1990$dC 2000\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromString214Test3() throws Exception {
        String biblioStr = Constants.STR_1F + "214 #3$aCastres$bla bas$clui$atoulouse$cmoi\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F + "214 #3$aCastres$bla bas$clui$atoulouse$cmoi\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromString214Test4() throws Exception {
        String biblioStr = Constants.STR_1F + "214 #3$aCastres$bla bas$clui$atoulouse$cmoi$dC 2009\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F + "214 #3$aCastres$bla bas$clui$atoulouse$cmoi$dC 2009\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromString214Test5() throws Exception {
        String biblioStr = Constants.STR_1F + "214 ##$rA Paris, chez Masson, libraire, rue Gallande, n° 27. An IX de la République [1800 ou 1801]$sDe l'imprimerie de Richard, place Cambrai, n° 4.\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F + "214 ##$rA Paris, chez Masson, libraire, rue Gallande, n° 27. An IX de la République [1800 ou 1801]$sDe l'imprimerie de Richard, place Cambrai, n° 4.\r" + Constants.STR_1E);
    }

    @Test
    void biblioFromString214Test6() throws Exception {
        String biblioStr = Constants.STR_1F + "214 #3$aCastres$clui$atoulouse$cmoi\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F + "214 #3$aCastres$clui$atoulouse$cmoi\r" + Constants.STR_1E);
    }
    
    @Test
    void biblioFromString214Test7() throws Exception {
        String biblioStr = Constants.STR_1F +
                "006 341720001:24-11-20\r" +
                "008 $aObx3\r" +
                "033 ##$ahttps://reseau-mirabel.info/revue/titre-id/2785$2Mir@bel$d20200421\r" +
                "100 0#$a199X$d199X-...\r" +
                "101 0#$aeng\r" +
                "102 ##$aUS\r" +
                "104 ##$au$bb$cy$dba$e0$ffre\r" +
                "110 ##$aa$bf$cb$f0$gu$hu\r" +
                "135 ##$ad$br\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cc\r" +
                "183 ##$P01$aceb\r" +
                "200 1#$a@Washington journalism review [test cidemis]\r" +
                "214 #0$aWashington, D.C.$cWashington Communications Corp.$d1983-1993\r" +
                "230 ##$aDonnées textuelles\r" +
                "304 ##$aTitre provenant de l'écran-titre\r" +
                "452 ##$0037647482\u001BI@Washington journalism review (1983), ISSN 0741-8876\u001BN\r" +
                "532 ##$a@WJR\r" +
                "608 ##$302724640X\u001BIPériodiques\u001BN$2rameau\r" +
                "676 ##$a071\r" +
                "712 02$a@University of Maryland at College Park$bCollege of Journalism$4340" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr);
        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "006 341720001:24-11-20\r" +
                "008 $aObx3\r" +
                "033 ##$ahttps://reseau-mirabel.info/revue/titre-id/2785$2Mir@bel$d20200421\r" +
                "100 0#$a199X$d199X-...\r" +
                "101 0#$aeng\r" +
                "102 ##$aUS\r" +
                "104 ##$au$bb$cy$dba$e0$ffre\r" +
                "110 ##$aa$bf$cb$f0$gu$hu\r" +
                "135 ##$ad$br\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cc\r" +
                "183 ##$P01$aceb\r" +
                "200 1#$a@Washington journalism review [test cidemis]\r" +
                "214 #0$aWashington, D.C.$cWashington Communications Corp.$d1983-1993\r" +
                "230 ##$aDonnées textuelles\r" +
                "304 ##$aTitre provenant de l'écran-titre\r" +
                "452 ##$0037647482\r" +
                "532 ##$a@WJR\r" +
                "608 ##$302724640X\u001BIPériodiques\u001BN$2rameau\r" +
                "676 ##$a071\r" +
                "712 02$a@University of Maryland at College Park$bCollege of Journalism$4340\r" + Constants.STR_1E);
    }

    @Test
    void testModifZone029() throws Exception {
        String biblioStr = Constants.STR_1F +
                "006 593502101:19-12-06\r" +
                "008 $aAax3\r" +
                "029 ##$aFR$b2006LIL2E104$eTH-PH-0086$mMEM-2006-0056$o459 (numéro d'ordre)$z2006LLL2E104\r" +
                "034 $aOCoLC$0493718812\r" +
                "100 0#$a2006\r" +
                "101 0#$afre$dfre$deng\r" +
                "102 ##$aFR\r" +
                "104 ##$ak$by$cy$dba$ffre\r" +
                "105 ##$aa$bm$ba$c0$d0$e0$fi$gy\r" +
                "106 ##$ar\r" +
                "200 1#$a@Régulation de la captation sélective des esters de cholestérol dans différents types cellulaires TEST$eimplication d'un réseau complexe de protéines (SR-BI, apolipoprotéine E, lipoprotéine lipase...)$fStéphanie Bultel$gsous la direction de Véronique Clavey\r" +
                "214 #1$d2006\r" +
                "215 ##$a1 vol. (239 f.)$cill. en noir et en coul.$d30 cm\r" +
                "310 ##$aPublication autorisée par le jury\r" +
                "328 #0$bThèse d'exercice$cPharmacie$eLille 2$d2006\r" +
                "328 #0$bMémoire du diplôme d'études spécialisées$cPharmacie spécialisée$eLille 2$d2006\r" +
                "328 #0$bThèse de doctorat$cSciences pharmaceutiques$eLille 2$d2006\r" +
                "700 #1$3111475384$4070\r" +
                "701 #1$3075890127$4727\r" +
                "711 01$3026404389$4295\r" + Constants.STR_1E;

        Biblio biblio = new Biblio(biblioStr);

        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "006 593502101:19-12-06\r" +
                "008 $aAax3\r" +
                "029 ##$aFR$b2006LIL2E104$eTH-PH-0086$mMEM-2006-0056$o459 (numéro d'ordre)$z2006LLL2E104\r" +
                "034 $aOCoLC$0493718812\r" +
                "100 0#$a2006\r" +
                "101 0#$afre$dfre$deng\r" +
                "102 ##$aFR\r" +
                "104 ##$ak$by$cy$dba$ffre\r" +
                "105 ##$aa$bm$ba$c0$d0$e0$fi$gy\r" +
                "106 ##$ar\r" +
                "200 1#$a@Régulation de la captation sélective des esters de cholestérol dans différents types cellulaires TEST$eimplication d'un réseau complexe de protéines (SR-BI, apolipoprotéine E, lipoprotéine lipase...)$fStéphanie Bultel$gsous la direction de Véronique Clavey\r" +
                "214 #1$d2006\r" +
                "215 ##$a1 vol. (239 f.)$cill. en noir et en coul.$d30 cm\r" +
                "310 ##$aPublication autorisée par le jury\r" +
                "328 #0$bThèse d'exercice$cPharmacie$eLille 2$d2006\r" +
                "328 #0$bMémoire du diplôme d'études spécialisées$cPharmacie spécialisée$eLille 2$d2006\r" +
                "328 #0$bThèse de doctorat$cSciences pharmaceutiques$eLille 2$d2006\r" +
                "700 #1$3111475384$4070\r" +
                "701 #1$3075890127$4727\r" +
                "711 01$3026404389$4295\r" + Constants.STR_1E);
    }

    @Test
    void testAjoutZone325() throws Exception {
        String biblioStr = Constants.STR_1F +
                "003 201925001\r" +
                "004 4018:19-06-17\r" +
                "005 341720001:08-10-21 17:53:25.000\r" +
                "006 4018:19-06-17\r" +
                "008 $aOax3\r" +
                "010 ##$A978-0-674-73442-5\r" +
                "017 70$a10.4159/harvard.9780674734425$2doi\r" +
                "100 1#$a1974\r" +
                "101 0#$aeng\r" +
                "102 ##$aDE$aUS\r" +
                "104 ##$a|$b|$cy$ffre\r" +
                "135 ##$ad$br\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cc\r" +
                "183 ##$P01$aceb\r" +
                "200 1#$aThe @Lyon Uprising of 1834 TEST AVEC 325$eSocial and Political Conflict in the Early July Monarchy$fRobert J. Bezucha\r" +
                "214 #0$aCambridge, Mass.$cHarvard University Press$d[1974]\r" +
                "325 ##$aNumérisation sur archive.org$uhttps://openlibrary.org/works/OL957949W/\r" +
                "325 ##$bReproduction numérique$cLieu inconnu$dArchive.org$e2020$faccès en ligne$gCollection Lyon Social$h0$iNote pour info sur le caractère complet de la repro$j3pd06$nNote sur la reproduction$uhttps://openlibrary.org/works/OL957949W/Renoir$x1292-8399$z20210913\r" +
                "801 #0$aDE$bIN-ChSCO$c20170329$grda\r" + Constants.STR_1E;
        Biblio biblio = new Biblio(biblioStr);

        assertThat(biblio.toString()).isEqualTo(Constants.STR_1F +
                "003 201925001\r" +
                "004 4018:19-06-17\r" +
                "005 341720001:08-10-21 17:53:25.000\r" +
                "006 4018:19-06-17\r" +
                "008 $aOax3\r" +
                "010 ##$A978-0-674-73442-5\r" +
                "017 70$a10.4159/harvard.9780674734425$2doi\r" +
                "100 1#$a1974\r" +
                "101 0#$aeng\r" +
                "102 ##$aDE$aUS\r" +
                "104 ##$a|$b|$cy$ffre\r" +
                "135 ##$ad$br\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cc\r" +
                "183 ##$P01$aceb\r" +
                "200 1#$aThe @Lyon Uprising of 1834 TEST AVEC 325$eSocial and Political Conflict in the Early July Monarchy$fRobert J. Bezucha\r" +
                "214 #0$aCambridge, Mass.$cHarvard University Press$d[1974]\r" +
                "325 ##$aNumérisation sur archive.org$uhttps://openlibrary.org/works/OL957949W/\r" +
                "325 ##$bReproduction numérique$cLieu inconnu$dArchive.org$e2020$faccès en ligne$gCollection Lyon Social$h0$iNote pour info sur le caractère complet de la repro$j3pd06$nNote sur la reproduction$uhttps://openlibrary.org/works/OL957949W/Renoir$x1292-8399$z20210913\r" +
                "801 #0$aDE$bIN-ChSCO$c20170329$grda\r" + Constants.STR_1E);
    }

    @Test
    void testAjout371et338() throws Exception {
        String biblioStr = Constants.STR_1F +
                "002 $aFRBNF46624974000000X$2FRBNF\r" +
                "003 http://www.sudoc.fr/232844194\r" +
                "004 4994:19-11-20\r" +
                "005 1837:23-11-21 16:40:10.000\r" +
                "006 4994:19-11-20\r" +
                "007 1837:23-11-21 16:40:10.000\r" +
                "008 $aAax3\r" +
                "010 ##$A978-2-35884-100-9$bbr.$d20 EUR\r" +
                "020 ##$aFR$b02043044\r" +
                "024 $aSIBB$bM$323306821X\r" +
                "100 0#$a2021\r" +
                "101 0#$afre\r" +
                "102 ##$aFR\r" +
                "104 ##$am$by$cy$dba$e0$ffre\r" +
                "105 ##$aa$bz$c0$d0$e1$fc$gb\r" +
                "106 ##$ar\r" +
                "181 ##$P01$ctxt\r" +
                "182 ##$P01$cn\r" +
                "183 ##$P01$anga\r" +
                "200 1#$a@Robert Schumann TEST$fJean Gallois\r" +
                "214 #0$a[Paris]$cBleu nuit éditeur\r" +
                "214 #4$dC 2021\r" +
                "215 ##$a1 vol. (176 p.)$cill.$d20 cm\r" +
                "320 ##$aBibliogr. p. 171. Discogr. p. 169-170. Index\r" +
                "325 ##$bReproduction numérique$cParis$dBibliothèque nationale de France, Centre national de la littérature pour la jeunesse$e2005$h0$i1976-$j3py02$uhttp://www.tucliquesici$y978-2-03-598509-5$z20210109\r" +
                "325 ##$bMicrofiche$cParis$dBnF$h0$i1888/11 (série 1, fasc. 2 -1936/12) (série 27, fasc. 6)$j1xx##$uhttp://gallica.bnf.fr/ark:/12148/cb343494147/1936$v20141202$x2419-6592\r" +
                "325 ##$bFac-similé$cParis$dEditeur incroyable$e2008$f2 volumes (48 p.)$gCollection rigolote$j1xx##$x2419-6592\r" +
                "325 ##$aReproduction numérique réalisée en 2021 par la bibliothèque$uhttp://www.labibliotheque.fr\r" +
                "338 ##$bCommission européenne$eUnion européenne$bCommission ministerielle$eMESRI$eMInistère de la santé$cH2021$dH2020-SFS-2016-2017$fPromoting One Health in Europe through joint actions on foodborne zoonoses, antimicrobial resistance and emerging microbiological hazards$gOne Health EJP\r" +
                "338 ##$bINCa$eMESRI$eMinistère des solidarités et de la santé$bIReSP$eINSERM$cSPADOC\r" +
                "338 ##$bAID$bCEA$ccontrat de thèse CEA/AID$d2021-47\r" +
                "371 0#$8PDF$aAccès réservé aux établissements ou bibliothèques abonnés\r" +
                "371 1#$aReproduction interdite$dautorisée à des seules fins de recherche\r" +
                "600 #1$3027128660Schumann, Robert (1810-1856 ; musicien)$2rameau\r" +
                "608 ##$3027281558Biographies$2rameau\r" +
                "700 #1$3026879972Gallois, Jean$4330 (Auteur prétendu)\r" +
                "711 02$3027801411@Union européenne. Commission européenne$4723 (Mécène)\r" +
                "801 #0$aFR$bFR-751131015$c20200929$gAFNOR$2intermrc\r" + Constants.STR_1E;

        Biblio biblio = new Biblio(biblioStr);

        assertThat(biblio.toString()).isEqualTo(
                Constants.STR_1F +
                        "002 $aFRBNF46624974000000X$2FRBNF\r" +
                        "003 http://www.sudoc.fr/232844194\r" +
                        "004 4994:19-11-20\r" +
                        "005 1837:23-11-21 16:40:10.000\r" +
                        "006 4994:19-11-20\r" +
                        "007 1837:23-11-21 16:40:10.000\r" +
                        "008 $aAax3\r" +
                        "010 ##$A978-2-35884-100-9$bbr.$d20 EUR\r" +
                        "020 ##$aFR$b02043044\r" +
                        "024 $aSIBB$bM$323306821X\r" +
                        "100 0#$a2021\r" +
                        "101 0#$afre\r" +
                        "102 ##$aFR\r" +
                        "104 ##$am$by$cy$dba$e0$ffre\r" +
                        "105 ##$aa$bz$c0$d0$e1$fc$gb\r" +
                        "106 ##$ar\r" +
                        "181 ##$P01$ctxt\r" +
                        "182 ##$P01$cn\r" +
                        "183 ##$P01$anga\r" +
                        "200 1#$a@Robert Schumann TEST$fJean Gallois\r" +
                        "214 #0$a[Paris]$cBleu nuit éditeur\r" +
                        "214 #4$dC 2021\r" +
                        "215 ##$a1 vol. (176 p.)$cill.$d20 cm\r" +
                        "320 ##$aBibliogr. p. 171. Discogr. p. 169-170. Index\r" +
                        "325 ##$bReproduction numérique$cParis$dBibliothèque nationale de France, Centre national de la littérature pour la jeunesse$e2005$h0$i1976-$j3py02$uhttp://www.tucliquesici$y978-2-03-598509-5$z20210109\r" +
                        "325 ##$bMicrofiche$cParis$dBnF$h0$i1888/11 (série 1, fasc. 2 -1936/12) (série 27, fasc. 6)$j1xx##$uhttp://gallica.bnf.fr/ark:/12148/cb343494147/1936$v20141202$x2419-6592\r" +
                        "325 ##$bFac-similé$cParis$dEditeur incroyable$e2008$f2 volumes (48 p.)$gCollection rigolote$j1xx##$x2419-6592\r" +
                        "325 ##$aReproduction numérique réalisée en 2021 par la bibliothèque$uhttp://www.labibliotheque.fr\r" +
                        "338 ##$bCommission européenne$eUnion européenne$bCommission ministerielle$eMESRI$eMInistère de la santé$cH2021$dH2020-SFS-2016-2017$fPromoting One Health in Europe through joint actions on foodborne zoonoses, antimicrobial resistance and emerging microbiological hazards$gOne Health EJP\r" +
                        "338 ##$bINCa$eMESRI$eMinistère des solidarités et de la santé$bIReSP$eINSERM$cSPADOC\r" +
                        "338 ##$bAID$bCEA$ccontrat de thèse CEA/AID$d2021-47\r" +
                        "371 0#$8PDF$aAccès réservé aux établissements ou bibliothèques abonnés\r" +
                        "371 1#$aReproduction interdite$dautorisée à des seules fins de recherche\r" +
                        "600 #1$3027128660Schumann, Robert (1810-1856 ; musicien)$2rameau\r" +
                        "608 ##$3027281558Biographies$2rameau\r" +
                        "700 #1$3026879972Gallois, Jean$4330 (Auteur prétendu)\r" +
                        "711 02$3027801411@Union européenne. Commission européenne$4723 (Mécène)\r" +
                        "801 #0$aFR$bFR-751131015$c20200929$gAFNOR$2intermrc\r" + Constants.STR_1E
        );
    }
}
