package com.webengage.sdk.android.utils.http;

import java.io.Serializable;

public enum RequestMethod implements Serializable{
	GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");
	private String s;

	RequestMethod(String s) {
		this.s = s;
	}

	public String toString() {
		return s;
	}
}
