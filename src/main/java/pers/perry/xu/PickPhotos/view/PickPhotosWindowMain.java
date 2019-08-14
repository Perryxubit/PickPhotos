package pers.perry.xu.PickPhotos.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pers.perry.xu.PickPhotos.model.PickPhotosModel;
import pers.perry.xu.PickPhotos.utils.ToolLanguage;


@SuppressWarnings("serial")
public class PickPhotosWindowMain extends JFrame  {
	private PickPhotosModel pickPhotosModel;
	private String targetPhotoPath = "";
	private String sourcePhotoPath = "";
	

	String targetFolder = "";

	int photo_width;
	int photo_height;
	int currentPhoto = -1; //index of current photo
	
	public PickPhotosWindowMain() throws IOException {
		super(ToolLanguage.getToolMessages("title"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Initialization
		this.pickPhotosModel = new PickPhotosModel();
		
		this.sourcePhotoPath = pickPhotosModel.getDefaultSourcePath().toString();
		this.targetPhotoPath = sourcePhotoPath;
		
		// create main window of the tool
		showMainWindow();
	}
	
	private void showMainWindow() throws IOException {
		//set size and location for main window
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int size_x = 600;
		int size_y = 800;
		int loc_x = dimension.width/2-size_x/2;
		int loc_y = dimension.height/2-size_y/2;
		
		//# The source/target directory input field
		JLabel sourceFileLabel = new JLabel(ToolLanguage.getToolMessages("input"));
		sourceFileLabel.setFont(new Font("", Font.BOLD, 18));
		
		final JTextField sourceFilePathField = new JTextField("", 28);
		sourceFilePathField.setText(sourcePhotoPath);
		sourceFilePathField.setFont(new Font("", Font.PLAIN, 18));
		sourceFilePathField.setPreferredSize(new Dimension(100, 28));
		
		JLabel targetFileLabel = new JLabel(ToolLanguage.getToolMessages("output"));
		targetFileLabel.setFont(new Font("", Font.BOLD, 18));
		
		final JTextField targetFilePathField = new JTextField("", 28);
		targetFilePathField.setText(targetPhotoPath);
		targetFilePathField.setFont(new Font("", Font.PLAIN, 18));
		targetFilePathField.setPreferredSize(new Dimension(100, 28));
		
		JButton startButton = new JButton(ToolLanguage.getToolMessages("start"));
		startButton.setPreferredSize(new Dimension(110, 30));
		startButton.setFont(new Font("", Font.BOLD, 20));
		
		JButton optionButton = new JButton(ToolLanguage.getToolMessages("options"));
		optionButton.setPreferredSize(new Dimension(110, 30));
		optionButton.setFont(new Font("", Font.BOLD, 20));
		
		//# Read the latest source/target path from the history pickphotos.log
		File pathFile = new File(pickPhotosModel.getWorkspacePath() + "\\" + "path.txt");
		BufferedReader br = new BufferedReader(new FileReader(pathFile));
		String str = br.readLine();
		while(str != null) {
		if(str.contains("#R#")) sourceFilePathField.setText(str.replace("#R#", ""));
		if(str.contains("#S#")) targetFilePathField.setText(str.replace("#S#", ""));
			str = br.readLine();
		}
		br.close();
					
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
				
		JButton deleteButton = new JButton("取消选择");
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
		JLabel photo = new JLabel("");
				
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
		toolTopPanel.add(photo, BorderLayout.CENTER); //center: photo
				
		//# Buttons listeners
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path targetPath = Paths.get(targetFilePathField.getText().trim());
				Path sourcePath = Paths.get(sourceFilePathField.getText().trim());
				pickPhotosModel.startShowPhotos(sourcePath, targetPath);
			}
		});
				
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//save the current photo record in index.txt and copy to target directory
				pickPhotosModel.savePhoto();
			}
		});
				
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pickPhotosModel.showNextPhoto();
			}
		});

		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pickPhotosModel.showPreviousPhoto();
			}
		});
				
		optionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openConfigurationWiindow();
			}
		});
						
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pickPhotosModel.deleteSavePhotoPhoto();
			}
		});
				
		//# create Main window
		add(toolTopPanel);
		setSize(size_x, size_y);
		setLocation(loc_x, loc_y);
		setVisible(true);
	}

	// Singleton Pattern
	private PickPhotosConfiguration configurationWindow;

	public void openConfigurationWiindow() {
		if (configurationWindow == null) {
			configurationWindow = new PickPhotosConfiguration(pickPhotosModel);
			configurationWindow.showOptionsWindow();
		} else {
			configurationWindow.showOptionsWindow();
		}
	}
}
