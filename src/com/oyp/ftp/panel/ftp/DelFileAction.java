package com.oyp.ftp.panel.ftp;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTPFile;

import com.oyp.ftp.MainStatic;
import com.oyp.ftp.panel.local.LocalPanel;
import com.oyp.ftp.utils.FtpFile;

/**
 * FTP面板的删除按钮的动作处理器 NO
 */
class DelFileAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FtpPanel ftpPanel;

	/**
	 * 删除动作处理器的构造方法
	 * 
	 * @param ftpPanel
	 *            - FTP资源管理面板
	 * @param name
	 *            - 动作名称
	 * @param icon
	 *            - 图标
	 */
	public DelFileAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon);
		this.ftpPanel = ftpPanel;
	}

	public void actionPerformed(ActionEvent e) {
		// 获取显示FTP资源列表的表格组件当前选择的所有行
		final int[] selRows = ftpPanel.ftpDiskTable.getSelectedRows();
		if (selRows.length < 1)
			return;
		int confirmDialog = JOptionPane.showConfirmDialog(ftpPanel, "确定要删除吗？");
		if (confirmDialog == JOptionPane.YES_OPTION) {
			Runnable runnable = new Runnable() {
				/**
				 * 删除服务器文件的方法
				 * 
				 * @param file
				 */
				private void delFile(FTPFile file) {
					try {
						if (file.isFile()) { // 如果删除的是文件
							boolean deleteFile = MainStatic.ftpClient.deleteFile(file.getName());
							if (deleteFile) {
								System.out.println("文件" + file.getName() + "删除成功");
							} else {
								System.out.println("文件" + file.getName() + "删除失败");
							}

						} else { // 如果删除的是文件夹
							MainStatic.ftpClient.changeWorkingDirectory(file.getName());
							System.out.println("当前目录====>" + MainStatic.ftpClient.printWorkingDirectory());
							FTPFile[] listFiles = MainStatic.ftpClient.listFiles();// 获得所有文件
							if (listFiles != null && listFiles.length > 0) {
								for (FTPFile tmp : listFiles) {
									delFile(tmp);
								}
								/***********删除完成后返回上一层目录start****************/
								// 当前工作目录的完整路径名
								String printWorkingDirectory = MainStatic.ftpClient.printWorkingDirectory();
								// 当前工作目录的目录名
								String rmDirectory = printWorkingDirectory
										.substring(printWorkingDirectory.lastIndexOf("/") + 1);
								// 返回到上一层目录
								MainStatic.ftpClient.changeToParentDirectory();
								/***********删除完成后返回上一层目录end****************/
								// 删除空目录
								boolean removeDirectory = MainStatic.ftpClient.removeDirectory(rmDirectory);
								if (removeDirectory) {
									System.out.println("目录删除成功：" + rmDirectory);
								} else {
									System.out.println("目录删除失败：" + rmDirectory);
								}
							} else {
								// 当前工作目录的完整路径名
								String printWorkingDirectory = MainStatic.ftpClient.printWorkingDirectory();
								// 当前工作目录的目录名
								String rmDirectory = printWorkingDirectory
										.substring(printWorkingDirectory.lastIndexOf("/") + 1);
								// 返回到上一层目录
								MainStatic.ftpClient.changeToParentDirectory();
								// 删除空目录
								boolean removeDirectory = MainStatic.ftpClient.removeDirectory(rmDirectory);
								if (removeDirectory) {
									System.out.println("目录删除成功：" + rmDirectory);
								} else {
									System.out.println("目录删除失败：" + rmDirectory);
								}
							}

						}
					} catch (Exception ex) {
						Logger.getLogger(LocalPanel.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				/**
				 * 线程的主体方法
				 */
				@SuppressWarnings("unused")
				public void run() {
					// 遍历显示FTP资源的表格的所有选择行
					for (int i = 0; i < selRows.length; i++) {
						// 获取每行的第一个单元值，并转换为FtpFile类型
						final FtpFile file = (FtpFile) ftpPanel.ftpDiskTable.getValueAt(selRows[i], 0);
						String fileName = null;
						try {
							fileName = MainStatic.ftpClient.printWorkingDirectory() + file.getName();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (file != null) {
							FTPFile[] listFiles;
							try {
								listFiles = MainStatic.ftpClient.listFiles();// 获得所有文件
								for (FTPFile tmp : listFiles) {
									if (tmp.getName().equals(file.getName())) {
										System.out.println("删除" + file.getName());
										delFile(tmp);
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					// 刷新FTP服务器资源列表
					DelFileAction.this.ftpPanel.refreshCurrentFolder(ftpPanel.ftpSelFilePathLabel.getText());
					JOptionPane.showMessageDialog(ftpPanel, "删除成功。");
				}
			};
			new Thread(runnable).start();
		}
	}
}