package tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.PdfSplitter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

public class ShrinkPDF {    
   public static void main(String args[]) throws Exception {
      
      
	   for (int i = 1; i < 6; i++) {
    	  
          String dest = "C:\\\\Temp\\PDF\\teste_output" + i + ".pdf";
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          PdfWriter writer = new PdfWriter(bos);
          
          String src = "C:\\\\Temp\\PDF\\teste" + i + ".pdf";
          PdfReader reader = new PdfReader(src);
          
          PdfDocument destpdf = new PdfDocument(writer);
          PdfDocument srcPdf = new PdfDocument(reader);
          
          PdfFont font = PdfFontFactory.createFont(FontProgramFactory.createFont(FontConstants.HELVETICA));
          Paragraph p = new Paragraph("This watermark is added UNDER the existing content").setFont(font).setFontSize(10).setFontColor(Color.RED);
             
			for (int j = 1; j <= srcPdf.getNumberOfPages(); j++) {
				PdfPage origPage = srcPdf.getPage(j);
				Rectangle orig = origPage.getPageSize();
				System.out.println("..........................................................");
				System.out.println("Documeto" + i);
				System.out.println("Pagina" + j);
				System.out.println("W: " + orig.getWidth());
				System.out.println("H: " + orig.getHeight());
				System.out.println("Top: " + orig.getTop());
				System.out.println("Bottom: " + orig.getBottom());
				System.out.println("Rotation: " + origPage.getRotation());

				PdfPage page = destpdf.addNewPage(new PageSize(origPage.getPageSize()));
				page.setRotation(origPage.getRotation());
				
				
				AffineTransform transformationMatrix = AffineTransform.getScaleInstance(0.95, 0.95);
				PdfCanvas canvas = new PdfCanvas(page);
				canvas.concatMatrix(transformationMatrix);
				
				PdfFormXObject pageCopy = origPage.copyAsFormXObject(destpdf);
				canvas.addXObject(pageCopy, (float) 30, (float) 30);				
				
				if (origPage.getRotation() == 0) {
					new Canvas(canvas, destpdf, destpdf.getDefaultPageSize()).showTextAligned(p, (orig.getLeft() + orig.getRight()) / 2 , 26, j,
							TextAlignment.CENTER, VerticalAlignment.TOP, (float) 0);
				}
				
				if (origPage.getRotation() == 90) {
					new Canvas(canvas, destpdf, destpdf.getDefaultPageSize()).showTextAligned(p,  orig.getWidth() - 26, (orig.getBottom() + orig.getTop()) / 2, j,
							TextAlignment.CENTER, VerticalAlignment.TOP, (float) -4.709);
				}
				
				if (origPage.getRotation() == 270) {
					new Canvas(canvas, destpdf, destpdf.getDefaultPageSize()).showTextAligned(p, 26, (orig.getBottom() + orig.getTop()) / 2, j,
							TextAlignment.CENTER, VerticalAlignment.TOP, (float) 4.709);
				}
			}
          			
			
			destpdf.close();
			writer.close();
			
			FileUtils.writeByteArrayToFile(new File(dest), bos.toByteArray());
			
			/*Document doc = new Document(destpdf);          
			doc.close();*/
	}
      
   }
   
   
}      