package com.windchillWS.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.windchillWS.constants.Configuration;
import com.windchillWS.enums.DocumentosDeGerenciamento;
import com.windchillWS.enums.DocumentosDeProjeto;
import com.windchillWS.enums.DocumentosDeReferencia;
import com.windchillWS.exception.BusinessException;
import com.windchillWS.model.DocumentType;
import com.windchillWS.utility.AtributtesAndValuesUtil;
import com.windchillWS.utility.FileCopyUtil;
import com.windchillWS.utility.PDFWatermarkShrink;
import com.windchillWS.utility.ZipFileConvert;

@Service
public class DocumentService {

	private String folderPath = System.getenv().get("PATH_DOWNLOAD");

	private String urlTask = null;
	private HttpClient httpClient = new HttpClient();
	private PostMethod postMethod = null;
	UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("wcadmin", "pass");

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

	public String createDocument(JSONObject jsonDocument) {

		urlTask = new String(Configuration.URL_WC + "Windchill/servlet/IE/tasks/test/createDocument.xml");
		postMethod = new PostMethod(urlTask);
		StringBuilder resp = new StringBuilder();

		if (!jsonDocument.isNull("fileName")) {

			try {
				FileCopyUtil.copyFileToUpload(jsonDocument.get("fileName").toString());
			} catch (Exception e) {
				return resp.append(jsonDocument.get("fileName").toString() + " " + e.getLocalizedMessage() + " file\n")
						.toString();
			}
		}

		Map<String, String> attributesAndValuesFixed = AtributtesAndValuesUtil
				.getAttributesAndValuesFixed(jsonDocument);
		Map<String, String> attributesAndValuesOthers = AtributtesAndValuesUtil
				.getAttributesAndValuesOthers(jsonDocument);
		Map<String, String> attributesAndValuesMultivalued = AtributtesAndValuesUtil
				.getAttributesAndValuesMultivalued(jsonDocument);

		int i = 0;

		for (Map.Entry<String, String> entry : attributesAndValuesFixed.entrySet()) {

			i++;

			String attribute = entry.getKey();
			String value = entry.getValue();

			if (!value.isEmpty()) {
				postMethod.addParameter(attribute, value);
			}

		}

		for (Map.Entry<String, String> entry : attributesAndValuesOthers.entrySet()) {

			i++;

			String attribute = entry.getKey();
			String value = entry.getValue();

			if (!value.isEmpty()) {
				postMethod.addParameter(attribute, value);
			}

		}

		for (Map.Entry<String, String> entry : attributesAndValuesMultivalued.entrySet()) {

			i++;

			String attribute = entry.getKey();
			String value = entry.getValue();

			if (!value.isEmpty()) {
				postMethod.addParameter(attribute, value);
			}

		}

		httpClient.getState().setAuthenticationPreemptive(true);
		httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);

		try {

			httpClient.executeMethod(postMethod);

		} catch (Exception e) {

			resp.append(e.getLocalizedMessage());

			return resp.toString();
		}

