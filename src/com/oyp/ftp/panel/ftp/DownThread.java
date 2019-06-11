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
 * FTP文件管理模块的FTP文件下载队列的线程
 */
public class DownThread extends Thread {
	private final FtpPanel ftpPanel; // FTP资源管理面板
	private boolean conRun = true; // 线程的控制变量
	@SuppressWarnings("unused")
	private String path; // FTP的路径信息
	private Object[] queueValues; // 下载任务的数组
	private File localRootPath;// 本地根目录

	/**
	 * 构造方法
	 * 
	 * @param ftpPanel
	 *            - FTP资源管理面板
	 */
	public DownThread(FtpPanel ftpPanel) {
		this.ftpPanel = ftpPanel;
		try {
			MainStatic.ftpClient.noop();
			// 连接到FTP服务器
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread() { // 创建保持服务器通讯的线程
			public void run() {
				while (conRun) {
					try {
						Thread.sleep(30000);
						MainStatic.ftpClient.noop(); // 定时向服务器发送消息，保持连接
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void stopThread() {// 停止线程的方法
		conRun = false;
	}

	public void run() { // 线程业务方法
		while (conRun) {
				try {
					Thread.sleep(1000);
					MainStatic.ftpClient.noop();
					queueValues = ftpPanel.queue.peek();
					if (queueValues == null) {
						continue;
					}
					// 所有文件夹目录
					ArrayList<String> pathArray = new ArrayList<String>();
					if (pathArray != null || pathArray.size() > 0) {
						pathArray.clear();
					}
					FTPFile file = (FTPFile) queueValues[0];// FTP文件对象
					// File localFolder = (File) queueValues[1];// 本地文件夹
					// 获得本地文件夹路径
					localRootPath = ftpPanel.frame.getLocalPanel().getCurrentFolder();
					if (file != null) {
						mkdirsFolder(MainStatic.ftpClient, file.getName(), pathArray);// 建立目录结构
						downFile(MainStatic.ftpClient, pathArray);//下载文件
						ftpPanel.frame.getLocalPanel().refreshCurrentFolder();// 刷新本地文件夹
					}
					
					Object[] args = ftpPanel.queue.peek();
					// 判断队列顶是否为处理的上一个任务。
					if (queueValues == null || args == null || !queueValues[0].equals(args[0]))
						continue;
					ftpPanel.queue.poll();
					conRun = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	// 添加
	/**
	 * 
	 * @param ftp
	 * @param path
	 *            远程目录
	 * @param pathArray
	 * @throws IOException
	 */
	private void mkdirsFolder(FTPClient ftp, String path, ArrayList<String> pathArray) throws IOException {
		String localPath = null;
		String caonima = "/" + path;
		boolean changeWorkingDirectory = ftp.changeWorkingDirectory(new String(caonima.getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET));
		System.out.println("进入文件夹" + caonima + "  " + changeWorkingDirectory);
		System.out.println(ftp.printWorkingDirectory());
		FTPFile[] files = ftp.listFiles();
		System.out.println(files.length);
		
		for (FTPFile ftpFile : files) {
			if (ftpFile.getName().equals(".") || ftpFile.getName().equals(".."))
				continue;
			if (ftpFile.isDirectory()) {// 如果是目录，则递归调用，查找里面所有文件
				path += "/" + ftpFile.getName();
				localPath = localRootPath + "\\" + path.replace("/", "\\");
				pathArray.add(path);
				File localFile = new File(localPath);
				if (!localFile.exists()) {
					localFile.mkdirs();
				}
				ftp.changeWorkingDirectory(new String(ftpFile.getName().getBytes(MainStatic.LOCAL_CHARSET), MainStatic.SERVER_CHARSET));// 改变当前路径
				mkdirsFolder(MainStatic.ftpClient, path, pathArray);// 递归调用
				path = path.substring(0, path.lastIndexOf("/"));// 避免对之后的同目录下的路径构造作出干扰，
			}
		}
	}

	private void downFile(FTPClient ftp, ArrayList<String> pathArray) {
		// 判断队列面板是否执行暂停命令
		while (ftpPanel.frame.getQueuePanel().isStop()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Object[] args = ftpPanel.queue.peek();
		// 判断队列顶是否为处理的上一个任务。
		if (queueValues == null || args == null || !queueValues[0].equals(args[0])) {
			return;
		}
		for (String string : pathArray) {
			String localFilePath = localRootPath + "\\" + string;// 构造本地路径
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