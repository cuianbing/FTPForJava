package com.oyp.ftp;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.oyp.ftp.panel.manager.FtpLinkDialog;

/**
 * ���ӵ���ť�Ķ�����
 */
class LinkToAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final FTPClientFrame client_Frame;

	/**
	 * ���췽��
	 * 
	 * @param frame
	 *            �����������
	 * @param string
	 *            ����������
	 * @param icon
	 *            ������ͼ��
	 */
	public LinkToAction(FTPClientFrame frame, String string, Icon icon) {
		super(string, icon); // ���ø��๹�췽��
		client_Frame = frame; // ��ֵ�����������
	}

	/**
	 * ���������¼�����
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// �������ӵ�FTPվ��Ի���
		FtpLinkDialog dialog = new FtpLinkDialog(this.client_Frame);
	}
}