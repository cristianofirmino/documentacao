package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.itextpdf.text.DocumentException;
import com.windchillWS.utility.DocumentPDFWatermark;

public class DownloadDocument {

	public static void main(String[] args) throws MalformedURLException {

		System.out.println(System.getenv().get("PATH_DOWNLOAD"));
		
	}

}