package com.windchillWS.enums;

public enum DocumentosDeGerenciamento {

	Documentos_De_Gerenciamento(""),
	Cronograma("net.empresa.cr_cronograma"),
	Declarações("net.empresa.dc_declaracoes"),
	Desenho("net.empresa.de_gerenciamento"),
	Especificação_Técnica("net.empresa.et_especificacao_tecnica"),
	Fluxograma("net.empresa.fl_fluxograma"),
	Histograma("net.empresa.histograma"),
	Instrução_de_Trabalho_Geral("net.empresa.it_instrucao_trabalho_geral"),
	Lista_Geral("net.empresa.li_lista_geral"),
	Manual("net.empresa.ma_manual"),
	Memória_de_Cálculo("net.empresa.memoria_de_calculo"),
	Memorial_Descritivo_Geral("net.empresa.md_memorial_descritivo_geral"),
	Matriz("net.empresa.mt_matriz"),
	Norma_Administrativa("net.empresa.na_norma_administrativa"),
	Programa("net.empresa.pg_programa"),
	Plano("net.empresa.pl_plano"),
	Política("net.empresa.po_politica"),
	Procedimento_Geral("net.empresa.pr_procedimento_geral"),
	Parecer_Técnico_Geral("net.empresa.pt_parecer_tecnico_geral"),
	RETEC_MTP_Brasil("net.empresa.retec_brasil"),
	Relatório("net.empresa.rl_relatorio");

	private String type;
	public static String typeDocument = "Documento de Gerenciamento";
	public static String prefix = "WCTYPE|wt.doc.WTDocument|net.empresa.empresa_doc|net.empresa.doc_gerenciamento|";

	DocumentosDeGerenciamento(String type){
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
		
		for (DocumentosDeGerenciamento tyPe : DocumentosDeGerenciamento.values()) {			
			if((prefix + tyPe.toString()).equals(clasS)){
				return tyPe.getDescription();
			}
		}		
		throw new EnumConstantNotPresentException(DocumentosDeGerenciamento.class, "Esse valor não existe");
	}

	public static String getValue(String description) {		
		description = description.replaceAll(" ", "_");		
		for (DocumentosDeGerenciamento tyPe : DocumentosDeGerenciamento.values()) {			
			if(description.equals(tyPe.name())){
				return tyPe.toString();
			}
		}		
		throw new EnumConstantNotPresentException(DocumentosDeGerenciamento.class, "Esse valor não existe");
	}
}
