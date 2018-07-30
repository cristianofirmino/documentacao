package com.windchillWS.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileConvert {

	public static byte[] zipFileConvert(List<byte[]> byteFiles, List<String> fileNameArray) {
		byte[] zipFile = null;
		try {
			ByteArrayOutputStream fos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (int i = 0; i < byteFiles.size(); i++) {
				InputStream fis = new ByteArrayInputStream(byteFiles.get(i));
				byte[] buffer = new byte[1024];
				zos.putNextEntry(new ZipEntry(fileNameArray.get(i)));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			zipFile = fos.toByteArray();
		} catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe.getLocalizedMessage());
		}		
		return zipFile;
	}
	
	
}
