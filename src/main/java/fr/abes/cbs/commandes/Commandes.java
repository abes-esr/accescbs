package fr.abes.cbs.commandes;

import fr.abes.cbs.connector.Cbs;
import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.utilitaire.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Commandes {
    @Getter
    @Setter
    private boolean isLogged;
    @Getter
    @Setter
    private boolean isConnected;
    private Cbs connector;

    @Getter
    private String rcr = "";

    public Commandes() {
        connector = new Cbs();
    }

    /**
     * retourne le résultat de la dernière commande lancée
     *
     * @return true si commande ok, false sinon
     */
    public boolean isCmdOk() {
        return connector.isCmdOk();
    }

    /**
     * met à jour la valeur de cmdOk dans connector
     *
     * @param cmdOk booléen correspondant au retour de la commande précèdente (OK ou NOK)
     */
    public void setCmdOk(boolean cmdOk) {
        connector.setCmdOk(cmdOk);
    }

    /**
     * Log un utilisateur au CBS
     *
     * @param login le login
     * @param pwd    le mot de passe
     * @return le message renvoyé par le serveur suite à l'authentification
     */
    public String log(final String login, final String pwd) throws CBSException {

        String query = new StringBuilder(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append("CPCSCR 00").toString();
        String resu = connector.tcpReq(query);
        if (!connector.isCmdOk()) {
            isLogged = false;
            return resu;
        }
        resu = connector.tcpReq("CUS\\LOG " + login + " " + pwd);
        if (!connector.isCmdOk()) {
            isLogged = false;
            return resu;
        }
        if ("M03".equals(resu.substring(0, 3)) || "02".equals(resu.substring(0, 2))) {
            isLogged = false;
            return resu;
        }
        isLogged = true;
        return resu;
    }

    /**
     * Ouvre la session TCPIP
     *
     * @param tip   adresse ip du serveur CBS
     * @param port1 port de connexion
     * @return Le message renvoyé par le serveur
     */
    public String connect(String tip, int port1) {
        String errorMsg = connector.connect(tip, port1);
        isConnected = errorMsg.isEmpty();
        return errorMsg;
    }

    /**
     * Ferme la session TCPIP
     */
    public void disconnect() {
        connector.close();
        isConnected = false;
        isLogged = false;
    }

    /**
     * Retourne le message d'erreur de la dernière commande CBS
     *
     * @return Message d'erreur
     */
    public String getErrorMessage() {
        return connector.getErrorMessage();
    }

    /**
     * met à jour la valeur de errorMessage dans connector
     *
     * @param errorMessage Message d'erreur
     */
    public void setErrorMessage(String errorMessage) {
        connector.setErrorMessage(errorMessage);
    }

    /**
     * Visualiser une notice parametres: no de record dans le liste
     * courte,retour en xml ou natif,format de recup:UNMA, UNX..
     *
     * @param noLigne    Numéro de la notice dans la liste
     * @param lotEncours lot en cours
     * @param fOrigine   format d'origine de la notice
     * @return la notice
     */
    public String view(final String noLigne, final String lotEncours, final String fOrigine) throws CBSException {
        String query = new StringBuilder().append(Constants.VTAFR).append(Constants.STR_1D)
                .append(Constants.VCUTF8).append(Constants.STR_1D).append(Constants.CUSTOO).append(lotEncours)
                .append("    ").append(noLigne).append(" ").append(fOrigine).toString();
        return connector.tcpReq(query);
    }

    /**
     * Affiche la liste des resultats suivants après une première recherche
     *
     * @param lotEncours lot sur lequel doit s'appliquer le next
     * @param pos        Position dans la liste
     * @return
     */
    public String next(final String lotEncours, final int pos) throws CBSException {
        String query = new StringBuilder().append("CUS\\too s").append(lotEncours).append(" ").append(pos)
                .append(" K").toString();
        return connector.tcpReq(query);
    }

    /**
     * Enregistrer une notice en edit
     *
     * @param notice Notice au format natif
     * @return Retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String valMod(final String notice, final int lgnotice, String lotEncours, String ppnEncours,
                         String noRecordEnEdit, String noticedeb, String leact) throws CBSException {

        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CMPPN).append(ppnEncours).append(Constants.UNMTOO).append(lotEncours).append(" ")
                .append(noRecordEnEdit).append(" ").append(Constants.UNMA).append(Constants.STR_1D)
                .append(Constants.BBWVDA0).append(Constants.STR_1E).append(Constants.VTX0T).append(noticedeb)
                .append(Constants.STR_1F).append("D").append(lgnotice).append(Constants.STR_1F).append("I").append(leact)
                .append(notice).append(Constants.STR_0D).append(Constants.STR_0D).append(Constants.STR_0D).append(Constants.STR_1F).append(Constants.STR_1E).toString();
        return connector.tcpReq(query);
    }


    /**
     * Transliterer une notice
     *
     * @param notice         Notice au format natif
     * @param leact          ??
     * @param timeStpEnCours timestamp
     * @param ppnEncours     ppn de la notice
     * @return Notice translitérée
     * @throws CBSException Erreur CBS
     */
    public String transliterer(final String notice, String leact, String timeStpEnCours, String lotEnCours, String ppnEncours) throws CBSException {

        int lettsp = Integer.parseInt(timeStpEnCours.substring(0, 4));
        lettsp--;

        String query = new StringBuilder().append(Constants.VSE).append(lotEnCours).append(Constants.STR_1D).append(Constants.VTI1)
                .append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR)
                .append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append("CPC\\TRA \\PPN ")
                .append(ppnEncours.replaceAll(Constants.BSA, "")).append(" ").append(Constants.UNM).append(Constants.STR_1D).append(Constants.BBWVDA0).append(Constants.STR_1E).append(Constants.VTX0TBIB)
                .append(timeStpEnCours).append(Constants.STR_1F).append("D").append(lettsp).append(Constants.STR_1F).append("I").append(leact).append(notice).append(Constants.STR_1F).append(Constants.STR_1E).append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Transliterer une notice, sans PPN
     *
     * @param notice Notice au format natif
     * @return Notice translitérée
     * @throws CBSException Erreur CBS
     */
    public String translitererSansPPN(final String notice) throws CBSException {
        String query = new StringBuilder().append(Constants.VTAFR).append(Constants.STR_1D)
                .append(Constants.VCUTF8).append(Constants.STR_1D).append("CUScre e").toString();
        String resu = connector.tcpReq(query);
        if (connector.isCmdOk() && connector.getErrorMessage().isEmpty() && resu.contains("VOK")) {
            query = new StringBuilder().append(Constants.VSE3).append(Constants.STR_1D).append(Constants.VTI1)
                    .append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR)
                    .append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                    .append("CPC\\TRA \\2 UNM").append(Constants.STR_1D).append("BBWVDA0").append(Constants.STR_1E).append("VTX0TBIB000200:00:00.000").append(Constants.STR_1F).append("D").append("1").append(Constants.STR_1F).append("I").append(notice)
                    .append(Constants.FINLIGNE).toString();

            return connector.tcpReq(query);

        }
        return resu;
    }

    /**
     * Suppression d'une notice
     *
     * @param nonotice   Numéro de notice dans la liste
     * @param lotEncours lot en cours
     * @param ppnEncours ppn de la notice
     * @return Retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String sup(final String nonotice, String lotEncours, String ppnEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VTAFR).append(Constants.STR_1D)
                .append(Constants.VCUTF8).append(Constants.STR_1D).append(Constants.CKPPN).append(ppnEncours)
                .append(";\\TOO S").append(lotEncours).append(" ").append(nonotice).append(" ").append(Constants.UNMA).toString();
        return connector.tcpReq(query);
    }

    /**
     * lance la commande sup l
     *
     * @param lotEncours
     * @return retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String supL(String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VSE).append(lotEncours)
                .append(Constants.STR_1D).append(Constants.VPRUNM).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append("CUSsup l").toString();
        return connector.tcpReq(query);
    }

    /**
     * Validation de la suppression d'une donnée locale
     *
     * @return retour du CBS
     * @throws CBSException
     */
    public String valSupL(String lotEncours, String ppnEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D).append(Constants.VTI1).append(Constants.STR_1D)
                .append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append("CPC\\KIL \\PPN ").append(ppnEncours).append(" L;\\TOO S").append(lotEncours).append(" 1 ").append(Constants.UNM).append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Permet de passer des parametres par defaut (utilisé dans wini par un
     * ecran de parametres)
     *
     * @param params Tableau de paramètres
     * @return Retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String setParams(final String[] params) throws CBSException {
        String triResultats = params[0];
        String triVol = params[1];
        String cleBal = params[2];
        String cleRech = params[3];
        String formatList = params[4];
        String formatListPEB = params[5];
        String formatDetail = params[6];
        String formatUnload = params[7];
        String formatModif = params[8];
        String nbResuAut = params[9];
        String reinitLim = params[10];
        String numLots = params[11];
        String rechNotLies = params[12];
        String triAuto = params[13];
        String affautoBU = params[14];
        String inconnu1 = params[15];
        String telechFormat = params[16];
        String affExemplaires = params[17];
        String inconnu2 = params[18];
        String formatDetailPEB = params[19];
        String triresults = params[20];
        String forceCollation = params[21];
        String erreurval = params[22];

        String query = new StringBuilder().append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D)
                .append(Constants.VCUTF8).append(Constants.STR_1D).append("CPC\\MUT \\PAR").append(Constants.STR_1D)
                .append("BPAVSS0").append(triResultats).append(Constants.STR_1E)
                .append("VMP0").append(triVol).append(Constants.STR_1E)
                .append("VMS0").append(cleBal).append(Constants.STR_1E)
                .append("VMZ0").append(cleRech).append(Constants.STR_1E)
                .append("VDK0").append(formatList).append(Constants.STR_1E)
                .append("VAK0").append(formatListPEB).append(Constants.STR_1E)
                .append("VDL0").append(formatDetail).append(Constants.STR_1E)
                .append("VDD0").append(formatUnload).append(Constants.STR_1E)
                .append("VDB0").append(formatModif).append(Constants.STR_1E)
                .append("VLI0").append(nbResuAut).append(Constants.STR_1E)
                .append("VRE0").append(reinitLim).append(Constants.STR_1E)
                .append("VNR0").append(numLots).append(Constants.STR_1E)
                .append("VZR0").append(rechNotLies).append(Constants.STR_1E)
                .append("VSB0").append(triAuto).append(Constants.STR_1E)
                .append("VSL0").append(affautoBU).append(Constants.STR_1E)
                .append("VEK0").append(inconnu1).append(Constants.STR_1E)
                .append("VMI0").append(telechFormat).append(Constants.STR_1E)
                .append("VCM0").append(affExemplaires).append(Constants.STR_1E)
                .append("VFC0").append(inconnu2).append(Constants.STR_1E)
                .append("VAL0").append(formatDetailPEB).append(Constants.STR_1E)
                .append("VSM0").append(triresults).append(Constants.STR_1E)
                .append("VCS0").append(forceCollation).append(Constants.STR_1E)
                .append("VVR0").append(erreurval).append(Constants.STR_1E)
                .append(Constants.STR_1D)
                .toString();

        return connector.tcpReq(query);
    }

    /**
     * envoi la commande "aff bib + n° rcr"
     *
     * @param rcr le rcr concerné
     * @return l'ILN de rattachement
     */
    public String affBib(final String rcr) throws CBSException {
        String query = new StringBuilder().append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR)
                .append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append("CUSaff bib ")
                .append(rcr).append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Envoie la commande CHE
     *
     * @param req Requête CHE
     * @return retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String che(String req) throws CBSException {
        String query = new StringBuilder().append(Constants.CUTF8).append(Constants.STR_1D).append(Constants.VSE1)
                .append(Constants.STR_1D).append(Constants.VPRK).append(Constants.STR_1D).append(Constants.VT1)
                .append(Constants.STR_1D).append(Constants.VTI1).append(Constants.STR_1D).append(" ")
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append("CUS").append(req).toString();
        return connector.tcpReq(query);
    }

    /**
     * Lance la commande cre exx
     *
     * @param numEx      : numéro de l'exemplaire à créer
     * @param lotEncours : lot en cours
     * @return retour du cbs
     * @throws CBSException Erreur CBS
     */
    public String creE(String numEx, String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VSE)
                .append(lotEncours).append(Constants.STR_1D).append(Constants.VPRUNM).append(Constants.STR_1D)
                .append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D)
                .append("CUScre e").append(numEx).toString();
        return connector.tcpReq(query);
    }

    /**
     * Lance la commande cre l
     *
     * @param lotEncours : lot en cours
     * @Return retour du cbs
     * @Throws CBSException erreur CBS
     */
    public String creL(String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VSE)
                .append(lotEncours).append(Constants.STR_1D).append(Constants.VPRUNM).append(Constants.STR_1D)
                .append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D)
                .append(Constants.VCUTF8).append(Constants.STR_1D).append("CUScre l").toString();
        return connector.tcpReq(query);
    }

    /**
     * Passe en édition un exemplaire
     *
     * @param numEx : numéro de l'exemplaire à modifier
     * @return : exemplaire sélectionné
     * @throws CBSException Erreur CBS
     */
    public String modE(String numEx, String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VSE)
                .append(lotEncours).append(Constants.STR_1D).append(Constants.VPRUNM).append(Constants.STR_1D)
                .append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D)
                .append("CUSmod e").append(numEx).toString();
        return connector.tcpReq(query);

    }

    /**
     * Créée une notice dans le CBS envoi la commande "cre" renseigne le message
     * renvoyé par le CBS suite à la création renseigne le ppn de la notice
     * créée dans PpnEncours si la création a réussi
     *
     * @param notice la notice à créer au format natif (pica)
     * @param flag   : 1 pour biblio, 2 pour autorités
     * @return le message renvoyé par le CBS suite à la création
     * @throws CBSException Erreur CBS
     */
    public String cre(String notice, int flag) throws CBSException {
        String query = new StringBuilder().append("CPC\\INV \\").append(flag).append(" UNM").append(Constants.STR_1D).append(Constants.BBWVTX0T).append("BIB000200:00:00.000").append(Constants.STR_1F)
                .append("D1").append(Constants.STR_1F).append("I").append(notice).append(Constants.FINLIGNE).toString();
        return connector.tcpReq(query);
    }

    /**
     * Rajoute un exemplaire à une notice Envoi de la commande cre e
     * n°exemplaire renvoie dans ErrorMessage, le message renvoyé par le CBS
     * suite au rajout de l'exemplaire modifie la valeur de CmdOk : true si
     * succès - false si échec renseigne le ppn de la notice créée dans
     * PpnEncours si la création a réussi
     *
     * @param exemplaire le nouveau num. d'exemplaire en concaténant "e" et NvNumEx
     * @return le message renvoyé par le CBS suite à la création de l'exemplaire
     * @throws CBSException Erreur CBS
     */
    public String valCreE(String exemplaire, String lotEncours, String ppnEncours) throws CBSException {
        String query = new StringBuilder("VSE").append(lotEncours).append(Constants.STR_1D).append("VTI1").append(Constants.STR_1D)
                .append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CIPPN).append(ppnEncours).append(" E").append(exemplaire.substring(1, 3)).append(Constants.UNMTOO)
                .append(lotEncours).append(" 1 UNM").append(Constants.STR_1D).append(Constants.BBWVTX0T).append("E").append(exemplaire.substring(1, 3)).append("000200:00:00.000")
                .append(Constants.STR_1F).append("D1").append(Constants.STR_1F).append("I").append(exemplaire).append(Constants.STR_0D)
                .append(Constants.STR_0D).append(Constants.STR_1F).append(Constants.STR_1E).toString();
        return connector.tcpReq(query);
    }

    /**
     * validation de la modification d'un exemplaire
     *
     * @param exemplaire l'exemplaire à modifier
     * @param numEx      Numéro d'exemplaire
     * @param lotEncours lot en cours
     * @param noticedeb  ??
     * @param ppnEncours ppn de la notice
     * @param lgexemp    ??
     * @return le message renvoyé par le CBS suite à la modification
     * @throws CBSException Erreur CBS
     */
    public String valModE(String exemplaire, String numEx, String lotEncours, String noticedeb, String ppnEncours,
                          int lgexemp) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CMPPN).append(ppnEncours).append(" E").append(numEx).append(Constants.UNMTOO)
                .append(lotEncours).append(" 1 ").append(Constants.UNMA).append(Constants.STR_1D)
                .append(Constants.BBWVTX0T).append("E").append(noticedeb).append(Constants.STR_1F)
                .append("D").append(lgexemp).append(Constants.STR_1F).append("I").append(exemplaire)
                .append(Constants.STR_0D).append(Constants.STR_1F).append(Constants.STR_1E).append(Constants.STR_1D)
                .toString();
        return connector.tcpReq(query);
    }

    /**
     * envoie echap au cbs
     *
     * @return
     */
    public String back(String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CUSTOO).append(lotEncours).append(" 1 ").append(Constants.UNM).append(Constants.STR_1D)
                .toString();
        return connector.tcpReq(query);
    }

    /**
     * valide la commande cre usa
     *
     * @param user Tableau contenant les infos utilisateur
     * @return Retour CBS
     * @throws CBSException Erreur CBS
     */
    public String creUsa(final String[] user) throws CBSException {
        String login = user[1];
        String group = user[0];
        String shortname = user[2];
        String passwd = user[3];
        String library = user[4];
        String language = user[5];
        String dbid = user[6];
        String usertype = user[7];
        String identify = user[8];
        String allowed = user[9];
        String sysmanager = user[10];
        String orgmanager = user[11];
        String depmanager = user[12];
        String ccholder = user[13];
        String logstats = user[14];
        String comment = (user.length == 16) ? user[15] : "";
        String usa = new StringBuilder().append(login).append(Constants.STR_1D).append("BUAVU20").append(group).append(Constants.STR_1E)
                .append("VU10").append(login).append(Constants.STR_1E).append("VU60").append(shortname).append(Constants.STR_1E)
                .append("VU40").append(passwd).append(Constants.STR_1E).append("VU30").append(library).append(Constants.STR_1E)
                .append("VU70").append(language).append(Constants.STR_1E).append("VU80").append(Constants.STR_1E).append("VUR0").append(dbid).append(Constants.STR_1E)
                .append("VU90").append(identify).append(Constants.STR_1E).append("VUA0").append(allowed).append(Constants.STR_1E).append("VUC0").append(sysmanager).append(Constants.STR_1E)
                .append("VUE0").append(orgmanager).append(Constants.STR_1E).append("VUF0").append(depmanager).append(Constants.STR_1E).append("VUG0").append(usertype).append(Constants.STR_1E)
                .append("VUI0").append(Constants.STR_1E).append("VUT0").append(ccholder).append(Constants.STR_1E).append("VUM0").append(logstats).append(Constants.STR_1E)
                .append("VUH0").append(Constants.STR_1E).append("VUO0").append(Constants.STR_1E).append("VUS0").append(comment).append(Constants.STR_1E).toString();
        String query = new StringBuilder().append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR)
                .append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append("CPC\\INV \\USE ")
                .append(usa).append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Supprime un utilisateur
     *
     * @param user Username
     * @return Retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String valSupUsa(String user) throws CBSException {
        String query = new StringBuilder().append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append(Constants.CPCWIS).append(user).toString();
        return connector.tcpReq(query);
    }

    /**
     * Affiche infos user
     *
     * @return Infos user
     * @throws CBSException Erreur CBS
     */
    public String affUsa() throws CBSException {
        String query = new StringBuilder().append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR)
                .append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append(Constants.CUS).append("aff usa").append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Passe une notice en cours en format Unimarc envoi de la commande "aff
     * format" renvoie dans ErrorMessage, le message renvoyé par le CBS suite au
     * changement de format modifie la valeur de CmdOk : true si succès - false
     * si échec
     *
     * @param format format à afficher
     * @return la notice au format unimarc
     * @throws CBSException Erreur CBS
     */
    public String affFormat(String format, String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VPRI).append(Constants.STR_1D)
                .append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D)
                .append(Constants.VCUTF8).append(Constants.STR_1D).append("CUSaff ").append(format)
                .append(Constants.STR_0D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Supprime un exemplaire d'une notice en cours, renvoie dans ErrorMessage,
     * le message renvoyé par le CBS suite à la suppression de l'exemplaire
     * modifie la valeur de CmdOk : true si succès - false si échec PpnEncours
     * doit contenir un ppn, suite à une recherche de notice par exemple
     *
     * @param exemplaire numéro de l'exemplaire à supprimer
     * @param lotEncours lot en cours
     * @param ppnEncours ppn de la notice
     * @return le résultat de la suppression
     * @throws CBSException Erreur CBS
     */
    public String supE(String exemplaire, String lotEncours, String ppnEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CKPPN).append(ppnEncours).append(" ").append(exemplaire).append(";\\TOO S")
                .append(lotEncours).append(" 1 ").append(Constants.UNMA).append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Supprime la notice bibliographique en cours renvoie dans ErrorMessage, le
     * message renvoyé par le CBS suite à la suppression de la notice modifie la
     * valeur de CmdOk : true si succès - false si échec PpnEncours doit
     * contenir un ppn, suite à une recherche de notice par exemple
     *
     * @return le résultat de la suppression
     * @throws CBSException Erreur CBS
     */
    public String sup(String lotEncours, String ppnEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CKPPN).append(ppnEncours).append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * lance la commande mod l
     *
     * @param lotEncours
     * @return le message renvoyé par le CBS suite à la modification
     * @throws CBSException Erreur CBS
     */
    public String modLoc(final String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VPRUNMA).append(Constants.STR_1D).append(Constants.VTAFR).append(Constants.STR_1D)
                .append("CUSmod l").append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

    /**
     * Rajoute des données locales (L035) à la notice en cours (ppnEncours)
     * renvoie dans ErrorMessage, le message renvoyé par le CBS suite à la
     * modification de la donnée locale modifie la valeur de CmdOk : true si
     * succès - false si échec PpnEncours doit contenir un ppn, suite à une
     * recherche de notice par exemple
     *
     * @param ppnEncours ppn de la notice
     * @param lotEncours lot en cours
     * @param notice     notice au format natif
     * @param vloc       la chaine à rajouter dans les données locales
     * @return le message renvoyé par le CBS suite à la modification
     * @throws CBSException Erreur CBS
     */
    public String valModLoc(final String notice, final String ppnEncours, final String lotEncours, final String vloc) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CMPPN).append(ppnEncours).append(" L").append(Constants.UNMTOO).append(lotEncours).append(" 1 ")
                .append(Constants.UNMA).append(Constants.STR_1D).append(Constants.BBWVTX0T).append(notice)
                .append(Constants.STR_1F).append("D").append(Integer.parseInt(notice.substring(3, 7)) - 1)
                .append(Constants.STR_1F).append("I").append(vloc).append(Constants.STR_0D).append(Constants.STR_0D)
                .append(Constants.STR_1F).append(Constants.STR_1E).toString();

        return connector.tcpReq(query);
    }

    /**
     * Valide la création de zone loc
     *
     * @param ppnEncours ppn
     * @param lotEncours lot en cours
     * @param vloc       zone loc
     * @return retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String valCreLoc(final String ppnEncours, final String lotEncours, final String vloc) throws CBSException {
        String query = new StringBuilder().append(Constants.VSE).append(lotEncours).append(Constants.STR_1D)
                .append(Constants.VTI1).append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D)
                .append(Constants.VTAFR).append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D)
                .append(Constants.CIPPN).append(ppnEncours).append(" L").append(Constants.UNMTOO).append(lotEncours).append(" 1 ")
                .append(Constants.UNMA).append(Constants.STR_1D).append(Constants.BBWVTX0T).append("LOK000200:00:00.000")
                .append(Constants.STR_1F).append("D1").append(Constants.STR_1F).append("I").append(vloc)
                .append(Constants.STR_0D).append(Constants.STR_0D).append(Constants.STR_1F).append(Constants.STR_1E).toString();
        return connector.tcpReq(query);
    }

    /**
     * Envoi la commande de modification
     *
     * @param noRecord   position de la notice dans la liste
     * @param lotEncours lot en cours
     * @return retour du CBS
     * @throws CBSException Erreur CBS
     */
    public String mod(final String noRecord, final String lotEncours) throws CBSException {
        String query = new StringBuilder().append(Constants.VTI1).append(Constants.STR_1D)
                .append(Constants.VSE).append(lotEncours).append(Constants.STR_1D).append(Constants.VPRUNM)
                .append(Constants.STR_1D).append(Constants.VT1).append(Constants.STR_1D).append(Constants.VTAFR)
                .append(Constants.STR_1D).append(Constants.VCUTF8).append(Constants.STR_1D).append(Constants.CUSMUT)
                .append(lotEncours).append(" ").append(noRecord).append(" ").append(Constants.UNM)
                .append(Constants.STR_1D).toString();
        return connector.tcpReq(query);
    }

}
