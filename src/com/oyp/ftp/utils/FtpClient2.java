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
	 * 获取ftp连接
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
			JOptionPane.showMessageDialog(null, "服务器拒绝连接");
			return result;
		}
		if (FTPReply.isPositiveCompletion(MainStatic.ftpClient.sendCommand("OPTS UTF8", "ON"))) {
			MainStatic.LOCAL_CHARSET = "UTF-8";// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
		}
		
		MainStatic.ftpClient.setControlEncoding(MainStatic.LOCAL_CHARSET);// 设置编码格式
		MainStatic.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		MainStatic.ftpClient.enterLocalPassiveMode();// 设置被动模式
		MainStatic.ftpClient.changeWorkingDirectory(f.getPath());
		result = true;
		return result;
	}

	/**
	 * 关闭ftp连接
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
	 * ftp上传文件
	 * 
	 * @param f
	 * @throws Exception
	 */
	public static void upload(File f) throws Exception {
		if (f.isDirectory()) {
			boolean makeDirectory = MainStatic.ftpClient.makeDirectory(f.getName()); // ftp是否存在目录
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
	 * 下载链接配置
	 * 
	 * @param f
	 * @param localBaseDir
	 *            本地目录
	 * @param remoteBaseDir
	 *            远程目录
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
						System.out.println("<" + files[i].getName() + ">下载失败");
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			System.out.println("下载过程中出现异常");
		}

	}

	/**
	 * 
	 * 下载FTP文件 当你需要下载FTP文件的时候，调用此方法 根据<b>获取的文件名，本地地址，远程地址</b>进行下载
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
					// 判断文件是否存在，存在则返回
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
	 * 列出该路径下的文件以及文件夹
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
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public boolean delectFile(String fileName) throws IOException {
		return MainStatic.ftpClient.deleteFile(fileName);
	}

}
