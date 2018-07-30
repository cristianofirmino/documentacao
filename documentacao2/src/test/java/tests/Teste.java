package tests;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.windchillWS.bean.DocumentRequest;
import com.windchillWS.bean.GRDRequest;

public class Teste {

	public static void main(String[] args) throws RestClientException, URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		ArrayList<DocumentRequest> docs = new ArrayList<>();
		DocumentRequest documentRequest = new DocumentRequest();
		documentRequest.setNumero("200123");
		documentRequest.setRevisao("C");

		for (int i = 0; i < 20; i++) {
			DocumentRequest doc = new DocumentRequest();
			doc.setNumero("16911-12-001");
			doc.setRevisao("0");
			doc.setCaderno("CC-1234");
			docs.add(doc);
		}

		GRDRequest request = new GRDRequest();
		request.setGrd("000000999");
		request.setSetor("TECNOLOGIA DA INFORMAÇÃO");

		docs.add(documentRequest);
		request.setDocumentos(docs);

		restTemplate.postForLocation(new URI("http://localhost/grd/makeGRD"), request);
	}

}