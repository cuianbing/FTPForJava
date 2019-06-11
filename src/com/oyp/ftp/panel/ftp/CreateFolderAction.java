package com.oyp.ftp.panel.ftp;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.oyp.ftp.MainStatic;

/**
 * 创建文件夹按钮的动作处理器
 * 
 * cuianbing ok
 */
class CreateFolderAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FtpPanel ftpPanel;

	/**
	 * 构造方法
	 * 
	 * @param ftpPanel
	 *            - FTP资源管理面板
	 * @param name
	 *            - 动作名称
	 * @param icon
	 *            - 动作图标
	 */
	public CreateFolderAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon); // 调用父类构造方法
		this.ftpPanel = ftpPanel; // 赋值FTP资源管理面板的引用
	}

	/**
	 * 创建文件夹的事件处理方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// 接收用户输入的新建文件夹的名称
		String folderName = JOptionPane.showInputDialog("请输入文件夹名称：");
		if (folderName == null)
			return;
		boolean makeDirectory = false;
		try {
			// 发送创建文件夹的命令
			makeDirectory = MainStatic.ftpClient.makeDirectory(
					new String(folderName.getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET));
			// 读取FTP服务器的命令返回码
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (makeDirectory) {// 如果返回码等于257（路径名建立完成）
			// 提示文件夹创建成功
			JOptionPane.showMessageDialog(ftpPanel, folderName + "文件夹，创建成功。", "创建文件夹", JOptionPane.INFORMATION_MESSAGE);
		} else {
			// 否则 提示用户该文件夹无法创建
			JOptionPane.showMessageDialog(ftpPanel, folderName + "文件夹无法被创建。", "创建文件夹", JOptionPane.ERROR_MESSAGE);
		}
		this.ftpPanel.refreshCurrentFolder(ftpPanel.ftpSelFilePathLabel.getText());
	}
}