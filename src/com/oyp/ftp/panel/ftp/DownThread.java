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
public class DownThread extends Thread {
	private final FtpPanel ftpPanel; // FTP��Դ�������
	private boolean conRun = true; // �̵߳Ŀ��Ʊ���
	@SuppressWarnings("unused")
	private String path; // FTP��·����Ϣ
	private Object[] queueValues; // �������������
	private File localRootPath;// ���ظ�Ŀ¼

	/**
	 * ���췽��
	 * 
	 * @param ftpPanel
	 *            - FTP��Դ�������
	 */
	public DownThread(FtpPanel ftpPanel) {
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

	public void run() { // �߳�ҵ�񷽷�
		while (conRun) {
				try {
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
					FTPFile file = (FTPFile) queueValues[0];// FTP�ļ�����
					// File localFolder = (File) queueValues[1];// �����ļ���
					// ��ñ����ļ���·��
					localRootPath = ftpPanel.frame.getLocalPanel().getCurrentFolder();
					if (file != null) {
						mkdirsFolder(MainStatic.ftpClient, file.getName(), pathArray);// ����Ŀ¼�ṹ
						downFile(MainStatic.ftpClient, pathArray);//�����ļ�
						ftpPanel.frame.getLocalPanel().refreshCurrentFolder();// ˢ�±����ļ���
					}
					
					Object[] args = ftpPanel.queue.peek();
					// �ж϶��ж��Ƿ�Ϊ�������һ������
					if (queueValues == null || args == null || !queueValues[0].equals(args[0]))
						continue;
					ftpPanel.queue.poll();
					conRun = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	// ���
	/**
	 * 
	 * @param ftp
	 * @param path
	 *            Զ��Ŀ¼
	 * @param pathArray
	 * @throws IOException
	 */
	private void mkdirsFolder(FTPClient ftp, String path, ArrayList<String> pathArray) throws IOException {
		String localPath = null;
		String caonima = "/" + path;
		boolean changeWorkingDirectory = ftp.changeWorkingDirectory(new String(caonima.getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET));
		System.out.println("�����ļ���" + caonima + "  " + changeWorkingDirectory);
		System.out.println(ftp.printWorkingDirectory());
		FTPFile[] files = ftp.listFiles();
		System.out.println(files.length);
		
		for (FTPFile ftpFile : files) {
			if (ftpFile.getName().equals(".") || ftpFile.getName().equals(".."))
				continue;
			if (ftpFile.isDirectory()) {// �����Ŀ¼����ݹ���ã��������������ļ�
				path += "/" + ftpFile.getName();
				localPath = localRootPath + "\\" + path.replace("/", "\\");
				pathArray.add(path);
				File localFile = new File(localPath);
				if (!localFile.exists()) {
					localFile.mkdirs();
				}
				ftp.changeWorkingDirectory(new String(ftpFile.getName().getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET));// �ı䵱ǰ·��
				mkdirsFolder(MainStatic.ftpClient, path, pathArray);// �ݹ����
				path = path.substring(0, path.lastIndexOf("/"));// �����֮���ͬĿ¼�µ�·�������������ţ�
			}
		}
	}

	private void downFile(FTPClient ftp, ArrayList<String> pathArray) {
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
		for (String string : pathArray) {
			String localFilePath = localRootPath + "\\" + string;// ���챾��·��
			FTPFile[] file = null;
			try {
				System.out.println("----------------");
				ftp.changeWorkingDirectory("/" + new String(string.getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET));
				System.out.println(ftp.printWorkingDirectory());
				file = ftp.listFiles();
				System.out.println("----------------");
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (FTPFile ftpFile : file) {
				if (ftpFile.getName().equals(".") || ftpFile.getName().equals(".."))
					continue;
				File localFile = new File(localFilePath);

				if (!ftpFile.isDirectory()) {
					OutputStream is = null;
					try {
						is = new FileOutputStream(localFile + "/" + ftpFile.getName());
						ftp.retrieveFile(new String(ftpFile.getName().getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET), is);
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
				}
			}
		}
		System.out.println("0k");
	}
}