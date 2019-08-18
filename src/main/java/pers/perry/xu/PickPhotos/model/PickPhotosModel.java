package pers.perry.xu.pickphotos.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import pers.perry.xu.pickphotos.exception.InvalidFilePathException;
import pers.perry.xu.pickphotos.utils.ToolConfiguration;
import pers.perry.xu.pickphotos.utils.Utils;
import pers.perryxu.pickphotos.persistence.dto.Photo;
import pers.perryxu.pickphotos.persistence.fileio.FileReadHandler;
import pers.perryxu.pickphotos.persistence.fileio.FileWriteHandler;

public class PickPhotosModel {

	final Logger logger = Logger.getLogger(PickPhotosModel.class);

	// the current photo index
	private int currentPhotoIndex = 0;

	// the file list of loaded directory
	private List<Path> photoList;

	// the map keeping all paths
	// source/target/work/workindex/worklog/workpath
	private HashMap<String, Path> pathsHashMap = null;

	// checked map
	private HashMap<String, Boolean> fileChecked;

	public PickPhotosModel() {
		logger.setLevel(ToolConfiguration.logLevel);

		try {
			fileChecked = new HashMap<String, Boolean>();

			// # load work space Paths into the pathsHashMap
			loadWorkSpace();
			
		} catch (IOException e) {
			Utils.processException(e);
		}
	}

	private void loadWorkSpace() throws IOException {
		String configWorkspace = (String) Utils.getProperties().get("workspace");
		Path workspace = null;
		if (configWorkspace != null) {
			workspace = Paths.get(configWorkspace + File.separator + ToolConfiguration.workspaceName);
		} else {
			workspace = Paths
					.get(System.getProperty("java.io.tmpdir") + File.separator + ToolConfiguration.workspaceName);
		}

		// mkdirs() if not existing
		if (!Files.exists(workspace)) {
			Files.createDirectories(workspace);
		}

		// initialize files
		// log.txt file stores the index of most recently checked photo
		Path indexFile = Paths.get(workspace + File.separator + "index.txt");
		Path logFile = Paths.get(workspace + File.separator + "log.txt");
		Path pathFile = Paths.get(workspace + File.separator + "path.txt");

		pathsHashMap = new HashMap<String, Path>();
		// saved into the path map
		pathsHashMap.put("work", workspace);
		pathsHashMap.put("workindex", indexFile);
		pathsHashMap.put("worklog", logFile);
		pathsHashMap.put("workpath", pathFile);
		pathsHashMap.put("source", workspace); // default work space
		pathsHashMap.put("target", workspace); // default work space
	}

	private void initialzeLogFiles(Path indexFile, Path logFile, Path pathFile) throws IOException {
		if (Files.exists(logFile)) // delete the existing log file
			Files.delete(logFile);
		if (Files.exists(pathFile)) // delete the existing path file

		// (re)create log and index files
		Files.createFile(indexFile);
		Files.createFile(logFile);
		Files.createFile(pathFile);
	}

	public boolean clearAllConfigFile() {
		Path indexFile = pathsHashMap.get("workindex");
		Path logFile = pathsHashMap.get("worklog");
		Path pathFile = pathsHashMap.get("workpath");

		Path recycleWorkSpace = Paths
				.get(pathsHashMap.get("work") + File.separator + ToolConfiguration.workspaceName + ".recycle");
		
		Path recycleWorkSpaceIndex = Paths.get(recycleWorkSpace + File.separator + indexFile.getFileName());
		Path recycleWorkSpaceLog = Paths.get(recycleWorkSpace + File.separator + logFile.getFileName());
		Path recycleWorkSpacePath = Paths.get(recycleWorkSpace + File.separator + pathFile.getFileName());

		// clear old recycle folder
		try {
			// replace files to recycle directory
			if (!Files.exists(recycleWorkSpace)) {
				Files.createDirectories(recycleWorkSpace);
			}
			if (!Files.exists(recycleWorkSpaceIndex)) {
				Files.copy(indexFile, recycleWorkSpaceIndex, StandardCopyOption.REPLACE_EXISTING);
			}
			if (!Files.exists(recycleWorkSpaceLog)) {
				Files.copy(logFile, recycleWorkSpaceLog, StandardCopyOption.REPLACE_EXISTING);
			}
			if (!Files.exists(recycleWorkSpacePath)) {
				Files.copy(pathFile, recycleWorkSpacePath, StandardCopyOption.REPLACE_EXISTING);
			}

			//delete files
			Files.deleteIfExists(indexFile);
			Files.deleteIfExists(logFile);
			Files.deleteIfExists(pathFile);

			logger.info("Copied 3 runtime files to the .recycle directory.");
			logger.info("Deleted 3 runtime production files.");
		} catch (IOException e) {
			Utils.processException(e);
			return false;
		}

		return true;
	}

	private boolean isValidPhoto(Path photoPath) {
		// # Judge the photo ext
		String extension = FilenameUtils.getExtension(photoPath.getFileName().toString()).toUpperCase();

		// Only identify: BMP JPG JPEG PNG GIF
		if (extension.equals("BMP") || extension.equals("JPG") || extension.equals("JPEG") || extension.equals("PNG")
				|| extension.equals("GIF")) {
			return true;
		} else {
			return false;
		}
	}

