package com.oyp.ftp;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.oyp.ftp.panel.ftp.FtpPanel;
import com.oyp.ftp.utils.FtpClient2;

/**
 * �Ͽ���ť�Ķ���������
 * cuianbing ok
 * 
 */
class CutLinkAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FTPClientFrame frame; // ����������ö���
	FtpPanel ftpPanel;
	public void setFtpPanel(FtpPanel ftpPanel) {
		this.ftpPanel = ftpPanel;
	}
	/**
	 * ���췽��
	 * 
	 * @param client_Frame
	 *            �����������
	 * @param string
	 *            ���������ƣ�������ʾ�ڰ�ť��˵��������
	 * @param icon
	 *            ������ͼ�꣬������ʾ�ڰ�ť��˵��������
	 */
	public CutLinkAction(FTPClientFrame client_Frame, String string, Icon icon) {
		super(string, icon); // ���ø���Ĺ��췽��
		frame = client_Frame; // ��ֵ���������ö���
		setEnabled(false); // ���ò�����״̬
	}

	/**
	 * ����Ͽ���ť�İ�ť�����¼��ķ���
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		frame.ftpPanel.stopDownThread(); // ֹͣ�����߳�
		frame.localPanel.stopUploadThread(); // ֹͣ�ϴ��߳�
		frame.getFtpPanel().getQueue().clear(); // ����������
		frame.getFtpPanel().clearTable(); // ���FTP��Դ�������
		frame.getLocalPanel().getQueue().clear(); // ����������Ķ���
		// ���FTP���Ӷ�����ڣ������Ѿ�����FTP������
		if (MainStatic.ftpClient != null && MainStatic.ftpClient.isConnected()) {
			FtpClient2.closeFtp();
		}
		ftpPanel.ftpSelFilePathLabel.setText("/");
		// �����ϴ���ť������
		frame.localPanel.getActionMap().get("uploadAction").setEnabled(
				false);
		// �������ذ�ť������
		frame.ftpPanel.getActionMap().get("downAction").setEnabled(false);
		setEnabled(false); // ���ñ���ť���Ͽ���������
	}
}