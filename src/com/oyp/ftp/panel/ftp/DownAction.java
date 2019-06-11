package com.oyp.ftp.panel.ftp;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTPFile;

import com.oyp.ftp.MainStatic;
import com.oyp.ftp.utils.FtpFile;

import sun.net.ftp.FtpProtocolException;

/**
 * ���ذ�ť�Ķ���������
 */
class DownAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private final FtpPanel ftpPanel;

	/**
	 * ���췽��
	 * 
	 * @param ftpPanel
	 *            - FTP��Դ�������
	 * @param name
	 *            - ����������
	 * @param icon
	 *            - ������ͼ��
	 */
	public DownAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon); // ���ø��๹�췽��
		this.ftpPanel = ftpPanel; // ��ֵFTP��Դ������������
		setEnabled(false); // ���ö���������
	}

	/**
	 * ���ذ�ť�Ķ����������������¼�������
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// ��ȡFTP��Դ��������ѡ����
		final int[] selRows = ftpPanel.ftpDiskTable.getSelectedRows();
		if (selRows.length < 1) {
			JOptionPane.showMessageDialog(ftpPanel, "��ѡ��Ҫ���ص��ļ���");
			return;
		}
		// ������������ѡ����
		for (int i = 0; i < selRows.length; i++) {
			// ��ȡÿ�еĵ�һ����Ԫֵ��ת����FtpFile��Ķ���
			final FtpFile file = (FtpFile) ftpPanel.ftpDiskTable.getValueAt(selRows[i], 0);
			if (file != null) {
				FTPFile[] listFiles = null;
				try {
					listFiles = MainStatic.ftpClient.listFiles();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				// �����ļ���
				File currentFolder = ftpPanel.frame.getLocalPanel().getCurrentFolder();
				for (FTPFile ftpFile : listFiles) {
					if (ftpFile.getName().equals(file.getName())) {// �ҵ�ѡ�е��ļ��л��ļ�������ӵ������������
						ftpPanel.queue.offer(new Object[] { ftpFile, currentFolder });
					}
					
					try {
						ftpPanel.setFtpClient(MainStatic.ftpClient);
					} catch (FtpProtocolException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		}
	}
}