	public Photo getPhotoWhenStart(Path sourcePath, Path targetPath) throws IOException {
		if (!Files.exists(sourcePath)) {
			Utils.showErrorWindow(new InvalidFilePathException("照片源文件夹路径不存在!"));
			return null;
		} else if (!Files.exists(targetPath)) {
			Utils.showErrorWindow(new InvalidFilePathException("保存照片的目标文件夹路径不存在!"));
			return null;
		}
		
		if (photoList == null) {
			photoList = new ArrayList<Path>();
		} else {
			photoList.clear();
		}
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath)) {
			for (Path file: stream) {
				if(isValidPhoto(file)) { // only add the valid photo files
					this.photoList.add(file);
				}
			}
		}
		logger.info("Totally found " + photoList.size() + " photos in sourcePath");

		Path indexFile = pathsHashMap.get("workindex");
		Path logFile = pathsHashMap.get("worklog");
		Path workPathFile = pathsHashMap.get("workpath");
		if (!Files.exists(indexFile)) {
			// No index file exists, then initialize all log files
			// (index.txt file stores the already copied file)
			initialzeLogFiles(indexFile, logFile, workPathFile);
			logger.info("Notice: index.txt file does not exist, created new index.txt and reset log.txt.");

			if (photoList.size() >= 1) { // put index in log.txt
				int firstIndex = 0;
				FileWriteHandler fileWriteHandler = new FileWriteHandler(logFile);
				fileWriteHandler.appendFileWithTimeStamp("Read photoList #" + firstIndex);
				fileWriteHandler.endWriting();
			}
		} else { // load index.txt into hashmap copymap
			FileReadHandler fileReadHandler = new FileReadHandler(indexFile);
			String str = fileReadHandler.readLine();
			while (str != null) {
				// initialize the fileChecked hashmap with records in index.txt
				fileChecked.put(str, true);
				str = fileReadHandler.readLine();
			}
			fileReadHandler.endReading();
			logger.info("Loaded existing index.txt successfully.");
		}

		// update 2 path in path.txt file when clicking the "start" button
		// non-append, override old files
		FileWriteHandler pathWriteHandler = new FileWriteHandler(workPathFile);
		pathWriteHandler.appendFile("#READFROM#" + sourcePath);
		pathWriteHandler.appendFile("#SAVETO#" + targetPath);
		pathWriteHandler.endWriting();
		logger.info("Updated workpath file path.txt successfully.");

		// return the current photo in Jlabel
		Photo photo = null;
		if (photoList.size() >= 1) {
			// first, find the latest photo from log.txt
			FileReadHandler fileReadHandler = new FileReadHandler(logFile);
			int max = 0;
			String str = fileReadHandler.readLine();
			while (str != null) {
				// e.g. Read photolist: #125
				String num = str.substring(str.indexOf("#") + 1);
				int No = Integer.parseInt(num);
				// get the biggest number, and return it as current photo index
				if (No > max) {
					max = No;
				}
				str = fileReadHandler.readLine();
			}
			fileReadHandler.endReading();

			currentPhotoIndex = max;
			Path photoPath = photoList.get(currentPhotoIndex);
			logger.info("Current photo index: " + currentPhotoIndex + " [" + photoPath + "]");

			photo = new Photo(photoPath);

			// update the text information area
			if (fileChecked.get(photoPath.toString()) != null) { // the photo was saved before
				photo.setSaved(true);
			} else {
				photo.setSaved(false); // not saved yet
			}
			String details = " 当前照片: " + (currentPhotoIndex + 1) + "/" + photoList.size() + "  照片名: "
					+ photoList.get(currentPhotoIndex).getFileName() + "  大小: "
					+ Files.size(photoList.get(currentPhotoIndex)) / 1024 + "KB";
			photo.setPhotoDescription(details);
		}

		return photo;
	}

	public HashMap<String, Path> getFilePaths() throws InvalidFilePathException {
		if (!isPathsValidated(pathsHashMap)) {
			throw new InvalidFilePathException("路径错误.");
		}
		return pathsHashMap;
	}

	private boolean isPathsValidated(HashMap<String, Path> pathsHashMap) {
		if (pathsHashMap == null) {
			return false;
		}

		return true;
	}

	public Photo getPhotoWhenPrevious() throws IOException {
		// show previous photo
		int photoBeforePrevious = currentPhotoIndex;
		currentPhotoIndex--;

		if (currentPhotoIndex >= 0) {
			// display prevous photo
			Path previousPhoto = photoList.get(currentPhotoIndex);
			logger.info("Previous photo: " + previousPhoto + ", index=" + currentPhotoIndex);

			Photo photo = new Photo(previousPhoto);

			// update log.txt
			Path logFile = pathsHashMap.get("worklog");
			FileWriteHandler fileWriteHandler = new FileWriteHandler(logFile, true);
			fileWriteHandler.appendFileWithTimeStamp("Read photoList #" + currentPhotoIndex);
			fileWriteHandler.endWriting();

			if (fileChecked.get(photoList.get(currentPhotoIndex).toString()) != null) { // the photo was saved before
				photo.setSaved(true);
			} else {
				photo.setSaved(false);
			}

			String details = " 当前照片: " + (currentPhotoIndex + 1) + "/" + photoList.size() + "  照片名: "
					+ photoList.get(currentPhotoIndex).getFileName() + "  大小: "
					+ Files.size(photoList.get(currentPhotoIndex)) / 1024 + "KB";
			photo.setPhotoDescription(details);

			return photo;
		} else {
			JOptionPane.showMessageDialog(null, "这是第一张!", "注意!", 1);
			currentPhotoIndex = photoBeforePrevious;
			return null;
		}
	}

	public Photo getPhotoWhenNext() throws IOException {
		// show next photo
		int photoBeforeNext = currentPhotoIndex;
		currentPhotoIndex++;

		if (currentPhotoIndex >= 0 && currentPhotoIndex < photoList.size()) {
			// display next photo
			Path nextPhoto = photoList.get(currentPhotoIndex);
			logger.info("Next photo: " + nextPhoto + ", index=" + currentPhotoIndex);

			Photo photo = new Photo(nextPhoto);

			// update log.txt
			Path logFile = pathsHashMap.get("worklog");
			FileWriteHandler fileWriteHandler = new FileWriteHandler(logFile, true);
			fileWriteHandler.appendFileWithTimeStamp("Read photoList #" + currentPhotoIndex);
			fileWriteHandler.endWriting();

			if (fileChecked.get(photoList.get(currentPhotoIndex).toString()) != null) { // the photo was saved before
				photo.setSaved(true);
			} else {
				photo.setSaved(false);
			}

			String details = " 当前照片: " + (currentPhotoIndex + 1) + "/" + photoList.size() + "  照片名: "
					+ photoList.get(currentPhotoIndex).getFileName() + "  大小: "
					+ Files.size(photoList.get(currentPhotoIndex)) / 1024 + "KB";
			photo.setPhotoDescription(details);

			return photo;
		} else {
			JOptionPane.showMessageDialog(null, "这是最后一张!", "注意!", 1);
			currentPhotoIndex = photoBeforeNext;
			return null;
		}
	}

	public boolean savePhoto(Path targetPath) throws IOException {
		if (currentPhotoIndex < 0 || currentPhotoIndex >= photoList.size()) {
			return false;
		}

		if (fileChecked.get(photoList.get(currentPhotoIndex).toString()) != null) {
			JOptionPane.showMessageDialog(null,
					"这张照片(" + photoList.get(currentPhotoIndex).getFileName() + ")已经保存过了, 不需要再次保存", "注意!", 1);
			return false;
		} else { // save the photo
			String targetFilePath = targetPath + File.separator + photoList.get(currentPhotoIndex).getFileName();
			Files.copy(photoList.get(currentPhotoIndex), Paths.get(targetFilePath),
					StandardCopyOption.REPLACE_EXISTING);
			logger.debug("Saved file to: " + targetFilePath + " from: " + photoList.get(currentPhotoIndex));

			// Mark the current photo to "Saved" status
			fileChecked.put(photoList.get(currentPhotoIndex).toString(), true);

			// append entry in index.txt file to mark the savings
			Path indexFile = pathsHashMap.get("workindex");

			FileWriteHandler fileWriteHandler = new FileWriteHandler(indexFile, true);
			fileWriteHandler.appendFile(photoList.get(currentPhotoIndex).toString());
			fileWriteHandler.endWriting();
			logger.info("update file index.txt successfully.");

			return true;
		}
	}

	public void deleteSavedPhoto(Path targetPath) throws IOException {
		// delete path from hashmap
		logger.info("Deleting saved photo (index = " + currentPhotoIndex + ")");
		String deletePath = photoList.get(currentPhotoIndex).toString();
		fileChecked.remove(deletePath);

		// re-write the index.txt saved list
		Path indexFile = pathsHashMap.get("workindex");

		FileReadHandler fileReadHandler = new FileReadHandler(indexFile);
		ArrayList<String> tempList = fileReadHandler.readAll();
		fileReadHandler.endReading();

		Files.delete(indexFile);
		Files.createFile(indexFile);

		FileWriteHandler fileWriteHandler = new FileWriteHandler(indexFile, true);
		for (int i=0; i< tempList.size(); i++) {
			if (!tempList.get(i).equals(deletePath))
				fileWriteHandler.appendFile(tempList.get(i));
		}
		fileWriteHandler.endWriting();
		logger.info("Updated index.txt (removed the photo from index.txt)");

		// delete the file in save folder
		Path targetFile = Paths.get(targetPath + File.separator + photoList.get(currentPhotoIndex).getFileName().toString());

		if (Files.exists(targetFile)) {
			Files.delete(targetFile);
			JOptionPane.showMessageDialog(null, "已取消选择该照片 ", "提示", JOptionPane.INFORMATION_MESSAGE);
			logger.info("The saved photo was deleted.");
		}
	}

}
