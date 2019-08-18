package pers.perryxu.pickphotos.persistence.dto;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Photo {

	private int photoWidth;

	private int photoHeight;

	private ImageIcon photoImage;

	private String photoDescription;

	private boolean isSaved;

	public Photo(Path photoPath) throws IOException {
		photoImage = new ImageIcon(photoPath.toString());
		isSaved = false;
		photoDescription = "";
		// get photo width/height
		BufferedImage bufferedImage = ImageIO.read(new File(photoPath.toString()));
		this.photoWidth = bufferedImage.getWidth();
		this.photoHeight = bufferedImage.getHeight();
	}

	public ImageIcon getDisplayedPhoto(int sizeX, int sizeY) {
		int displayedWidth = sizeX;
		int displayedHeight = (int) (photoHeight / (photoWidth / (double) sizeY)); // resize photo size
		photoImage.setImage(
				photoImage.getImage().getScaledInstance(displayedWidth, displayedHeight, Image.SCALE_DEFAULT));

		return photoImage;
	}

	public int getPhotoWidth() {
		return photoWidth;
	}

	public void setPhotoWidth(int photoWidth) {
		this.photoWidth = photoWidth;
	}

	public int getPhotoHeight() {
		return photoHeight;
	}

	public void setPhotoHeight(int photoHeight) {
		this.photoHeight = photoHeight;
	}

	public ImageIcon getPhotoImage() {
		return photoImage;
	}

	public void setPhotoImage(ImageIcon photoImage) {
		this.photoImage = photoImage;
	}

	public String getPhotoDescription() {
		return photoDescription;
	}

	public void setPhotoDescription(String photoDescription) {
		this.photoDescription = photoDescription;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public void setSaved(boolean isSaved) {
		this.isSaved = isSaved;
	}
}
