package tests;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.windchillWS.utility.DocumentPDFWatermark;

public class TesteDownload {

	public static void main(String[] args) {

		String folderPath = System.getenv().get("PATH_DOWNLOAD");
		String urlTask = null;
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = null;
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("wcadmin", "pass");
		String number = "200010";
		String revision = "C";

		System.out.println( "<" +  "> Iniciando download da requisicao ");

		urlTask = new String("http://server/Windchill/servlet/IE/tasks/test/downloadDocument2");
		postMethod = new PostMethod(urlTask);
		postMethod.addParameter("number", number);
		postMethod.addParameter("revision", revision);

		httpClient.getState().setAuthenticationPreemptive(true);
		httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		HttpEntity<byte[]> manipulateFileEntity = null;

		String fileName = number + "_" + revision + ".pdf";
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		HttpHeaders header = new HttpHeaders();
		header.set(HttpHeaders.CONTENT_TYPE, "application/pdf");
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "\"" + fileName + "\"");

		if (Arrays.asList(listOfFiles).stream().anyMatch(f -> f.getName().equals(fileName))) {

			try {
				File file = new File(folderPath + fileName);
				FileInputStream fis = new FileInputStream(file);
				byte[] manipulatePdfByteArray = new DocumentPDFWatermark().manipulatePdf(fis, "teste1", "teste2", "teste3", "teste4");
				header.setContentLength(manipulatePdfByteArray.length);
				manipulateFileEntity = new HttpEntity<byte[]>(manipulatePdfByteArray, header);
				fis.close();

				System.out.println("Arquivo encontrado " + fileName + "...");
				System.out.println("...................................................................................................");
				//return manipulateFileEntity;

			} catch (Exception e) {
				System.out.println(fileName +"\n" + e.getMessage());
			}

		} else {

			System.out.println("Arquivo não encontrado "+ fileName +"...");

			urlTask = new String("http://server/Windchill/servlet/IE/tasks/test/downloadDocument2");
			postMethod = new PostMethod(urlTask);
			postMethod.addParameter("number", number);
			postMethod.addParameter("revision", revision);

			httpClient.getState().setAuthenticationPreemptive(true);
			httpClient.getState().setCredentials(AuthScope.ANY_REALM, credentials);
			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

			try {

				httpClient.executeMethod(postMethod);
				System.out.println("Chamou a Task para o arquivo "+ fileName + "...");
				System.out.println(urlTask);

				//while (true) {

					if (Arrays.asList(listOfFiles).stream().anyMatch(f -> f.getName().equals(fileName))) {
						File file = new File(folderPath + fileName);
						FileInputStream fis = new FileInputStream(file);
						byte[] manipulatePdfByteArray = new DocumentPDFWatermark().manipulatePdf(fis, "teste1", "teste2", "teste3", "teste4");
						header.setContentLength(manipulatePdfByteArray.length);
						manipulateFileEntity = new HttpEntity<byte[]>(manipulatePdfByteArray, header);
						fis.close();
						System.out.println("Arquivo encontrado pós chamada da Task..." + fileName + "...");
						System.out.println("...................................................................................................");
						//return manipulateFileEntity;

					}

			} catch (Exception e) {

				System.out.println(fileName +"\n" + e.getMessage());
			}
		}

		System.out.println( "<" +  "> Finalizando download da requisicao ");

	}

}

