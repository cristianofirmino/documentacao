package tests;

import com.windchillWS.bean.DocumentRequest;

public class Tests {

	public static void main(String[] args) {

		/*String number = "01-EMPRESA/C-01211/18";
		System.out.println(number.replace("/", "-"));*/
		
		DocumentRequest doc = new DocumentRequest();
		
		if(doc.getCaderno() == null) {
			System.out.println("Nulo");
		}
		
		if(doc.getCaderno().isEmpty()) {
			System.out.println("Vazio");
		}

	}

}
