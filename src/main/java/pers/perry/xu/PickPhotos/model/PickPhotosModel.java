package pers.perry.xu.PickPhotos.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

import pers.perry.xu.PickPhotos.utils.Utils;

public class PickPhotosModel {

	private File[] photoList;

	// the current photo index
	private int currentPhotoIndex = 0;

	// log files
	private Path indexFile;
	private Path logFile;
	private Path pathFile;
	private Path workspacePath;

	

	private HashMap<String, Boolean> copymap;

	public PickPhotosModel() {
		this.copymap = new HashMap<String, Boolean>();

		try {
			// # load work space Path
			loadWorkSpace();


		} catch (IOException e) {
			Utils.processException(e);
		}
	}

	private void loadWorkSpace() throws IOException {
		String workspace = (String) Utils.getProperties().get("workspace");
		if (workspace != null) {
			this.workspacePath = Paths.get(workspace + File.separator + "PickPhotosRuntime");
		} else {
			this.workspacePath = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "PickPhotosRuntime");
		}

		// mkdirs() if not existing
		if (!Files.exists(this.workspacePath)) {
			Files.createDirectories(this.workspacePath);
		}

		// initialize files
		// log.txt file stores the index of most recently checked photo
		this.indexFile = Paths.get(this.workspacePath + File.separator + "index.txt");
		if (!Files.exists(indexFile)) {
			Files.createFile(indexFile);
		}

		this.logFile = Paths.get(this.workspacePath + File.separator + "log.txt");
		if (!Files.exists(logFile)) {
			Files.createFile(logFile);
		}

