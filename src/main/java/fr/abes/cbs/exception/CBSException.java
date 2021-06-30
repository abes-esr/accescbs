package fr.abes.cbs.exception;

import lombok.Getter;

/**
 * Classe de gestion des exceptions suite à une erreur de commande CBS
 * @author maraval
 *
 */
public class CBSException extends Exception {
	private static final long serialVersionUID = 1L;
	@Getter private final String codeErreur;
	
	public CBSException(String code, String message) {
		super(message);
		codeErreur = code;
	}
}
