package com.oyp.ftp.panel.ftp;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import com.oyp.ftp.MainStatic;

/**
 * �����ļ��а�ť�Ķ���������
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
	 * ���췽��
	 * 
	 * @param ftpPanel
	 *            - FTP��Դ�������
	 * @param name
	 *            - ��������
	 * @param icon
	 *            - ����ͼ��
	 */
	public CreateFolderAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon); // ���ø��๹�췽��
		this.ftpPanel = ftpPanel; // ��ֵFTP��Դ������������
	}

	/**
	 * �����ļ��е��¼�������
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// �����û�������½��ļ��е�����
		String folderName = JOptionPane.showInputDialog("�������ļ������ƣ�");
		if (folderName == null)
			return;
		boolean makeDirectory = false;
		try {
			// ���ʹ����ļ��е�����
			makeDirectory = MainStatic.ftpClient.makeDirectory(
					new String(folderName.getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET));
			// ��ȡFTP���������������
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (makeDirectory) {// ������������257��·����������ɣ�
			// ��ʾ�ļ��д����ɹ�
			JOptionPane.showMessageDialog(ftpPanel, folderName + "�ļ��У������ɹ���", "�����ļ���", JOptionPane.INFORMATION_MESSAGE);
		} else {
			// ���� ��ʾ�û����ļ����޷�����
			JOptionPane.showMessageDialog(ftpPanel, folderName + "�ļ����޷���������", "�����ļ���", JOptionPane.ERROR_MESSAGE);
		}
		this.ftpPanel.refreshCurrentFolder(ftpPanel.ftpSelFilePathLabel.getText());
	}
}