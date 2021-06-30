package fr.abes.cbs.process;

import fr.abes.cbs.commandes.Commandes;
import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.utilitaire.Constants;
import fr.abes.cbs.utilitaire.Utilitaire;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessCBS {
	@Getter
	private Commandes clientCBS;
	private String timeStpEncours = "000200:00:00.000";
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
	@Getter
	@Setter
	private String actEncours;
	private int pos = 1;
	@Getter
	@Setter
	private int nbNotices;
	// la longueur de la notice récupérée suite à une recherche
	// liste des ppn retournés par la recherche
	@Getter
	@Setter
	private StringBuilder listePpn;
	@Getter
	private String nvNumEx;
	@Getter
	private int nbExPPnEncours;
	private int lgNoticeSearch;
	@Getter
	private List<List<String>> resultatsTable;
	@Getter
	private List<String[]> resultatsList;
	@Getter
	@Setter
	private String rcr;

	public ProcessCBS() {
		clientCBS = new Commandes();
		listePpn = new StringBuilder();
		nbNotices = 0;
		lotEncours = 0;
		resultatsTable = new ArrayList<>();
		resultatsList = new ArrayList<>();
	}

	public boolean isCmdOk() {
		return clientCBS.isCmdOk();
	}

	public void disconnect() {
		clientCBS.disconnect();
	}

	/**
	 * Lance la commande CHE et récupère le nombre de résultats de recherche
	 * @param query requête CHE
	 * @return résultat de la recherche
	 * @throws CBSException Erreur CBS
	 */
	public String search(String query) throws CBSException {
		onEdit = false;
		String resu = clientCBS.che(query);
		// on réinitialise la liste de ppn après chaque recherche
		listePpn = new StringBuilder();

		nbNotices = Utilitaire.getNbNoticesFromChe(resu);
		switch (nbNotices) {
		case 1:
			nbNotices = 1;
			lotEncours = Integer.parseInt(Utilitaire.recupEntre(resu, Constants.STR_1D + "VSIS", Constants.STR_1D));
			ppnEncours = Utilitaire.recupEntre(resu, "LPP", Constants.STR_1B);
			lgNoticeSearch = resu.length();
			break;
		case 0:
			nbNotices = 0;
			break;
		default:
			lotEncours = Integer.parseInt(Utilitaire.recupEntre(resu, "VSIS", Constants.STR_1D + "KTA"));
			String[] lstrecords = resu.split(Constants.STR_1B + "H" + Constants.STR_1B + "LPP");
			initTablesResult(lstrecords);
			lgNoticeSearch = resu.length();
		}
		return resu;
	}

	/**
	 * Initialise les tables de résultats
	 * @param lstrecords liste des enregistrements
	 */
	private void initTablesResult(String[] lstrecords) {
		// on réinitialise les tableaux de résultats et la liste des ppn
		// courantes à chaque requête
		resultatsTable.clear();
		resultatsList.clear();
		// on supprime la première ligne du tableau de la boucle qui ne
		// correspond pas à un résultat de recherche
		for (int i = 1; i < lstrecords.length; i++) {
			String ligne = lstrecords[i];
			String ppn = ligne.substring(0, 9);
			listePpn.append(ppn + ";");
			String lg = Utilitaire.recupEntre(ligne, Constants.SEPS1, Constants.STR_1B);
			String type = Utilitaire.recupEntre(ligne, Constants.LMA, Constants.STR_1B);
			String auteur = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV0", Constants.STR_1B);
			String titre = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV1", Constants.STR_1B);
			String inconnu2 = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV2", Constants.STR_1B);
			String inconnu3 = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV3", Constants.STR_1B);
			String inconnu4 = Utilitaire.recupEntre(ligne, Constants.STR_1B + "LV4", Constants.STR_1B);
			if (ppn.length() > 8 && !lg.isEmpty()) {
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
	 * @param port Port du serveur CBS
	 * @param login Utilisateur
	 * @param passwd Mot de passe
	 * @throws CBSException Erreur CBS
	 */
	public void authenticate(String serveur, String port, String login, String passwd) throws CBSException {
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
	 * Visualiser une notice parametres: no de record dans le liste
	 * courte,retour en xml ou natif,format de recup:UNMA, UNX..
	 * 
	 * @param noLigne Numéro du résultat à retourner
	 * @param xml format natif ou XML
	 * @param formatOrigine format d'origine de la notice
	 * @return la notice noLigne en string
	 * @throws CBSException Erreur CBS
	 */
	public String view(final String noLigne, final boolean xml, String formatOrigine) throws CBSException {
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
	}

	/**
	 * Affiche la liste des resultats suivants après une première recherche
	 * 
	 * @return Retourne les 16 prochains résultats
	 * @throws CBSException Erreur CBS
	 */
	public String next() throws CBSException {
		onEdit = false;
		pos = pos + 16;
		String resu = clientCBS.next(String.valueOf(lotEncours), pos);

		String[] lstrecords = resu.split("\r\n|\r|\n");
		initTablesResult(lstrecords);
		return resu;
	}

	/**
	 * Met une notice en edit sur le CBS et la retourne en XML le numero de
	 * record est la position dans la liste courte
	 * 
	 * @param noRecord Numéro de la notice souhaitée
	 * @return notice en XML en édition
	 * @throws CBSException Erreur CBS
	 * @deprecated
	 */
	@Deprecated
	public String editerEnXml(final String noRecord) throws CBSException {
		return Utilitaire.xmlFormatEdit(this.editerJCBS(noRecord));
	}

	/**
	 * Crée une notice dans le CBS 
	 * @param notice Notice pica
	 * @return Résultat de la création
	 * @throws CBSException Erreur CBS
	 */
	public String enregistrerNew(final String notice) throws CBSException {
		String resu = clientCBS.cre(notice, 1);
		onEdit = false;
		onNew = true;
		ppnEncours = Utilitaire.recupEntre(resu, "avec PPN ", Constants.STR_1D).trim();
		return resu;

	}

	/**
	 * Enregistrer une notice en edit
	 * (pica)
	 * 
	 * @param notice Notice au format natif
	 * @return Réponse du CBS
	 * @throws CBSException Erreur CBS
	 */
	public String enregistrer(final String notice) throws CBSException {

        int lettsp = Integer.parseInt(timeStpEncours.substring(0, 4));
        lettsp--;
        String leact = ("P").equals(actEncours) ? Constants.STR_1B +"P" : "";
        String noticedeb = "BIB" + timeStpEncours;
        String resu = clientCBS.valMod(notice, lettsp, String.valueOf(lotEncours), ppnEncours, "1", noticedeb, leact);
        
        onEdit = false;
        return resu;

    }


	/**
	 * Enregistrer une nouvelle notice d'autorites
	 * 
	 * @param notice notice au format natif
	 * @return Réponse du CBS
	 * @throws CBSException Erreur CBS
	 */
	public String enregistrerNewAut(final String notice) throws CBSException {
		String resu = clientCBS.cre(notice, 2);
		onEdit = false;
		onNew = true;
		ppnEncours = Utilitaire.recupEntre(resu, Constants.MSG_APPN, Constants.STR_1D).trim();
		return resu;
	}

	/**
	 * envoi commande de translitération
	 * 
	 * @param notice Notice au format natif
	 * @return Réponse du CBS
	 */
	public String transliterer(final String notice) throws CBSException {
		String leact = "";
		if (Constants.LP.equals(actEncours)) {
			leact = (char) 27 + Constants.LP;
		}
		String resu = clientCBS.transliterer(notice, leact, timeStpEncours, String.valueOf(lotEncours), ppnEncours);
		onEdit = false;
		onNew = true;
		return resu;
	}

	/**
	 * envoi commande de translitération sans ppn
	 * 
	 * @param notice Notice au format natif
	 * @return Réponse du CBS
	 * @throws CBSException Erreur CBS
	 */
	public String translitererSansPPN(final String notice) throws CBSException {
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
	 */
	public String supprimer(final String nonotice) throws CBSException {
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
	 */
	public String setParams(final String[] params) throws CBSException {
		return clientCBS.setParams(params);
	}

	/**
	 * Envoi au CBS la commande "mod" suivi de validation de la modification
	 * 
	 * @param norecord Position de la notice dans la liste
	 * @param notice Notice au format natif
	 * @return le message renvoyé par le CBS suite à la modification
	 * @throws CBSException Erreur CBS
	 */
	public String modifierNotice(String norecord, String notice) throws CBSException {
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
	}

	/**
	 * Retourne l'iln de rattachement d'un rcr passé en paramètre envoi la
	 * commande "aff bib + n° rcr"
	 * 
	 * @param rcr
	 *            le rcr concerné
	 * @return l'ILN de rattachement
	 * @throws CBSException Erreur CBS
	 */
	public String ilnRattachement(final String rcr) throws CBSException {
		String resu = clientCBS.affBib(rcr);
		return Utilitaire.recupEntre(resu, Constants.STR_1E + "VAG" + Constants.STR_1B + "P", Constants.STR_1E + "VAV");
	}

	/**
	 * Passe en édition un exemplaire
	 * 
	 * @param numEx
	 *            : numéro de l'exemplaire à modifier
	 * @return : exemplaire sélectionné
	 * @throws CBSException Erreur CBS
	 */
	public String editerExemplaire(String numEx) throws CBSException {
		String resu = clientCBS.modE(numEx, String.valueOf(lotEncours));
		String exemp;
		if (resu.contains(Constants.STR_0D + Constants.STR_0D + Constants.STR_1E + Constants.VMC)) {
			exemp = numEx + Utilitaire.recupEntre(resu, Constants.STR_1F + "e" + numEx, Constants.STR_1E + Constants.VMC);
		} else {
			exemp = numEx + Utilitaire.recupEntre(resu, Constants.STR_1F + "e" + numEx, Constants.STR_1E + Constants.STR_1D + "VV");
		}

		return exemp;
	}

	/**
	 * Passe en édition une notice et renvoi la notice
	 * Le numéro d'exemplaire est dans un champ de la classe
	 * 
	 * @param noRecord notice au format natif, en mode édition
	 * @return Notice éditée
	 * @throws CBSException Erreur CBS
	 */
	public String editer(final String noRecord) throws CBSException {
		// num de l'exemplaire en cours
		nbExPPnEncours = 0;
		// num du prochain exemplaire
		nvNumEx = "";
		int posLastEx;
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
	}

	/**
	 * Passage en édition comme utilisé dans JCBS (comptabilité IDRef
	 * @return Notice au format édition
	 */
	public String editerJCBS(String noRecord) throws CBSException {
		String resu = this.editer(noRecord);
		String notice;
		actEncours="";
		if (resu.contains(Constants.STR_1B + "P001")){
			ppnEncours = Utilitaire.recupEntre(resu, Constants.STR_1B +"P001", Constants.STR_1B);
			ppnEncours = ppnEncours.substring(0, ppnEncours.length() - 1).trim().replaceAll("\\$a", "");

			notice = Utilitaire.recupEntre(resu, Constants.STR_1F + Constants.STR_1B +"P", Constants.STR_0D +Constants.STR_0D + Constants.STR_1E);
			//on suprime le P et le dernier caractere parasite
			actEncours="P";
		}
		else
			if (resu.contains(Constants.STR_1B +"P003")){
				ppnEncours = Utilitaire.recupEntre(resu, Constants.STR_1B +"P003", Constants.STR_1B);
				ppnEncours = ppnEncours.substring(0, ppnEncours.length() - 1).trim().replaceAll("\\$a", "");
				//entre 1F1B et 0D0E donne la notice avec le P001
				notice = Utilitaire.recupEntre(resu, Constants.STR_1F+ Constants.STR_1B +"P", Constants.STR_0D +Constants.STR_0D + Constants.STR_1E);
				if (("").equals(notice)) {
					notice = Utilitaire.recupEntre(resu, Constants.STR_1F+ Constants.STR_1B +"P", Constants.STR_0D + Constants.STR_1E);
				}
				if (("").equals(notice)) {
					//cas ou la notice termine par une zone protégée
					notice = Utilitaire.recupEntre(resu, Constants.STR_1F+ Constants.STR_1B +"P", Constants.STR_0D + Constants.STR_1B + "D" + Constants.STR_1E);
				}
				//on suprime le P et le dernier caractere parasite
				actEncours="P";
			}
			else
			{
				ppnEncours = Utilitaire.recupEntre(resu, Constants.STR_1F+"003", Constants.STR_0D);
				ppnEncours = ppnEncours.trim().replaceAll("\\$a", "");
				notice = Utilitaire.recupEntre(resu, Constants.STR_1F, Constants.STR_0D +Constants.STR_0D + Constants.STR_1E);
				actEncours="";
			}
		timeStpEncours = Utilitaire.recupEntre(resu, "VTXTBIB", Constants.STR_1F);

		//pour recupere les exemplaires a faire....
		String exemp = Utilitaire.recupEntre(resu, "VTXTE", Constants.STR_1E + "VMC");
		StringBuilder eXp = new StringBuilder();
		if (("").equals(exemp)) {
			exemp = "VTXTE" + exemp;
			String[] lstblocsExp = exemp.split(Constants.STR_1E);
			for (String blocExp : lstblocsExp) // foreach (String blocExp in lstblocsExp)
			{
				String[] sbloc = blocExp.split(Constants.STR_1F);
				if (sbloc.length == 2) {
					String trv = sbloc[0].replaceAll("VTXT", "");
					eXp.append(trv.substring(0, 3) + " " + trv.substring(3) + Constants.STR_0D);
					eXp.append(sbloc[1]);
				}
			}
		}
		if (("").equals(eXp.toString())) {
			notice += eXp;
		}
		return notice;
	}

	/**
	 * Lance la commande pour créer un exemplaire
	 * @param numEx : numéro de l'exemplaire (sans le e, sur 2 digits) à passer en paramètre de la commande cre exx
	 * @return le message renvoyé par le CBS suite à la création de l'exemplaire
	 * @throws CBSException erreur CBS
	 */
	public String creerExemplaire(String numEx) throws CBSException {
		return clientCBS.creE(numEx, String.valueOf(lotEncours));
	}

	/**
	 * Lance la commande pour créer la donnée locale
	 * @return message renvoyé par le cbs suite au lancement de la commande cre l
	 * @throws CBSException erreur CBS
	 */
	public String creerDonneeLocale() throws CBSException {
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
	 * @param exemplaire
	 *            le nouveau num. d'exemplaire en concaténant "e" et NvNumEx
	 * @return le message renvoyé par le CBS suite à la création de l'exemplaire
	 * @throws CBSException Erreur CBS
	 */
	public String newExemplaire(String exemplaire) throws CBSException {
		return clientCBS.valCreE(exemplaire, String.valueOf(lotEncours), ppnEncours);
	}

	/**
	 * Modifie un exemplaire dans le CBS
	 * 
	 * @param exemplaire
	 *            : l'exemplaire à modifier
	 * @param numEx Numéro d'exemplaire
	 * @return le message renvoyé par le CBS suite à la modification
	 * @throws CBSException Erreur CBS
	 */
	public String modifierExemp(String exemplaire, String numEx) throws CBSException {
		String resu = clientCBS.modE(numEx, String.valueOf(lotEncours));
		String noticedeb = Utilitaire.recupEntre(resu, Constants.VTXTE, Constants.STR_1F);
		int lgexemp = Integer.parseInt(noticedeb.substring(2, 6)) - 1;
		return clientCBS.valModE(exemplaire, numEx, String.valueOf(lotEncours), noticedeb, ppnEncours, lgexemp);
	}

	public String back() throws CBSException {
		return clientCBS.back(String.valueOf(lotEncours));
	}

	/**
	 * Ajout d'un utilisateur Envoi de la commande cre usa puis validation
	 * renvoie dans ErrorMessage, le message renvoyé par le CBS suite au rajout
	 * de l'utilisateur modifie la valeur de CmdOk : true si succès - false si
	 * échec
	 * 
	 * @param user Tableau contenant les infos de l'utilisateur à créer
	 * @return le message renvoyé par le CBS suite à la création de
	 *         l'utilisateur
	 * @throws CBSException Erreur CBS
	 */
	public String newUsa(final String[] user) throws CBSException {
		return clientCBS.creUsa(user);
	}

	/**
	 * Supprimer un utilisateur
	 * @param user Nom de l'utilisateur à supprimer
	 * @return Réponse du CBS
	 * @throws CBSException Erreur CBS
	 */
	public String supUsa(final String user) throws CBSException {
		return clientCBS.valSupUsa(user);
	}

	/**
	 * Affiche les informations de l'utilisateur
	 * @return les informations de l'utilisateur connecté
	 */
	public String affUsa() throws CBSException {
		return clientCBS.affUsa();
	}

	/**
	 * Met à jour le RCR actuel avec celui de l'utilisateur connecté
	 * @throws CBSException Erreur CBS
	 */
	private void majRcr() throws CBSException {
		String result = this.affUsa();
		this.setRcr(result.substring(result.indexOf("VU3") + 3, result.indexOf(" " + Constants.STR_1E)));
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
	public String affUnma() throws CBSException {
		return clientCBS.affFormat("unma", String.valueOf(lotEncours));
	}

	/**
	 * Passe une notice en cours au format en paramètre envoi de la commande
	 * "aff format" renvoie dans ErrorMessage, le message renvoyé par le CBS
	 * suite au changement de format modifie la valeur de CmdOk : true si succès
	 * - false si échec
	 * 
	 * @param format
	 *            format à afficher
	 * @return la notice au format unimarc
	 * @throws CBSException Erreur CBS
	 */
	public String affFormat(String format) throws CBSException {
		return clientCBS.affFormat(format, String.valueOf(lotEncours));
	}

	/**
	 * Supprime un exemplaire d'une notice en cours, renvoie dans ErrorMessage,
	 * le message renvoyé par le CBS suite à la suppression de l'exemplaire
	 * modifie la valeur de CmdOk : true si succès - false si échec PpnEncours
	 * doit contenir un ppn, suite à une recherche de notice par exemple
	 * 
	 * @param exemplaire
	 *            numéro de l'exemplaire à supprimer
	 * @return le résultat de la suppression
	 * @throws CBSException Erreur CBS
	 */
	public String supExemplaire(String exemplaire) throws CBSException {
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
	public String supBiblio() throws CBSException {
		return clientCBS.sup(String.valueOf(lotEncours), ppnEncours);
	}

	/**
	 * Rajoute des données locales (L035) à la notice en cours (ppnEncours)
	 * renvoie dans ErrorMessage, le message renvoyé par le CBS suite à la
	 * modification de la donnée locale modifie la valeur de CmdOk : true si
	 * succès - false si échec PpnEncours doit contenir un ppn, suite à une
	 * recherche de notice par exemple
	 * 
	 * @param vloc
	 *            !!!!!!!!!!!!
	 * @return le message renvoyé par le CBS suite à la modification
	 * @throws CBSException Erreur CBS
	 */
	public String modLoc(final String vloc) throws CBSException {
		String resu = clientCBS.modLoc(String.valueOf(lotEncours));
		String noticedeb = Utilitaire.recupEntre(resu, Constants.VTXT, Constants.STR_1F);
		return clientCBS.valModLoc(noticedeb, ppnEncours, String.valueOf(lotEncours), vloc);
	}

	/**
	 * Crée la 1ère donnée locale pour la notice en cours (PpnEncours) envoi de
	 * la commande "cre loc" renvoie dans ErrorMessage, le message renvoyé par
	 * le CBS suite à la création de la donnée locale modifie la valeur de CmdOk
	 * : true si succès - false si échec PpnEncours doit contenir un ppn, suite
	 * à une recherche de notice par exemple
	 *
	 * @param vloc
	 *            !!!!!!!!!!!!
	 * @return le message renvoyé par le CBS suite à la création de la donnée
	 *         locale
	 * @throws CBSException Erreur CBS
	 */
	public String newLoc(final String vloc) throws CBSException {
		return clientCBS.valCreLoc(ppnEncours, String.valueOf(lotEncours), vloc);
	}

	/**
	 * Méthode de suppression du bloc de donnée locale d'une notice
	 *
	 * 2 étapes sont nécessaires :
	 * 		* lancement de la commande sup l affichant le bloc de donnée locale
	 * 		* validation de la suppression
	 * @return message renvoyé par le CBS suite à la suppression
	 * @throws CBSException erreur CBS
	 */
	public String supLoc() throws CBSException {
		clientCBS.supL(String.valueOf(lotEncours));
		return clientCBS.valSupL(String.valueOf(lotEncours), ppnEncours);
	}

}
