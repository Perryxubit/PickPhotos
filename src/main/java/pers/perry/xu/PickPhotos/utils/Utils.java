package pers.perry.xu.PickPhotos.utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.yaml.snakeyaml.Yaml;

public class Utils {
	
	public static void showErrorWindow(String errorMessage) {
		JOptionPane.showMessageDialog(null, errorMessage, ToolLanguage.getToolMessages("error"), 1);
	}

	public static void processException(Exception exception) {
		
		String runningMode = (String) getProperties().get("runtime_mode");
		if(runningMode != null && runningMode.equals("gui")) {
			showErrorWindow(exception.getMessage());
		} else {
			System.out.println(exception.getMessage());
		}
	}
	
	
	private static HashMap<String, Object> properties;
	
	public static HashMap<String, Object> getProperties() {
		if(properties == null) {
			loadProperties();
		}
		
		return properties;
	}
	
	@SuppressWarnings("unchecked")
	public static void loadProperties() {
		Yaml yaml = new Yaml();
		try(InputStream in = new FileInputStream("/Users/perry/eclipse-workspace/PickPhotos/src/main/resources/config.yml")){
			properties = yaml.loadAs(in, HashMap.class);
		} catch (IOException e) {
			Utils.processException(e);
		}  
	}
}
