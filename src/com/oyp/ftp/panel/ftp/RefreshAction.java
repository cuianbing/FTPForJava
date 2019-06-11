package com.oyp.ftp.panel.ftp;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.oyp.ftp.MainStatic;

/**
 * ˢ�°�ť�Ķ���������
 */
class RefreshAction extends AbstractAction {
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
	 *            - ����������
	 * @param icon
	 *            - ������ͼ��
	 */
	public RefreshAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon); // ���ø��๹�췽��
		this.ftpPanel = ftpPanel; // ��ֵFTP������������
	}

	/**
	 * ˢ�°�ť�Ķ����������������¼�������
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ftpPanel.refreshCurrentFolder(MainStatic.ftpClient.printWorkingDirectory());
		} catch (IOException e1) {
			e1.printStackTrace();
		} // ����ˢ��FTP��Դ�б�ķ���
	}
}