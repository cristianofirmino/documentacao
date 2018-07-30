package tests;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;

import com.windchillWS.service.DocumentService;
import com.windchillWS.web.DocumentoController;

public class RelatedDocumentTest {

	public static void main(String[] args) {

		JSONObject jsonObject = new JSONObject();
		jsonObject.accumulate("number", "20001*");
		jsonObject.accumulate("revision", "");
		jsonObject.accumulate("docType", "");
		jsonObject.accumulate("name", "");

		String searchRelatedDocument = new DocumentoController().searchDocument(jsonObject.toString());

		JSONArray jsonArray = new JSONArray(searchRelatedDocument);

		
		System.out.println("Tamanho: " + jsonArray.length());
		
		JSONArray jsonArray2 = new JSONArray();
		
		for (int i = 0; i < jsonArray.length(); i++) {
			
			JSONObject obj = new JSONObject(jsonArray.get(i).toString());
			
			if(obj.get("GRAU_SIGILO").equals("GRAU") || obj.get("GRAU_SIGILO").equals(null)){
				
				jsonArray2.put(obj);
			}
			
		}
		
		System.out.println("Tamanho: " +jsonArray2.length());
		System.out.println(jsonArray2.toString());

	}

}
