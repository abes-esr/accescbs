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
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

class CommandesTest {

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
    void connectBadLogin() throws CBSException {
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
        //R??sultats dans un fichier ?? part car les .properties ne supportent pas l'UTF-8
        assertThat(cmd.search("che mti testtcn")).contains("LPP230721486");
        assertThat(cmd.getNbNotices()).isEqualTo(2);

        assertThat(cmd.getListePpn().toString()).isEqualToIgnoringWhitespace(prop.getProperty("search.listPPnResult"));

        assertThat(cmd.search("che mti fdfsdfsfzerzrzrzrzfsdf")).contains("Aucune re??ponse trouve??e");
        assertThat(cmd.getNbNotices()).isEqualTo(0);
        assertThat(cmd.getListePpn().toString()).isEmpty();

    }

    @DisplayName("ILN Rattachement")
    @Test
    void ilnRattachement() throws CBSException {
        assertThat(cmd.ilnRattachement(prop.getProperty("iln.rcrAbes"))).isEqualTo(prop.getProperty("iln.ilnAbes"));
    }

    @DisplayName("Cr??ation de notice")
    @Test
    void enregistrerNew() throws CBSException {
        StringBuilder notice = new StringBuilder();
        notice.append("008 $aAax").append(Constants.STR_0D).append("100 0#$a2018").append(Constants.STR_0D).append("101 0#$afre").append(Constants.STR_0D).append("200 0#$atestApiCreation@TEST").append(Constants.STR_0D);

        String resu = cmd.enregistrerNew(notice.toString());
        assertThat(resu).contains("Notice cre??e??e");
        /*La suppression ne fonctionne pas en TU car la validation d'une notice prend plusieurs minutes ??\_(???)_/??
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

        //On v??rifie la date de modification et le contenu de la notice pour voir si la modification a bien fonctionn??
        assertThat(resu).contains("008 $aAbx").contains("Modifie??: 341720001:" + dateFormat.format(date));

        //2nde modification de la notice pour remise en ??tat pr??c??dent
        noticeModifiee = Utilitaire.modifZoneBiblio(notice, "008", "$a", "Aax");
        resu = cmd.modifierNotice("1", noticeModifiee);
        assertThat(resu).contains("008 $aAax").contains("Modifie??: 341720001:" + dateFormat.format(date));
    }


    @DisplayName("Affichage d'une notice en xml")
    @Test
    void view() throws CBSException, UnsupportedEncodingException {
        //Recherche de la notice a afficher
        cmd.search("che ppn 230721486");
        cmd.affUnma();
        //R??cup??ration de la notice au format XML
        String resu = cmd.view("1", true, "UNMA");

        //On v??rifie que l'on a bien r??cup??r?? la bonne notice au format XML
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
        //V??rifier que la 1ere notice est la 17e
        assertThat(cmd.next()).contains("LNR  17");
    }

    @DisplayName("Creation de notice d'autorit??")
    @Test
    void enregistrerNewAut() throws CBSException {
        String notice = "008 $aTp1" + Constants.STR_0D + "200 #0$aMonsieurAPI" + Constants.STR_0D;
        String resu = cmd.enregistrerNewAut(notice);
        assertThat(resu).contains("Notice cre??e??e");
    }

    @DisplayName("Cr??ation d'exemplaire")
    @Test
    void newExemplaire() throws CBSException {
        noticeTestEnEdition();
        //R??cup??ration du prochain num??ro d'exemplaire ?? cr??er
        String nvExemplaire = cmd.getNvNumEx();
        //Cr??ation de l'exemplaire, puis on v??rifie que la zone A99 (epn d'un exemplaire) et le num??ro de l'exemplaire cr????
        assertThat(cmd.newExemplaire(nvExemplaire + " $bx" + Constants.STR_0D + "930 $b341720001$jg")).contains("A99 ").contains(nvExemplaire);
    }

    @DisplayName("Passage en mode ??dition d'un exemplaire")
    @Test
    void editerExemp() throws CBSException {
        noticeTestEnEdition();
        //R??cup??ration du prochain num??ro d'exemplaire ?? cr??er
        String numEx = getLastNumEx().substring(1, 3);
        //Si e01, il n'y a donc aucun exemplaire existant sur cette notice, ??a va ??tre g??nant pour en modifier un...
        assertThat(numEx).isNotEqualTo("01");
        //On v??rifie que CBS nous retourne bien l'exemplaire pass?? en ??dition, donc qu'il contient A99 et son num??ro d'exemplaire, signe que tout s'est bien pass??
        assertThat(cmd.editerExemplaire(numEx)).contains("A99 ").contains(numEx);
    }

    @DisplayName("Modification d'exemplaire")
    @Test
    void modifierExemp() throws CBSException {
        noticeTestEnEdition();
        String numEx = getLastNumEx().substring(1, 3);
        //On modifie le dernier exemplaire en changeant la valeur de $j en b (sans trop savoir ?? quoi cela correspond)
        //Puis on regarde dans le retour du CBS si la modif a bien ??t?? effectu?? (et donc si on a $jb)
        assertThat(cmd.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jb", numEx)).contains("$jb");
        //Enfin on remet l'exemplaire dans son ??tat initial ($jg) pour les prochains passages, et on v??rifie que cela a bien fonctionn?? ?? nouveau
        assertThat(cmd.modifierExemp(getLastNumEx() + " $bx" + Constants.STR_0D + "930 $b341720001$jg", numEx)).contains("$jg");
    }

    @DisplayName("Suppression d'exemplaire")
    @Test
    void supprExep() throws CBSException, InterruptedException {
        noticeTestEnEdition();
        String nvExemplaire = getLastNumEx();
        //Si e01, il n'y a donc aucun exemplaire existant sur cette notice, ??a va ??tre g??nant pour en modifier un...
        assertThat(nvExemplaire).isNotEqualTo("e01");
        //On v??rifie qu'apr??s suppression que le num??ro d'exemplaire n'apparait plus dans la notice
        assertThat(cmd.supExemplaire(nvExemplaire)).doesNotContain(nvExemplaire);
    }

    @DisplayName("Cr??ation puis suppression User")
    @Test
    void creUsa() throws CBSException {
        //G??n??ration d'un login al??atoire, afin d'??viter un username d??j?? pris
        int nombreAleatoire = 1000000 + (int) (Math.random() * ((9999999 - 1000000) + 1));
        String login = String.valueOf(nombreAleatoire);
        //On cr??e le tableau user contenant toutes les infos de l'user ?? cr??er
        String[] user = {"abes", login, "catalogueur 341720001", "1exercice", "341720001", "FR", "1,1", "L", "N", "Y", "N", "N", "N", "N", "Y", "test"};
        //On cr??e le user puis on v??rifie le retour
        assertThat(cmd.newUsa(user)).contains("notice cre??e??e");
        //On le supprime pour ne pas remplir inutilement la base, puis on v??rifie
        assertThat(cmd.supUsa(login)).contains("notice supprime??e");
    }

    @DisplayName("Set Params")
    @Test
    void setParams() throws CBSException {
        //Tableau des param??tres
        String[] params = {"-", "+", "AUT", "MTI", "K", "KOR", "UNU", "I", "UNM", "9999", "Y", "*", "*", "*", "*", "0", "0", "A", "SYS", "LAN", "YOP", "0", "A"};
        //On v??rifie que les params ont bien ??t?? mis ?? jour (le CBS r??pond ok)
        assertThat(cmd.setParams(params)).contains("03OK");
    }


    @DisplayName("Translit??ration")
    @Test
    void transliterer() throws CBSException {
        cmd.search("che ppn 026946769");
        String notice = cmd.editerJCBS("1");
        notice = notice.replace("200 #1$601$7ba$aKazantzaki??s$bNikos$f1883-1957" + Constants.STR_0D, "");
        notice = notice.replace("200 #1$a", "200 #1$601$7ga$a");
        notice = notice.replace("#1$601$7ga$a??????????????????????$b??????????$f1883-1957", "#1$601$7ga$a??????????????????????$b??????????$f1883-1957" + Constants.STR_0D + "200 #1$601$7ba");
        String resu = Utilitaire.recupNoticeBib(cmd.transliterer(notice));
        assertThat(cmd.enregistrer(resu.substring(17))).contains("#1$601$7ba$aKazantzaki??s$bNikos$f1883-1957");
    }

/*    @DisplayName("Translit??ration sans PPN")
    @Test
    @Disabled
    void translitererSansPPN() throws CBSException {
        cmd.search("che ppn 026946769");
        String notice = cmd.editerJCBS("1");
        notice = notice.replace("200 #1$601$7ba$aKazantzaki??s$bNikos$f1883-1957" + Constants.STR_0D, "");
        notice = notice.replace("200 #1$a", "200 #1$601$7ga$a");
        notice = notice.replace("#1$601$7ga$a??????????????????????$b??????????$f1883-1957", "#1$601$7ga$a??????????????????????$b??????????$f1883-1957" + Constants.STR_0D + "200 #1$601$7ba");
        String resu = Utilitaire.recupNoticeBib(cmd.translitererSansPPN(notice));
        assertThat(cmd.enregistrer(resu.substring(17))).contains("#1$601$7ba$aKazantzaki??s$bNikos$f1883-1957");
    }*/

    @DisplayName("Cr??ation puis suppression de donn??e locale")
    @Test
    void newLoc() throws CBSException {
        noticeTestEnEdition();
        //On v??rifie que la notice modifi??e contient bien le nouveau champ L035
        assertThat(cmd.newLoc("L035 $a1")).contains("L035");
        //On v??rifie que le champ L035 n'apparait plus dans la notice une fois supprim??
        assertThat(cmd.supLoc()).doesNotContain("L035");
    }

    @DisplayName("Modification donn??e locale")
    @Test
    void modLoc() throws CBSException {
        cmd.search("che ppn 23073426X");
        cmd.creerDonneeLocale();
        assertThat(cmd.newLoc("L035 $a1")).contains("L035 ##$a1");
        assertThat(cmd.modLoc("L035 $a2")).contains("L035 ##$a2");
        assertThat(cmd.supLoc()).doesNotContain("L035 ##$a2");
    }

    /**
     * Retourne le dernier num??ro d'exemplaire d'une notice (ex : e07 s'il y a 7 exemplaire)
     * Retourne e01 si pas d'exemplaire existant
     *
     * @return Le dernier num??ro d'exemplaire, sous forme d'une string (ex : e07)
     */
    private String getLastNumEx() {
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
     * Recherche la notice utilis?? pour les tests et la retourne, utile pour factoriser les TU ici
     *
     * @return La notice utilis?? pour les tests (ppn 219041989)
     * @throws CBSException si erreur lors de la recherche (pas connect??, pas de r??sultat, etc.)
     */
    private String noticeTestEnEdition() throws CBSException {
        cmd.search("che ppn 23073426X");
        cmd.affUnma();
        return cmd.editer("1");
    }
}

