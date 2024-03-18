package fr.abes.cbs;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.notices.Exemplaire;
import fr.abes.cbs.notices.NoticeConcrete;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class CommandesTest {

    private ProcessCBS cmd;
    private Properties prop;
    private Integer poll;
    
    private String ip;
    private String port;
    private String login;
    private String password;
    @BeforeEach
    void initAll() throws CBSException, IOException {
        prop = new Properties();
        prop.load(Objects.requireNonNull(CommandesTest.class.getResource("/CommandesTest.properties")).openStream());
        this.poll = Integer.parseInt(prop.getProperty("connect.poll"));
        this.ip = prop.getProperty("connect.ip");
        this.port = prop.getProperty("connect.port");
        this.login = prop.getProperty("connect.login");
        this.password = prop.getProperty("connect.password");
                
        cmd = new ProcessCBS(poll);
        cmd.authenticate(ip, port, login, password);
    }

    @DisplayName("Connexion ok")
    @Test
    void connect() {
        assertThat(cmd.getClientCBS().isLogged()).isTrue();
        assertThat(cmd.getClientCBS().isConnected()).isTrue();
    }

    @DisplayName("Connexion avec mauvais login/password/IP")
    @Test
    void connectBadLogin() throws CBSException {
        ProcessCBS com = new ProcessCBS(poll);
        try {
            com.authenticate(ip, port, login, "BadPassword");
        } catch (CBSException e) {
            assertThat(com.getClientCBS().isLogged()).isFalse();
            assertThat(com.getClientCBS().isConnected()).isTrue();
        }
        com.disconnect();

        try {
            com.authenticate(ip, port, "BadLogin", prop.getProperty("connect.password"));
        } catch (CBSException e) {
            assertThat(com.getClientCBS().isLogged()).isFalse();
            assertThat(com.getClientCBS().isConnected()).isTrue();
        }

        try {
            com.authenticate(prop.getProperty("MAUVAISEIP"), prop.getProperty("MAUVAISPORT"), "BadLogin", prop.getProperty("connect.password"));
        } catch (CBSException e) {
            assertThat(com.getClientCBS().isLogged()).isFalse();
            assertThat(com.getClientCBS().isConnected()).isTrue();
        }
        com.disconnect();
    }

    @DisplayName("Authentification avec choix de base")
    @Test
    void connectLogicalBdd() throws CBSException {
        ProcessCBS com = new ProcessCBS(poll);
        try {
            com.authenticateWithLogicalDb(ip, port, login, "BadPassword", "1.900");
        } catch (CBSException e) {
            assertThat(com.getClientCBS().isLogged()).isFalse();
            assertThat(com.getClientCBS().isConnected()).isTrue();
        }
        com.disconnect();

        try {
            com.authenticateWithLogicalDb(ip, port, "BadLogin", password, "1.900");
        } catch (CBSException e) {
            assertThat(com.getClientCBS().isLogged()).isFalse();
            assertThat(com.getClientCBS().isConnected()).isTrue();
        }

        try {
            com.authenticateWithLogicalDb(prop.getProperty("MAUVAISEIP"), prop.getProperty("MAUVAISPORT"), "BadLogin", password, "1.900");
        } catch (CBSException e) {
            assertThat(com.getClientCBS().isLogged()).isFalse();
            assertThat(com.getClientCBS().isConnected()).isTrue();
        }
        com.disconnect();

        com.authenticateWithLogicalDb(ip, port, login, password, "1.900");
        com.disconnect();
    }


    @DisplayName("Deconnexion")
    @Test
    void disconnect() throws CBSException {
        ProcessCBS com = new ProcessCBS(poll);
        com.authenticate(ip, port, login, prop.getProperty("connect.password"));
        assertThat(com.getClientCBS().isLogged()).isTrue();
        assertThat(com.getClientCBS().isConnected()).isTrue();
        com.disconnect();
        assertThat(com.getClientCBS().isLogged()).isFalse();
        assertThat(com.getClientCBS().isConnected()).isFalse();
    }

    @DisplayName("Affichage info user")
    @Test
    void affUsa() throws CBSException {
        assertThat(cmd.affUsa()).contains(prop.getProperty("iln.rcrAbes"));
    }

    @DisplayName("Recherche")
    @Test
    void search() throws CBSException {
        //Résultats dans un fichier à part car les .properties ne supportent pas l'UTF-8
        assertThat(cmd.search("che mti testtcn")).contains("LPP23309668X");
        assertThat(cmd.getNbNotices()).isEqualTo(2);

        assertThat(cmd.getListePpn().toString()).isEqualToIgnoringWhitespace(prop.getProperty("search.listPPnResult"));

        assertThat(cmd.search("che mti fdfsdfsfzerzrzrzrzfsdf")).contains("Aucune réponse trouvée");
        assertThat(cmd.getNbNotices()).isEqualTo(0);
        assertThat(cmd.getListePpn().toString()).isEmpty();

    }

    @DisplayName("Noticées liées : REL")
    @Test
    void rel() throws CBSException {
        cmd.search("che ppn 230721486");
        assertThat(cmd.rel()).isEqualTo(2);
    }

    @DisplayName("Notices liées : REL aucune résultat")
    @Test
    void relNoResult() throws CBSException {
        cmd.search("che mti trioptjldksjlk"); //aucune réponse trouvé
        CBSException exception = Assertions.assertThrows(CBSException.class, () -> cmd.rel());
        Assertions.assertEquals("Impossible de lancer la commande rel : pas de lot en cours", exception.getMessage());
    }

    @DisplayName("ILN Rattachement")
    @Test
    void ilnRattachement() throws CBSException {
        assertThat(cmd.ilnRattachement(prop.getProperty("iln.rcrAbes"))).isEqualTo(prop.getProperty("iln.ilnAbes"));
    }

    @DisplayName("Création de notice")
    @Test
    void enregistrerNew() throws CBSException {
        StringBuilder notice = new StringBuilder();
        notice.append("008 $aAax").append(Constants.STR_0D).append("100 0#$a2018").append(Constants.STR_0D).append("101 0#$afre").append(Constants.STR_0D).append("200 0#$atestApiCreation@TEST").append(Constants.STR_0D);

        String resu = cmd.enregistrerNew(notice.toString());
        assertThat(resu).contains("Notice créée");
        /*La suppression ne fonctionne pas en TU car la validation d'une notice prend plusieurs minutes ¯\_(ツ)_/¯
        System.out.println("Suppr : " + cmd.supprimer("1"));*/
    }

    @DisplayName("Modification de notice")
    @Test
    void modifierNotice() throws CBSException {
        //Date du jour
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date date = new Date();

        //Recherche de la notice a modifier
        String notice = noticeTestEnEdition();
        String noticeModifiee = Utilitaire.modifZoneBiblio(notice, "008", "$a", "Abx");
        String resu = cmd.modifierNotice("1", noticeModifiee);

        //On vérifie la date de modification et le contenu de la notice pour voir si la modification a bien fonctionné
        assertThat(resu).contains("008 $aAbx").contains("Modifié: 341720001:" + dateFormat.format(date));

        //2nde modification de la notice pour remise en état précédent
        noticeModifiee = Utilitaire.modifZoneBiblio(notice, "008", "$a", "Aax");
        resu = cmd.modifierNotice("1", noticeModifiee);
        assertThat(resu).contains("008 $aAax").contains("Modifié: 341720001:" + dateFormat.format(date));
    }


    @DisplayName("Affichage d'une notice en xml")
    @Test
    void view() throws CBSException {
        //Recherche de la notice a afficher
        cmd.search("che ppn 230721486");
        cmd.affUnma();
        //Récupération de la notice au format XML
        String resu = cmd.view("1", true, "UNMA");

        //On vérifie que l'on a bien récupéré la bonne notice au format XML
        String xmlResult = new Scanner(Objects.requireNonNull(CommandesTest.class.getResourceAsStream("/viewXml.txt")), "UTF-8").useDelimiter("\\A").next();
        assertThat(resu).contains(xmlResult);

        resu = cmd.view("1", false, "UNMA");
        assertThat(resu).contains("003 https://www.sudoc.fr/230721486");
    }

    @DisplayName("Next()")
    @Test
    void next() throws CBSException {
        cmd.search("che mti ours");
        cmd.affUnma();
        //Vérifier que la 1ere notice est la 17e
        assertThat(cmd.next()).contains("LNR  17");
    }

    @DisplayName("Creation de notice d'autorité")
    @Test
    void enregistrerNewAut() throws CBSException {
        String notice = "008 $aTp1" + Constants.STR_0D + "200 #0$aMonsieurAPI" + Constants.STR_0D;
        String resu = cmd.enregistrerNewAut(notice);
        assertThat(resu).contains("Notice créée");
    }

    @DisplayName("Création d'exemplaire")
    @Test
    void newExemplaire() throws CBSException {
        noticeTestEnEdition();
        //Récupération du prochain numéro d'exemplaire à créer
        String nvExemplaire = cmd.getNvNumEx();
        //Création de l'exemplaire, puis on vérifie que la zone A99 (epn d'un exemplaire) et le numéro de l'exemplaire créé
        assertThat(cmd.newExemplaire(nvExemplaire + " $bx" + Constants.STR_0D + "930 $b341720001$jg")).contains("A99 ").contains(nvExemplaire);
    }

    @DisplayName("Passage en mode édition d'un exemplaire")
    @Test
    void editerExemp() throws CBSException {
        noticeTestEnEdition();
        //Récupération du prochain numéro d'exemplaire à créer
        String numEx = getLastNumEx().substring(1, 3);
        //Si e01, il n'y a donc aucun exemplaire existant sur cette notice, ça va être génant pour en modifier un...
        assertThat(numEx).isNotEqualTo("01");
        //On vérifie que CBS nous retourne bien l'exemplaire passé en édition, donc qu'il contient A99 et son numéro d'exemplaire, signe que tout s'est bien passé
        assertThat(cmd.editerExemplaire(numEx)).contains("A99 ").contains(numEx);
    }

    @DisplayName("Modification d'exemplaire")
    @Test
    void modifierExemp() throws CBSException {
        noticeTestEnEdition();
        String numEx = getLastNumEx().substring(1, 3);
        //On modifie le dernier exemplaire en changeant la valeur de $j en b (sans trop savoir à quoi cela correspond)
        //Puis on regarde dans le retour du CBS si la modif a bien été effectué (et donc si on a $jb)
        assertThat(cmd.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jb", numEx)).contains("$jb");
        //Enfin on remet l'exemplaire dans son état initial ($jg) pour les prochains passages, et on vérifie que cela a bien fonctionné à nouveau
        assertThat(cmd.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jg", numEx)).contains("$jg");
    }

    @DisplayName("Modification d'exemplaire avec construction d'un objet")
    @Test
    void modifierExempAvecContructionObjet() throws CBSException, ZoneException {
        noticeTestEnEdition();
        String exemplaireStr = getLastNumEx() + " $bx";
        Exemplaire exemplaire = new Exemplaire(exemplaireStr);

        exemplaire.addZone("930", "b", "341720001");
        exemplaire.addSousZone("930", "$j", "b");

        String numEx = getLastNumEx().substring(1, 3);
        //On modifie le dernier exemplaire en changeant la valeur de $j en b (sans trop savoir à quoi cela correspond)
        //Puis on regarde dans le retour du CBS si la modif a bien été effectué (et donc si on a $jb)
        assertThat(cmd.modifierExemp(exemplaire.toString(), numEx)).contains("$jb");
        //Enfin on remet l'exemplaire dans son état initial ($jg) pour les prochains passages, et on vérifie que cela a bien fonctionné à nouveau
        assertThat(cmd.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jg", numEx)).contains("$jg");
    }

    @DisplayName("Suppression d'exemplaire")
    @Test
    void supprExep() throws CBSException, InterruptedException {
        noticeTestEnEdition();
        String nvExemplaire = getLastNumEx();
        //Si e01, il n'y a donc aucun exemplaire existant sur cette notice, ça va être génant pour en modifier un...
        assertThat(nvExemplaire).isNotEqualTo("e01");
        //On vérifie qu'après suppression que le numéro d'exemplaire n'apparait plus dans la notice
        assertThat(cmd.supExemplaire(nvExemplaire)).doesNotContain(nvExemplaire);
    }

    @DisplayName("Création puis suppression User")
    @Test
    void creUsa() throws CBSException {
        //Génération d'un login aléatoire, afin d'éviter un username déjà pris
        int nombreAleatoire = 1000000 + (int) (Math.random() * ((9999999 - 1000000) + 1));
        String login = String.valueOf(nombreAleatoire);
        //On crée le tableau user contenant toutes les infos de l'user à créer
        String[] user = {"abes", login, "catalogueur 341720001", "1exercice", "341720001", "FR", "1,1", "L", "N", "Y", "N", "N", "N", "N", "Y", "test"};
        //On crée le user puis on vérifie le retour
        assertThat(cmd.newUsa(user)).contains("notice créée");
        //On le supprime pour ne pas remplir inutilement la base, puis on vérifie
        assertThat(cmd.supUsa(login)).contains("notice supprimée");
    }

    @DisplayName("Set Params")
    @Test
    void setParams() throws CBSException {
        //Tableau des paramètres
        String[] params = {"-", "+", "AUT", "MTI", "K", "KOR", "UNU", "I", "UNM", "9999", "Y", "*", "*", "*", "*", "0", "0", "A", "SYS", "LAN", "YOP", "0", "A"};
        //On vérifie que les params ont bien été mis à jour (le CBS répond ok)
        assertThat(cmd.setParams(params)).contains("03OK");
    }

    @DisplayName("Création puis suppression de donnée locale")
    @Test
    void newLoc() throws CBSException {
        noticeTestEnEdition();
        //On vérifie que la notice modifiée contient bien le nouveau champ L035
        assertThat(cmd.newLoc("L035 $a1")).contains("L035");
        //On vérifie que le champ L035 n'apparait plus dans la notice une fois supprimé
        assertThat(cmd.supLoc()).doesNotContain("L035");
    }

    @DisplayName("Modification donnée locale")
    @Test
    void modLoc() throws CBSException {
        cmd.search("che ppn 23073426X");
        cmd.creerDonneeLocale();
        assertThat(cmd.newLoc("L035 $a1")).contains("L035 ##$a1");
        assertThat(cmd.modLoc("L035 $a2")).contains("L035 ##$a2");
        assertThat(cmd.supLoc()).doesNotContain("L035 ##$a2");
    }

    /**
     * Retourne le dernier numéro d'exemplaire d'une notice (ex : e07 s'il y a 7 exemplaire)
     * Retourne e01 si pas d'exemplaire existant
     *
     * @return Le dernier numéro d'exemplaire, sous forme d'une string (ex : e07)
     */
    String getLastNumEx() {
        String numEx = cmd.getNvNumEx();
        if (!numEx.equals("e01")) {
            int num = Integer.parseInt(numEx.substring(1, 3));
            if (num <= 10) {
                numEx = "e0" + (num - 1);
            } else {
                numEx = "e" + (num - 1);
            }
        }
        return numEx;
    }

    /**
     * teste le passage en mode édition d'une notice et la récupération sous forme d'objet
     */
    @Test
    @DisplayName("test editerNoticeConcrete")
    void editerNoticeConcreteTest() throws CBSException, ZoneException {
        cmd.search("che ppn 23073426X");
        cmd.newLoc("L035 $a123456789");
        cmd.back();
        NoticeConcrete notice = cmd.editerNoticeConcrete("1");
        Assertions.assertEquals(5, notice.getExemplaires().size());

        String biblioExpected = "100 0#$a2018\r" +
                "101 0#$aeng\r" +
                "200 0#$aNotice de test de modification dans API Sudoc@testmodifier Notice\r";
        Assertions.assertTrue(notice.getNoticeBiblio().toString().contains(biblioExpected));

        String donneesLocExpected = "L035 ##$a123456789";
        Assertions.assertTrue(notice.getNoticeLocale().toString().contains(donneesLocExpected));
        cmd.supLoc();
    }

    @Test
    @DisplayName("test edition avec notice concrète")
    void modifierNoticeConcreteTest() throws CBSException, ZoneException {
        //Date du jour
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date date = new Date();

        //Recherche de la notice a modifier
        cmd.search("che ppn 23073426X");
        NoticeConcrete notice = cmd.editerNoticeConcrete("1");
        cmd.back();
        notice.getNoticeBiblio().addSousZone("200", "c", "test");
        String resu = cmd.modifierNoticeConcrete("1", notice);

        //On vérifie la date de modification et le contenu de la notice pour voir si la modification a bien fonctionné
        assertThat(resu).contains("008 $aAax").contains("Modifié: 341720001:" + dateFormat.format(date));

        //2nde modification de la notice pour remise en état précédent
        notice.getNoticeBiblio().deleteSousZone("200", "c");
        resu = cmd.modifierNoticeConcrete("1", notice);
        assertThat(resu).contains("008 $aAax").contains("Modifié: 341720001:" + dateFormat.format(date));
    }

    /**
     * Recherche la notice utilisé pour les tests et la retourne, utile pour factoriser les TU ici
     *
     * @return La notice utilisé pour les tests (ppn 219041989)
     * @throws CBSException si erreur lors de la recherche (pas connecté, pas de résultat, etc.)
     */
    String noticeTestEnEdition() throws CBSException {
        cmd.search("che ppn 23073426X");
        cmd.affUnma();
        return cmd.editer("1");
    }
}

