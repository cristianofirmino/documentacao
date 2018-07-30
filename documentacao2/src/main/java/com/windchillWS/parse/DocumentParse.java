package com.windchillWS.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.windchillWS.enums.DocumentosDeGerenciamento;
import com.windchillWS.enums.DocumentosDeProjeto;
import com.windchillWS.enums.DocumentosDeReferencia;

public class DocumentParse {

	private String getObid(String obid) {
		try {
			String[] chaves = obid.split(":");
			if (chaves.length > 2) {
				String obid2 = chaves[2].substring(0, 5);
				return obid2;
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}
	
	public String parseDate(String dateParse) {

		if (dateParse.contains(" BRT")) {
			dateParse = dateParse.replaceAll(" BRT", "");
		} else if (dateParse.contains(" BRST")) {
			dateParse = dateParse.replaceAll(" BRST", "");
		}

		SimpleDateFormat formatWindchill = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dateWindchill = formatWindchill.parse(dateParse);
			SimpleDateFormat formatoFrontEnd = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
			return formatoFrontEnd.format(dateWindchill).toString();
		} catch (ParseException e) {
			System.out.println(e.getLocalizedMessage());
		}

		return "";
	}
	
	public String parseOneJson(JSONObject objectJsonArray) {
		JSONArray jsonArray = new JSONArray();
		JSONObject objectJson = objectJsonArray.getJSONObject("wc:INSTANCE");
		jsonArray.put(objectJson);

		if (objectJson.get("class").toString().contains("net.empresa.doc_ref")) {
			String clasS = DocumentosDeReferencia.getDescription(objectJson.get("class").toString());
			objectJson.remove("class");
			objectJson.put("class", clasS);
			objectJson.put("download", "document/download?number=" + objectJson.get("number").toString());
			objectJson.put("fileName", "sem conteúdo");
		}

		if (objectJson.get("class").toString().contains("net.empresa.doc_projeto")) {
			String clasS = DocumentosDeProjeto.getDescription(objectJson.get("class").toString());
			objectJson.remove("class");
			objectJson.put("class", clasS);
			objectJson.put("download", "document/download?number=" + objectJson.get("number").toString());
			objectJson.put("fileName", "sem conteúdo");
		}
		
		if (objectJson.get("class").toString().contains("net.empresa.doc_gerenciamento")) {
			String clasS = DocumentosDeGerenciamento.getDescription(objectJson.get("class").toString());
			objectJson.remove("class");
			objectJson.put("class", clasS);
			objectJson.put("download", "document/download?number=" + objectJson.get("number").toString());
			objectJson.put("fileName", "sem conteúdo");
		}

		if (jsonArray != null && jsonArray.length() > 0) {
			for (Iterator iterator = jsonArray.iterator(); iterator.hasNext();) {
				JSONObject obj = (JSONObject) iterator.next();
				if (obj.get("class").toString().equalsIgnoreCase("wt.content.ApplicationData"))
					iterator.remove();

				obj.put("modifyDate", parseDate(obj.getString("thePersistInfo.modifyStamp").toString()));
				obj.remove("thePersistInfo.modifyStamp");
				obj.put("createDate", parseDate(obj.getString("thePersistInfo.createStamp").toString()));
				obj.remove("thePersistInfo.createStamp");
			}
		}

		return jsonArray.toString();
	}
	
	private String parseMultipleJson(JSONArray arrayJson) {

		if (arrayJson == null || arrayJson.length() == 0)
			return "[]";

		Map<String, String> files = new HashMap<String, String>();
		Map<String, String> files2 = new HashMap<String, String>();
		Map<String, String> files3 = new HashMap<String, String>();

		if (arrayJson != null && arrayJson.length() > 0) {
			for (Iterator iterator = arrayJson.iterator(); iterator.hasNext();) {
				JSONObject jsonObject = (JSONObject) iterator.next();
				if (jsonObject.get("class") != null
						&& jsonObject.get("class").toString().indexOf("wt.content.ApplicationData") != -1) {
					if (jsonObject.getString("role").equals("PRIMARY")) {
						String obid = getObid(jsonObject.getString("obid"));
						if (obid != null) {
							files3.put(obid, jsonObject.getString("fileName"));
						}

						files.put(jsonObject.getString("wt.content.HolderToContent.theContentHolder"),
								jsonObject.getString("fileName"));
						files2.put(jsonObject.getString("obid"), jsonObject.getString("fileName"));

					}

					iterator.remove();
				}

			}
		}

		for (Object object : arrayJson) {

			JSONObject obj = (JSONObject) object;

			JSONArray classArr = obj.getJSONArray("class");
			String name = files.get(obj.getString("obid"));
			if (name == null)
				name = files2.get(obj.getString("obid"));
			String obid = getObid(obj.getString("obid"));
			if (name == null && obid != null)
				name = files3.get(obid);
			if (name != null)
				obj.put("fileName", name);
			else
				obj.put("fileName", "");

			if (classArr != null && classArr.length() > 0) {
				String classe = classArr.getString(0);
				if (classe.contains("net.empresa.doc_ref")) {
					String clasS = DocumentosDeReferencia.getDescription(classe);
					obj.remove("class");
					obj.put("class", clasS);
					obj.put("download", "document/download?number=" + obj.get("number").toString()
							+ "&revision=" + obj.get("revision").toString());
				}

				if (classe.contains("net.empresa.doc_projeto")) {
					String clasS = DocumentosDeProjeto.getDescription(classe);
					obj.remove("class");
					obj.put("class", clasS);
					obj.put("download", "document/download?number=" + obj.get("number").toString()
							+ "&revision=" + obj.get("revision").toString());
				}
				
				if (classe.contains("net.empresa.doc_gerenciamento")) {
					String clasS = DocumentosDeGerenciamento.getDescription(classe);
					obj.remove("class");
					obj.put("class", clasS);
					obj.put("download", "document/download?number=" + obj.get("number").toString()
							+ "&revision=" + obj.get("revision").toString());
				}

				obj.put("modifyDate", (parseDate((obj.getJSONArray("thePersistInfo.modifyStamp")).getString(0))));
				obj.remove("thePersistInfo.modifyStamp");
				obj.put("createDate", (parseDate((obj.getJSONArray("thePersistInfo.createStamp")).getString(0))));
				obj.remove("thePersistInfo.createStamp");

			}
		}
		return arrayJson.toString();
	}
}
