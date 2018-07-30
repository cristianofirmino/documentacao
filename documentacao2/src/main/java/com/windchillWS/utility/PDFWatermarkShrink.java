package com.windchillWS.utility;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.windchillWS.exception.BusinessException;

public class PDFWatermarkShrink {

	PdfWriter writer;
	PdfReader reader;
	PdfDocument destPdf;
	PdfDocument srcPdf;
	Paragraph frase1;
	Paragraph frase2;
	
	public static void main(String[] args) throws FileNotFoundException, BusinessException {
		PDFWatermarkShrink pdf = new PDFWatermarkShrink();
		InputStream inputStream = new FileInputStream("C:\\dev\\teste.pdf");
		pdf.manipulatePdf(inputStream, "teste", "teste", "teste", "teste");
	}

	public byte[] manipulatePdf(InputStream inputStream, String fraseLinha1, String fraseLinha2, String caderno,
			String produto) throws BusinessException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			writer = new PdfWriter(bos);
			reader = new PdfReader(inputStream);
			destPdf = new PdfDocument(writer);
			srcPdf = new PdfDocument(reader);
		} catch (Exception e) {
			throw new BusinessException(e.getLocalizedMessage());
		}

		PdfFont font;
 
		try {
			font = PdfFontFactory.createFont(FontProgramFactory.createFont(FontConstants.HELVETICA_BOLD));
		} catch (Exception e) {
			throw new BusinessException(e.getLocalizedMessage());
		}

		if (fraseLinha2.equals("CÓPIA NÃO CONTROLADA")) {            
			frase1 = new Paragraph("EMPRESA").setFont(font).setFontSize(10).setFontColor(Color.RED);
			frase2 = new Paragraph(fraseLinha2).setFont(font).setFontSize(10).setFontColor(Color.RED);
		}

		if (fraseLinha2.equals("CÓPIA CONTROLADA")) {

			if (!caderno.isEmpty() && !fraseLinha1.contains(caderno))
				fraseLinha1 = caderno + " - " + fraseLinha1;
			if (!produto.isEmpty() && !fraseLinha1.contains(produto))
				fraseLinha1 = fraseLinha1 + " - " + produto;
			if (!fraseLinha1.contains("EMPRESA"))
				fraseLinha1 = "EMPRESA - " + fraseLinha1;
			frase1 = new Paragraph(fraseLinha1).setFont(font).setFontSize(10).setFontColor(Color.RED);
			frase2 = new Paragraph(fraseLinha2).setFont(font).setFontSize(10).setFontColor(Color.RED);
		}

		for (int i = 1; i <= srcPdf.getNumberOfPages(); i++) {
			PdfPage origPage = srcPdf.getPage(i);
			Rectangle ret = origPage.getPageSize();

			PdfPage page = destPdf.addNewPage(new PageSize(origPage.getPageSize()));
			page.setRotation(origPage.getRotation());
			
			AffineTransform transformationMatrix = AffineTransform.getScaleInstance(0.90, 0.90);
			PdfCanvas canvas = new PdfCanvas(page);
			canvas.concatMatrix(transformationMatrix);

			PdfFormXObject pageCopy;
			try {
				pageCopy = origPage.copyAsFormXObject(destPdf);
			} catch (IOException e) {
				throw new BusinessException(e.getLocalizedMessage());
			}

			canvas.addXObject(pageCopy, (float) 30, (float) 30);
			
			System.out.println("Rotação da caralha: " + origPage.getRotation());
			

			if (origPage.getRotation() == 0) {
				new Canvas(canvas, destPdf, destPdf.getDefaultPageSize()).showTextAligned(frase1,
						(ret.getLeft() + ret.getRight()) / 2, 26, i, TextAlignment.CENTER, VerticalAlignment.TOP,
						(float) 0);

				new Canvas(canvas, destPdf, destPdf.getDefaultPageSize()).showTextAligned(frase2,
						(ret.getLeft() + ret.getRight()) / 2, 13, i, TextAlignment.CENTER, VerticalAlignment.TOP,
						(float) 0);
			}

			if (origPage.getRotation() == 90) {
				new Canvas(canvas, destPdf, destPdf.getDefaultPageSize()).showTextAligned(frase1, ret.getWidth() + 26,
						(ret.getBottom() + ret.getTop()) / 2, i, TextAlignment.CENTER, VerticalAlignment.TOP,
						(float) -4.709);

				new Canvas(canvas, destPdf, destPdf.getDefaultPageSize()).showTextAligned(frase2, ret.getWidth() + 39,
						(ret.getBottom() + ret.getTop()) / 2, i, TextAlignment.CENTER, VerticalAlignment.TOP,
						(float) -4.709);
			}

			if (origPage.getRotation() == 270) {
				new Canvas(canvas, destPdf, destPdf.getDefaultPageSize()).showTextAligned(frase1, 26,
						(ret.getBottom() + ret.getTop()) / 2, i, TextAlignment.CENTER, VerticalAlignment.TOP,
						(float) 4.709);

				new Canvas(canvas, destPdf, destPdf.getDefaultPageSize()).showTextAligned(frase2, 13,
						(ret.getBottom() + ret.getTop()) / 2, i, TextAlignment.CENTER, VerticalAlignment.TOP,
						(float) 4.709);
			}
		}

		try {
			destPdf.close();
			writer.close();
			reader.close();
		} catch (Exception e) {
			throw new BusinessException(e.getLocalizedMessage());
		}

		return bos.toByteArray();

	}

}
