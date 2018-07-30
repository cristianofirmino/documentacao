package com.windchillWS.bean;

public class DocumentRequest {
	private String numero;
	private String revisao;
	private String caderno;

	public DocumentRequest() {
		super();
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRevisao() {
		return revisao;
	}

	public void setRevisao(String revisao) {
		this.revisao = revisao;
	}

	public String getCaderno() {
		return caderno;
	}

	public void setCaderno(String caderno) {
		this.caderno = caderno;
	}
}
