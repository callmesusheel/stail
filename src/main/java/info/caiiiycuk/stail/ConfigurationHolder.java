package info.caiiiycuk.stail;

import info.caiiiycuk.stail.impl.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

public class ConfigurationHolder {

	private static ConfigurationHolder instance;
	
	private final Configuration configuration;
	
	private ConfigurationHolder(String file) throws Exception {
		FileInputStream inputStream = new FileInputStream(file);
		configuration = new Configuration(inputStream);

		try {
			inputStream.close();
		} catch (IOException e) {
		}
	}
	
	static void create(String file) throws Exception {
		instance = new ConfigurationHolder(file);
	}
	
	public static Configuration c() {
		return instance.configuration;
	}
	
}
