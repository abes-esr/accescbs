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

/**
 * Représente une session au CBS
 */
@Slf4j
public class Cbs {
    @Getter
    @Setter
    private boolean cmdOk;
    private Socket s;

    public Cbs() {
    }

    /**
     * socket disconnection
     */
    public void close() throws CBSException {
        disconnect();
    }

    /**
     * socket method connection
     *
     * @param tip  is socket address
     * @param port is socket port
     * @return true si la tentateive de connexion a réussi, false autrement
     */
    public boolean connect(final String tip, final int port) throws CBSException {
        try {
            s = new Socket(tip, port);
            return s.isConnected();
        } catch (Exception e) {
            throw new CBSException(Level.ERROR, "Error connecting to " + tip + " " + port + " : " + e.getMessage());
        }
    }

    /**
     * @param query to send to the socket
     * @return the result of query execution
     */
    public String tcpReq(final String query) throws CBSException, IOException {
        String resu = req(query);
        checkForErrors(resu);
        return resu;
    }


    /**
     * la méthode ferme une connexion tcp/ip avec le CBS
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

    private String req(String query) throws IOException {
        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            query += Constants.STR_29 + Constants.STR_03;
            byte[] bytes = query.getBytes(StandardCharsets.UTF_8);
            out.write(bytes, 0, bytes.length);
            return receiveMessageFromServer();
        } catch (Exception e) {
            log.error("Erreur socket");
            cmdOk = false;
            throw new IOException(e);
        }
    }

    private String receiveMessageFromServer() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = s.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        try {
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = br.read(buffer)) != -1) {
                sb.append(buffer, 0, charsRead);
                if (sb.lastIndexOf(Constants.STR_03) == (sb.length() - 1)) {
                    break;
                }
            }
        } catch (Exception ex) {
            log.error("Erreur réponse : " + ex.getMessage());
            throw ex;
        }
        return sb.toString();
    }

    /**
     * Vérifie la présence d'erreurs dans un retour CBS, et lève une exception si il y en a
     *
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
