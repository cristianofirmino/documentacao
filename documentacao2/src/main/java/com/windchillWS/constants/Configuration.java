package com.windchillWS.constants;

import java.nio.file.FileSystems;

public class Configuration {

	public static final String URL_GRD = "http://" + System.getenv("GRD_HOST")+ "/";
	public static final String URL_WC = "http://" + System.getenv("WC_HOST") + "/";
	public static final String PATH_CONTROLADO = System.getenv("PATH_CONTROLADO");
	
	public static final String HOST_WC = System.getenv("WC_HOST");
	
	public static final String PATH_CARGA_WC = "\\\\\\\\127.0.0.1\\\\carga\\\\";
	public static final String PATH_TEMP_WC = "\\\\127.0.0.1\\Windchill\\temp\\wcadmin\\";	
	
	public static final String FILE_SEPARATOR = FileSystems.getDefault().getSeparator();
	
	
}
