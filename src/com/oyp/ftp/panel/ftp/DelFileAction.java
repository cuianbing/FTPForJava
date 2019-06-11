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
 * FTP����ɾ����ť�Ķ��������� NO
 */
class DelFileAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FtpPanel ftpPanel;

	/**
	 * ɾ�������������Ĺ��췽��
	 * 
	 * @param ftpPanel
	 *            - FTP��Դ�������
	 * @param name
	 *            - ��������
	 * @param icon
	 *            - ͼ��
	 */
	public DelFileAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon);
		this.ftpPanel = ftpPanel;
	}

	public void actionPerformed(ActionEvent e) {
		// ��ȡ��ʾFTP��Դ�б�ı�������ǰѡ���������
		final int[] selRows = ftpPanel.ftpDiskTable.getSelectedRows();
		if (selRows.length < 1)
			return;
		int confirmDialog = JOptionPane.showConfirmDialog(ftpPanel, "ȷ��Ҫɾ����");
		if (confirmDialog == JOptionPane.YES_OPTION) {
			Runnable runnable = new Runnable() {
				/**
				 * ɾ���������ļ��ķ���
				 * 
				 * @param file
				 */
				private void delFile(FTPFile file) {
					try {
						if (file.isFile()) { // ���ɾ�������ļ�
							boolean deleteFile = MainStatic.ftpClient.deleteFile(file.getName());
							if (deleteFile) {
								System.out.println("�ļ�" + file.getName() + "ɾ���ɹ�");
							} else {
								System.out.println("�ļ�" + file.getName() + "ɾ��ʧ��");
							}

						} else { // ���ɾ�������ļ���
							MainStatic.ftpClient.changeWorkingDirectory(file.getName());
							System.out.println("��ǰĿ¼====>" + MainStatic.ftpClient.printWorkingDirectory());
							FTPFile[] listFiles = MainStatic.ftpClient.listFiles();// ��������ļ�
							if (listFiles != null && listFiles.length > 0) {
								for (FTPFile tmp : listFiles) {
									delFile(tmp);
								}
								/***********ɾ����ɺ󷵻���һ��Ŀ¼start****************/
								// ��ǰ����Ŀ¼������·����
								String printWorkingDirectory = MainStatic.ftpClient.printWorkingDirectory();
								// ��ǰ����Ŀ¼��Ŀ¼��
								String rmDirectory = printWorkingDirectory
										.substring(printWorkingDirectory.lastIndexOf("/") + 1);
								// ���ص���һ��Ŀ¼
								MainStatic.ftpClient.changeToParentDirectory();
								/***********ɾ����ɺ󷵻���һ��Ŀ¼end****************/
								// ɾ����Ŀ¼
								boolean removeDirectory = MainStatic.ftpClient.removeDirectory(rmDirectory);
								if (removeDirectory) {
									System.out.println("Ŀ¼ɾ���ɹ���" + rmDirectory);
								} else {
									System.out.println("Ŀ¼ɾ��ʧ�ܣ�" + rmDirectory);
								}
							} else {
								// ��ǰ����Ŀ¼������·����
								String printWorkingDirectory = MainStatic.ftpClient.printWorkingDirectory();
								// ��ǰ����Ŀ¼��Ŀ¼��
								String rmDirectory = printWorkingDirectory
										.substring(printWorkingDirectory.lastIndexOf("/") + 1);
								// ���ص���һ��Ŀ¼
								MainStatic.ftpClient.changeToParentDirectory();
								// ɾ����Ŀ¼
								boolean removeDirectory = MainStatic.ftpClient.removeDirectory(rmDirectory);
								if (removeDirectory) {
									System.out.println("Ŀ¼ɾ���ɹ���" + rmDirectory);
								} else {
									System.out.println("Ŀ¼ɾ��ʧ�ܣ�" + rmDirectory);
								}
							}

						}
					} catch (Exception ex) {
						Logger.getLogger(LocalPanel.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				/**
				 * �̵߳����巽��
				 */
				@SuppressWarnings("unused")
				public void run() {
					// ������ʾFTP��Դ�ı�������ѡ����
					for (int i = 0; i < selRows.length; i++) {
						// ��ȡÿ�еĵ�һ����Ԫֵ����ת��ΪFtpFile����
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
								listFiles = MainStatic.ftpClient.listFiles();// ��������ļ�
								for (FTPFile tmp : listFiles) {
									if (tmp.getName().equals(file.getName())) {
										System.out.println("ɾ��" + file.getName());
										delFile(tmp);
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					// ˢ��FTP��������Դ�б�
					DelFileAction.this.ftpPanel.refreshCurrentFolder(ftpPanel.ftpSelFilePathLabel.getText());
					JOptionPane.showMessageDialog(ftpPanel, "ɾ���ɹ���");
				}
			};
			new Thread(runnable).start();
		}
	}
}