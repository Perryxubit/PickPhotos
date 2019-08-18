package pers.perryxu.pickphotos.persistence.fileio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import pers.perry.xu.pickphotos.utils.Utils;

public class FileWriteHandler {

	private BufferedWriter bufferedWriter = null;

	public FileWriteHandler(Path file) {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file.toString()));
		} catch (IOException e) {
			Utils.processException(e);
		}
	}

	public FileWriteHandler(Path file, boolean isAppend) {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(file.toString(), isAppend));
		} catch (IOException e) {
			Utils.processException(e);
		}
	}

	public void appendFile(String content) {
		try {
			bufferedWriter.write(content);
			bufferedWriter.newLine();
		} catch (IOException e) {
			Utils.processException(e);
		}
	}

	public void appendFileWithTimeStamp(String content) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			bufferedWriter.write(df.format(new Date()) + "    " + content);
			bufferedWriter.newLine();
		} catch (IOException e) {
			Utils.processException(e);
		}
	}

	public void endWriting() {
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			Utils.processException(e);
		}
	}
}
