package pers.perry.xu.pickphotos.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import pers.perry.xu.pickphotos.controller.PickPhotosController;
import pers.perry.xu.pickphotos.exception.InvalidFilePathException;
import pers.perry.xu.pickphotos.utils.ToolConfiguration;
import pers.perry.xu.pickphotos.utils.ToolLanguage;
import pers.perry.xu.pickphotos.utils.Utils;
import pers.perryxu.pickphotos.persistence.dto.Photo;
import pers.perryxu.pickphotos.persistence.fileio.FileReadHandler;


@SuppressWarnings("serial")
public class PickPhotosWindowMain extends JFrame  {
	final Logger logger = Logger.getLogger(PickPhotosWindowMain.class);
	
	public PickPhotosWindowMain(PickPhotosController controller) throws IOException, InvalidFilePathException {
		super(ToolLanguage.getToolMessages("title"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		logger.setLevel(ToolConfiguration.logLevel);

		// create main window of the tool
		showMainWindow(controller);
	}

	private void showMainWindow(final PickPhotosController controller) throws IOException, InvalidFilePathException {
		//set size and location for main window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		final int size_x = 1000;
		final int size_y = 800;
		final int loc_x = dimension.width/2-size_x/2;
		final int loc_y = dimension.height/2-size_y/2;
		
		//# The source/target directory input field
		JLabel sourceFileLabel = new JLabel(ToolLanguage.getToolMessages("input"));
		sourceFileLabel.setFont(new Font("", Font.BOLD, 18));
		
		final JTextField sourceFilePathField = new JTextField("", 32);
		sourceFilePathField.setText(controller.getPickPhotosModel().getFilePaths().get("source").toString());
		sourceFilePathField.setFont(new Font("", Font.PLAIN, 18));
		sourceFilePathField.setPreferredSize(new Dimension(100, 28));
		
		JLabel targetFileLabel = new JLabel(ToolLanguage.getToolMessages("output"));
		targetFileLabel.setFont(new Font("", Font.BOLD, 18));
		
		final JTextField targetFilePathField = new JTextField("", 32);
		targetFilePathField.setText(controller.getPickPhotosModel().getFilePaths().get("target").toString());
		targetFilePathField.setFont(new Font("", Font.PLAIN, 18));
		targetFilePathField.setPreferredSize(new Dimension(100, 28));
		
		JButton startButton = new JButton(ToolLanguage.getToolMessages("start"));
		startButton.setPreferredSize(new Dimension(110, 30));
		startButton.setFont(new Font("", Font.BOLD, 20));
		
		JButton optionButton = new JButton(ToolLanguage.getToolMessages("options"));
		optionButton.setPreferredSize(new Dimension(110, 30));
		optionButton.setFont(new Font("", Font.BOLD, 20));
		
		//# Read the latest source/target path from the history pickphotos.log
		Path pathFile = controller.getPickPhotosModel().getFilePaths().get("workpath");
		if (Files.exists(pathFile)) {// load work space path file if existing
			FileReadHandler fileReadHandler = new FileReadHandler(pathFile);
			String str = fileReadHandler.readLine();
			while(str != null) {
				if (str.contains(ToolConfiguration.sourceFlagInLog)) {
					sourceFilePathField.setText(str.replace(ToolConfiguration.sourceFlagInLog, "").trim());
				}
				if (str.contains(ToolConfiguration.targetFlagInLog)) {
					targetFilePathField.setText(str.replace(ToolConfiguration.targetFlagInLog, "").trim());
				}
				str = fileReadHandler.readLine();
			}
			fileReadHandler.endReading();
		}
					
		//# put components in the java GUI
		JPanel sourcePanel = new JPanel();
		sourcePanel.setLayout(new GridLayout());
		sourcePanel.add(sourceFileLabel);
		sourcePanel.add(sourceFilePathField);
		sourcePanel.add(startButton);
				
		JPanel targetPanel = new JPanel();
		sourcePanel.setLayout(new FlowLayout());
		targetPanel.add(targetFileLabel);
		targetPanel.add(targetFilePathField);
		targetPanel.add(optionButton);

		JPanel startPanel = new JPanel();
		startPanel.setLayout(new GridLayout(2,1));
				
		final JButton deleteButton = new JButton("取消选择");
		deleteButton.setPreferredSize(new Dimension(135, 35));
		deleteButton.setFont(new Font("", Font.BOLD, 20));	
		deleteButton.setVisible(false);
		
		
		//msgLabel is used to show the status of current photo:
		JLabel msgLabel = new JLabel(ToolLanguage.getToolMessages("msgstartpicking")); 		
		msgLabel.setFont(new Font("", Font.BOLD, 24));
		
		JLabel photoInfoLabel = new JLabel("");
		photoInfoLabel.setFont(new Font("", Font.BOLD, 24));
				
		JPanel startSubPanel = new JPanel();
		startSubPanel.setLayout(new FlowLayout());
		startSubPanel.add(msgLabel);
		startSubPanel.add(deleteButton);
		startPanel.add(startSubPanel);
		startPanel.add(photoInfoLabel);
				
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(sourcePanel, BorderLayout.NORTH);
		inputPanel.add(targetPanel, BorderLayout.CENTER);
		inputPanel.add(startPanel, BorderLayout.SOUTH);
				
		//Jlabel photo is used to display the current photo
		final JLabel photoPanel = new JLabel("");
				
		JButton saveButton = new JButton(ToolLanguage.getToolMessages("save"));
		saveButton.setPreferredSize(new Dimension(170, 60));
		saveButton.setFont(new Font("", Font.BOLD, 22));
		JButton prevButton = new JButton(ToolLanguage.getToolMessages("previouspic"));
		prevButton.setPreferredSize(new Dimension(170, 60));
		prevButton.setFont(new Font("", Font.BOLD, 22));
		JButton nextButton = new JButton(ToolLanguage.getToolMessages("nextpic"));
		nextButton.setPreferredSize(new Dimension(170, 60));
		nextButton.setFont(new Font("", Font.BOLD, 22));
	
		JPanel prevButtonPanel = new JPanel();
		prevButtonPanel.setLayout(new FlowLayout());
		prevButtonPanel.add(prevButton);
		JPanel saveButtonPanel = new JPanel();
		saveButtonPanel.setLayout(new FlowLayout());
		saveButtonPanel.add(saveButton);
		JPanel nextButtonPanel = new JPanel();
		nextButtonPanel.setLayout(new FlowLayout());
		nextButtonPanel.add(nextButton);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(1, 3));
		buttonsPanel.add(prevButtonPanel);
		buttonsPanel.add(saveButtonPanel);
		buttonsPanel.add(nextButtonPanel);	
				
		//# Top panel setting for the whole main window
		JPanel toolTopPanel = new JPanel(); 
		toolTopPanel.setLayout(new BorderLayout());
		toolTopPanel.add(inputPanel, BorderLayout.NORTH); //north: input panel
		toolTopPanel.add(buttonsPanel, BorderLayout.SOUTH); //south: button panel
		toolTopPanel.add(photoPanel, BorderLayout.CENTER); //center: photo
				
		//# Buttons listeners
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path newTargetPath = Paths.get(targetFilePathField.getText().trim());
				Path newSourcePath = Paths.get(sourceFilePathField.getText().trim());
				
				ImageIcon picture = null;
				try {
					Photo photo = controller.getPickPhotosModel().getPhotoWhenStart(newSourcePath, newTargetPath);
					if(photo != null) {
						picture = photo.getDisplayedPhoto(size_x, size_y);
						photoPanel.setIcon(picture);
						if (photo.isSaved()) {
							deleteButton.setVisible(true);
						} else {
							deleteButton.setVisible(false);
						}
					}
				} catch (IOException ioException) {
					Utils.processException(ioException);
				}
			}
		});
				
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//save the current photo record in index.txt and copy to target directory
				controller.getPickPhotosModel().savePhoto();
			}
		});
				
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageIcon picture = null;
				try {
					Photo photo = controller.getPickPhotosModel().getPhotoWhenNext();
					if (photo != null) {
						picture = photo.getDisplayedPhoto(size_x, size_y);
						photoPanel.setIcon(picture);
						if (photo.isSaved()) {
							deleteButton.setVisible(true);
						} else {
							deleteButton.setVisible(false);
						}
					}
				} catch (IOException ioException) {
					Utils.processException(ioException);
				}
			}
		});

		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageIcon picture = null;
				try {
					Photo photo = controller.getPickPhotosModel().getPhotoWhenPrevious();
					if (photo != null) {
						picture = photo.getDisplayedPhoto(size_x, size_y);
						photoPanel.setIcon(picture);
						if (photo.isSaved()) {
							deleteButton.setVisible(true);
						} else {
							deleteButton.setVisible(false);
						}
					}
				} catch (IOException ioException) {
					Utils.processException(ioException);
				}
			}
		});
				
		optionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
						
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.getPickPhotosModel().deleteSavedPhoto();
			}
		});
				
		//# create Main window
		add(toolTopPanel);
		setSize(size_x, size_y);
		setLocation(loc_x, loc_y);
		setVisible(true);
	}
}
