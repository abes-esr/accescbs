package fr.abes.cbs.process;

import fr.abes.cbs.commandes.Commandes;
import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.exception.ZoneException;
import fr.abes.cbs.models.LogicalBdd;
import fr.abes.cbs.notices.Biblio;
import fr.abes.cbs.notices.DonneeLocale;
import fr.abes.cbs.notices.Exemplaire;
import fr.abes.cbs.notices.NoticeConcrete;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ProcessCBS {
    @Getter
    private final Commandes clientCBS;
    @Getter
    @Setter
    private int lotEncours;
    @Getter
    @Setter
    private boolean onEdit;
    @Getter
    @Setter
    private boolean onNew;
    @Getter
    @Setter
    private boolean onNewAut;
    @Getter
    @Setter
    private boolean onNewExp;
    @Getter
    @Setter
    private String ppnEncours = "";
    @Getter
    @Setter
    public boolean hasExpl;
    private int pos = 1;
    @Getter
    @Setter
    private int nbNotices;

    // liste des ppn retournés par la recherche
    @Getter
    @Setter
    private StringBuilder listePpn;
    @Getter
    private String nvNumEx;
    @Getter
    private int nbExPPnEncours;
    // la longueur de la notice récupérée suite à une recherche
    private int lgNoticeSearch;
    @Getter
    private final List<List<String>> resultatsTable;
    @Getter
    private final List<String[]> resultatsList;
    @Getter
    @Setter
    private String rcr;

    /**
     * Constructeur par défaut avec un délai entre 2 requête de 10ms
     */
    public ProcessCBS() {
        clientCBS = new Commandes();
        listePpn = new StringBuilder();
        nbNotices = 0;
        lotEncours = 0;
        resultatsTable = new ArrayList<>();
        resultatsList = new ArrayList<>();
    }

    public void disconnect() throws CBSException {
        clientCBS.disconnect();
    }

    /**
     * Lance la commande CHE et récupère le nombre de résultats de recherche
     *
     * @param query requête CHE
     * @return résultat de la recherche
     * @throws IOException Erreur de communication avec le CBS
     */
    public String search(String query) throws IOException {
        String resu = "";
        this.onEdit = false;
        // on réinitialise la liste de ppn après chaque recherche
        this.listePpn = new StringBuilder();
        try {
            resu = clientCBS.che(query);
            this.nbNotices = Utilitaire.getNbNoticesFromChe(resu);
            switch (this.nbNotices) {
                case 0:
                    break;
                case 1:
                    this.lotEncours = Integer.parseInt(Utilitaire.recupEntre(resu, Constants.STR_1D + "VSIS", Constants.STR_1D));
                    this.ppnEncours = Utilitaire.recupEntre(resu, "LPP", Constants.STR_1B);
                    break;
                default:
                    this.lotEncours = Integer.parseInt(Utilitaire.recupEntre(resu, "VSIS", Constants.STR_1D));
                    String[] lstrecords = resu.split(Constants.STR_1B + "H" + Constants.STR_1B + "LPP");
                    initTablesResult(lstrecords);
            }
            this.lgNoticeSearch = resu.length();
            return resu;
        } catch (Exception ex) {
            log.error("Erreur recherche " + query + " / result : " + resu.replace("\r", "").replace("\n", "") + " erreur : " + ex.getMessage());
            throw new IOException(ex);
        }
    }

    /**
     * Initialise les tables de résultats
     *
     * @param lstrecords liste des enregistrements
     */
    private void initTablesResult(String[] lstrecords) throws CBSException {
        // on réinitialise les tableaux de résultats et la liste des ppn
        // courantes à chaque requête
        resultatsTable.clear();
        resultatsList.clear();
        // on supprime la première ligne du tableau de la boucle qui ne
        // correspond pas à un résultat de recherche
        for (int i = 1; i < lstrecords.length; i++) {
            String ligne = lstrecords[i];
            String ppn = ligne.substring(0, 9);
            listePpn.append(ppn).append(";");
            String lg = Utilitaire.recupEntre(ligne, Constants.SEPS1, Constants.STR_1B);
            String type = Utilitaire.recupEntre(ligne, Constants.LMA, Constants.STR_1B);
            String auteur = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV0", Constants.STR_1B);
            String titre = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV1", Constants.STR_1B);
            String inconnu2 = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV2", Constants.STR_1B);
            String inconnu3 = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV3", Constants.STR_1B);
            String inconnu4 = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV4", Constants.STR_1B);
            if (!lg.isEmpty()) {
                String[] lignet2 = new String[8];
                ArrayList<String> lignet = new ArrayList<>();
                lignet.add(lg.trim());
                lignet2[0] = Utilitaire.cv(lg.trim());
                lignet.add(ppn.trim());
                lignet2[1] = Utilitaire.cv(ppn.trim());
                lignet.add(type.trim());
                lignet2[2] = Utilitaire.cv(type.trim());
                lignet.add(auteur.trim());
                lignet2[3] = Utilitaire.cv(auteur.trim());
                lignet.add(titre.trim());
                lignet2[4] = Utilitaire.cv(titre.trim());
                lignet.add(inconnu2.trim());
                lignet2[5] = Utilitaire.cv(inconnu2.trim());
                lignet.add(inconnu3.trim());
                lignet2[6] = Utilitaire.cv(inconnu3.trim());
                lignet.add(inconnu4.trim());
                lignet2[7] = Utilitaire.cv(inconnu4.trim());
                resultatsTable.add(lignet);
                resultatsList.add(lignet2);
            }
        }
    }

    /**
     * authentifie un utilisateur au CBS de bout en bout (sélection base
     * incluse)
     *
     * @param serveur IP du serveur CBS
     * @param port    Port du serveur CBS
     * @param login   Utilisateur
     * @param passwd  Mot de passe
     * @throws CBSException  Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public void authenticate(String serveur, String port, String login, String passwd) throws CBSException, IOException {
        if (!clientCBS.isConnected()) {
            clientCBS.connect(serveur, Integer.parseInt(port));
        }
        if (!clientCBS.isLogged()) {
            String resu = clientCBS.log(login, passwd);
            if (resu.contains(Constants.STR_1B + "LSY")) {
                resu = clientCBS.che("\\SYS SU");
                if (resu.contains(Constants.STR_1B + "ECatalogue")) {
                    clientCBS.che("\\BES 1 Catalogue");
                }
                clientCBS.che("\\BES 1");
            }
            if (clientCBS.isLogged()) {
                this.majRcr();
            }
        }
        this.setLotEncours(0);
    }

    /**
     * authentifie un utilisateur au CBS sur une base logique donnée
     *
     * @param serveur IP du serveur CBS
     * @param port    Port du serveur CBS
     * @param login   Utilisateur
     * @param passwd  Mot de passe
     * @param bdd     numéro de base logique où se connecter (ex : 1.1 ou 1.201)
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public void authenticateWithLogicalDb(String serveur, String port, String login, String passwd, String bdd) throws CBSException, IOException {
        if (!clientCBS.isConnected()) {
            clientCBS.connect(serveur, Integer.parseInt(port));
        }
        if (!clientCBS.isLogged()) {
            String resu = clientCBS.log(login, passwd);
            if (resu.contains(Constants.STR_1B + "LSY")) {
                resu = clientCBS.che("\\SYS SU");
                String bddAsString = resu.substring(resu.indexOf("VKZ"), resu.lastIndexOf("V/VOK"));
                List<LogicalBdd> listBdd = new ArrayList<>();
                //alimentation de la liste des bases logiques retournées par le CBS
                for (String vkz : bddAsString.split("VKZ")) {
                    if (!vkz.isEmpty()) {
                        LogicalBdd logicalBdd = new LogicalBdd(vkz);
                        listBdd.add(logicalBdd);
                    }
                }
                //recherche de la base passée en paramètre dans la liste retournée par le cbs
                Optional<LogicalBdd> resultBdd = listBdd.stream().filter(logicalBdd -> logicalBdd.getBddNumber().equals(bdd)).findFirst();
                if (resultBdd.isPresent()) {
                    clientCBS.che("\\BES " + resultBdd.get().getLineNumber());
                }
            }
        }
    }

    /**
     * Visualiser une notice parametres: no de record dans le liste
     * courte,retour en xml ou natif,format de recup:UNMA, UNX..
     *
     * @param noLigne       Numéro du résultat à retourner
     * @param xml           format natif ou XML
     * @param formatOrigine format d'origine de la notice
     * @return la notice noLigne en string
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication CBS
     */
    public String view(final String noLigne, final boolean xml, String formatOrigine) throws CBSException, IOException {
        if (this.lotEncours == 0) {
            throw new CBSException(Level.ERROR, "Impossible de lancer la commande view : pas de lot en cours");
        }
        try {
            String fOrigine = formatOrigine;
            if (formatOrigine.isEmpty()) {
                fOrigine = Constants.UNMA;
            }
            onEdit = false;
            String resu = clientCBS.view(noLigne, String.valueOf(lotEncours), fOrigine);

            ppnEncours = Utilitaire.recupEntre(resu, "LPP", Constants.STR_1B);
            if (!xml) {
                return resu;
            }
            String chaine;
            // c'est unx en minuscule et en plus ca marche sans ce test
            if ("UNX".equals(fOrigine) || "UNU".equals(fOrigine)) {
                chaine = Utilitaire.recupEntre(resu.replaceAll("<BR>", Constants.STR_0D),
                        Constants.STR_0D + Constants.STR_0D, "</TABLE>");
            } else {
                chaine = Utilitaire.recupEntre(resu.replaceAll("<BR>", Constants.STR_0D), "<TABLE>", "</TABLE>");
            }
            return Utilitaire.xmlFormat(chaine);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Affiche la liste des resultats suivants après une première recherche
     *
     * @return Retourne les 16 prochains résultats
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String next() throws CBSException, IOException {
        try {
            onEdit = false;
            pos = pos + 16;
            String resu = clientCBS.next(String.valueOf(lotEncours), pos);

            String[] lstrecords = resu.split("\r\n|\r|\n");
            initTablesResult(lstrecords);
            return resu;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Crée une notice dans le CBS
     *
     * @param notice Notice pica
     * @return Résultat de la création
     * @throws CBSException  Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String enregistrerNew(final String notice) throws CBSException, IOException {
        try {
            String resu = clientCBS.cre(notice, 1);
            onEdit = false;
            onNew = true;
            ppnEncours = Utilitaire.recupEntre(resu, "avec PPN ", Constants.STR_1D).trim();
            return resu;
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        }

    }

    /**
     * Enregistrer une nouvelle notice d'autorites
     *
     * @param notice notice au format natif
     * @return Réponse du CBS
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String enregistrerNewAut(final String notice) throws IOException, CBSException {
        try {
            String resu = clientCBS.cre(notice, 2);
            onEdit = false;
            onNew = true;
            ppnEncours = Utilitaire.recupEntre(resu, Constants.MSG_APPN, Constants.STR_1D).trim();
            return resu;
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }


    /**
     * envoi commande de translitération sans ppn
     *
     * @param notice Notice au format natif
     * @return Réponse du CBS
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String translitererSansPPN(final String notice) throws CBSException, IOException {
        String resu = clientCBS.translitererSansPPN(notice);
        onEdit = false;
        onNew = true;
        return resu;
    }

    /**
     * Supprime une notice
     *
     * @param nonotice position de la notice dans le liste de resultats
     * @return Réponse du CBS
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String supprimer(final String nonotice) throws CBSException, IOException {
        String resu = clientCBS.sup(nonotice, String.valueOf(lotEncours), ppnEncours);
        if (clientCBS.isCmdOk()) {
            onEdit = false;
        }
        return resu;
    }

    /**
     * Permet de passer des parametres par defaut (utilisé dans wini par un
     * ecran de parametres)
     *
     * @param params Tableau des paramètres pour le CBS
     * @return Réponse du CBS
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String setParams(final String[] params) throws CBSException, IOException {
        return clientCBS.setParams(params);
    }

    /**
     * Envoi au CBS la commande "mod" suivi de validation de la modification
     *
     * @param norecord Position de la notice dans la liste
     * @param notice   Notice au format natif
     * @return le message renvoyé par le CBS suite à la modification
     * @throws CBSException  erreur de validation
     * @throws IOException erreur de communication avec le CBS
     */
    public String modifierNotice(String norecord, String notice) throws IOException, CBSException {
        try {
            String resu = clientCBS.mod(norecord, String.valueOf(lotEncours));
            String noticedeb = Utilitaire.recupEntre(resu, "VTXT", Constants.STR_1F);
            int lgnotice;
            // on regarde si la lg de la notice a plus ou moins de 9999 caractères
            if (Integer.parseInt(noticedeb.substring(3, 7)) >= 9999) {
                lgnotice = lgNoticeSearch + 5000;
            } else {
                lgnotice = Integer.parseInt(noticedeb.substring(3, 7)) - 1;
            }
            return clientCBS.valMod(notice, lgnotice, String.valueOf(lotEncours), ppnEncours, norecord, noticedeb, "");
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Envoie au CBS la modification d'une notice sous forme d'objet
     *
     * @param norecord position de la notice dans le jeu de résultat
     * @param notice   notice au format NoticeConcrete
     * @return le message renvoyé par le CBS suite à la validation
     * @throws CBSException  erreur de validation CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String modifierNoticeConcrete(String norecord, NoticeConcrete notice) throws IOException, CBSException {
        try {
            String resu = clientCBS.mod(norecord, String.valueOf(lotEncours));
            String noticeStr = notice.getNoticeBiblio().toString();
            String noticedeb = Utilitaire.recupEntre(resu, "VTXT", Constants.STR_1F);
            int lgnotice;
            // on regarde si la lg de la notice a plus ou moins de 9999 caractères
            if (Integer.parseInt(noticedeb.substring(3, 7)) >= 9999) {
                lgnotice = lgNoticeSearch + 5000;
            } else {
                lgnotice = Integer.parseInt(noticedeb.substring(3, 7)) - 1;
            }
            String noticeLocDeb = resu.substring(resu.indexOf("VTXTLOK") + 7);
            noticeLocDeb = noticeLocDeb.substring(0, noticeLocDeb.indexOf(Constants.STR_1F));
            if (notice.getNoticeLocale() != null) {
                int lgNoticeLoc = notice.getNoticeLocale().toString().length() + 1;
                if (lgNoticeLoc != 1) {
                    noticeStr += Constants.STR_1F + Constants.STR_1E;
                    noticeStr += "VTX0TLOK" + noticeLocDeb + "D" + lgNoticeLoc + Constants.STR_1F + "I" + notice.getNoticeLocale().toString() + Constants.STR_1D + Constants.STR_1D;
                }
            }
            if (!notice.getExemplaires().isEmpty()) {
                noticeStr += Constants.STR_1F + Constants.STR_1E;
                for (Exemplaire exemplaire : notice.getExemplaires()) {
                    String noticeExempDeb = resu.substring(resu.indexOf("VTXTE" + exemplaire.getNumEx()) + 7);
                    noticeExempDeb = noticeExempDeb.substring(0, noticeExempDeb.indexOf(Constants.STR_1F));
                    int lgNoticeExemp = exemplaire.toString().length() + 1;
                    noticeStr += "VTXT0TE" + exemplaire.getNumEx() + noticeExempDeb + "D" + lgNoticeExemp + Constants.STR_1F + "I" + exemplaire + Constants.STR_1D + Constants.STR_1D;
                }
            }
            return clientCBS.valMod(noticeStr, lgnotice, String.valueOf(lotEncours), ppnEncours, norecord, noticedeb, "");
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }


    /**
     * Retourne l'iln de rattachement d'un rcr passé en paramètre envoi la
     * commande "aff bib + n° rcr"
     *
     * @param rcr le rcr concerné
     * @return l'ILN de rattachement
     * @throws IOException Erreur de communication avec le CBS
     */
    public String ilnRattachement(final String rcr) throws IOException {
        try {
            String resu = clientCBS.affBib(rcr);
            return Utilitaire.recupEntre(resu, Constants.STR_1E + "VAG" + Constants.STR_1B + "P", Constants.STR_1E + "VAV");
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Passe en édition un exemplaire
     *
     * @param numEx : numéro de l'exemplaire à modifier
     * @return : exemplaire sélectionné
     * @throws CBSException  Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String editerExemplaire(String numEx) throws CBSException, IOException {
        try {
            String resu = clientCBS.modE(numEx, String.valueOf(lotEncours));
            String exemp;
            if (resu.contains(Constants.STR_0D + Constants.STR_0D + Constants.STR_1E + Constants.VMC)) {
                exemp = numEx + Utilitaire.recupEntre(resu, Constants.STR_1F + "e" + numEx, Constants.STR_1E + Constants.VMC);
            } else {
                exemp = numEx + Utilitaire.recupEntre(resu, Constants.STR_1F + "e" + numEx, Constants.STR_1E + Constants.STR_1D + "VV");
            }
            return exemp;
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(Constants.ERREUR_SYSTEME);
        }
    }

    /**
     * Passe en édition une notice et renvoi la notice
     * Le numéro d'exemplaire est dans un champ de la classe
     *
     * @param noRecord notice au format natif, en mode édition
     * @return Notice éditée
     * @throws CBSException  Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String editer(final String noRecord) throws CBSException, IOException {
        // num de l'exemplaire en cours
        nbExPPnEncours = 0;
        // num du prochain exemplaire
        nvNumEx = "";
        int posLastEx;
        try {
            String resu = clientCBS.mod(noRecord, String.valueOf(lotEncours));
            String exemp;
            // pour recupere les exemplaires
            if (resu.contains(Constants.STR_1E + Constants.VMC)) {
                exemp = Constants.VTXTE + Utilitaire.recupEntre(resu, Constants.VTXTE, Constants.STR_1E + Constants.VMC);
            } else {
                exemp = Constants.VTXTE
                        + Utilitaire.recupEntre(resu, Constants.VTXTE, Constants.STR_1E + Constants.STR_1D + "VV");
            }

            if (exemp.equals(Constants.VTXTE)) {
                // il n'y a pas d'exemplaire de la notice le rcr d'iln
                nvNumEx = "e01";
                hasExpl = false;
            } else {
                hasExpl = exemp.contains(rcr);
                // la notice a au moins 1 exemplaire pour le rcr d'iln
                posLastEx = exemp.lastIndexOf(Constants.VTXTE);
                if (posLastEx < 0) {
                    nbExPPnEncours = 1;
                } else {
                    nbExPPnEncours = Integer.parseInt(exemp.substring(posLastEx + 5, posLastEx + 7));
                }

                nbExPPnEncours = nbExPPnEncours + 1;
                if (String.valueOf(nbExPPnEncours).length() == 1) {
                    nvNumEx = "e0" + nbExPPnEncours;
                } else {
                    nvNumEx = "e" + nbExPPnEncours;
                }
            }

            onEdit = true;
            onNew = false;
            return resu;
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Passe en édition une notice et renvoi la notice sous forme d'objet avec les exemplaires et les données locales
     * Le numéro d'exemplaire est dans un champ de la classe
     *
     * @param noRecord notice au format natif, en mode édition
     * @return Notice éditée
     * @throws CBSException  Erreur CBS
     * @throws ZoneException erreur de construction de la notice
     * @throws IOException erreur de communication avec le CBS
     */
    public NoticeConcrete editerNoticeConcrete(final String noRecord) throws CBSException, ZoneException, IOException {
        String resu = "";
        // num de l'exemplaire en cours
        nbExPPnEncours = 0;
        // num du prochain exemplaire
        nvNumEx = "";
        int posLastEx;
        try {
            resu = clientCBS.mod(noRecord, String.valueOf(lotEncours));
            //on récupère la notice bibliographique
            String biblio = Utilitaire.recupEntre(resu, Constants.VTXTBIB, Constants.STR_1E);
            Biblio noticeBiblio = new Biblio(biblio.substring(biblio.indexOf(Constants.STR_1F) + 1));
            String donneesLoc = Utilitaire.recupEntre(resu, Constants.VTXTLOK, Constants.STR_1E);
            DonneeLocale donneeLocale = new DonneeLocale(donneesLoc.substring(donneesLoc.indexOf(Constants.STR_1F) + 1));
            // on récupère les données locales
            String exemps;
            // on récupère les exemplaires
            if (resu.contains(Constants.STR_1E + Constants.VMC)) {
                exemps = Constants.VTXTE + Utilitaire.recupEntre(resu, Constants.VTXTE, Constants.STR_1E + Constants.VMC);
            } else {
                exemps = Constants.VTXTE
                        + Utilitaire.recupEntre(resu, Constants.VTXTE, Constants.STR_1E + Constants.STR_1D + "VV");
            }
            List<Exemplaire> exemplaires = new ArrayList<>();
            if (!exemps.equals("VTXTE")) {
                hasExpl = exemps.contains(rcr);
                // la notice a au moins 1 exemplaire pour le rcr d'iln
                posLastEx = exemps.lastIndexOf(Constants.VTXTE);
                if (posLastEx < 0) {
                    nbExPPnEncours = 1;
                } else {
                    nbExPPnEncours = Integer.parseInt(exemps.substring(posLastEx + 5, posLastEx + 7));
                }

                nbExPPnEncours = nbExPPnEncours + 1;
                if (String.valueOf(nbExPPnEncours).length() == 1) {
                    nvNumEx = "e0" + nbExPPnEncours;
                } else {
                    nvNumEx = "e" + nbExPPnEncours;
                }
                for (String exemp : exemps.split(Constants.STR_1E)) {
                    Exemplaire exemplaire = new Exemplaire(exemp.substring(exemp.indexOf(Constants.STR_1F) + 1));
                    exemplaires.add(exemplaire);
                }
            } else {
                // il n'y a pas d'exemplaire de la notice le rcr d'iln
                nvNumEx = "e01";
                hasExpl = false;
            }

            onEdit = true;
            onNew = false;
            return new NoticeConcrete(noticeBiblio, donneeLocale, exemplaires);
        } catch (ZoneException | CBSException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Erreur passage en édition" + ex.getMessage());
            throw new IOException(ex);
        }
    }

    /**
     * Lance la commande pour créer un exemplaire
     *
     * @param numEx : numéro de l'exemplaire (sans le e, sur 2 digits) à passer en paramètre de la commande cre exx
     * @return le message renvoyé par le CBS suite à la création de l'exemplaire
     * @throws CBSException erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String creerExemplaire(String numEx) throws CBSException, IOException {
        return clientCBS.creE(numEx, String.valueOf(lotEncours));
    }

    /**
     * Lance la commande pour créer la donnée locale
     *
     * @return message renvoyé par le cbs suite au lancement de la commande cre l
     * @throws CBSException erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String creerDonneeLocale() throws CBSException, IOException {
        return clientCBS.creL(String.valueOf(lotEncours));
    }

    /**
     * Rajoute un exemplaire à une notice Envoi de la commande cre e
     * n°exemplaire renvoie dans ErrorMessage, le message renvoyé par le CBS
     * suite au rajout de l'exemplaire modifie la valeur de CmdOk : true si
     * succès - false si échec renseigne le ppn de la notice créée dans
     * PpnEncours si la création a réussi
     * utilisée uniquement par IdRef
     *
     * @param exemplaire le nouveau num. d'exemplaire en concaténant "e" et NvNumEx
     * @return le message renvoyé par le CBS suite à la création de l'exemplaire
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String newExemplaire(String exemplaire) throws CBSException, IOException {
        return clientCBS.valCreE(exemplaire, String.valueOf(lotEncours), ppnEncours);
    }

    /**
     * Modifie un exemplaire dans le CBS
     *
     * @param exemplaire : l'exemplaire à modifier
     * @param numEx      Numéro d'exemplaire
     * @return le message renvoyé par le CBS suite à la modification
     * @throws CBSException  Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String modifierExemp(String exemplaire, String numEx) throws CBSException, IOException {
        try {
            String resu = clientCBS.modE(numEx, String.valueOf(lotEncours));
            String noticedeb = Utilitaire.recupEntre(resu, Constants.VTXTE, Constants.STR_1F);
            int lgexemp = Integer.parseInt(noticedeb.substring(2, 6)) - 1;
            return clientCBS.valModE(exemplaire, numEx, String.valueOf(lotEncours), noticedeb, ppnEncours, lgexemp);
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void back() throws CBSException, IOException {
        try {
            clientCBS.back(String.valueOf(lotEncours));
        } catch (Exception ex) {
            log.error("Erreur retour arrière : " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Ajout d'un utilisateur Envoi de la commande cre usa puis validation
     * renvoie dans ErrorMessage, le message renvoyé par le CBS suite au rajout
     * de l'utilisateur modifie la valeur de CmdOk : true si succès - false si
     * échec
     *
     * @param user Tableau contenant les infos de l'utilisateur à créer
     * @return le message renvoyé par le CBS suite à la création de
     * l'utilisateur
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String newUsa(final String[] user) throws CBSException, IOException {
        return clientCBS.creUsa(user);
    }

    /**
     * Supprimer un utilisateur
     *
     * @param user Nom de l'utilisateur à supprimer
     * @return Réponse du CBS
     * @throws CBSException Erreur CBS
     * @throws IOException erreur de communication avec le CBS
     */
    public String supUsa(final String user) throws CBSException, IOException {
        return clientCBS.valSupUsa(user);
    }

    /**
     * Affiche les informations de l'utilisateur
     *
     * @return les informations de l'utilisateur connecté
     */
    public String affUsa() throws CBSException, IOException {
        return clientCBS.affUsa();
    }

    /**
     * Met à jour le RCR actuel avec celui de l'utilisateur connecté
     *
     */
    private void majRcr() throws IOException {
        try {
            String result = this.affUsa();
            String endTrame = result.substring(result.indexOf("VU3") + 3);
            this.setRcr(endTrame.substring(0, endTrame.indexOf(Constants.STR_1E)));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Passe une notice en cours en format Unimarc envoi de la commande "aff
     * unma" renvoie dans ErrorMessage, le message renvoyé par le CBS suite au
     * changement de format modifie la valeur de CmdOk : true si succès - false
     * si échec
     *
     * @return la notice au format unimarc
     * @throws CBSException Erreur CBS
     */
    public String affUnma() throws CBSException, IOException {
        return clientCBS.affFormat("unma", String.valueOf(lotEncours));
    }

    /**
     * Passe une notice en cours au format en paramètre envoi de la commande
     * "aff format" renvoie dans ErrorMessage, le message renvoyé par le CBS
     * suite au changement de format modifie la valeur de CmdOk : true si succès
     * - false si échec
     *
     * @param format format à afficher
     * @return la notice au format unimarc
     * @throws CBSException Erreur CBS
     */
    public String affFormat(String format) throws CBSException, IOException {
        return clientCBS.affFormat(format, String.valueOf(lotEncours));
    }

    /**
     * Récupère la liste des notices liées
     *
     * @return le nombre de notices liées
     * @throws CBSException  erreur CBS
     */
    public Integer rel() throws CBSException, IOException {
        if (this.lotEncours == 0)
            throw new CBSException(Level.ERROR, "Impossible de lancer la commande rel : pas de lot en cours");
        try {
            String result = clientCBS.rel(String.valueOf(lotEncours));
            this.lotEncours = Integer.parseInt(Utilitaire.recupEntre(result, Constants.STR_1D + "VSIS", Constants.STR_1D));
            return Integer.parseInt(Utilitaire.recupEntre(result, "VSZ", Constants.STR_1D));
        } catch (CBSException ex) {
            throw ex;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Supprime un exemplaire d'une notice en cours, renvoie dans ErrorMessage,
     * le message renvoyé par le CBS suite à la suppression de l'exemplaire
     * modifie la valeur de CmdOk : true si succès - false si échec PpnEncours
     * doit contenir un ppn, suite à une recherche de notice par exemple
     *
     * @param exemplaire numéro de l'exemplaire à supprimer
     * @return le résultat de la suppression
     * @throws CBSException Erreur CBS
     */
    public String supExemplaire(String exemplaire) throws CBSException, IOException {
        return clientCBS.supE(exemplaire, String.valueOf(lotEncours), ppnEncours);
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
    public String supBiblio() throws CBSException, IOException {
        return clientCBS.sup(String.valueOf(lotEncours), ppnEncours);
    }

    /**
     * Rajoute des données locales (L035) à la notice en cours (ppnEncours)
     * renvoie dans ErrorMessage, le message renvoyé par le CBS suite à la
     * modification de la donnée locale modifie la valeur de CmdOk : true si
     * succès - false si échec PpnEncours doit contenir un ppn, suite à une
     * recherche de notice par exemple
     *
     * @param vloc !!!!!!!!!!!!
     * @return le message renvoyé par le CBS suite à la modification
     * @throws CBSException  Erreur CBS
     */
    public String modLoc(final String vloc) throws CBSException, IOException {
        try {
            String resu = clientCBS.modLoc(String.valueOf(lotEncours));
            String noticedeb = Utilitaire.recupEntre(resu, Constants.VTXT, Constants.STR_1F);
            return clientCBS.valModLoc(noticedeb, ppnEncours, String.valueOf(lotEncours), vloc);
        } catch (CBSException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Crée la 1ère donnée locale pour la notice en cours (PpnEncours) envoi de
     * la commande "cre loc" renvoie dans ErrorMessage, le message renvoyé par
     * le CBS suite à la création de la donnée locale modifie la valeur de CmdOk
     * : true si succès - false si échec PpnEncours doit contenir un ppn, suite
     * à une recherche de notice par exemple
     *
     * @param vloc !!!!!!!!!!!!
     * @return le message renvoyé par le CBS suite à la création de la donnée
     * locale
     * @throws CBSException Erreur CBS
     */
    public String newLoc(final String vloc) throws CBSException, IOException {
        return clientCBS.valCreLoc(ppnEncours, String.valueOf(lotEncours), vloc);
    }

    /**
     * Méthode de suppression du bloc de donnée locale d'une notice
     * <p>
     * 2 étapes sont nécessaires :
     * * lancement de la commande sup l affichant le bloc de donnée locale
     * * validation de la suppression
     *
     * @return message renvoyé par le CBS suite à la suppression
     * @throws CBSException erreur CBS
     */
    public String supLoc() throws CBSException, IOException {
        clientCBS.supL(String.valueOf(lotEncours));
        return clientCBS.valSupL(String.valueOf(lotEncours), ppnEncours);
    }

}
