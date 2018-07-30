package com.windchillWS.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class FileDownloadResponseHandler implements ResponseHandler<OutputStream> {

	private ByteArrayOutputStream target = null;

	public FileDownloadResponseHandler() {
	}

	public OutputStream handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		InputStream source = response.getEntity().getContent();
		this.target = new ByteArrayOutputStream(source.available());
		IOUtils.copy(source, target);
		return this.target;
	}

}