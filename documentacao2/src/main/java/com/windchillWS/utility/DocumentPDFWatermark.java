package com.windchillWS.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.windchillWS.exception.BusinessException;

public class DocumentPDFWatermark {

	private PdfContentByte over;

	public byte[] manipulatePdf(InputStream inputStream, String fraseLinha1, String fraseLinha2, String caderno, String submarino) throws BusinessException {
		PdfReader reader;
		try {
			reader = new PdfReader(inputStream);
		} catch (IOException e) {
			throw new BusinessException(e.getLocalizedMessage());
		}
		
		int pages = reader.getNumberOfPages();
		ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
		PdfStamper stamper;
		
			try {
				stamper = new PdfStamper(reader, fileOutputStream);
			} catch (DocumentException | IOException e) {
				throw new BusinessException(e.getLocalizedMessage());
			}
		

		Font font = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
		font.setColor(BaseColor.RED);
		Phrase phrase2 = new Phrase(fraseLinha2, font);
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(0.6f);
		Rectangle pagesize;
		float x, y, z;
		
		float percentagex = 0.7f;
    	float percentagey = 0.7f;
    	
		for (int i = 1; i <= pages; i++) {
			
			float offsetX = (reader.getPageSize(i).getWidth() * (1 - percentagex)) / 2;
    	    float offsetY = (reader.getPageSize(i).getHeight() * (1 - percentagey)) / 2;
    	    stamper.getUnderContent(i).setLiteral(
    	        String.format("\nq %s 0 0 %s %s %s cm\nq\n", 
    	        		percentagex, percentagey, offsetX, offsetY));
    	    stamper.getOverContent(i).setLiteral("\nQ\nQ\n");
    	    
			pagesize = reader.getPageSize(i);
			x = pagesize.getWidth() / 2;
			y = 26;
			z = y - 13;

			this.over = stamper.getOverContent(i);
			this.over.saveState();
			this.over.setGState(gs1);
			
			if(fraseLinha2.equals("CÓPIA NÃO CONTROLADA")) {
				Phrase phrase1 = new Phrase("EMPRESA", font);
				ColumnText.showTextAligned(this.over, Element.ALIGN_CENTER, phrase1, x, y, 0);
				ColumnText.showTextAligned(this.over, Element.ALIGN_CENTER, phrase2, x, z, 0);
			}
			
			if (fraseLinha2.equals("CÓPIA CONTROLADA")) {
				
				font = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
				font.setColor(BaseColor.RED);
				if (!caderno.isEmpty() && !fraseLinha1.contains(caderno)) fraseLinha1 = caderno + " - " + fraseLinha1;
				if (!submarino.isEmpty() && !fraseLinha1.contains(submarino)) fraseLinha1 = fraseLinha1 + " - " + submarino;
				if (!fraseLinha1.contains("EMPRESA")) fraseLinha1 = "EMPRESA - " + fraseLinha1;
				Phrase phrase1 = new Phrase(fraseLinha1, font);
				ColumnText.showTextAligned(this.over, Element.ALIGN_CENTER, phrase1, x, y, 0);
				ColumnText.showTextAligned(this.over, Element.ALIGN_CENTER, phrase2, x, z, 0);
				
			}
		}
		
		try {
			stamper.close();
		} catch (DocumentException | IOException e) {
			throw new BusinessException(e.getLocalizedMessage());
		} 
		reader.close();
		
		byte[] manipulatePdfByteArray = fileOutputStream.toByteArray();

		return manipulatePdfByteArray;
	}
}
