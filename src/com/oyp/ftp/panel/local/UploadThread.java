
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
 * FTP�ļ�����ģ��ı����ļ��ϴ����е��߳�
 */
class UploadThread extends Thread {
	private LocalPanel localPanel;
	String path = "";// �ϴ��ļ��ı������·��
	String selPath;// ѡ��ı����ļ���·��
	private boolean conRun = true; // �̵߳Ŀ��Ʊ���
	private Object[] queueValues; // ������������

	public UploadThread(LocalPanel localPanel, String server, int port, String userStr, String passStr) {
		this.localPanel = localPanel;
		new Thread() { // �������ַ�����ͨѶ���߳�
			public void run() {
				while (conRun) {
					try {
						Thread.sleep(30000);
						// ��ʱ�������������Ϣ����������
						MainStatic.ftpClient.noop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void stopThread() { // ֹͣ�̵߳ķ���
		conRun = false;
	}

	/**
	 * �ϴ��̵߳ĵݹ鷽�����ϴ��ļ��е��������ļ��к�����
	 * 
	 * @param file
	 * @param ftpFile
	 */
	private void copyFile(File file, FtpFile ftpFile) { // �ݹ�����ļ��еķ���
		// �ж϶�������Ƿ�ִ����ͣ����
		while (localPanel.frame.getQueuePanel().isStop()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Object[] args = localPanel.queue.peek();
		// �ж϶��ж��ǲ�����һ�����������
		if (queueValues == null || args == null || !queueValues[0].equals(args[0]))
			return;
		try {

			path = file.getParentFile().getPath().replace(selPath, "");
			System.out.println("ѡ��ı����ļ���·����" + selPath);

			ftpFile.setName(path.replace("\\", "/"));
			String ftpPath = ftpFile.getAbsolutePath();
			System.out.println("�ļ�����·��====" + ftpPath);
			if (file.isFile()) {
				UploadPanel uploadPanel = localPanel.frame.getUploadPanel();// �ϴ����
				String remoteFile = path + "\\" + file.getName(); // Զ��FTP���ļ�������·��
				System.out.println("Զ��FTP���ļ�������·��:" + remoteFile);// zigbee\Components\hal\target\CC2530EB\hal_oad.c
				double fileLength = file.length() / Math.pow(1024, 2);
				ProgressArg progressArg = new ProgressArg((int) (file.length() / 1024), 0, 0);// ���Ȳ���
				String size = String.format("%.4f MB", fileLength);
				Object[] row = new Object[] { file.getAbsoluteFile(), size, remoteFile, "���������ƣ���ʱ���ţ�", progressArg };
				uploadPanel.addRow(row); // �����

				FileInputStream fis = null; // �����ļ���������
				try {
					fis = new FileInputStream(file); // ��ʼ���ļ���������
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				File file2 = new File(file.getPath());
				FileInputStream input = new FileInputStream(file2);
				MainStatic.ftpClient.storeFile(new String(file2.getName().getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET), input);
				input.close();
				
				progressArg.setValue(progressArg.getValue() + 30);// �ۼӽ�����
				progressArg.setValue(progressArg.getMax()); // ����������
				fis.close(); // �ر��ļ�������
			} else{
				path = file.getPath().replace(selPath, "");
				ftpFile.setName(path.replace("\\", ""));
				// System.out.println("Dirpath��"+path);
				/** ��Ŀ¼�л�����ǰFTP�������ĵ�ǰĿ¼ */
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
	 * �̵߳����巽��
	 */
	public void run() { // �̵߳����巽��
		while (conRun) {
			try {
				Thread.sleep(1000); // �߳�����1��
				Queue<Object[]> queue = localPanel.queue; // ��ȡ�������Ķ��ж���
				queueValues = queue.peek(); // ��ȡ�����׵Ķ���
				if (queueValues == null) { // ����ö���Ϊ��
					continue; // ������һ��ѭ��
				}
				File file = (File) queueValues[0]; // ��ȡ�����еı����ļ�����
				FtpFile ftpFile = (FtpFile) queueValues[1]; // ��ȡ�����е�FTP�ļ�����
				if (file != null) {
					selPath = file.getParent();
					copyFile(file, ftpFile); // ���õݹ鷽���ϴ��ļ�
					FtpPanel ftpPanel = localPanel.frame.getFtpPanel();
					ftpPanel.refreshCurrentFolder(ftpPanel.ftpSelFilePathLabel.getText()); // ˢ��FTP����е���Դ
				}
				Object[] args = queue.peek();
				// �ж϶��ж��Ƿ�Ϊ�������һ������
				if (queueValues == null || args == null || !queueValues[0].equals(args[0])) {
					continue;
				}
				queue.remove(); // �Ƴ�������Ԫ��
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}