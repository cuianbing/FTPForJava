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
public class DownThreadCopy extends Thread {
	private final FtpPanel ftpPanel; // FTP资源管理面板
	private boolean conRun = true; // 线程的控制变量
	private String path; // FTP的路径信息
	private Object[] queueValues; // 下载任务的数组
	private File localRootPath;//本地根目录
	/**
	 * 构造方法
	 * 
	 * @param ftpPanel
	 *            - FTP资源管理面板
	 */
	public DownThreadCopy(FtpPanel ftpPanel) {
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

	/**
	 * 下载线程的递归方法，用户探索FTP下载文件夹的所有子文件夹和内容
	 * 
	 * @param file
	 *            FTP文件对象
	 * @param localFolder
	 *            本地文件夹对象
	 */
	private void downFile(FTPFile file, File localFolder) {
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

		if (file.isFile()) {// 文件则进行下载
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
		} else if (file.isDirectory()) { // 如果下载的是文件夹则在本地创建文件夹
			// 创建本地文件夹对象
			
		}

	}

	public void run() { // 线程业务方法
		while (conRun) {
			try {
				System.out.println("下载队列开始");
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
				
				FTPFile file = (FTPFile) queueValues[0];//FTP文件对象
				File localFolder = (File) queueValues[1];//本地文件夹
				//获得本地文件夹路径
				localRootPath = ftpPanel.frame.getLocalPanel().getCurrentFolder();
				mkdirsFolder(MainStatic.ftpClient, path, pathArray);//建立目录结构
				
				
				if (file != null) {
//					path = file.getPath();
					MainStatic.ftpClient.changeWorkingDirectory(path);
					downFile(file, localFolder);
					path = null;
					ftpPanel.frame.getLocalPanel().refreshCurrentFolder();//刷新本地文件夹
				}
				Object[] args = ftpPanel.queue.peek();
				// 判断队列顶是否为处理的上一个任务。
				if (queueValues == null || args == null || !queueValues[0].equals(args[0]))
					continue;
				ftpPanel.queue.poll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 添加
	private void mkdirsFolder(FTPClient ftp, String path, ArrayList<String> pathArray) throws IOException {
		ftp.changeWorkingDirectory(path);
		System.out.println(ftp.printWorkingDirectory());
		FTPFile[] files = ftp.listFiles();
		for (FTPFile ftpFile : files) {
			if (ftpFile.getName().equals(".") || ftpFile.getName().equals(".."))
				continue;
			if (ftpFile.isDirectory()) {// 如果是目录，则递归调用，查找里面所有文件
				path += "/" + ftpFile.getName();
				pathArray.add(path);
				ftp.changeWorkingDirectory(path);// 改变当前路径
				mkdirsFolder(ftp, path, pathArray);// 递归调用
				path = path.substring(0, path.lastIndexOf("/"));// 避免对之后的同目录下的路径构造作出干扰，
			}
		}
		//将目录结构建立起来
		for(String string :pathArray) {
			String localPath = localRootPath + string.replace("/", "\\");
			File localFile = new File(localPath);
			System.out.println("新建文件夹====》" + localPath);
			if (!localFile.exists()) {
				localFile.mkdirs();
			}
		}
	}
}