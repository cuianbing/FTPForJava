
package com.oyp.ftp.panel.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Queue;

import com.oyp.ftp.MainStatic;
import com.oyp.ftp.panel.ftp.FtpPanel;
import com.oyp.ftp.panel.queue.UploadPanel;
import com.oyp.ftp.utils.FtpFile;
import com.oyp.ftp.utils.ProgressArg;

/**
 * FTP文件管理模块的本地文件上传队列的线程
 */
class UploadThread extends Thread {
	private LocalPanel localPanel;
	String path = "";// 上传文件的本地相对路径
	String selPath;// 选择的本地文件的路径
	private boolean conRun = true; // 线程的控制变量
	private Object[] queueValues; // 队列任务数组

	public UploadThread(LocalPanel localPanel, String server, int port, String userStr, String passStr) {
		this.localPanel = localPanel;
		new Thread() { // 创建保持服务器通讯的线程
			public void run() {
				while (conRun) {
					try {
						Thread.sleep(30000);
						// 定时向服务器发送消息，保持连接
						MainStatic.ftpClient.noop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void stopThread() { // 停止线程的方法
		conRun = false;
	}

	/**
	 * 上传线程的递归方法，上传文件夹的所有子文件夹和内容
	 * 
	 * @param file
	 * @param ftpFile
	 */
	private void copyFile(File file, FtpFile ftpFile) { // 递归遍历文件夹的方法
		// 判断队列面板是否执行暂停命令
		while (localPanel.frame.getQueuePanel().isStop()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Object[] args = localPanel.queue.peek();
		// 判断队列顶是不是上一个处理的任务。
		if (queueValues == null || args == null || !queueValues[0].equals(args[0]))
			return;
		try {

			path = file.getParentFile().getPath().replace(selPath, "");
			System.out.println("选择的本地文件的路径：" + selPath);

			ftpFile.setName(path.replace("\\", "/"));
			String ftpPath = ftpFile.getAbsolutePath();
			System.out.println("文件绝对路径====" + ftpPath);
			if (file.isFile()) {
				UploadPanel uploadPanel = localPanel.frame.getUploadPanel();// 上传面板
				String remoteFile = path + "\\" + file.getName(); // 远程FTP的文件名绝对路径
				System.out.println("远程FTP的文件名绝对路径:" + remoteFile);// zigbee\Components\hal\target\CC2530EB\hal_oad.c
				double fileLength = file.length() / Math.pow(1024, 2);
				ProgressArg progressArg = new ProgressArg((int) (file.length() / 1024), 0, 0);// 进度参数
				String size = String.format("%.4f MB", fileLength);
				Object[] row = new Object[] { file.getAbsoluteFile(), size, remoteFile, "服务器名称（暂时空着）", progressArg };
				uploadPanel.addRow(row); // 添加列

				FileInputStream fis = null; // 本地文件的输入流
				try {
					fis = new FileInputStream(file); // 初始化文件的输入流
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				File file2 = new File(file.getPath());
				FileInputStream input = new FileInputStream(file2);
				MainStatic.ftpClient.storeFile(new String(file2.getName().getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET), input);
				input.close();
				
				progressArg.setValue(progressArg.getValue() + 30);// 累加进度条
				progressArg.setValue(progressArg.getMax()); // 结束进度条
				fis.close(); // 关闭文件输入流
			} else{
				path = file.getPath().replace(selPath, "");
				ftpFile.setName(path.replace("\\", ""));
				// System.out.println("Dirpath："+path);
				/** 将目录切换到当前FTP服务器的当前目录 */
				MainStatic.ftpClient.makeDirectory(new String(file.getName().getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
				MainStatic.ftpClient.changeWorkingDirectory(new String(file.getName().getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
				File[] listFiles = file.listFiles();
				for (File subFile : listFiles) {
					Thread.sleep(0, 50);
					copyFile(subFile, ftpFile);
				}
				MainStatic.ftpClient.changeToParentDirectory();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 线程的主体方法
	 */
	public void run() { // 线程的主体方法
		while (conRun) {
			try {
				Thread.sleep(1000); // 线程休眠1秒
				Queue<Object[]> queue = localPanel.queue; // 获取本地面板的队列对象
				queueValues = queue.peek(); // 获取队列首的对象
				if (queueValues == null) { // 如果该对象为空
					continue; // 进行下一次循环
				}
				File file = (File) queueValues[0]; // 获取队列中的本队文件对象
				FtpFile ftpFile = (FtpFile) queueValues[1]; // 获取队列中的FTP文件对象
				if (file != null) {
					selPath = file.getParent();
					copyFile(file, ftpFile); // 调用递归方法上传文件
					FtpPanel ftpPanel = localPanel.frame.getFtpPanel();
					ftpPanel.refreshCurrentFolder(ftpPanel.ftpSelFilePathLabel.getText()); // 刷新FTP面板中的资源
				}
				Object[] args = queue.peek();
				// 判断队列顶是否为处理的上一个任务。
				if (queueValues == null || args == null || !queueValues[0].equals(args[0])) {
					continue;
				}
				queue.remove(); // 移除队列首元素
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}