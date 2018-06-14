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

	public ConcurrentRunSaodException(Timestamp since) {
		super(String.format("Compute running since %s", since));

		this.since = since;
	}

	public Timestamp getSince() {
		return since;
	}

}
