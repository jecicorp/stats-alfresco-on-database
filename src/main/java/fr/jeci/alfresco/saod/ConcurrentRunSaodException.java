package fr.jeci.alfresco.saod;

import java.sql.Timestamp;

/**
 * Specifique Exception to Concurrent Run
 * 
 * @author jlesage
 *
 */
public class ConcurrentRunSaodException extends SaodException {
	private static final long serialVersionUID = -5559284673021880777L;

	private Timestamp since;

	/**
	 * Constructor of a ConcurrentRunSoad exception
	 * @param since : time since the concurrentRunSoadEception is running
	 */
	public ConcurrentRunSaodException(Timestamp since) {
		super(String.format("Compute running since %1$td/%1$tm/%1$tY %1$tT", since));

		this.since = since;
	}

	/**
	 * Return the time since the application is running
	 * @return since : time
	 */
	public Timestamp getSince() {
		return since;
	}

}
