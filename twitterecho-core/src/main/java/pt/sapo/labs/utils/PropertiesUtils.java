package pt.sapo.labs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtils {

	private PropertiesUtils() {  }
	/**
	 * Load a properties file from the classpath
	 * @param propsName
	 * @return Properties
	 * @throws Exception
	 */
	public static Properties load(String propsName) {
		Properties props = new Properties();
		URL url = ClassLoader.getSystemResource(propsName);
		try {
			props.load(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}

	/**
	 * Load a Properties File
	 * @param propsFile
	 * @return Properties
	 * @throws java.io.IOException
	 */
	public static Properties load(File propsFile) {
		Properties props = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(propsFile);
			props.load(fis);
			fis.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    

		return props;
	}

}
