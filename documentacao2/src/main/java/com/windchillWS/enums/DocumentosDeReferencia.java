package com.windchillWS.enums;

public enum DocumentosDeReferencia {

	Documento_de_Referência(""),
	Contratos("net.empresa.contratos"),
	COR_DCNS("net.empresa.cor_dcns"),
	COS("net.empresa.cos"),
	Declarações("net.empresa.declaracoes"),
	DOCS_Gamatron("net.empresa.docs_gamatron"),
	DOCS_Nuclep("net.empresa.docs_nuclep"),
	Documento_CBS("net.empresa.documentocbs"),
	Documento_DCNS("net.empresa.documentodcns"),
	Documento_EBN("net.empresa.documento_ebn"),
	Documento_Forcenedores("net.empresa.documentofornecedores"),
	Documento_MARINHA("net.empresa.documentomarinha"),
	Documento_Subcontratado("net.empresa.documentosubcontratado"),
	Fatos_Externos_Geradores_de_Impacto("net.empresa.doc_fegi"),
	Fotos_DCNS("net.empresa.fotos_dcns"),
	NCR("net.empresa.ncr"),
	NCR_CONFIG("net.empresa.ncr|net.empresa.ncr_config"),
	NCR_UFEM("net.empresa.ncr|net.empresa.ncr_ufem"),
	NCR_UFER("net.empresa.ncr|net.empresa.ncr_ufer"),
	Norma_Técnica("net.empresa.norma_tecnica"),
	Pr_Addendum("net.empresa.praddendum"),
	Publicações("net.empresa.publicacoes"),
	QAF("net.empresa.qaf"),
	RETEC("net.empresa.retec"),
	TAF("net.empresa.taf"),
	TAF_NUCLEP("net.empresa.taf_nuclep"),
	TDP_IS("net.empresa.tdp_is"),
	TIR("net.empresa.tir");

	private String type;
	public static String typeDocument = "Documento de Referência";
	public static String prefix = "WCTYPE|wt.doc.WTDocument|net.empresa.empresa_doc|net.empresa.doc_ref|";

	DocumentosDeReferencia(String type){
		this.type = type;
	}

	@Override
	public String toString() {		
		return this.type;
	}
		
	public String getDescription(){		
		return this.name().replaceAll("_", " ");
	}
	
	public static String getDescription(String clasS) {
		
		for (DocumentosDeReferencia tyPe : DocumentosDeReferencia.values()) {			
			if((prefix + tyPe.toString()).equals(clasS)){
				return tyPe.getDescription();
			}
		}		
		throw new EnumConstantNotPresentException(DocumentosDeReferencia.class, "Esse valor não existe: " + clasS);
	}

	public static String getValue(String description) {		
		description = description.replaceAll(" ", "_");		
		for (DocumentosDeReferencia tyPe : DocumentosDeReferencia.values()) {			
			if(description.equals(tyPe.name())){
				return tyPe.toString();
			}
		}		
		throw new EnumConstantNotPresentException(DocumentosDeReferencia.class, "Esse valor não existe: " + description);
	}	
		
}
