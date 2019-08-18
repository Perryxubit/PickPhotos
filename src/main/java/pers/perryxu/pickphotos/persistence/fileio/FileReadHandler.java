package pers.perryxu.pickphotos.persistence.fileio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import pers.perry.xu.pickphotos.utils.Utils;

public class FileReadHandler {

	private BufferedReader bufferedReader = null;

	public FileReadHandler(Path file) {
		try {
			bufferedReader = new BufferedReader(new FileReader(file.toString()));
		} catch (FileNotFoundException e) {
			Utils.processException(e);
		}
	}

	public String readLine() {
		String line = "";
		try {
			line = bufferedReader.readLine();
		} catch (IOException e) {
			Utils.processException(e);
		}
		return line;
	}

	public void endReading() {
		try {
			bufferedReader.close();
		} catch (IOException e) {
			Utils.processException(e);
		}
	}
}
