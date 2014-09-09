package com.oracle.oes.datapoolintegration.auth;

import java.io.Serializable;

/**
 * Simple response for resolving policies for a result of boolean type
 * (true/false)
 * 
 * @author OCS
 * 
 */
public class SimpleAuthResponse implements Serializable, AuthResponse {

	// id
	private static final long serialVersionUID = -731556467701193496L;

	// properties
	private Boolean allowed;

	// getters and setters
	public Boolean getAllowed() {
		return allowed;
	}

	public void setAllowed(Boolean allowed) {
		this.allowed = allowed;
	}

}
