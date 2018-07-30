package com.windchillWS.utility;

import java.net.URL;

import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.stereotype.Component;

import com.windchillWS.constants.Configuration;

@Component
public class DownloaderUtil {

	public CloseableHttpResponse download(URL url) {
		CloseableHttpClient httpclient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).build();
		
		try {
			HttpGet get = new HttpGet(url.toURI());
			
			HttpHost targetHost = new HttpHost(Configuration.HOST_WC, 80, "http");
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
			        new AuthScope(targetHost.getHostName(), targetHost.getPort()),
			        new UsernamePasswordCredentials("wcadmin", "pass"));

			AuthCache authCache = new BasicAuthCache();
			BasicScheme basicAuth = null;
			authCache.put(targetHost, (AuthScheme) basicAuth);

			HttpClientContext context = HttpClientContext.create();
			context.setCredentialsProvider(credsProvider);
			context.setAuthCache(authCache);

			
			CloseableHttpResponse response = httpclient.execute(get, context);
			return response;
			
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(httpclient);
		}
	}

}