package fr.abes.cbs.connector;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.utilitaire.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/** Représente une session au CBS
 */

@Slf4j
public class Cbs {
    @Getter @Setter private boolean cmdOk;
    @Getter @Setter private String errorMessage;
    private Socket s;

    /**
     * socket disconnection
     */
    public void close() {
        disconnect();
    }

    /**
     * socket method connection
     * @param tip is socket address
     * @param port is socket port
     * @return the error message
     */
    public String connect(final String tip, final int port) {
        errorMessage = connectTcp(tip, port);
        return errorMessage;
    }

    /**
     * @param query to send to the socket
     * @return the result of query execution
     */
    public String tcpReq(final String query) throws CBSException {
        String resu = req(query);
        checkForErrors(resu);
        return resu;
    }

    /**
     * Pour ouvrir une connexion tcp/ip avec le CBS.
     *
     * @param tip L'adresse ip du serveur (CBS) avec lequel on veut ouvrir une connexion.
     * @param port le n° de port.
     * @return le résultat de la connexion tcp/ip
     *
     */
    private String connectTcp(final String tip, final int port) {
        try {
            s = new Socket(tip, port);
            if (s.isConnected()) {
                return "";
            } else {
                return "connect ko";
            }
        } catch (Exception e) {
        	log.error("Error connecting to "+tip+" "+port, e);
            return "ko " + e.toString();
        }
    }

    /**
     * la méthode ferme une connexion tcp/ip avec le CBS
     *
     */
    private void disconnect() {
        try {
            if (s.isConnected()) {
                s.close();
            }
        } catch (Exception e) {
        	log.error("Error disconnecting socket", e);
        }
    }
    /**
     * la méthode envoie une requête au CBS
     * @param query la requête a envoyer
     * @return le résultat de l'exécution de la requête
     *
     */
    private String req(final String query) {
        try {
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            String chaine1 = String.valueOf((char) 29);
            String chaine2 = String.valueOf((char) 03);
            String myString = query + chaine1 + chaine2;
            byte[] bytes = myString.getBytes("UTF-8");
            out.write(bytes, 0, bytes.length);
            int nb;
            int nbtours = 0;
            StringBuilder resu = new StringBuilder();
            nb = in.read();
            while (nb > 0) {
                byte[] red = new byte[nb];
                in.read(red, 0, nb);
                String res = new String(red, "UTF-8");
                resu.append(res);
                nb = in.available();
                while (nbtours < 20 && nb == 0) {
                    if(resu.toString().contains("\u0003")){
                        break;
                    }
                    nbtours++;
                    Thread.sleep(10);
                    nb = in.available();
                }
            }
            String res = resu.toString();
            if(res.contains(Constants.VERROR)){
            	errorMessage = res;
            } else {
            	errorMessage = "";
            }
            cmdOk = true;
            return res;
        } catch (IOException | InterruptedException e) {
        	log.error("Error executing query "+query, e);
            cmdOk = false;
            return new StringBuilder("Req ko " + e.toString()).toString();
        }
    }

    /**
     * Vérifie la présence d'erreurs dans un retour CBS, et lève une exception si il y en a
     * @param resu Retour du CBS
     * @throws CBSException Erreur CBS
     */
	private void checkForErrors(String resu) throws CBSException {
		//erreur à l'authentification
		if (resu.contains("V/VREJECT")) {
			setCmdOk(false);
			throw new CBSException("V/VREJECT", resu.substring(2, resu.indexOf(Constants.STR_1D)));
		}
		//erreur cas général
		if (resu.contains("V/VERROR")) {
			setCmdOk(false);
            throw new CBSException("V/VERROR", resu.substring(resu.indexOf("M02") + 3, resu.indexOf(Constants.STR_1D, resu.indexOf("M02") + 3)));
		}
		setCmdOk(true);
	}
}
