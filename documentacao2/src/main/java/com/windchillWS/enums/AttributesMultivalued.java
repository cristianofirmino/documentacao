package com.windchillWS.enums;

public enum AttributesMultivalued {

	answerAtt("empresa.ANSWER_ATT"),
	answerDate("empresa.ANSWER_DATE"),
	answerNumber("empresa.ANSWER_NUMBER"),
	answerWriter("empresa.ANSWER_WRITER"),
	areaAtuacao("empresa.AREA_ATUACAO"),
	buscaReferencia("empresa.BUSCA_REFERENCIA"),
	empresaCmd("empresa.CMD"),
	objType("empresa.NUM_OBJ_TYPE"),
	empresaOutros("empresa.OUTROS"),
	palavraChave("empresa.PALAVRA_CHAVE"),
	questionAtt("empresa.QUESTION_ATT"),
	empresaRevisor("empresa.REVISOR"),
	sitFunc("empresa.SIT_FUNC"),
	sitGeo("empresa.SIT_GEO"),
	sitOutros("empresa.SIT_OUTROS");

	
	private String name;

	private AttributesMultivalued(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static String attributes() {

		AttributesMultivalued[] values = AttributesMultivalued.values();
		StringBuilder valuesString = new StringBuilder();

		for (int i = 0; i < values.length; i++) {
			valuesString.append(values[i] + " ");
		}

		return valuesString.toString();
	}
	
}
