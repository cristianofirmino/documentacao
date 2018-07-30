package com.windchillWS.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.windchillWS.constants.Configuration;
import com.windchillWS.exception.BusinessException;
import com.windchillWS.thread.MakeDocumentGRD;

@Service
public class GrdService {

	@Autowired
	MakeDocumentGRD makeDocumentGRD;

	@Async
	public void makeGrd(String number, String revision, String caderno, String grd, String setor, String submarino) {
		try {
			makeDocumentGRD.callAsync(number, revision, caderno, grd, setor, submarino);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteGRD(String grd) throws BusinessException {
		File grdFolder = new File(Configuration.PATH_CONTROLADO + grd);				
		if (grdFolder.exists()) {	
			try {
				FileUtils.deleteDirectory(grdFolder);
			} catch (IOException e) {				
				throw new BusinessException("Erro deletando a pasta: "+ grd + " em: " + Configuration.PATH_CONTROLADO);
			}
		} else {
			throw new BusinessException("Pasta da GRD: "+ grd + " não existe em: " + Configuration.PATH_CONTROLADO);
		}
	}
}
