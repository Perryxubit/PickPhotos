package pers.perry.xu.pickphotos.view;
//package pers.perry.xu.PickPhotos.view;
//
//import java.awt.BorderLayout;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.Toolkit;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//import javax.swing.filechooser.FileSystemView;
//
//import pers.perry.xu.PickPhotos.model.PickPhotosModel;
//import pers.perry.xu.PickPhotos.utils.ToolLanguage;
//
//@SuppressWarnings("serial")
//public class PickPhotosConfiguration extends JFrame{
//
//	private PickPhotosModel pickPhotosModel;
//
//	public PickPhotosConfiguration(PickPhotosModel pickPhotosModel) {
//		super(ToolLanguage.getToolMessages("options"));
//		this.pickPhotosModel = pickPhotosModel;
//
//		// show options configruation window
//		showOptionsWindow();
//	}
//
//	public void showOptionsWindow() {
//		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
//		int size_x = 500;
//		int size_y = 300;
//		int loc_x = dimension.width/2-size_x/2;
//		int loc_y = dimension.height/2-size_y/2;
//		
//		JTextField configFile = new JTextField("", 32);
//		FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView(); 
//		configFile.setText(fsv.getDefaultDirectory().getPath() + "\\" + "PickYourPhotos");
//		configFile.setEditable(false);
//		configFile.setFont(new Font("", Font.PLAIN, 18));
//		configFile.setPreferredSize(new Dimension(100, 28));
//
//		JButton initializeButton = new JButton("初始化配置文件");
//		initializeButton.setPreferredSize(new Dimension(200, 30));
//		initializeButton.setFont(new Font("", Font.BOLD, 20));
//		
//		JPanel initPanel = new JPanel();
//		initPanel.setLayout(new BorderLayout());
//		initPanel.add(configFile, BorderLayout.NORTH);
//		JPanel buttonPanel = new JPanel();
//		buttonPanel.add(initializeButton);
//		initPanel.add(buttonPanel, BorderLayout.SOUTH);
//		
//		JPanel topPanel = new JPanel();
//		topPanel.setLayout(new BorderLayout());
//		topPanel.add(initPanel, BorderLayout.NORTH);
//		
//		initializeButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int n = JOptionPane.showConfirmDialog(null, "您是否真的要初始化所有配置文件?\n(重置照片选择进度，此操作不可逆转)  ","警告",JOptionPane.YES_NO_OPTION);
//				if(n == 0) {
//					int res = pickPhotosModel.clearAllConfigFile();
//					if(res != 0) JOptionPane.showMessageDialog(null, "初始化失败 (是否已经初始化过?) ", "提示",JOptionPane.INFORMATION_MESSAGE);
//					else {
//						JOptionPane.showMessageDialog(null, "初始化成功，本软件将会自动退出\n若要继续使用请重新运行本软件  ", "提示",JOptionPane.INFORMATION_MESSAGE);
//						System.exit(0);
//					}
//				}
//			}
//		});
//				
//		add(topPanel);
//		setSize(size_x, size_y);
//        setLocation(loc_x, loc_y);
//        setVisible(true);
//	}
//}
