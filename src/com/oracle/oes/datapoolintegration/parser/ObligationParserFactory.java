package com.oracle.oes.datapoolintegration.parser;

/**
 * Factory for ObligationParserType
 * 
 */
public class ObligationParserFactory {

	// instance used by the factory
	private static ObligationParserType instance;

	/**
	 * Returns the instance.
	 * 
	 * @return
	 */
	public static ObligationParserType getInstance() {

		if (instance == null) {
			instance = new ObligationParser();
		}

		return instance;

	}

}
