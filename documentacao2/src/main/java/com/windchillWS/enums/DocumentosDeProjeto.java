package com.windchillWS.enums;

public enum DocumentosDeProjeto {

	Documento_de_Projeto(""),
	Caderno_de_Construção("net.empresa.cc_caderno_construcao"),
	Diagrama_de_Blocos("net.empresa.db_diagrama_blocos"),
	Desenho_de_Dutos("net.empresa.desenho_dutos"),
	Desenho("net.empresa.de_desenho"),
	Desenho_Isométrico("net.empresa.desenho_isometrico"),
	Desenho_de_Spool("net.empresa.desenho_spool"),
	Diagrama_de_Montagem_Estruturado("net.empresa.dme_diagrama_montagem"),
	Designação_do_Teste("net.empresa.designacao_teste"),
	Folha_de_Corte("net.empresa.fc_folha_corte"),
	Infault("net.empresa.infault"),
	ISOMÉTRICO("net.empresa.iso_isometrico"),
	Instrução_de_Trabalho("net.empresa.it_projeto"),
	Lista_de_Documentos("net.empresa.lista_documentos"),
	Lista_de_Juntas("net.empresa.lista_juntas"),
	Lista("net.empresa.li_lista"),
	Lista_de_Spool("net.empresa.lista_de_spool"),
	Lista_de_Inpeções_e_Testes("net.empresa.lista_insp_testes"),
	Lista_de_Materiais_por_Atividade("net.empresa.lma"),
	Lista_de_Material("net.empresa.lm_lista_material"),
	Memorial_Descritivo("net.empresa.memorial_descritivo"),
	Procedimento("net.empresa.pr_procedimento"),
	Registro_de_Inspeção("net.empresa.registro_inspecao"),
	Requisição_de_Material("net.empresa.rm_requisicao_material");

	private String type;
	public static String typeDocument = "Documento de Projeto";
	public static String prefix = "WCTYPE|wt.doc.WTDocument|net.empresa.empresa_doc|net.empresa.doc_projeto|";

	DocumentosDeProjeto(String type){
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
		
		for (DocumentosDeProjeto tyPe : DocumentosDeProjeto.values()) {			
			if((prefix + tyPe.toString()).equals(clasS)){
				return tyPe.getDescription();
			}
		}		
		throw new EnumConstantNotPresentException(DocumentosDeProjeto.class, "Esse valor não existe");
	}

	public static String getValue(String description) {		
		description = description.replaceAll(" ", "_");		
		for (DocumentosDeProjeto tyPe : DocumentosDeProjeto.values()) {			
			if(description.equals(tyPe.name())){
				return tyPe.toString();
			}
		}		
		throw new EnumConstantNotPresentException(DocumentosDeProjeto.class, "Esse valor não existe");
	}
	
}