		if (postMethod.getStatusCode() == HttpStatus.SC_OK) {

			resp.append(postMethod.getResponseBodyAsString());

			return resp.toString();

		} else {

			System.out.println(postMethod.getStatusLine());
			resp.append(postMethod.getStatusLine());

			return resp.toString();

		}

	}

	private String parseDate(String dateParse) {

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

	private String parseMultipleJson(JSONArray arrayJson) {

		JSONArray arrayJsonParsed = new JSONArray();

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

		arrayJson.forEach(j -> {

			JSONObject obj = (JSONObject) j;

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
				obj.remove("class");
				obj.put("class", classe);
				obj.put("download", "document/download?number=" + obj.get("number").toString() + "&revision="
						+ obj.get("revision").toString());
				obj.put("modifyDate", (parseDate((obj.getJSONArray("thePersistInfo.modifyStamp")).getString(0))));
				obj.remove("thePersistInfo.modifyStamp");
				obj.put("createDate", (parseDate((obj.getJSONArray("thePersistInfo.createStamp")).getString(0))));
				obj.remove("thePersistInfo.createStamp");
			}

			if (obj.get("GRAU_SIGILO").equals("OSTENSIVO") || obj.get("GRAU_SIGILO").equals(null)) {
				obj.remove("GRAU_SIGILO");
				obj.put("GRAU_SIGILO", "OSTENSIVO");
				arrayJsonParsed.put(obj);
			}

		});

		System.out.println("tamanho: " + arrayJson.length());
		System.out.println("tamanho: " + arrayJsonParsed.length());
		return arrayJsonParsed.toString();
	}

	public String parseOneJson(JSONObject objectJsonArray) {
		JSONArray jsonArray = new JSONArray();
		JSONObject objectJson = objectJsonArray.getJSONObject("wc:INSTANCE");

		if (objectJson.get("GRAU_SIGILO").equals("OSTENSIVO") || objectJson.get("GRAU_SIGILO").equals(null)) {

			objectJson.remove("GRAU_SIGILO");
			objectJson.put("GRAU_SIGILO", "OSTENSIVO");
			jsonArray.put(objectJson);
			objectJson.put("download", "document/download?number=" + objectJson.get("number").toString());
			objectJson.put("fileName", "sem conteúdo");

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
		}

		return jsonArray.toString();
	}

	public String indexSearchDocument(String keyword) throws BusinessException {

		urlTask = new String(Configuration.URL_WC + "Windchill/servlet/IE/tasks/test/indexSearchDocument.xml");
		postMethod = new PostMethod(urlTask);
		StringBuilder resp = new StringBuilder();

		System.out.println("-------------------- Dados enviados para Task---------------------");
		System.out.println(urlTask);

		postMethod.addParameter("keyword", keyword);

		System.out.println("POST param " + postMethod.getParameter("keyword").toString());

		httpClient.getState().setAuthenticationPreemptive(true);
		httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);

		try {

			httpClient.executeMethod(postMethod);

		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		if (postMethod.getStatusCode() == HttpStatus.SC_OK) {

			resp.append(postMethod.getResponseBodyAsString());
			JSONObject jsonFromWindchill = XML.toJSONObject(resp.toString());

			try {
				JSONObject collection = jsonFromWindchill.getJSONObject("wc:COLLECTION");
				if (collection != null) {
					JSONObject objectJsonArray = collection.getJSONObject("wt.fc.WTObject");
					if (objectJsonArray != null) {

						JSONArray arrayJson = new JSONArray();
						try {
							arrayJson = objectJsonArray.getJSONArray("wc:INSTANCE");
						} catch (Exception e) {
							return parseOneJson(objectJsonArray);
						}

						return parseMultipleJson(arrayJson);
					}
				}
				return new JSONArray().toString();
				
			} catch (Exception e) {
				
				throw new BusinessException("Os critérios da busca ultrapassaram o limite ou nenhum resultado foi encontrado");
			}

		} else {

			System.out.println(postMethod.getStatusLine());
			throw new BusinessException(postMethod.getStatusLine().toString());
		}

	}

	public String searchDocument(String parameters) {

		urlTask = new String(Configuration.URL_WC + "Windchill/servlet/IE/tasks/test/searchDocument");
		postMethod = new PostMethod(urlTask);
		StringBuilder resp = new StringBuilder();

		JSONObject jsonObject = new JSONObject(parameters);
		Iterator<String> keys = jsonObject.keys();

		System.out.println("-------------------- Dados enviados para Task---------------------");
		System.out.println(urlTask);

		while (keys.hasNext()) {

			String key = keys.next();
			postMethod.addParameter(key, jsonObject.get(key).toString());

		}

		System.out.println("POST param " + postMethod.getParameter("name").toString());
		System.out.println("POST param " + postMethod.getParameter("number").toString());
		System.out.println("POST param " + postMethod.getParameter("docType").toString());
		System.out.println("POST param " + postMethod.getParameter("revision").toString());

		httpClient.getState().setAuthenticationPreemptive(true);
		httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

		try {
			httpClient.executeMethod(postMethod);

		} catch (Exception e) {

			resp.append(e.getLocalizedMessage());
			JSONObject jsonFromWindchill = XML.toJSONObject(resp.toString());

			return jsonFromWindchill.toString();
		}

		if (postMethod.getStatusCode() == HttpStatus.SC_OK) {

			resp.append(postMethod.getResponseBodyAsString());
			JSONObject jsonFromWindchill = XML.toJSONObject(resp.toString());

			try {
				JSONObject collection = jsonFromWindchill.getJSONObject("wc:COLLECTION");
				if (collection != null) {
					JSONObject objectJsonArray = collection.getJSONObject("wt.fc.WTObject");
					if (objectJsonArray != null) {
						JSONArray arrayJson;
						try {
							arrayJson = objectJsonArray.getJSONArray("wc:INSTANCE");
						} catch (Exception e) {

							return parseOneJson(objectJsonArray);

						}

						return parseMultipleJson(arrayJson);
					}
				}

				return new JSONArray().toString();
			} catch (Exception ex) {
				JSONArray jsonArray = new JSONArray();
				System.out.println(ex.getLocalizedMessage());

				return jsonArray.toString();
			}

		} else {
			resp.append(postMethod.getStatusLine());
			JSONObject jsonFromWindchill = XML.toJSONObject(resp.toString());

			return jsonFromWindchill.toString();
		}
	}

	public String searchRelatedDocument(String parameters) {

		urlTask = new String(Configuration.URL_WC + "Windchill/servlet/IE/tasks/test/searchRelatedDocument");
		postMethod = new PostMethod(urlTask);
		StringBuilder resp = new StringBuilder();
		JSONObject jsonObject = new JSONObject(parameters);
		Iterator<String> keys = jsonObject.keys();

		System.out.println("-------------------- Dados enviados para Task---------------------");
		System.out.println(urlTask);

		while (keys.hasNext()) {
			String key = keys.next();
			postMethod.addParameter(key, jsonObject.get(key).toString());
		}

		System.out.println("POST param " + postMethod.getParameter("number").toString());
		System.out.println("POST param " + postMethod.getParameter("revision").toString());
		httpClient.getState().setAuthenticationPreemptive(true);
		httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

		try {
			httpClient.executeMethod(postMethod);
		} catch (Exception e) {
			resp.append(e.getLocalizedMessage());
			JSONObject jsonFromWindchill = XML.toJSONObject(resp.toString());
			return jsonFromWindchill.toString();
		}

		if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
			resp.append(postMethod.getResponseBodyAsString());
			JSONObject jsonFromWindchill = XML.toJSONObject(resp.toString());

			try {
				JSONObject collection = jsonFromWindchill.getJSONObject("wc:COLLECTION");
				if (collection != null) {
					JSONObject objectJsonArray = collection.getJSONObject("wt.fc.WTObject");
					if (objectJsonArray != null) {
						JSONArray arrayJson;
						try {
							arrayJson = objectJsonArray.getJSONArray("wc:INSTANCE");
						} catch (Exception e) {
							return parseOneJson(objectJsonArray);
						}
						return parseMultipleJson(arrayJson);
					}
				}
				return new JSONArray().toString();
			} catch (Exception ex) {
				JSONArray jsonArray = new JSONArray();
				System.out.println(ex.getLocalizedMessage());
				return jsonArray.toString();
			}

		} else {
			resp.append(postMethod.getStatusLine());
			JSONObject jsonFromWindchill = XML.toJSONObject(resp.toString());
			return jsonFromWindchill.toString();
		}
	}

	public HttpEntity<byte[]> downloadDocument(String number, String revision)
			throws ClientProtocolException, IOException {

		HttpEntity<byte[]> manipulateFileEntity = null;
		folderPath = new String(folderPath + "nao_controlado" + Configuration.FILE_SEPARATOR);

		String fileName = number.replace("/", "-") + "_" + revision + ".pdf";
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		System.out.println(folderPath);

		HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.CONTENT_TYPE, "application/pdf");
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "\"" + fileName + "\"");

		if (Arrays.asList(listOfFiles).stream().anyMatch(f -> f.getName().equals(fileName))) {

			try {
				File file = new File(folderPath + fileName);
				FileInputStream fis = new FileInputStream(file);
				byte[] fileByteArray = IOUtils.toByteArray(fis);
				header.setContentLength(fileByteArray.length);
				manipulateFileEntity = new HttpEntity<byte[]>(fileByteArray, header);
				fis.close();

				System.out.println("Arquivo encontrado " + fileName + "...");
				System.out.println(
						"...................................................................................................");
				return manipulateFileEntity;

			} catch (Exception e) {
				System.out.println(fileName + "\n" + e.getMessage());
			}

		} else {

			System.out.println("Arquivo não encontrado " + fileName + "...");

			urlTask = new String(
					Configuration.URL_WC + "Windchill/servlet/IE/tasks/test/downloadDocumentNaoControlado");
			postMethod = new PostMethod(urlTask);
			postMethod.addParameter("number", number);
			postMethod.addParameter("revision", revision);

			httpClient.getState().setAuthenticationPreemptive(true);
			httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);
			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

			try {

				httpClient.executeMethod(postMethod);
				System.out.println("Chamou a Task para o arquivo " + fileName + "...");
				System.out.println(urlTask);

				while (true) {

					TimeUnit.MILLISECONDS.sleep(10);
					listOfFiles = folder.listFiles();

					if (Arrays.asList(listOfFiles).stream().anyMatch(f -> f.getName().equals(fileName))) {
						String fraseLinha2 = "CÓPIA NÃO CONTROLADA";
						File file = new File(folderPath + fileName);
						FileInputStream fis = new FileInputStream(file);
						byte[] manipulatePdfByteArray = new PDFWatermarkShrink().manipulatePdf(fis, "", fraseLinha2, "",
								"");
						header.setContentLength(manipulatePdfByteArray.length);
						manipulateFileEntity = new HttpEntity<byte[]>(manipulatePdfByteArray, header);
						fis.close();
						FileUtils.writeByteArrayToFile(new File(folderPath + fileName), manipulatePdfByteArray);
						System.out.println("Arquivo encontrado pós chamada da Task..." + fileName + "...");
						System.out.println(
								"...................................................................................................");
						return manipulateFileEntity;

					}
				}

			} catch (Exception e) {

				System.out.println(fileName + "\n" + e.getMessage());
			}
		}

		return manipulateFileEntity;

	}

	public HttpEntity<byte[]> downloadZipFileDocument(String JsonDocuments) {

		JSONArray jsonArray = new JSONArray(JsonDocuments);
		List<String> fileNameArray = new ArrayList<>();
		List<byte[]> byteArrayList = new ArrayList<>();

		Map<String, Integer> fileNameDuplicateMap = new HashMap<String, Integer>();

		for (int i = 0; i < jsonArray.length(); i++) {

			JSONObject jsonObject = (JSONObject) jsonArray.get(i);

			String number = jsonObject.get("number").toString();
			String revision = jsonObject.get("revision").toString();

			try {

				HttpEntity<byte[]> downloadDocument = downloadDocument(number, revision);

				String fileString = Arrays.asList(downloadDocument.getHeaders()
						.getFirst(HttpHeaders.CONTENT_DISPOSITION).replaceAll("\"", "").split("=")).get(1);

				fileNameArray.add(fileString);
				byteArrayList.add(downloadDocument.getBody());

			} catch (Exception e) {
				System.out.println(number + " " + revision + "\n" + e.getMessage());
			}
		}

		byte[] zipFileConvert = ZipFileConvert.zipFileConvert(byteArrayList, fileNameArray);

		HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.CONTENT_TYPE, "application/zip");
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=arquivos.zip");
		header.setContentLength(zipFileConvert.length);

		HttpEntity<byte[]> ZipFileDocument = new HttpEntity<byte[]>(zipFileConvert, header);

		return ZipFileDocument;
	}

	public String documentTypes() {

		JSONArray jsonArray = new JSONArray();

		for (DocumentosDeReferencia docType : DocumentosDeReferencia.values()) {

			JSONObject jsonObject = new JSONObject(new DocumentType(docType.getDescription(),
					docType.prefix + docType.toString(), docType.typeDocument));
			jsonArray.put(jsonObject);

		}

		for (DocumentosDeProjeto docType : DocumentosDeProjeto.values()) {

			JSONObject jsonObject = new JSONObject(new DocumentType(docType.getDescription(),
					docType.prefix + docType.toString(), docType.typeDocument));
			jsonArray.put(jsonObject);

		}

		for (DocumentosDeGerenciamento docType : DocumentosDeGerenciamento.values()) {

			JSONObject jsonObject = new JSONObject(new DocumentType(docType.getDescription(),
					docType.prefix + docType.toString(), docType.typeDocument));
			jsonArray.put(jsonObject);

		}

		System.out.println("Chamada tipos de documento......");

		return jsonArray.toString();
	}
}
