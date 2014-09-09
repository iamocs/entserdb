package com.oracle.oes.datapoolintegration.location;

/**
 * Interface used by the ObligationParserType for resolving Locations based.
 * 
 * @author OCS
 * 
 */
public interface LocationResolverType {

	/**
	 * Returns the location represented by a name.
	 * 
	 * @param name
	 *            Name of the location
	 * @return Location as a String
	 * @throws Exception
	 */
	public abstract String getLocation(String name);

}