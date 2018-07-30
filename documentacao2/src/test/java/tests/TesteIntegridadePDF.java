package tests;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

public class TesteIntegridadePDF {

	public static void main(String[] args) throws IOException {
		
		File file = new File("\\\\server\\Download$\\controlado\\00147_0.pdf");
		PDDocument.load(file);


	}

}
