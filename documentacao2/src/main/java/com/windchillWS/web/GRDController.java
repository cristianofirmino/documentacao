package com.windchillWS.web;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.windchillWS.bean.DocumentRequest;
import com.windchillWS.bean.GRDRequest;
import com.windchillWS.constants.Configuration;
import com.windchillWS.exception.BusinessException;
import com.windchillWS.service.GrdService;
import com.windchillWS.utility.FileCopyUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping("grd")
public class GRDController {
	@Autowired
	GrdService service;
	
	private final String secret_base_64 = "sçlkdjsdçlkfjgçsdlkfgjçsdlkfgjsdçflkg´rpeiotw´peorit´weprotiwe´porti~dgks~dlfgk~sdçlg=";

	@RequestMapping(value = "makeGRD", method = RequestMethod.POST)
	public void makeGRD(@RequestBody GRDRequest request) throws Exception {

		if (request != null && request.getGrd() != null && request.getDocumentos() != null) {
			for (DocumentRequest doc : request.getDocumentos()) {
				if(doc.getCaderno() == null) doc.setCaderno("");
				if(request.getSubmarino() == null) request.setSubmarino("");
				service.makeGrd(doc.getNumero(), doc.getRevisao(), doc.getCaderno(), request.getGrd(), request.getSetor(), request.getSubmarino());
			}
		}

	}
 
	@RequestMapping(value = "download/{grd}", method = RequestMethod.GET) 
	public ResponseEntity<byte[]> downloadDocuments(@PathVariable(name="grd") String grd, @CookieValue(value="__EMPRESASSOACCESS__", required=false) String sso) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		//validar futuramente role do usuário
		if (sso==null) return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);		
		try {
			Claims claims = (Claims) Jwts.parser().setSigningKey(secret_base_64).parse(sso).getBody();
			if (claims.get("rid")==null) return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED); 
		} catch (Exception e) {
			return new ResponseEntity<>(null,HttpStatus.UNAUTHORIZED);
		}
		
		if (grd == null) return new ResponseEntity<byte[]>(baos.toByteArray(),HttpStatus.INTERNAL_SERVER_ERROR);
		String folderPathControlado = Configuration.PATH_CONTROLADO+grd;
		System.out.println("Obtendo download");
		System.out.println(folderPathControlado);
		try {
			FileCopyUtil.pack(folderPathControlado, baos);
			byte[] item = baos.toByteArray();
			HttpHeaders header = new HttpHeaders();
		    header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		    header.set(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + grd.replace("-", "_")+".zip");
		    header.setContentLength(item.length);
		    
			return new ResponseEntity<byte[]>(item,header, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
		}				
	}
	
	@RequestMapping(value = "delete/{grd}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteGRD(@PathVariable(name="grd") String grd) {
		try {
			service.deleteGRD(grd);
			return new ResponseEntity<String>("Pasta " + grd + " deletada com sucesso", HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
