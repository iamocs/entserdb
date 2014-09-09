package com.oracle.oes.datapoolintegration.auth;

/**
 * Response for resolving policies based on conditions. This class is an
 * extensionto SimpleAuthResponse, so a property named allowed is included.
 * 
 * @author OCS
 * 
 */
public class ConditionAuthResponse extends SimpleAuthResponse {

	// id
	private static final long serialVersionUID = 6940613121832503422L;

	// properties
	private String condition;

	// getters and setters
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
