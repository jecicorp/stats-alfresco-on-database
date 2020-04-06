package fr.jeci.alfresco.saod;

/**
 * Specific Projet Exception
 * 
 * @author jlesage
 *
 */
public class SaodException extends Exception {
	private static final long serialVersionUID = -943127812908920724L;

	/**
	 * Constructor of a specific SaodException
	 * @param string name of the exception
	 */
	public SaodException(String string) {
		super(string);
	}

	/**
	 * Constructor of a specific SaodException
	 * @param string name of the exception
	 * @param e the exception
	 */
	public SaodException(String string, Exception e) {
		super(string, e);
	}

}
