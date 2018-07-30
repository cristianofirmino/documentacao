package tests;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class DeleteFolder {

	public static void main(String[] args) {
		
		try {
			FileUtils.deleteDirectory(new File("\\\\127.0.0.1\\Download$\\controlado\\" + "000000997"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
