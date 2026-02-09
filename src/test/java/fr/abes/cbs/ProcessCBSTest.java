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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessCBSTest {

    private ProcessCBS processCBS;
    private Properties prop;
    private String ip;
    private String port;
    private String login;
    private String password;
    @BeforeEach
    void initAll() throws CBSException, IOException {
        prop = new Properties();
        prop.load(Objects.requireNonNull(ProcessCBSTest.class.getResource("/CommandesTest.properties")).openStream());
        this.ip = prop.getProperty("connect.ip");
        this.port = prop.getProperty("connect.port");
        this.login = prop.getProperty("connect.login");
        this.password = prop.getProperty("connect.password");

        processCBS = new ProcessCBS();
        processCBS.authenticate(ip, port, login, password);
    }

    @DisplayName("Connexion ok")
    @Test
    void connect() {
        assertThat(processCBS.getClientCBS().isLogged()).isTrue();
        assertThat(processCBS.getClientCBS().isConnected()).isTrue();
    }

    @DisplayName("Connexion avec mauvais login/password/IP")
    @Test
    void connectBadLogin() throws CBSException, IOException {
        ProcessCBS com = new ProcessCBS();
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
    void connectLogicalBdd() throws CBSException, IOException {
        ProcessCBS com = new ProcessCBS();
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
    void disconnect() throws CBSException, IOException {
        ProcessCBS com = new ProcessCBS();
        com.authenticate(ip, port, login, prop.getProperty("connect.password"));
        assertThat(com.getClientCBS().isLogged()).isTrue();
        assertThat(com.getClientCBS().isConnected()).isTrue();
        com.disconnect();
        assertThat(com.getClientCBS().isLogged()).isFalse();
        assertThat(com.getClientCBS().isConnected()).isFalse();
    }

    @DisplayName("Affichage info user")
    @Test
    void affUsa() throws CBSException, IOException {
        assertThat(processCBS.affUsa()).contains(prop.getProperty("iln.rcrAbes"));
    }

    @DisplayName("Recherche")
    @Test
    void search() throws IOException {
        //Résultats dans un fichier à part car les .properties ne supportent pas l'UTF-8
        assertThat(processCBS.search("che mti testtcn")).contains("LPP23309668X");
        assertThat(processCBS.getNbNotices()).isEqualTo(2);

        assertThat(processCBS.getListePpn().toString()).isEqualToIgnoringWhitespace(prop.getProperty("search.listPPnResult"));

        assertThat(processCBS.search("che mti fdfsdfsfzerzrzrzrzfsdf")).contains("Aucune réponse trouvée");
        assertThat(processCBS.getNbNotices()).isEqualTo(0);
        assertThat(processCBS.getListePpn().toString()).isEmpty();

    }

    @DisplayName("Noticées liées : REL")
    @Test
    void rel() throws CBSException, IOException {
        processCBS.search("che ppn 230721486");
        assertThat(processCBS.rel()).isEqualTo(2);
    }

    @DisplayName("Notices liées : REL aucune résultat")
    @Test
    void relNoResult() throws IOException {
        processCBS.search("che mti trioptjldksjlk"); //aucune réponse trouvé
        CBSException exception = Assertions.assertThrows(CBSException.class, () -> processCBS.rel());
        Assertions.assertEquals("Impossible de lancer la commande rel : pas de lot en cours", exception.getMessage());
    }

    @DisplayName("ILN Rattachement")
    @Test
    void ilnRattachement() throws IOException {
        assertThat(processCBS.ilnRattachement(prop.getProperty("iln.rcrAbes"))).isEqualTo(prop.getProperty("iln.ilnAbes"));
    }

    @DisplayName("Création de notice")
    @Test
    void enregistrerNew() throws CBSException, IOException {
        StringBuilder notice = new StringBuilder();
        notice.append("008 $aAax").append(Constants.STR_0D).append("100 0#$a2018").append(Constants.STR_0D).append("101 0#$afre").append(Constants.STR_0D).append("200 0#$atestApiCreation@TEST").append(Constants.STR_0D);

        String resu = processCBS.enregistrerNew(notice.toString());
        assertThat(resu).contains("Notice créée");
        /*La suppression ne fonctionne pas en TU car la validation d'une notice prend plusieurs minutes ¯\_(ツ)_/¯*/

        //System.out.println("Suppr : " + cmd.supprimer("1"));
    }

    @DisplayName("Modification de notice")
    @Test
    void modifierNotice() throws CBSException, IOException {
        //Date du jour
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date date = new Date();

        //Recherche de la notice a modifier
        String notice = noticeTestEnEdition();
        String noticeModifiee = Utilitaire.modifZoneBiblio(notice, "008", "$a", "Abx");
        String resu = processCBS.modifierNotice("1", noticeModifiee);

        //On vérifie la date de modification et le contenu de la notice pour voir si la modification a bien fonctionné
        assertThat(resu).contains("008 $aAbx").contains("Modifié: 341720001:" + dateFormat.format(date));

        //2nde modification de la notice pour remise en état précédent
        noticeModifiee = Utilitaire.modifZoneBiblio(notice, "008", "$a", "Aax");
        resu = processCBS.modifierNotice("1", noticeModifiee);
        assertThat(resu).contains("008 $aAax").contains("Modifié: 341720001:" + dateFormat.format(date));
    }


    @DisplayName("Affichage d'une notice en xml")
    @Test
    void view() throws CBSException, IOException {
        //Recherche de la notice a afficher
        processCBS.search("che ppn 230721486");
        processCBS.affUnma();
        //Récupération de la notice au format XML
        String resu = processCBS.view("1", true, "UNMA");

        //On vérifie que l'on a bien récupéré la bonne notice au format XML
        String xmlResult = new Scanner(Objects.requireNonNull(ProcessCBSTest.class.getResourceAsStream("/viewXml.txt")), "UTF-8").useDelimiter("\\A").next();
        assertThat(resu).contains(xmlResult);

        resu = processCBS.view("1", false, "UNMA");
        assertThat(resu).contains("003 https://www.sudoc.fr/230721486");
    }

    @DisplayName("Next()")
    @Test
    void next() throws CBSException, IOException {
        processCBS.search("che mti ours");
        processCBS.affUnma();
        //Vérifier que la 1ere notice est la 17e
        assertThat(processCBS.next()).contains("LNR  17");
    }

    @DisplayName("Creation de notice d'autorité")
    @Test
    void enregistrerNewAut() throws CBSException, IOException {
        String notice = "008 $aTp1" + Constants.STR_0D + "200 #0$aMonsieurAPI" + Constants.STR_0D;
        String resu = processCBS.enregistrerNewAut(notice);
        assertThat(resu).contains("Notice créée");
    }

    @DisplayName("Création d'exemplaire")
    @Test
    void newExemplaire() throws CBSException, IOException {
        noticeTestEnEdition();
        //Récupération du prochain numéro d'exemplaire à créer
        String nvExemplaire = processCBS.getNvNumEx();
        //Création de l'exemplaire, puis on vérifie que la zone A99 (epn d'un exemplaire) et le numéro de l'exemplaire créé
        assertThat(processCBS.newExemplaire(nvExemplaire + " $bx" + Constants.STR_0D + "930 $b341720001$jg")).contains("A99 ").contains(nvExemplaire);
    }

    @DisplayName("Passage en mode édition d'un exemplaire")
    @Test
    void editerExemp() throws CBSException, IOException {
        noticeTestEnEdition();
        //Récupération du prochain numéro d'exemplaire à créer
        String numEx = getLastNumEx().substring(1, 3);
        //Si e01, il n'y a donc aucun exemplaire existant sur cette notice, ça va être génant pour en modifier un...
        assertThat(numEx).isNotEqualTo("01");
        //On vérifie que CBS nous retourne bien l'exemplaire passé en édition, donc qu'il contient A99 et son numéro d'exemplaire, signe que tout s'est bien passé
        assertThat(processCBS.editerExemplaire(numEx)).contains("A99 ").contains(numEx);
    }

    @DisplayName("Modification d'exemplaire")
    @Test
    void modifierExemp() throws CBSException, IOException {
        noticeTestEnEdition();
        String numEx = getLastNumEx().substring(1, 3);
        //On modifie le dernier exemplaire en changeant la valeur de $j en b (sans trop savoir à quoi cela correspond)
        //Puis on regarde dans le retour du CBS si la modif a bien été effectué (et donc si on a $jb)
        assertThat(processCBS.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jb", numEx)).contains("$jb");
        //Enfin on remet l'exemplaire dans son état initial ($jg) pour les prochains passages, et on vérifie que cela a bien fonctionné à nouveau
        assertThat(processCBS.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jg", numEx)).contains("$jg");
    }

    @DisplayName("Modification d'exemplaire avec construction d'un objet")
    @Test
    void modifierExempAvecContructionObjet() throws CBSException, ZoneException, IOException {
        noticeTestEnEdition();
        String exemplaireStr = getLastNumEx() + " $bx";
        Exemplaire exemplaire = new Exemplaire(exemplaireStr);

        exemplaire.addZone("930", "b", "341720001");
        exemplaire.addSousZone("930", "$j", "b");

        String numEx = getLastNumEx().substring(1, 3);
        //On modifie le dernier exemplaire en changeant la valeur de $j en b (sans trop savoir à quoi cela correspond)
        //Puis on regarde dans le retour du CBS si la modif a bien été effectué (et donc si on a $jb)
        assertThat(processCBS.modifierExemp(exemplaire.toString(), numEx)).contains("$jb");
        //Enfin on remet l'exemplaire dans son état initial ($jg) pour les prochains passages, et on vérifie que cela a bien fonctionné à nouveau
        assertThat(processCBS.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jg", numEx)).contains("$jg");
    }

    @DisplayName("Suppression d'exemplaire")
    @Test
    void supprExep() throws CBSException, IOException {
        noticeTestEnEdition();
        String nvExemplaire = getLastNumEx();
        //Si e01, il n'y a donc aucun exemplaire existant sur cette notice, ça va être génant pour en modifier un...
        assertThat(nvExemplaire).isNotEqualTo("e01");
        //On vérifie qu'après suppression que le numéro d'exemplaire n'apparait plus dans la notice
        assertThat(processCBS.supExemplaire(nvExemplaire)).doesNotContain(nvExemplaire);
    }

    @DisplayName("Création puis suppression User")
    @Test
    void creUsa() throws CBSException, IOException {
        //Génération d'un login aléatoire, afin d'éviter un username déjà pris
        int nombreAleatoire = 1000000 + (int) (Math.random() * ((9999999 - 1000000) + 1));
        String login = String.valueOf(nombreAleatoire);
        //On crée le tableau user contenant toutes les infos de l'user à créer
        String[] user = {"abes", login, "catalogueur 341720001", "1exercice", "341720001", "FR", "1,1", "L", "N", "Y", "N", "N", "N", "N", "Y", "test"};
        //On crée le user puis on vérifie le retour
        assertThat(processCBS.newUsa(user)).contains("notice créée");
        //On le supprime pour ne pas remplir inutilement la base, puis on vérifie
        assertThat(processCBS.supUsa(login)).contains("notice supprimée");
    }

    @DisplayName("Set Params")
    @Test
    void setParams() throws CBSException, IOException {
        //Tableau des paramètres
        String[] params = {"-", "+", "AUT", "MTI", "K", "KOR", "UNU", "I", "UNM", "9999", "Y", "*", "*", "*", "*", "0", "0", "A", "SYS", "LAN", "YOP", "0", "A"};
        //On vérifie que les params ont bien été mis à jour (le CBS répond ok)
        assertThat(processCBS.setParams(params)).contains("03OK");
    }

    @DisplayName("Création puis suppression de donnée locale")
    @Test
    void newLoc() throws CBSException, IOException {
        noticeTestEnEdition();
        //On vérifie que la notice modifiée contient bien le nouveau champ L035
        assertThat(processCBS.newLoc("L035 $a1")).contains("L035");
        //On vérifie que le champ L035 n'apparait plus dans la notice une fois supprimé
        assertThat(processCBS.supLoc()).doesNotContain("L035");
    }

    @DisplayName("Modification donnée locale")
    @Test
    void modLoc() throws CBSException, IOException {
        processCBS.search("che ppn 23073426X");
        processCBS.creerDonneeLocale();
        assertThat(processCBS.newLoc("L035 $a1")).contains("L035 ##$a1");
        assertThat(processCBS.modLoc("L035 $a2")).contains("L035 ##$a2");
        assertThat(processCBS.supLoc()).doesNotContain("L035 ##$a2");
    }

    /**
     * Retourne le dernier numéro d'exemplaire d'une notice (ex : e07 s'il y a 7 exemplaire)
     * Retourne e01 si pas d'exemplaire existant
     *
     * @return Le dernier numéro d'exemplaire, sous forme d'une string (ex : e07)
     */
    String getLastNumEx() {
        String numEx = processCBS.getNvNumEx();
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
    void editerNoticeConcreteTest() throws CBSException, ZoneException, IOException {
        processCBS.search("che ppn 23073426X");
        processCBS.back();
        NoticeConcrete notice = processCBS.editerNoticeConcrete("1");
        Assertions.assertEquals(5, notice.getExemplaires().size());

        String biblioExpected = "100 0#$a2018\r" +
                "101 0#$aeng\r" +
                "200 0#$aNotice de test de modification dans API Sudoc@testmodifier Notice";
        Assertions.assertTrue(notice.getNoticeBiblio().toString().contains(biblioExpected));
    }

    @Test
    @DisplayName("test edition avec notice concrète")
    void modifierNoticeConcreteTest() throws CBSException, ZoneException, IOException {
        //Date du jour
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date date = new Date();

        //Recherche de la notice a modifier
        processCBS.search("che ppn 23073426X");
        NoticeConcrete notice = processCBS.editerNoticeConcrete("1");
        processCBS.back();
        notice.getNoticeBiblio().addSousZone("200", "c", "test");
        String resu = processCBS.modifierNoticeConcrete("1", notice);

        //On vérifie la date de modification et le contenu de la notice pour voir si la modification a bien fonctionné
        assertThat(resu).contains("008 $aAax").contains("Modifié: 341720001:" + dateFormat.format(date));

        //2nde modification de la notice pour remise en état précédent
        notice.getNoticeBiblio().deleteSousZone("200", "c");
        resu = processCBS.modifierNoticeConcrete("1", notice);
        assertThat(resu).contains("008 $aAax").contains("Modifié: 341720001:" + dateFormat.format(date));
    }

    /**
     * Recherche la notice utilisé pour les tests et la retourne, utile pour factoriser les TU ici
     *
     * @return La notice utilisé pour les tests (ppn 219041989)
     * @throws CBSException si erreur lors de la recherche (pas connecté, pas de résultat, etc.)
     */
    String noticeTestEnEdition() throws CBSException, IOException {
        processCBS.search("che ppn 23073426X");
        processCBS.affUnma();
        return processCBS.editer("1");
    }

    @Test
    @DisplayName("test aff k 003")
    void affkSurNoticeTCN() throws CBSException, IOException {
        processCBS.search("che mti testtcn");
        List<String> result = processCBS.getPpnsFromResultList(2);
        assertThat(result).contains("23309668X");
        assertThat(result).contains("230721486");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("test aff k 003 pagine")
    void affkSurBouquetPagine() throws CBSException, IOException {
        processCBS.search("che bou EUROPRESSE_GLOBAL_BIBESR");
        Integer size = processCBS.rel();
        List<String> result = processCBS.getPpnsFromResultList(size);
        assertThat(result.size()).isEqualTo(size);
    }



    @Test
    void testAffK() throws IOException, CBSException {
        processCBS.search("che bou EUROPRESSE_GLOBAL_BIBESR");
        Integer size = processCBS.rel();
        List<String> ppn = processCBS.getPpnsFromResultList(size);
        ppn.forEach(System.out::println);
    }

}

