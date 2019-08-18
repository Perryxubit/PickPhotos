package pers.perry.xu.pickphotos.controller;

import java.io.IOException;

import org.apache.log4j.Logger;

import pers.perry.xu.pickphotos.exception.InvalidFilePathException;
import pers.perry.xu.pickphotos.model.PickPhotosModel;
import pers.perry.xu.pickphotos.utils.ToolConfiguration;
import pers.perry.xu.pickphotos.view.PickPhotosWindowMain;

public class PickPhotosController {

	final Logger logger = Logger.getLogger(PickPhotosController.class);

	// only keep one model per controller
	private PickPhotosModel pickPhotosModel;

	public PickPhotosController() {
		logger.setLevel(ToolConfiguration.logLevel);
		logger.info("Create Controller - " + this.getClass());
	}

	public PickPhotosModel getPickPhotosModel() {
		// return singleton model
		if (pickPhotosModel == null) {
			logger.debug("Create initial PickPhotosModel model.");
			pickPhotosModel = new PickPhotosModel();
		}
		return pickPhotosModel;
	}

	public void createView() throws IOException, InvalidFilePathException {
		// create main Swing window
		logger.info("Create Initial View of main window.");
		new PickPhotosWindowMain(this);
	}
}
