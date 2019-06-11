package com.oyp.ftp.panel;

import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import com.oyp.ftp.utils.*;

/**
 *  ��Ⱦ������Դ��FTP��Դ����������Ⱦ��
 */
public class FTPTableCellRanderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	private final ImageIcon folderIcon = new ImageIcon(getClass().getResource(
			"/com/oyp/ftp/res/folderIcon.JPG")); // �ļ���ͼ��
	private final ImageIcon fileIcon = new ImageIcon(getClass().getResource(
			"/com/oyp/ftp/res/fileIcon.JPG")); // �ļ�ͼ��
	private static FTPTableCellRanderer instance = null; // ��Ⱦ����ʵ������

	/**
	 * ����յĹ��췽��
	 */
	private FTPTableCellRanderer() {
	}

	/**
	 * ��ȡ��Ⱦ��ʵ������ķ���
	 * 
	 * @return ��Ⱦ����ʵ������
	 */
	public static FTPTableCellRanderer getCellRanderer() {
		if (instance == null)
			instance = new FTPTableCellRanderer();
		return instance;
	}

	/**
	 * ��д���ñ�����ݵķ���
	 */
	@Override
	protected void setValue(Object value) {
		if (value instanceof FileInterface) {
			FileInterface file = (FileInterface) value;
			// ��ȡFileSystemView���ʵ������
			FileSystemView view = FileSystemView.getFileSystemView();
			if (file.isDirectory()) {
				setText(file.toString());
				setIcon(folderIcon);
			} else {
				if (file instanceof File) { // �������ΪFile��
					Icon icon = view.getSystemIcon((File) file);// ��ȡ�ļ���ͼ��
					setIcon(icon); // ���ñ��Ԫͼ��
				} else if (file instanceof FtpFile) { // �������ΪFtpFile��
					FtpFile ftpfile = (FtpFile) file;
					try {
						// ʹ��FtpFile���ļ����ƴ�����ʱ�ļ�
						File tempFile = File.createTempFile("tempfile_",
								ftpfile.getName());
						// ��ȡ��ʱ�ļ���ͼ��
						Icon icon = view.getSystemIcon(tempFile);
						tempFile.delete(); // ɾ����ʱ�ļ�
						setIcon(icon); // ���ñ��Ԫͼ��
					} catch (IOException e) {
						e.printStackTrace();
						setIcon(fileIcon);
					}
				}
				setText(file.toString()); // �����ı�����
			}
		} else { // ���ѡ��Ĳ����ļ����ļ���
			setIcon(folderIcon); // ���ñ��õ��ļ���ͼ��
			setText(value.toString()); // ��������
		}
	}
}
