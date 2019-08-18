package pers.perry.xu.pickphotos.utils;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class ToolLanguage {

	final Logger logger = Logger.getLogger(ToolLanguage.class);

	private static String language = "chn";

	private static HashMap<String, String> chineseMap;

	private static HashMap<String, String> englishMap;

	{
		logger.setLevel(ToolConfiguration.logLevel);
	}

	public static String getToolMessages(String messageKey) {
		String message = null;
		
		if(language.toLowerCase().equals("english") || language.toLowerCase().equals("eng")) {
			if(englishMap == null) {
				initMsg(Language.English);
			}
			
			message = englishMap.get(messageKey);
		} else if(language.toLowerCase().equals("chinese") || language.toLowerCase().equals("chn")) {
			if(chineseMap == null) {
				initMsg(Language.Chinese);
			}
			
			message = chineseMap.get(messageKey);
		}
		
		if(message == null || message.length() == 0) {
			message = "#";
		}
		
		return message;
	}
	
	
	private static void initMsg(Language lan) {
		String[] key = {
				"error", "title", "previouspic", "nextpic", "save",
				"input", "output", "msgstartpicking", "start", "options"
			};
		
		if(lan == Language.Chinese) {
			chineseMap = new HashMap<String, String>();
			String[] msg = {
					"程序发生错误!", "照片快速挑选工具", "上一张照片", "下一张照片", "保存照片",
					"照片目录:", "把照片导出到:", "点击开始按钮开始挑选照片", "开始", "高级"
				};
			
			for (int i=0; i<key.length; i++) {
				chineseMap.put(key[i], msg[i]);
			}
		} else if(lan == Language.English) {
			englishMap = new HashMap<String, String>();
			String[] msg = {
					"Error happened!", "Pick your photos in simplest way :)", "Previous one", "Next one", "Save this photo",
					"Pick the photos from:", "Export the picked photos to:", "Click START button to pick photos", "Start", "Advance"
				};
			
			for (int i=0; i<key.length; i++) {
				englishMap.put(key[i], msg[i]);
			}
		}
	}
	
	private enum Language {
		English, Chinese
	}
}
