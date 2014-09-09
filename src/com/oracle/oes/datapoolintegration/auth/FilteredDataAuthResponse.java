package com.oracle.oes.datapoolintegration.auth;

/**
 * Reponse for resolving policies based on the filter of some elements.
 * 
 * @author OCS
 * 
 */
public class FilteredDataAuthResponse extends SimpleAuthResponse {

	// id
	private static final long serialVersionUID = -9005935026551781531L;

	// properties
	private Object object;

	// getters and setters
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
