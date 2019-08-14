package pers.perry.xu.PickPhotos;

import java.io.IOException;

import pers.perry.xu.PickPhotos.utils.Utils;
import pers.perry.xu.PickPhotos.view.PickPhotosWindowMain;

public class PickPhotosMain {
	
	PickPhotosMain(String[] args) {
		try {
			//load configuration from yml
			Utils.getProperties();
			
			//create main window view
			new PickPhotosWindowMain();
			
		} catch (IOException e) {
			Utils.processException(e);
		}
	}
	
	public static void main(String[] args) {
		new PickPhotosMain(args);
	}
}
