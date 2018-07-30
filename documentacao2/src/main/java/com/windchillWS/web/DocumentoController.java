package com.windchillWS.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import com.windchillWS.exception.BusinessException;
import com.windchillWS.service.DocumentService;

@RestController
@RequestMapping("document")
public class DocumentoController {
	
	@RequestMapping(value = "create", consumes = "application/JSON", produces = "application/JSON", method = RequestMethod.POST)
	public String createDocumentJSON(@RequestBody String jsonS) {

		StringBuilder logOperation = new StringBuilder();

		if (jsonS.startsWith("{")) {

			JSONObject jsonDocument = new JSONObject(jsonS);
			logOperation.append(new DocumentService().createDocument(jsonDocument));

		} else if (jsonS.startsWith("[")) {

			JSONArray documentsParseJSON = new JSONArray(jsonS);

			for (Object object : documentsParseJSON) {
				JSONObject jsonDocument = (JSONObject) object;
				logOperation.append(new DocumentService().createDocument(jsonDocument));
			}

		}

		JSONObject json = XML.toJSONObject(logOperation.toString());
		return json.toString();
	}

	@RequestMapping(value = "indexSearch", produces = "application/JSON", method = RequestMethod.GET)
	public ResponseEntity<String> indexSearchDocument(@RequestParam("keyword") String keyword) {

		String indexSearchDocument = null;
		try {
			indexSearchDocument = new DocumentService().indexSearchDocument(keyword);
		} catch (BusinessException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
		}
		return ResponseEntity.ok(indexSearchDocument);
	}

	@RequestMapping(value = "search", produces = "aplication/JSON", method = RequestMethod.POST)
	public String searchDocument(@RequestBody String jsonS) {

		String searchDocument = new DocumentService().searchDocument(jsonS);
		return searchDocument;
	}
	
	@RequestMapping(value = "searchRelated", produces = "aplication/JSON", method = RequestMethod.POST)
	public String searchRelatedDocument(@RequestBody String jsonS) {

		String searchDocument = new DocumentService().searchRelatedDocument(jsonS);
		return searchDocument;
	}

	@RequestMapping(value = "download", method = RequestMethod.GET)
	public HttpEntity<byte[]> downloadDocument(@RequestParam("number") String number, @RequestParam("revision") String revision) throws Exception {

		HttpEntity<byte[]> downloadDocument = null;
		try {
			downloadDocument = new DocumentService().downloadDocument(number, revision);
		} catch (HttpServerErrorException e) {
			throw e;
		}

		return downloadDocument;

	}
	
	@RequestMapping(value = "download", method = RequestMethod.POST)
	public HttpEntity<byte[]> downloadDocuments(@RequestBody String jsonDocuments) throws Exception {

		HttpEntity<byte[]> downloadZipFileDocument = null;
		try {
			downloadZipFileDocument = new DocumentService().downloadZipFileDocument(jsonDocuments);
		} catch (HttpServerErrorException e) {
			throw e;
		}
		return downloadZipFileDocument;
	}

	@RequestMapping(value = "documentTypes", produces = "aplication/JSON", method = RequestMethod.GET)
	public String documentTypes() {

		String documentTypes = new DocumentService().documentTypes();

		return documentTypes;
	}

	@ExceptionHandler(value = { HttpServerErrorException.class })
	@ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
	
	public void downloadException(Exception ex, WebRequest req, HttpServletResponse response) throws IOException {		
		response.sendRedirect("http://documentacao.net/pages/error/401.html");
	}

}