		this.pathFile = Paths.get(this.workspacePath + File.separator + "path.txt");
		if (!Files.exists(pathFile)) {
			Files.createFile(pathFile);
		}
	}

	public Path getDefaultSourcePath() {
		FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();
		String rootPath = fsv.getDefaultDirectory().getPath() + File.separator + "PickYourPhotos";
		return Paths.get(rootPath);
	}

	public void startShowPhotos(Path sourcePath, Path targetPath) {
		if (!Files.exists(sourcePath)) {
			Utils.showErrorWindow("照片源文件夹路径不存在!");
			return;
		}

		if (!Files.exists(targetPath)) {
			Utils.showErrorWindow("保存照片的目标文件夹路径不存在!");
			return;
		}

		try {
			photoList = new File(sourcePhotoPath).listFiles();
			System.out.println("amount of files: " + photoList.length);
			if (!indexFile.exists()) { // no index file exists, create new one and initialize it (index.txt file stores
										// the already copied file)
				if (logFile.exists())
					logFile.delete();
				indexFile.createNewFile();
				logFile.createNewFile();
				System.out.println("index.txt file does not exist, created index.txt and reset log.txt");
				if (photoList.length >= 1) { // put index in log.txt
					BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					int firstp = 0;
					while (judgePhoto(photoList[firstp]) == false && firstp + 1 < photoList.length)
						firstp++;
					String write = df.format(new Date()) + "__Read photolist__#" + firstp + "#"; // display the first
																									// photo by default
					bw.write(write);
					bw.newLine();
					bw.close();
				}
			} else { // load index.txt into hashmap copymap
				BufferedReader br = new BufferedReader(new FileReader(indexFile));
				String str = br.readLine();
				while (str != null) {
					copymap.put(str, true);
					str = br.readLine();
				}
				br.close();
				System.out.println("load file index.txt successfully.");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, e1.getMessage(), "注意!", 1);
		}

		// update 2 path in path.txt file when clicking the "start" button
		try {
			if (pathFile.exists())
				pathFile.delete();
			pathFile.createNewFile();
			BufferedWriter bwp = new BufferedWriter(new FileWriter(pathFile));
			bwp.write("#R#" + readfile_input.getText());
			bwp.newLine();
			bwp.write("#S#" + savefile_input.getText());
			bwp.newLine();
			bwp.close();
			System.out.println("update file path.txt successfully.");
		} catch (IOException e2) {
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null, e2.getMessage(), "注意!", 1);
		}

		// display the current photo in Jlabel
		if (photoList.length >= 1) {
			// first, find the latest photo in log.txt
			try {
				currentPhoto = getIndexOfCurrentPhoto(logFile); // get current photo index with log.txt file
				System.out.println("Current photo index: " + currentPhoto);
				String photoPath = photoList[currentPhoto].getPath();
				System.out.println("display the current photo: " + photoPath);
				// get wid/hei from the orginal photo
				BufferedImage bufferedImage = ImageIO.read(new File(photoPath));
				int pho_wid = bufferedImage.getWidth();
				int pho_hei = bufferedImage.getHeight();
				photo_width = (int) (size_x);
				photo_height = (int) (pho_hei / (pho_wid / (double) size_y)); // resize photo to suit the window size

				// ### set photo in Jlabel ###
				ImageIcon image = new ImageIcon(photoPath);
				image.setImage(image.getImage().getScaledInstance(photo_width, photo_height, Image.SCALE_DEFAULT));
				photo.setIcon(image);

				// update the text information area
				if (copymap.get(photoList[currentPhoto].getName()) != null) { // saved before
					notice_label.setText("该照片已经保存过了!!!!!");
					deleteButton.show();
				} else {
					notice_label.setText("未保存"); // not saved yet
					deleteButton.hide();
				}
				String details = " 当前照片: " + (currentPhoto + 1) + "/" + photoList.length + "  照片名: "
						+ photoList[currentPhoto].getName() + "  大小: " + photoList[currentPhoto].length() / 1024 + "KB";
				photoInfo_label.setText(details);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, e1.getMessage(), "注意!", 1);
			}
		}
	}

	public void showPreviousPhoto() {
//		// previous photo
//		int photobefore = currentPhotoIndex;
//		currentPhotoIndex--;
//		while (currentPhotoIndex >= 0 && judgePhoto(photoList[currentPhoto]) == false)
//			currentPhotoIndex--;
//		if (currentPhotoIndex >= 0) {
//			// display prevous photo
//			String nextPhoto = photoList[currentPhotoIndex].getPath();
//			System.out.println("index: " + currentPhotoIndex + ",display the current photo: " + nextPhoto);
//			// get wid/hei from the photo
//			try {
//				BufferedImage bufferedImage = ImageIO.read(new File(nextPhoto));
//				int pho_wid = bufferedImage.getWidth();
//				int pho_hei = bufferedImage.getHeight();
//				photo_width = (int) (size_x);
//				photo_height = (int) (pho_hei / (pho_wid / (double) size_y)); // scale
//			} catch (IOException e1) {
//				e1.printStackTrace();
//				JOptionPane.showMessageDialog(null, e1.getMessage(), "注意!", 1);
//			}
//
//			// display photo
//			if (judgePhoto(photoList[currentPhoto]) == true) { // is photo
//				ImageIcon image = new ImageIcon(nextPhoto);
//				image.setImage(image.getImage().getScaledInstance(photo_width, photo_height, Image.SCALE_DEFAULT));
//				photo.setIcon(image);
//			} else
//				photo.setText("当前文件不是照片文件。");
//
//			// no need to update log.txt
//			// update the information text area
//			if (copymap.get(photoList[currentPhotoIndex].getName()) != null) { // saved before
//				notice_label.setText("     该照片已经保存过了!!!    ");
//				deleteButton.show();
//			} else {
//				notice_label.setText(" 未保存 ");
//				deleteButton.hide();
//			}
//			String details = " 当前照片: " + (currentPhotoIndex + 1) + "/" + photoList.length + "  照片名: "
//					+ photoList[currentPhotoIndex].getName() + "  大小: " + photoList[currentPhoto].length() / 1024
//					+ "KB";
//			;
//			photoInfo_label.setText(details);
//		} else {
//			JOptionPane.showMessageDialog(null, "这是第一张!", "注意!", 1);
//			currentPhotoIndex = photobefore;
//		}
	}

	public void showNextPhoto() {
//		File logFile = new File(this.workspacePath + "\\" + "log.txt");
//		// next photo
//		int photobefore = currentPhotoIndex;
//		currentPhotoIndex++;
//		while (currentPhotoIndex < photoList.length && judgePhoto(photoList[currentPhotoIndex]) == false)
//			currentPhoto++;
//		if (currentPhotoIndex >= 0 && currentPhotoIndex < photoList.length) {
//			// display next photo
//			String nextPhoto = photoList[currentPhotoIndex].getPath();
//			System.out.println("index: " + currentPhotoIndex + ",display the current photo: " + nextPhoto);
//			// get wid/hei from the photo
//			try {
//				BufferedImage bufferedImage = ImageIO.read(new File(nextPhoto));
//				int pho_wid = bufferedImage.getWidth();
//				int pho_hei = bufferedImage.getHeight();
//				photo_width = (int) (size_x);
//				photo_height = (int) (pho_hei / (pho_wid / (double) size_y)); // scale
//			} catch (IOException e1) {
//				e1.printStackTrace();
//				JOptionPane.showMessageDialog(null, e1.getMessage(), "注意!", 1);
//			}
//
//			// display photo
//			if (judgePhoto(photoList[currentPhotoIndex]) == true) { // is photo
//				ImageIcon image = new ImageIcon(nextPhoto);
//				image.setImage(image.getImage().getScaledInstance(photo_width, photo_height, Image.SCALE_DEFAULT));
//				photo.setIcon(image);
//			} else
//				photo.setText("当前文件不是照片文件。");
//
//			// update log.txt
//			try {
//				BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
//				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				String write = df.format(new Date()) + "__Read photolist__#" + currentPhotoIndex + "#";
//				bw.write(write);
//				bw.newLine();
//				bw.close();
//			} catch (Exception e2) {
//				e2.printStackTrace();
//				JOptionPane.showMessageDialog(null, e2.getMessage(), "注意!", 1);
//			}
//
//			// update the text information area
//			if (copymap.get(photoList[currentPhotoIndex].getName()) != null) { // saved before
//				notice_label.setText("     该照片已经保存过了!!!    ");
//				deleteButton.show();
//			} else {
//				notice_label.setText(" 未保存 "); // not saved yet
//				deleteButton.hide();
//			}
//			String details = " 当前照片: " + (currentPhotoIndex + 1) + "/" + photoList.length + "  照片名: "
//					+ photoList[currentPhotoIndex].getName() + "  大小: " + photoList[currentPhoto].length() / 1024
//					+ "KB";
//			;
//			photoInfo_label.setText(details);
//		} else {
//			JOptionPane.showMessageDialog(null, "这是最后一张!", "注意!", 1);
//			currentPhotoIndex = photobefore;
//		}
	}

	public void deleteSavePhotoPhoto() {
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

	private boolean judgePhoto(File photo) {
		// Judge the photo suffix
		String extension = photo.getName().substring(photo.getName().indexOf(".") + 1).toUpperCase();
		// Only identify: BMP JPG JPEG PNG GIF
		if (extension.equals("BMP") || extension.equals("JPG") || extension.equals("JPEG") || extension.equals("PNG")
				|| extension.equals("GIF"))
			return true;
		return false;
	}

	public int clearAllConfigFile() {
		File[] configF = new File[3];
		configF[0] = new File(this.workspacePath + "\\" + "index.txt");
		configF[1] = new File(this.workspacePath + "\\" + "log.txt");
		configF[2] = new File(this.workspacePath + "\\" + "path.txt");
		File backup = new File(this.workspacePath + "\\" + "Recycle");

		// clear old recycle folder
		new File(this.workspacePath + "\\Recycle\\index.txt").delete();
		new File(this.workspacePath + "\\Recycle\\log.txt").delete();
		new File(this.workspacePath + "\\Recycle\\path.txt").delete();
		if (backup.isDirectory())
			backup.delete();
		backup.mkdirs();

		try {
			// copy three files
			for (int i = 0; i < 3; i++) {
				FileInputStream input = new FileInputStream(configF[i]);
				String oldFileName = configF[i].getName().substring(configF[i].getName().lastIndexOf("\\") + 1) + "";
				FileOutputStream output = new FileOutputStream(new File(backup.getPath() + "\\" + oldFileName));
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = input.read(b)) != -1) {
					output.write(b, 0, len);
				}
				output.flush();
				output.close();
				input.close();
			}
			// delete all file:
			for (int i = 0; i < 3; i++)
				configF[i].delete();

		} catch (Exception e) {
			e.printStackTrace();
			return 1; // failed
		}

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

	private int getIndexOfCurrentPhoto(File logfile) throws IOException {
		int max = 0;
		BufferedReader br = new BufferedReader(new FileReader(logfile));
		String str = br.readLine();
		while (str != null) {
			// e.g. Read photolist: #125#
			String num = str.substring(str.indexOf("#") + 1, str.lastIndexOf("#"));
			int No = Integer.parseInt(num);
			// get the biggest number, and return it as current photo index
			if (No > max) {
				max = No;
			}
			str = br.readLine();
		}
		br.close();
		return max;
	}


}
