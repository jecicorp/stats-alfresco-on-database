package fr.jeci.alfresco.saod;

/**
 * Specific Projet Exception
 * 
 * @author jlesage
 *
 */
public class SaodException extends Exception {
	private static final long serialVersionUID = -943127812908920724L;

	public SaodException(String string) {
		super(string);
	}

	public SaodException(String string, Exception e) {
		super(string, e);
	}

}
