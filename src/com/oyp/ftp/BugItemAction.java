package com.oyp.ftp;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
/**
 * ���  ����(H)-->���󱨸�(U)
 * ����ϵͳ�ĵ����ʼ��������ʼ�
 */
class BugItemAction implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				URI uri = new URI("mailto:chian_cab@163.com");
				desktop.mail(uri);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}