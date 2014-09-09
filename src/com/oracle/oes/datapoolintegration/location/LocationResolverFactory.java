package com.oracle.oes.datapoolintegration.location;

import java.io.IOException;

/**
 * Factory for LocationResolverType
 * 
 * @author OCS
 * 
 */
public class LocationResolverFactory {

	// instance used by the factory
	private static LocationResolverType instance;

	/**
	 * Returns the instance.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static LocationResolverType getInstance() {

		if (instance == null) {
			instance = new LocationResolver();
		}

		return instance;

	}

}
