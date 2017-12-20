package Util;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfRW {
	public static void init_config(String propertie_path) {
		Properties propertie = new Properties();
		
		FileInputStream inputFile;
		try {
			inputFile = new FileInputStream(propertie_path);
			propertie.load(inputFile);
			inputFile.close();
			
			DfConf.linuxFlag = Integer.parseInt(propertie.getProperty("linuxFlag"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
