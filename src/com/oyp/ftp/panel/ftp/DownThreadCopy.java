package com.oyp.ftp.panel.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.oyp.ftp.MainStatic;

/**
 * FTP�ļ�����ģ���FTP�ļ����ض��е��߳�
 */
public class DownThreadCopy extends Thread {
	private final FtpPanel ftpPanel; // FTP��Դ�������
	private boolean conRun = true; // �̵߳Ŀ��Ʊ���
	private String path; // FTP��·����Ϣ
	private Object[] queueValues; // �������������
	private File localRootPath;//���ظ�Ŀ¼
	/**
	 * ���췽��
	 * 
	 * @param ftpPanel
	 *            - FTP��Դ�������
	 */
	public DownThreadCopy(FtpPanel ftpPanel) {
		this.ftpPanel = ftpPanel;
		try {
			MainStatic.ftpClient.noop();
			// ���ӵ�FTP������
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread() { // �������ַ�����ͨѶ���߳�
			public void run() {
				while (conRun) {
					try {
						Thread.sleep(30000);
						MainStatic.ftpClient.noop(); // ��ʱ�������������Ϣ����������
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void stopThread() {// ֹͣ�̵߳ķ���
		conRun = false;
	}

	/**
	 * �����̵߳ĵݹ鷽�����û�̽��FTP�����ļ��е��������ļ��к�����
	 * 
	 * @param file
	 *            FTP�ļ�����
	 * @param localFolder
	 *            �����ļ��ж���
	 */
	private void downFile(FTPFile file, File localFolder) {
		// �ж϶�������Ƿ�ִ����ͣ����
		while (ftpPanel.frame.getQueuePanel().isStop()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Object[] args = ftpPanel.queue.peek();
		// �ж϶��ж��Ƿ�Ϊ�������һ������
		if (queueValues == null || args == null || !queueValues[0].equals(args[0])) {
			return;
		}

		if (file.isFile()) {// �ļ����������
			OutputStream is = null;
			try {
				is = new FileOutputStream(localFolder + "/" + file.getName());
				MainStatic.ftpClient.retrieveFile(file.getName(), is);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (file.isDirectory()) { // ������ص����ļ������ڱ��ش����ļ���
			// ���������ļ��ж���
			
		}

	}

	public void run() { // �߳�ҵ�񷽷�
		while (conRun) {
			try {
				System.out.println("���ض��п�ʼ");
				Thread.sleep(1000);
				MainStatic.ftpClient.noop();
				queueValues = ftpPanel.queue.peek();
				if (queueValues == null) {
					continue;
				}
				// �����ļ���Ŀ¼
				ArrayList<String> pathArray = new ArrayList<String>();
				if (pathArray != null || pathArray.size() > 0) {
					pathArray.clear();
				}
				
				FTPFile file = (FTPFile) queueValues[0];//FTP�ļ�����
				File localFolder = (File) queueValues[1];//�����ļ���
				//��ñ����ļ���·��
				localRootPath = ftpPanel.frame.getLocalPanel().getCurrentFolder();
				mkdirsFolder(MainStatic.ftpClient, path, pathArray);//����Ŀ¼�ṹ
				
				
				if (file != null) {
//					path = file.getPath();
					MainStatic.ftpClient.changeWorkingDirectory(path);
					downFile(file, localFolder);
					path = null;
					ftpPanel.frame.getLocalPanel().refreshCurrentFolder();//ˢ�±����ļ���
				}
				Object[] args = ftpPanel.queue.peek();
				// �ж϶��ж��Ƿ�Ϊ�������һ������
				if (queueValues == null || args == null || !queueValues[0].equals(args[0]))
					continue;
				ftpPanel.queue.poll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ���
	private void mkdirsFolder(FTPClient ftp, String path, ArrayList<String> pathArray) throws IOException {
		ftp.changeWorkingDirectory(path);
		System.out.println(ftp.printWorkingDirectory());
		FTPFile[] files = ftp.listFiles();
		for (FTPFile ftpFile : files) {
			if (ftpFile.getName().equals(".") || ftpFile.getName().equals(".."))
				continue;
			if (ftpFile.isDirectory()) {// �����Ŀ¼����ݹ���ã��������������ļ�
				path += "/" + ftpFile.getName();
				pathArray.add(path);
				ftp.changeWorkingDirectory(path);// �ı䵱ǰ·��
				mkdirsFolder(ftp, path, pathArray);// �ݹ����
				path = path.substring(0, path.lastIndexOf("/"));// �����֮���ͬĿ¼�µ�·�������������ţ�
			}
		}
		//��Ŀ¼�ṹ��������
		for(String string :pathArray) {
			String localPath = localRootPath + string.replace("/", "\\");
			File localFile = new File(localPath);
			System.out.println("�½��ļ���====��" + localPath);
			if (!localFile.exists()) {
				localFile.mkdirs();
			}
		}
	}
}