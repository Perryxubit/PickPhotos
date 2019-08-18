package pers.perry.xu.pickphotos.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
			workspace = Paths.get(configWorkspace + File.separator + "PickPhotosRuntime");
		} else {
			workspace = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "PickPhotosRuntime");
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
			if (fileChecked.get(photoPath.getFileName().toString()) != null) { // the photo was saved before
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

			if (fileChecked.get(photoList.get(currentPhotoIndex).getFileName().toString()) != null) { // the photo was
																										// saved before
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

			if (fileChecked.get(photoList.get(currentPhotoIndex).getFileName().toString()) != null) { // the photo was
																										// saved before
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

	public void deleteSavedPhoto() {
//		try {
//			// 1. delete from hashmap
//			String deleteName = photoList[currentPhoto].getName();
//			System.out.println("Delete  saved photo (index = " + currentPhoto + ")");
//			copymap.remove(deleteName);
//
//			// 2. re-write the index.txt saved list
//			File indexFile = new File(this.workspacePath + "\\" + "index.txt");
//			ArrayList<String> templist = new ArrayList<String>();
//			BufferedReader br = new BufferedReader(new FileReader(indexFile));
//			String str = br.readLine();
//			while (str != null) {
//				if (!str.equals(deleteName))
//					templist.add(str);
//				str = br.readLine();
//			}
//			br.close();
//
//			indexFile.delete();
//			indexFile.createNewFile();
//			BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile, true));
//			for (int i = 0; i < templist.size(); i++) {
//				bw.write(templist.get(i));
//				bw.newLine();
//			}
//			bw.close();
//			System.out.println("Updated index.txt (removed the photo)");
//
//			// 3. delete the file in save folder
//			File deleteFile = new File(targetPhotoPath + "\\" + photoList[currentPhoto].getName());
//			if (deleteFile.exists()) {
//				deleteFile.delete();
//				System.out.println(deleteFile.getPath() + " deleted.");
//			} else
//				System.out.println("No file exist? need t ocheck inconsistency!");
//
//			// 4. changed info text field
//			notice_label.setText(" 未保存 ");
//			deleteButton.hide();
//
//			JOptionPane.showMessageDialog(null, "已取消选择该照片 ", "提示", JOptionPane.INFORMATION_MESSAGE);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}



	public int clearAllConfigFile() {
//		File[] configF = new File[3];
//		configF[0] = new File(this.workspacePath + "\\" + "index.txt");
//		configF[1] = new File(this.workspacePath + "\\" + "log.txt");
//		configF[2] = new File(this.workspacePath + "\\" + "path.txt");
//		File backup = new File(this.workspacePath + "\\" + "Recycle");
//
//		// clear old recycle folder
//		new File(this.workspacePath + "\\Recycle\\index.txt").delete();
//		new File(this.workspacePath + "\\Recycle\\log.txt").delete();
//		new File(this.workspacePath + "\\Recycle\\path.txt").delete();
//		if (backup.isDirectory())
//			backup.delete();
//		backup.mkdirs();
//
//		try {
//			// copy three files
//			for (int i = 0; i < 3; i++) {
//				FileInputStream input = new FileInputStream(configF[i]);
//				String oldFileName = configF[i].getName().substring(configF[i].getName().lastIndexOf("\\") + 1) + "";
//				FileOutputStream output = new FileOutputStream(new File(backup.getPath() + "\\" + oldFileName));
//				byte[] b = new byte[1024 * 5];
//				int len;
//				while ((len = input.read(b)) != -1) {
//					output.write(b, 0, len);
//				}
//				output.flush();
//				output.close();
//				input.close();
//			}
//			// delete all file:
//			for (int i = 0; i < 3; i++)
//				configF[i].delete();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return 1; // failed
//		}

		// initialize successfully
		return 0;
	}


	public void savePhoto() {
//		if (currentPhoto >= 0) {
//			if (copymap.get(photoList[currentPhoto].getName()) != null)
//				JOptionPane.showMessageDialog(null, "这张照片(" + photoList[currentPhoto].getName() + ")已经保存过了, 不需要再次保存",
//						"注意!", 1);
//			else { // save the photo
//				copymap.put(photoList[currentPhoto].getName(), true);
//				// first add to index.txt
//				File indexFile = new File(this.workspacePath + "\\" + "index.txt");
//				try {
//					// first add entry in index.txt file
//					BufferedWriter bw = new BufferedWriter(new FileWriter(indexFile, true));
//					bw.write(photoList[currentPhoto].getName());
//					bw.newLine();
//					bw.close();
//
//					// then copy the file
//					InputStream inStream = new FileInputStream(photoList[currentPhoto].getPath());
//					FileOutputStream fs = new FileOutputStream(
//							targetPhotoPath + "\\" + photoList[currentPhoto].getName());
//					byte[] buffer = new byte[1444];
//					int length, bytesum = 0, byteread = 0;
//					while ((byteread = inStream.read(buffer)) != -1) {
//						bytesum += byteread;
////		                   System.out.println(bytesum); 
//						fs.write(buffer, 0, byteread);
//					}
//					fs.close();
//					inStream.close();
//
//					// inform the users that save is successfully.
//					System.out.println("update file index.txt successfully.");
//					notice_label.setText("该照片已经保存过了!!!!!");
//					deleteButton.show();
//					JOptionPane.showMessageDialog(null, "保存成功!", "提示", 1);
//				} catch (Exception e2) {
//					e2.printStackTrace();
//					JOptionPane.showMessageDialog(null, e2.getMessage(), "注意!", 1);
//				}
//			}
//		}
	}


}
