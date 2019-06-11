package com.oyp.ftp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.oyp.ftp.FtpInfo;
import com.oyp.ftp.MainStatic;

public class FtpClient2 {

	public static boolean makeDirectory(String dirname) throws Exception {
		boolean makeDirectory = MainStatic.ftpClient.makeDirectory(dirname);
		return makeDirectory;

	}

	/**
	 * ��ȡftp����
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static boolean connectFtp(FtpInfo f) throws Exception {
		// FtpInfo f = new FtpInfo();
		boolean result = false;
		int reply;
		MainStatic.ftpClient.connect(f.getIpAddr(), f.getPort());
		MainStatic.ftpClient.login(f.getUserName(), f.getPwd());
		
		reply = MainStatic.ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			MainStatic.ftpClient.disconnect();
			JOptionPane.showMessageDialog(null, "�������ܾ�����");
			return result;
		}
		if (FTPReply.isPositiveCompletion(MainStatic.ftpClient.sendCommand("OPTS UTF8", "ON"))) {
			MainStatic.LOCAL_CHARSET = "UTF-8";// ������������UTF-8��֧�֣����������֧�־���UTF-8���룬�����ʹ�ñ��ر��루GBK��.
		}
		
		MainStatic.ftpClient.setControlEncoding(MainStatic.LOCAL_CHARSET);// ���ñ����ʽ
		MainStatic.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		MainStatic.ftpClient.enterLocalPassiveMode();// ���ñ���ģʽ
		MainStatic.ftpClient.changeWorkingDirectory(f.getPath());
		result = true;
		return result;
	}

	/**
	 * �ر�ftp����
	 */
	public static void closeFtp() {
		if (MainStatic.ftpClient != null && MainStatic.ftpClient.isConnected()) {
			try {
				MainStatic.ftpClient.logout();
				MainStatic.ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ftp�ϴ��ļ�
	 * 
	 * @param f
	 * @throws Exception
	 */
	public static void upload(File f) throws Exception {
		if (f.isDirectory()) {
			boolean makeDirectory = MainStatic.ftpClient.makeDirectory(f.getName()); // ftp�Ƿ����Ŀ¼
			if (makeDirectory) {
				MainStatic.ftpClient.changeWorkingDirectory(f.getName());
			} else {
				MainStatic.ftpClient.makeDirectory(f.getName());
				MainStatic.ftpClient.changeWorkingDirectory(f.getName());
			}
			String[] files = f.list();
			for (String fstr : files) {
				File file1 = new File(f.getPath() + "/" + fstr);
				if (file1.isDirectory()) {
					upload(file1);
				} else {
					File file2 = new File(f.getPath() + "/" + fstr);
					FileInputStream input = new FileInputStream(file2);
					MainStatic.ftpClient.storeFile(file2.getName(), input);
					input.close();
				}
			}
		} else {
			File file2 = new File(f.getPath());
			FileInputStream input = new FileInputStream(file2);
			MainStatic.ftpClient.storeFile(file2.getName(), input);
			input.close();
		}
	}

	/**
	 * ������������
	 * 
	 * @param f
	 * @param localBaseDir
	 *            ����Ŀ¼
	 * @param remoteBaseDir
	 *            Զ��Ŀ¼
	 * @throws Exception
	 */
	public static void startDown(FtpInfo f, String localBaseDir, String remoteBaseDir) throws Exception {
		try {
			FTPFile[] files = null;
			boolean changedir = MainStatic.ftpClient.changeWorkingDirectory(remoteBaseDir);
			System.out.println(changedir);
			if (changedir) {
				MainStatic.ftpClient.setControlEncoding("GBK");
				files = MainStatic.ftpClient.listFiles();
				for (int i = 0; i < files.length; i++) {
					try {
						downloadFile(files[i], localBaseDir, remoteBaseDir);
					} catch (Exception e) {
						System.out.println(e);
						System.out.println("<" + files[i].getName() + ">����ʧ��");
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("���ع����г����쳣");
		}

	}

	/**
	 * 
	 * ����FTP�ļ� ������Ҫ����FTP�ļ���ʱ�򣬵��ô˷��� ����<b>��ȡ���ļ��������ص�ַ��Զ�̵�ַ</b>��������
	 * 
	 * @param ftpFile
	 * @param relativeLocalPath
	 * @param relativeRemotePath
	 */
	private static void downloadFile(FTPFile ftpFile, String relativeLocalPath, String relativeRemotePath) {
		if (ftpFile.isFile()) {
			if (ftpFile.getName().indexOf("?") == -1) {
				OutputStream outputStream = null;
				try {
					File locaFile = new File(relativeLocalPath + ftpFile.getName());
					// �ж��ļ��Ƿ���ڣ������򷵻�
					if (locaFile.exists()) {
						return;
					} else {
						outputStream = new FileOutputStream(relativeLocalPath + ftpFile.getName());
						MainStatic.ftpClient.retrieveFile(ftpFile.getName(), outputStream);
						outputStream.flush();
						outputStream.close();
					}
				} catch (Exception e) {
					System.out.println(e);
				} finally {
					try {
						if (outputStream != null) {
							outputStream.close();
						}
					} catch (IOException e) {
					}
				}
			}
		} else {
			String newlocalRelatePath = relativeLocalPath + ftpFile.getName();
			String newRemote = new String(relativeRemotePath + ftpFile.getName().toString());
			File fl = new File(newlocalRelatePath);
			if (!fl.exists()) {
				fl.mkdirs();
			}
			try {
				newlocalRelatePath = newlocalRelatePath + '/';
				newRemote = newRemote + "/";
				String currentWorkDir = ftpFile.getName().toString();
				boolean changedir = MainStatic.ftpClient.changeWorkingDirectory(currentWorkDir);
				if (changedir) {
					FTPFile[] files = null;
					files = MainStatic.ftpClient.listFiles();
					for (int i = 0; i < files.length; i++) {
						downloadFile(files[i], newlocalRelatePath, newRemote);
					}
				}
				if (changedir) {
					MainStatic.ftpClient.changeToParentDirectory();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �г���·���µ��ļ��Լ��ļ���
	 * 
	 * @param pathName
	 * @return
	 */
	public FTPFile[] list(String pathName) {
		FTPFile[] files = null;
		try {
			files = MainStatic.ftpClient.listFiles(pathName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public boolean delectFile(String fileName) throws IOException {
		return MainStatic.ftpClient.deleteFile(fileName);
	}

}
