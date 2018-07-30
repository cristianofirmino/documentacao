package com.windchillWS.enums;

public enum AttributesFixed {

	number, name, state, docType, fileName, grauSigilo, revision, obid;

	public static String attributes() {

		AttributesFixed[] values = AttributesFixed.values();
		StringBuilder valuesString = new StringBuilder();

		for (int i = 0; i < values.length; i++) {
			valuesString.append(values[i] + " ");
		}

		return valuesString.toString();
	}
}
