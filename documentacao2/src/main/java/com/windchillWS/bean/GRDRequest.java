package com.windchillWS.bean;

import java.util.List;

public class GRDRequest {

	public GRDRequest() {
		super();
	}

	private String grd;
	private String setor;
	private String submarino;
	private List<DocumentRequest> documentos;

	public String getGrd() {
		return grd;
	}

	public void setGrd(String grd) {
		this.grd = grd;
	}

	public List<DocumentRequest> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<DocumentRequest> documentos) {
		this.documentos = documentos;
	}

	public String getSetor() {
		return setor;
	}

	public void setSetor(String setor) {
		this.setor = setor;
	}

	public String getSubmarino() {
		return submarino;
	}

	public void setSubmarino(String submarino) {
		this.submarino = submarino;
	}

}
