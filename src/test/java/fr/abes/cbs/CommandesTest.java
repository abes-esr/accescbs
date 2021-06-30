package fr.abes.cbs;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.process.ProcessCBS;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CommandesTest {

    private ProcessCBS cmd;
    private Properties prop;

    @BeforeEach
    void initAll() throws CBSException {
        prop = new Properties();
        try {
            prop.load(CommandesTest.class.getResource("/CommandesTest.properties").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        cmd = new ProcessCBS();
        cmd.authenticate(prop.getProperty("connect.ip"), prop.getProperty("connect.port"), prop.getProperty("connect.login"), prop.getProperty("connect.password"));
    }

    @DisplayName("Connexion ok")
    @Test
    void connect() {
        assertThat(cmd.getClientCBS().isLogged()).isTrue();
        assertThat(cmd.getClientCBS().isConnected()).isTrue();
    }

    @DisplayName("Connexion avec mauvais login/password/IP")
    @Test
    void connectBadLogin() {
        ProcessCBS com = new ProcessCBS();
        try {
            com.authenticate(prop.getProperty("connect.ip"), prop.getProperty("connect.port"), prop.getProperty("connect.login"), "BadPassword");
        } catch (CBSException e) {
            assertThat(com.getClientCBS().isLogged()).isFalse();
            assertThat(com.getClientCBS().isConnected()).isTrue();
        }
        com.disconnect();

        try {
            com.authenticate(prop.getProperty("connect.ip"), prop.getProperty("connect.port"), "BadLogin", prop.getProperty("connect.password"));
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

    @DisplayName("Deconnexion")
    @Test
    void disconnect() throws CBSException {
        ProcessCBS com = new ProcessCBS();
        com.authenticate(prop.getProperty("connect.ip"), prop.getProperty("connect.port"), prop.getProperty("connect.login"), prop.getProperty("connect.password"));
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
        assertThat(cmd.search("che mti testtcn")).contains("LPP230721486");
        assertThat(cmd.getNbNotices()).isEqualTo(2);

        assertThat(cmd.getListePpn().toString()).isEqualToIgnoringWhitespace(prop.getProperty("search.listPPnResult"));

        assertThat(cmd.search("che mti fdfsdfsfzerzrzrzrzfsdf")).contains("Aucune réponse trouvée");
        assertThat(cmd.getNbNotices()).isEqualTo(0);
        assertThat(cmd.getListePpn().toString()).isEmpty();

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
        String xmlResult = new Scanner(CommandesTest.class.getResourceAsStream("/viewXml.txt"), "UTF-8").useDelimiter("\\A").next();
        assertThat(resu).contains(xmlResult);

        resu = cmd.view("1", false, "UNMA");
        assertThat(resu).contains("003 http://www.sudoc.fr/230721486");
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
        if (numEx.equals("01"))
            log.error("Pas d'exemplaire sur la notice 219041989, impossible d'en modifier/supprimer un");
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

    @DisplayName("Suppression d'exemplaire")
    @Test
    void supprExep() throws CBSException, InterruptedException {
        noticeTestEnEdition();
        String nvExemplaire = getLastNumEx();
        //Si e01, il n'y a donc aucun exemplaire existant sur cette notice, ça va être génant pour en modifier un...
        if (nvExemplaire.equals("e01"))
            log.error("Pas d'exemplaire sur la notice 23073426X, impossible d'en modifier/supprimer un");
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


    @DisplayName("Translitération")
    @Test
    void transliterer() throws CBSException {
        cmd.search("che ppn 026946769");
        String notice = cmd.editerJCBS("1");
        notice = notice.replace("200 #1$601$7ba$aKazantzakīs$bNikos$f1883-1957" + Constants.STR_0D, "");
        notice = notice.replace("200 #1$a", "200 #1$601$7ga$a");
        notice = notice.replace("#1$601$7ga$aΚαζαντζακης$bΝικος$f1883-1957", "#1$601$7ga$aΚαζαντζακης$bΝικος$f1883-1957" + Constants.STR_0D + "200 #1$601$7ba");
        String resu = Utilitaire.recupNoticeBib(cmd.transliterer(notice));
        assertThat(cmd.enregistrer(resu.substring(17))).contains("#1$601$7ba$aKazantzakīs$bNikos$f1883-1957");
    }

/*    @DisplayName("Translitération sans PPN")
    @Test
    @Disabled
    void translitererSansPPN() throws CBSException {
        cmd.search("che ppn 026946769");
        String notice = cmd.editerJCBS("1");
        notice = notice.replace("200 #1$601$7ba$aKazantzakīs$bNikos$f1883-1957" + Constants.STR_0D, "");
        notice = notice.replace("200 #1$a", "200 #1$601$7ga$a");
        notice = notice.replace("#1$601$7ga$aΚαζαντζακης$bΝικος$f1883-1957", "#1$601$7ga$aΚαζαντζακης$bΝικος$f1883-1957" + Constants.STR_0D + "200 #1$601$7ba");
        String resu = Utilitaire.recupNoticeBib(cmd.translitererSansPPN(notice));
        assertThat(cmd.enregistrer(resu.substring(17))).contains("#1$601$7ba$aKazantzakīs$bNikos$f1883-1957");
    }*/

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
    private String getLastNumEx() {
        String numEx = cmd.getNvNumEx();
        if (!numEx.equals("e01")) {
            int num = Integer.parseInt(numEx.substring(1, 3));
            if (num <= 10) {
                numEx = "e0" + String.valueOf(num - 1);
            } else {
                numEx = "e" + String.valueOf(num - 1);
            }
        }
        return numEx;
    }

    /**
     * Recherche la notice utilisé pour les tests et la retourne, utile pour factoriser les TU ici
     *
     * @return La notice utilisé pour les tests (ppn 219041989)
     * @throws CBSException si erreur lors de la recherche (pas connecté, pas de résultat, etc.)
     */
    private String noticeTestEnEdition() throws CBSException {
        cmd.search("che ppn 23073426X");
        cmd.affUnma();
        return cmd.editer("1");
    }
}

