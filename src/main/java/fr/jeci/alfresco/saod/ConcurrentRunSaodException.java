package fr.jeci.alfresco.saod;

/**
 * Specifique Exception to Concurrent Run
 * 
 * @author jlesage
 *
 */
public class ConcurrentRunSaodException extends SaodException {
	private static final long serialVersionUID = -5559284673021880777L;

	public ConcurrentRunSaodException(String string) {
		super(string);
	}

}
