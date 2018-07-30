package com.windchillWS.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.http.auth.AuthScope;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.windchillWS.constants.Configuration;
import com.windchillWS.exception.BusinessException;
import com.windchillWS.utility.PDFWatermarkShrink;

@Component
public class MakeDocumentGRD { 
	
	@Autowired 
	RestTemplate restTemplate;

	private String fraseLinha1 = "";
	private String fraseLinha2 = "CÓPIA CONTROLADA";
	private String folderPathControlado = Configuration.PATH_CONTROLADO;
	private String urlTask = Configuration.URL_WC+"Windchill/servlet/IE/tasks/test/downloadDocumentControlado";
	private String urlUpdate = Configuration.URL_GRD+"api/grd/updateDocProcessado";
	private String urlErro = Configuration.URL_GRD+"api/grd/updateDocErro";

	public void callAsync(String number, String revision, String caderno, String grd, String setor, String submarino) throws BusinessException {
		File grdFolder = grdFolder(number, revision, grd);
		if (grdFolder == null) throw new BusinessException("Erro ao gerar a pasta de GRD no servidor");
		String fileName = number.replace("/", "-") + "_" + revision + ".pdf";
		System.out.println("Início do processamento " + fileName);
		
		if (isDocumentExist(fileName)) {
			System.out.println("Arquivo encontrado " + fileName + "...");
			aplicaMarcaDAgua(grdFolder, fileName, grd, setor, caderno, submarino);
		} else {
			System.out.println("Arquivo não encontrado " + fileName + "...");
			System.out.println("Iniciando download da requisicao do Windchill ");
			try {
				getFromWindchill(number,revision);
				Thread.sleep(4000);
			} catch (Exception e) {
				throw new BusinessException(e.getLocalizedMessage());
			}
			finally { 	
				try {					
					aplicaMarcaDAgua(grdFolder, fileName, grd, setor, caderno, submarino);
					Thread.sleep(4000);
				} catch (Exception e) {
					try {
						updateDocErro(grd,number,revision);
					} catch (Exception e1) {
						throw new BusinessException("Erro ao atualizar o status do documento da GRD " + grd + " - " + number + Configuration.FILE_SEPARATOR + revision );
					}
					throw new BusinessException(e.getMessage());
				}
			}
		} 
		try {
			updateDocProcessado(grd,number,revision);
		} catch (Exception e) {
			throw new BusinessException("Erro ao atualizar o status do documento da GRD " + grd + " - " + number + Configuration.FILE_SEPARATOR + revision );
		}
		
		System.out.println("<" + "> Finalizou a geração da GRD " + grd + " - " + number + Configuration.FILE_SEPARATOR + revision );
	}
	
	private File grdFolder(String number, String revision, String grd) throws BusinessException {
		String grdFolderPath = folderPathControlado + grd + Configuration.FILE_SEPARATOR;
		File grdFolder = new File(grdFolderPath);
		if (!grdFolder.exists()) {
			try {
				grdFolder.mkdir();
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new BusinessException("Erro criar pasta " + grdFolder.getAbsolutePath());
			}
		}
		return grdFolder;
	}

	private boolean isDocumentExist(String fileName) {
		System.out.println("Procurando arquivo " + folderPathControlado + fileName);
		Path path = Paths.get(folderPathControlado + fileName);
		return java.nio.file.Files.exists(path);
	}
	
	private boolean isDocumentControladoOpen(File grdFolder, String fileName) throws BusinessException  {
		File fileControlado = new File(folderPathControlado + fileName);
		try {
			FileUtils.touch(fileControlado);
			return false;
		} catch (IOException e) {			
			e.printStackTrace();
			return true; 
		}
	}

	private void aplicaMarcaDAgua(File grdFolder, String fileName, String grd, String setor, String caderno, String submarino) throws BusinessException {
		Instant start = Instant.now();
		File fileControlado = new File(folderPathControlado + fileName);
		FileInputStream fis;
		fraseLinha1 = new String(grd + " - " + setor);		
		try {
			String tmpFilePath = System.getProperty("java.io.tmpdir")+File.separator+String.format("tmpdf-%s", UUID.randomUUID().toString());
			File tmpFile = new File(tmpFilePath);
			if (tmpFile.exists()) tmpFile.delete();
            RandomAccessFile raf = new RandomAccessFile(tmpFile, "rw");
			PDDocument.load(fileControlado,raf);
			try {
				fis = new FileInputStream(fileControlado);
				if(caderno.length() > 0) caderno = "Anexo - " + caderno;
				byte[] manipulatePdfByteArray = new PDFWatermarkShrink().manipulatePdf(fis, fraseLinha1, fraseLinha2, caderno, submarino);
				fis.close();				
				if(caderno.length() > 0) {
					FileUtils.writeByteArrayToFile(new File(grdFolder.getAbsolutePath() + Configuration.FILE_SEPARATOR + caderno + Configuration.FILE_SEPARATOR + fileName), manipulatePdfByteArray);
				} else {
					FileUtils.writeByteArrayToFile(new File(grdFolder.getAbsolutePath() + Configuration.FILE_SEPARATOR + fileName), manipulatePdfByteArray);
				}
				
				Instant end = Instant.now();
				System.out.println("Tempo para aplicar a Marca DAgua " + Duration.between(start, end) + " - "  + fileName);
			} 		
			catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException("Erro ao aplicar a Marca D'Agua do documento " + fileName);
			}
		} catch (IOException e) {
			fileControlado.delete();			
			throw new BusinessException("Arquivo não é um PDF válido " + fileName);
		}		
	
		
	}
	 
	@SuppressWarnings("deprecation")
	private void getFromWindchill(String number, String revision) throws BusinessException {
		try {
			HttpClient httpClient = new HttpClient();
			PostMethod postMethod = null;
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("wcadmin", "pass");
			postMethod = new PostMethod(urlTask);
			postMethod.addParameter("number", number);
			postMethod.addParameter("revision", revision);

			httpClient.getState().setAuthenticationPreemptive(true);
			httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);
			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpClient.executeMethod(postMethod);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("Erro ao obter o documento do WC " + number + Configuration.FILE_SEPARATOR + revision);
		}
		
	}
	
	private void updateDocProcessado(String grd, String numero, String revisao) throws BusinessException
	{		
		Map<String,String> request = new HashMap<String,String>();
		request.put("grd", grd);
		request.put("documento", numero+"_"+revisao);
		try {
			System.out.println("Chamando webservices para status do documento: " + urlUpdate + " : " + grd + " - " + numero );
			restTemplate.put(urlUpdate, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateDocErro(String grd, String numero, String revisao) throws BusinessException
	{		
		Map<String,String> request = new HashMap<String,String>();
		request.put("grd", grd);
		request.put("documento", numero+"_"+revisao);
		try {
			System.out.println("Chamando webservices para status do documento: " + urlErro + " : " + grd + " - " + numero );
			restTemplate.put(urlErro, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
