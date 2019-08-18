package pers.perry.xu.pickphotos;

import java.io.IOException;

import pers.perry.xu.pickphotos.controller.PickPhotosController;
import pers.perry.xu.pickphotos.exception.InvalidFilePathException;
import pers.perry.xu.pickphotos.utils.Utils;

public class PickPhotosMain {

	PickPhotosMain(String[] args) {
		try {
			// initialze
			toolInitialization();

			//create main window view
			PickPhotosController controller = new PickPhotosController();
			controller.createView();
			
		} catch (IOException | InvalidFilePathException e) {
			Utils.processException(e);
		}
	}
	
	private static void toolInitialization() {
		//load configuration from yml
		Utils.getProperties();


	}

	public static void main(String[] args) {
		new PickPhotosMain(args);
	}
}
