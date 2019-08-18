package pers.perry.xu.pickphotos.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pers.perry.xu.pickphotos.controller.PickPhotosController;
import pers.perry.xu.pickphotos.exception.InvalidFilePathException;
import pers.perry.xu.pickphotos.utils.ToolLanguage;

@SuppressWarnings("serial")
public class PickPhotosWindowConfiguration extends JFrame {

	public PickPhotosWindowConfiguration(PickPhotosController controller) throws InvalidFilePathException {
		super(ToolLanguage.getToolMessages("options"));

		// show options configruation window
		showOptionsWindow(controller);
	}

	public void showOptionsWindow(final PickPhotosController controller) throws InvalidFilePathException {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int sizeX = 700;
		int sizeY = 100;
		int locationX = dimension.width / 2 - sizeX / 2;
		int locationY = dimension.height / 2 - sizeY / 2;

		Path workspacePath = controller.getPickPhotosModel().getFilePaths().get("work");

		JTextField configFile = new JTextField("", 32);
		configFile.setText("运行时文件目录:" + " " + workspacePath.toString());
		configFile.setEditable(false);
		configFile.setFont(new Font("", Font.PLAIN, 18));
		configFile.setPreferredSize(new Dimension(100, 28));

		JButton initializeButton = new JButton("初始化配置文件");
		initializeButton.setPreferredSize(new Dimension(200, 30));
		initializeButton.setFont(new Font("", Font.BOLD, 20));

		JPanel initPanel = new JPanel();
		initPanel.setLayout(new BorderLayout());
		initPanel.add(configFile, BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(initializeButton);
		initPanel.add(buttonPanel, BorderLayout.SOUTH);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(initPanel, BorderLayout.NORTH);

		initializeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirmation = JOptionPane.showConfirmDialog(null, "您是否真的要初始化所有配置文件?\n(重置照片选择进度，此操作不可逆转)  ", "警告",
						JOptionPane.YES_NO_OPTION);
				if (confirmation == 0) {
					boolean res = controller.getPickPhotosModel().clearAllConfigFile();
					// reset whole runtime environment

					if (!res) {
						JOptionPane.showMessageDialog(null, "初始化失败, 请联系支持部门。 ", "提示",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "初始化成功，本软件将会自动退出\n若要继续使用请重新运行本软件  ", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
				}
			}
		});

		add(topPanel);
		setSize(sizeX, sizeY);
		setLocation(locationX, locationY);
		setVisible(true);
	}
}
