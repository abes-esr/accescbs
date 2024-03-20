package fr.abes.cbs.exception;

import lombok.Getter;
import org.apache.logging.log4j.Level;

/**
 * Classe de gestion des exceptions suite à une erreur de commande CBS
 * @author maraval
 *
 */
public class CBSException extends Exception {
	private static final long serialVersionUID = 1L;
	@Getter private final Level codeErreur;
	
	public CBSException(Level code, String message) {
		super(message);
		codeErreur = code;
	}
}
