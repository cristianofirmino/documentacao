package com.windchillWS.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import com.windchillWS.constants.Configuration;

@Component
public class FileCopyUtil {

	public static String copyFileToUpload(String fileName) throws Exception {

		try {
			File srcFile = new File(Configuration.PATH_CARGA_WC + fileName);
			File destFile = new File(Configuration.PATH_TEMP_WC + fileName);
			FileUtils.copyFile(srcFile, destFile);
			return "Arquivo copiado com sucesso: " + fileName;
		} catch (Exception e) {
			throw e;
		}

	}

	public static void pack(String sourceDirPath, ByteArrayOutputStream baos) throws IOException {
		Path pp = Paths.get(sourceDirPath);
		try (ZipOutputStream zs = new ZipOutputStream(baos)) {
			Files.walk(pp).filter(path -> !Files.isDirectory(path)).forEach(path -> {
				System.out.println("Zipando " + path.getFileName().toString());
				ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
				try {
					zs.putNextEntry(zipEntry);
					Files.copy(path, zs);
					zs.closeEntry();
				} catch (IOException e) {
					System.err.println(e);
				}
			});
		}
	}
}
