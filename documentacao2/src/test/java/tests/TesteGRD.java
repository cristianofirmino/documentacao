package tests;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

import com.windchillWS.bean.DocumentRequest;
import com.windchillWS.bean.GRDRequest;

public class TesteGRD {

	public static void main(String[] args) {
		RestTemplate restTemplate = new RestTemplate();		
			
		try {
				GRDRequest request = new GRDRequest();
				DocumentRequest documentRequest = new DocumentRequest();
				List<DocumentRequest> listDocs = new ArrayList<>();
				
				documentRequest.setNumero("200010");
				documentRequest.setRevisao("R");
				listDocs.add(documentRequest);
				request.setDocumentos(listDocs);
				request.setGrd("GRD-CRISTIANO");
				
				restTemplate.postForLocation(new URI("http://localhost:8082/grd/makeGRD"), request);
			} catch (Exception e) {
				e.printStackTrace();
			}			
		 

	}

}
