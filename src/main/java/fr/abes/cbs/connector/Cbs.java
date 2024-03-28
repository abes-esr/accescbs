package fr.abes.cbs.connector;

import fr.abes.cbs.exception.CBSException;
import fr.abes.cbs.utilitaire.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

/** Représente une session au CBS
 */
public class Cbs {
    @Getter @Setter private boolean cmdOk;
    @Getter @Setter private String errorMessage;
    private Socket s;
    private final Integer poll;

    public Cbs(Integer poll) {
        this.poll = poll;
    }
    /**
     * socket disconnection
     */
    public void close() throws CBSException {
        disconnect();
    }

    /**
     * socket method connection
     * @param tip is socket address
     * @param port is socket port
     * @return the error message
     */
    public String connect(final String tip, final int port) throws CBSException {
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
    private String connectTcp(final String tip, final int port) throws CBSException {
        try {
            s = new Socket(tip, port);
            if (s.isConnected()) {
                return "";
            } else {
                return "connect ko";
            }
        } catch (Exception e) {
        	throw new CBSException(Level.ERROR, "Error connecting to " + tip + " " + port + " : " + e.getMessage());
        }
    }

    /**
     * la méthode ferme une connexion tcp/ip avec le CBS
     *
     */
    private void disconnect() throws CBSException {
        try {
            if (s.isConnected()) {
                s.close();
            }
        } catch (Exception e) {
            throw new CBSException(Level.ERROR, "Error disconnecting socket : " + e.getMessage());
        }
    }

     private String req(String query) {
        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            query += Constants.STR_29 + Constants.STR_03;
            byte[] bytes = query.getBytes(StandardCharsets.UTF_8);
            out.write(bytes, 0, bytes.length);

            String res = receiveMessageFromServer();

            if(res.contains(Constants.VERROR)){
                errorMessage = res;
            } else {
                errorMessage = "";
            }
            cmdOk = true;
            return res;
        } catch (IOException  e) {
            cmdOk = false;
            return "Req ko " + e;
        }
    }

    private String receiveMessageFromServer() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = s.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);

        char[] buffer = new char[8192];
        int charsRead;
        br.read();
        int cpt = 0;
        while ((charsRead = br.read(buffer)) != -1) {
            cpt++;
            sb.append(buffer, 0, charsRead);
            if (!br.ready()) {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Vérifie la présence d'erreurs dans un retour CBS, et lève une exception si il y en a
     * @param resu Retour du CBS
     * @throws CBSException Erreur CBS
     */
	private void checkForErrors(String resu) throws CBSException {
        if (resu.isEmpty()) {
            setCmdOk(false);
            throw new CBSException(Level.ERROR, "Erreur inconnue");
        }
		//erreur à l'authentification
		if (resu.contains("V/VREJECT")) {
			setCmdOk(false);
			throw new CBSException(Level.ERROR, resu.substring(2, resu.indexOf(Constants.STR_1D)));
		}
		//erreur cas général
		if (resu.contains("V/VERROR")) {
			setCmdOk(false);
            throw new CBSException(Level.ERROR, resu.substring(resu.indexOf("M02") + 3, resu.indexOf(Constants.STR_1D, resu.indexOf("M02") + 3)));
		}
		setCmdOk(true);
	}
}
