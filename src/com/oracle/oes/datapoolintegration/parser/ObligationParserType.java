package com.oracle.oes.datapoolintegration.parser;

import java.util.Map;

import org.openliberty.openaz.azapi.pep.Obligation;

/**
 * Interface used by the OESWrapper for translation OES Obligation maps into a
 * query.
 * 
 * @author OCS
 * 
 */
public interface ObligationParserType {

	/**
	 * Parse the obligations in the Map object into a query, returned as a
	 * String.
	 * 
	 * @param obligations
	 *            Map of obligations returned by the OES
	 * @return Query String
	 * @throws Exception
	 */
	public String parseObligations(Map<String, Obligation> obligations) throws Exception;

}
