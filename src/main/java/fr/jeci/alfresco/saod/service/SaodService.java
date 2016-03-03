package fr.jeci.alfresco.saod.service;

import fr.jeci.alfresco.saod.SaodException;

public interface SaodService {

	/**
	 * Load data from Alfresco DB into local DB
	 * @throws SaodException 
	 */
	void loadDataFromAlfrescoDB() throws SaodException;

}
