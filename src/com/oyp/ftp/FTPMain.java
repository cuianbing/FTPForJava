package com.oyp.ftp;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.apache.commons.net.ftp.FTPClient;

import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;

@SuppressWarnings("deprecation")
public class FTPMain {
	
	/**
	 * ��Ӧ�õĳ������
	 * cuianbing
	 */
	public static void main(String args[]) {
		try {
			MainStatic.ftpClient = new FTPClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//���� runnable �� run ������ EventQueue ��ָ���߳��ϱ����á�
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//ʹ�� LookAndFeel �������õ�ǰ��Ĭ����ۡ� 
					UIManager.setLookAndFeel(new NimbusLookAndFeel());//����һ���ǳ�Ư�������
//					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					FTPClientFrame client_Frame = new FTPClientFrame();
					client_Frame.setVisible(true);
				} catch (Exception ex) {
					Logger.getLogger(FTPClientFrame.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		});
	}
}
