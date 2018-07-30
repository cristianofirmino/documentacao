package com.windchillWS.model;

public class DocumentType {

	private String description;
	private String docTypeCode;
	private String type;

	public DocumentType(String description, String docTypeCode, String type) {

		this.description = description;
		this.docTypeCode = docTypeCode;
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDocTypeCode() {
		return docTypeCode;
	}

	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DocumentType [description=" + description + ", docTypeCode=" + docTypeCode + ", type=" + type + "]";
	}
	
	

}
