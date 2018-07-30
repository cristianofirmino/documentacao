package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.windchillWS.exception.BusinessException;
import com.windchillWS.utility.PDFWatermarkShrink;

public class TestePDF {

	public static final String DEST = "C:\\\\Temp\\PDF\\teste_output.pdf";
	public static final String ORIG = "C:\\\\Temp\\PDF\\teste4.pdf";
	public static String fraseLinha1 = "GRD-123456";
	public static String fraseLinha2 = "CÓPIA NÃO CONTROLADA";
	
	public static void main(String[] args) throws BusinessException, IOException {

		FileInputStream fis = new FileInputStream(ORIG);

		byte[] pdfOut = new PDFWatermarkShrink().manipulatePdf(fis, fraseLinha1, fraseLinha2, "CC-123456", "Produto");
		FileUtils.writeByteArrayToFile(new File(DEST), pdfOut);

	}

}
