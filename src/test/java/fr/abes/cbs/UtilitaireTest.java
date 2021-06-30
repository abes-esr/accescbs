package fr.abes.cbs;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;


public class UtilitaireTest {
    private static ProcessCBS cmd;
    private static String noticeResource;
    private static String noticeResourceAvecExemplaire;
    private static String noticeResourceAvecExemplaireAbes;

    @BeforeAll
    static void initAll() throws CBSException {
        Properties prop = new Properties();
        try {
            prop.load(UtilitaireTest.class.getResource("/CommandesTest.properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        cmd = new ProcessCBS();
        cmd.authenticate(prop.getProperty("connect.ip"), prop.getProperty("connect.port"), prop.getProperty("connect.login"), prop.getProperty("connect.password"));

        noticeResource = new Scanner(UtilitaireTest.class.getResourceAsStream("/noticePica.txt"), "UTF-8").useDelimiter("\\A").next();
        noticeResourceAvecExemplaire = new Scanner(UtilitaireTest.class.getResourceAsStream("/noticeAvecExemplaire.txt"), "UTF-8").useDelimiter("\\A").next();
        noticeResourceAvecExemplaireAbes = new Scanner(UtilitaireTest.class.getResourceAsStream("/noticeAvecExemplaireAbes.txt"), "UTF-8").useDelimiter("\\A").next();

    }

    @DisplayName("RecupEntre")
    @Test
    void recupEntre(){
        assertThat(Utilitaire.recupEntre("Ceci est une <test>phrase de</test> test", "<test>", "</test>")).isEqualTo("phrase de");
        assertThat(Utilitaire.recupEntre("Ceci est une <test>phrase de</test> test", "<test>", "")).isEqualTo("<test>phrase de</test> test");
    }

    @DisplayName("Nb de notices from CHE")
    @Test
    void getNbNoticesFromChe() throws CBSException {
        assertThat(Utilitaire.getNbNoticesFromChe(cmd.search("che mti fdfsdfsfzerzrzrzrzfsdf"))).isEqualTo(0);
        assertThat(Utilitaire.getNbNoticesFromChe(cmd.search("che ppn 230721486"))).isEqualTo(1);
        assertThat(Utilitaire.getNbNoticesFromChe(cmd.search("che mti testtcn"))).isEqualTo(2);
    }

    @DisplayName("Conversion Pica -> XML")
    @Test
    void xmlFormat() throws CBSException {
        //On vérifie la présence d'un champ afin de vérifier que le XML n'est pas vide et donc que la notice a bien été convertie
        assertThat(Utilitaire.xmlFormat(noticeTestEnEdition())).contains("<controlfield  tag=\"008\" >$aAax</controlfield>");
    }

    @DisplayName("Conversion Pica -> XML pour les notices en édition")
    @Test
    void xmlFormatEdit() throws CBSException {
        //On vérifie la présence d'un champ afin de vérifier que le XML n'est pas vide et donc que la notice a bien été convertie
        assertThat(Utilitaire.xmlFormatEdit(noticeTestEnEdition())).contains("<subfield code=\"a\">Aax</subfield></datafield>");
    }

    @DisplayName("Conversion liste résultats -> XML")
    @Test
    void getRecordSetAsXml() throws CBSException {
        cmd.search("che mti ours");
        //On vérifie que l'on a bien une liste XML non vide en retour
        assertThat(Utilitaire.getRecordSetAsXml(cmd.getResultatsTable())).contains("<RecordsList><Record><Col0>1</Col0>");
    }

    @DisplayName("Conversion liste résultats -> XML du 1er résultat uniquement")
    @Test
    void getRecordSetAsXmlFromFor() throws CBSException {
        cmd.search("che mti ours");
        //On vérifie que l'on a bien une liste XML non vide  d'un seul élement en retour
        String xml = Utilitaire.getRecordSetAsXmlFromFor(0,1, cmd.getResultatsTable());
        assertThat(xml).contains("<RecordsList><Record><Col0>1</Col0>").doesNotContain("<RecordsList><Record><Col0>2</Col0>");
        xml = Utilitaire.getRecordSetAsXmlFromFor(1,1, cmd.getResultatsTable());
        assertThat(xml).contains("<RecordsList><Record><Col0>1</Col0>").doesNotContain("<RecordsList><Record><Col0>2</Col0>");
    }

    @DisplayName("Récupération d'un tag")
    @Test
    void getTag() throws CBSException {
        String notice = new Scanner(CommandesTest.class.getResourceAsStream("/noticePica.txt"), "UTF-8").useDelimiter("\\A").next();
        //On vérifie que getTag nous retourne bien la bonne valeur
        assertThat(Utilitaire.getTag(notice, "008", "")).isEqualTo("$aAax");
        assertThat(Utilitaire.getTag(notice, "101", "0#")).isEqualTo("$aeng");
    }

    @DisplayName("Récupération d'une zone")
    @Test
    void getZone() throws CBSException {
        assertThat(Utilitaire.getZone(noticeResource, "008", "")).isEqualTo("$aAax");
        assertThat(Utilitaire.getZone(noticeResource, "101", "$a")).isEqualTo("eng");
    }

    @DisplayName("Récupération d'une zone contenant une valeur")
    @Test
    void getZoneWithValue(){
        assertThat(Utilitaire.getZoneWithValue(noticeResource, "101", "$a", "eng")).isTrue();
    }

    @DisplayName("ಠ_ಠ")
    @Test
    void format(){
        //Cette méthode raccourcie une String pour y coller des points de suspension
        //On vérifie donc qu'on obtiens bien une String plus courte avec des points de suspension, sauf si la String est déjà assez courte (2e cas)
        assertThat(Utilitaire.format("Ceci est une autre phrase de test",6)).isEqualTo("Cec...");
        assertThat(Utilitaire.format("Test",4)).isEqualTo("Test");
    }

    @DisplayName("Conversion XML -> Marc")
    @Test
    void xml2MarcEdit() throws CBSException {
        cmd.search("che ppn 230721486");
        String xml = Utilitaire.xmlFormat(cmd.editer("1"));
        assertThat(Utilitaire.xml2MarcEdit(xml)).contains("008 $aAax").contains("101 0$afre");
    }

    @DisplayName("Conversion XML -> Marc")
    @Test
    void xml2Marc() throws CBSException, IOException {
        //String content = Files.readString(Paths.get("/noticeXML.xml"), StandardCharsets.US_ASCII);

        Scanner scanner = new Scanner(UtilitaireTest.class.getResourceAsStream("/noticeXML.xml"), "UTF-8").useDelimiter("\\r");
        StringBuilder notice = new StringBuilder();
        while (scanner.hasNext()) {
            notice.append(scanner.next());
        }
        assertThat(Utilitaire.xml2Marc(notice.toString())).isEqualTo(
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
                        "328 #0$bThèse de doctorat$cGénie des procédés$d2010$eEcole centrale Paris\r" +
                        "606 ##$3034228942$3027253139$2rameau\r" +
                        "606 ##$3029677440$3027253139$2rameau\r"
                );
    }

    @DisplayName("Récupération notice biblio")
    @Test
    void recupNoticeBib(){
        //On vérifie que la méthode supprime bien les infos superflues de la notice
        assertThat(Utilitaire.recupNoticeBib(noticeResource)).contains("200 0#$aNotice de test de modification dans API Sudoc@testmodifier Notice").doesNotContain("Création: 341720001").doesNotContain("Cutf-8");
    }

    @DisplayName("Ajout d'une zone biblio")
    @Test
    void ajoutZoneBiblio() throws CBSException {
        //On récupère notre notice de test, puis on y ajoute la zone 102
        String noticeAvecAjout = Utilitaire.ajoutZoneBiblio(noticeTestEnEdition(), "102", "$a", "valeurTest");
        //On vérifie que la zone 102 est bien présente
        assertThat(noticeAvecAjout).contains("102 $avaleurTest").contains("008 $aAax");
    }

    @DisplayName("Modif d'une zone biblio")
    @Test
    void modifZoneBiblio() throws CBSException {
        //On récupère notre notice de test, puis on y modifie la zone 101
        String noticeModifiee = Utilitaire.modifZoneBiblio(noticeTestEnEdition(), "101", "$a", "fr");
        //On vérifie la valeur de la zone 101
        assertThat(noticeModifiee).contains("101 0#$afr");
    }

    @DisplayName("Suppr d'une zone biblio")
    @Test
    void suppZoneBiblio() throws CBSException {
        //On supprime la zone 101
        String noticeModifiee = Utilitaire.suppZoneBiblio(noticeTestEnEdition(), "101", "$a");
        //On vérifie son absence
        assertThat(noticeModifiee).contains("101 0#$beng" + Constants.STR_0D);
    }

    @DisplayName("Suppr d'une zone d'exemplaire")
    @Test
    void suppZoneExemp() {
        String exemp = Constants.STR_1F +
                "e03 $a02-12-05$bx\r" +
                "930 ##$b301892102$a306.485 ETH$ju\r" +
                "985 $atest\r" +
                "A97 02-12-05 16:22:37.000 \r" +
                "A98 301892102:02-12-05\r" +
                "A99 248398830\r" +
                "\r" + Constants.STR_1E +  Constants.STR_1E;
        assertThat(Utilitaire.suppZoneExemp(exemp, "985")).isEqualTo(Constants.STR_1F +
                "e03 $a02-12-05$bx\r" +
                "930 ##$b301892102$a306.485 ETH$ju\r" +
                "A97 02-12-05 16:22:37.000 \r" +
                "A98 301892102:02-12-05\r" +
                "A99 248398830\r");
    }

    @DisplayName("Suppr d'une zone biblio avec pattern")
    @Test
    void suppZoneBiblioWithPattern() throws CBSException {
        //On supprime la zone 101 qui contient "eng"
        String noticeModifiee = Utilitaire.suppZoneBiblioWithPattern(noticeTestEnEdition(), "101", "$a", "fre");
        //On vérifie son absence
        assertThat(noticeModifiee).doesNotContain("101 0#$a");
    }

    @DisplayName("Supp d'une zone biblio avec pattern quand zone répétée")
    @Test
    void suppZoneBiblioWithPatternZoneRepet() throws CBSException {
        cmd.search("che ppn 219041989");
        String notice = cmd.editer("1");
        String noticeModifiee = Utilitaire.suppZoneBiblioWithPattern(notice, "607", "$x", "Early works");
        assertThat(noticeModifiee).contains("607 ##$aGreat Britain$xPolitics and government$z1642-1649$2lc").doesNotContain("607 ##$aGreat Britain$xPolitics and government$z1642-1649$xEarly works to 1800$2lc");
    }

    @DisplayName("Numéro du prochain exemplaire")
    @Test
    void numExemplaire(){
        //Notice sans exemplaire, prochain exemple => e01
        assertThat(Utilitaire.numExemplaire(noticeResource)).isEqualTo("e01");
        //Notice avec un seul exemplaire, prochain exemple => e02
        assertThat(Utilitaire.numExemplaire(noticeResourceAvecExemplaire)).isEqualTo("e02");
    }

    @DisplayName("Numéro exemplaire depuis EPN")
    @Test
    void epnToExemplaire() {
        //On vérifie que l'epn 556670205 nous retourne bien e01 (c'est le premier exemplaire)
        assertThat(Utilitaire.epnToExemplaire(noticeResourceAvecExemplaireAbes, "556670205")).isEqualTo("e01");
    }

    @DisplayName("Récupération de la réponse d'une commande")
    @Test
    void messageCommande() throws CBSException {
        //La recherche "mti ours" nous répond bien "recherche mti ours"
        assertThat(Utilitaire.messageCommande(cmd.search("che mti ours"))).contains("VCOrecherche mti ours");
    }

    private String noticeTestEnEdition() throws CBSException {
        cmd.search("che ppn 230721486");
        return cmd.editer("1");
    }

    @DisplayName("Récupération du label d'une zone")
    @Test
    void getLabelFromZone() {
        String zoneE = "E856 $atest";
        String zoneL = "L012 $atest";
        String zoneExemp = "e01 $a19-01-19";
        String zoneNormale = "930 ##$b341720001";
        assertThat(Utilitaire.getLabelZone(zoneE)).isEqualTo("E856");
        assertThat(Utilitaire.getLabelZone(zoneL)).isEqualTo("L012");
        assertThat(Utilitaire.getLabelZone(zoneExemp)).isEqualTo("e01");
        assertThat(Utilitaire.getLabelZone(zoneNormale)).isEqualTo("930");
    }

    @DisplayName("Suppression de l'expension")
    @Test
    void deleteExpensionFromValueTest() {
        String valeur = "197213251\u001BI@OECD productivity statistics (Tables ed.), ISSN 2414-2581\u001BN";
        assertThat(Utilitaire.deleteExpensionFromValue(valeur)).isEqualTo("197213251");
    }

    @DisplayName("Suppression de l'expension")
    @Test
    void deleteExpensionFromValueTest2() {
        String valeur = "197213251OECD productivity statistics (Tables ed.), ISSN 2414-2581\u001BN";
        assertThat(Utilitaire.deleteExpensionFromValue(valeur)).isEqualTo("197213251OECD productivity statistics (Tables ed.), ISSN 2414-2581\u001BN");
    }

}
