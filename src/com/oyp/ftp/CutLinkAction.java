package com.oyp.ftp;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.oyp.ftp.panel.ftp.FtpPanel;
import com.oyp.ftp.utils.FtpClient2;

/**
 * 断开按钮的动作处理类
 * cuianbing ok
 * 
 */
class CutLinkAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FTPClientFrame frame; // 父窗体的引用对象
	FtpPanel ftpPanel;
	public void setFtpPanel(FtpPanel ftpPanel) {
		this.ftpPanel = ftpPanel;
	}
	/**
	 * 构造方法
	 * 
	 * @param client_Frame
	 *            父窗体的引用
	 * @param string
	 *            动作的名称，它将显示在按钮或菜单项组件上
	 * @param icon
	 *            动作的图标，它将显示在按钮或菜单项组件上
	 */
	public CutLinkAction(FTPClientFrame client_Frame, String string, Icon icon) {
		super(string, icon); // 调用父类的构造方法
		frame = client_Frame; // 赋值父窗体引用对象
		setEnabled(false); // 设置不可用状态
	}

	/**
	 * 处理断开按钮的按钮动作事件的方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		frame.ftpPanel.stopDownThread(); // 停止下载线程
		frame.localPanel.stopUploadThread(); // 停止上传线程
		frame.getFtpPanel().getQueue().clear(); // 清空任务队列
		frame.getFtpPanel().clearTable(); // 清除FTP资源表格内容
		frame.getLocalPanel().getQueue().clear(); // 清除本地面板的队列
		// 如果FTP连接对象存在，并且已经连接FTP服务器
		if (MainStatic.ftpClient != null && MainStatic.ftpClient.isConnected()) {
			FtpClient2.closeFtp();
		}
		ftpPanel.ftpSelFilePathLabel.setText("/");
		// 设置上传按钮不可用
		frame.localPanel.getActionMap().get("uploadAction").setEnabled(
				false);
		// 设置下载按钮不可用
		frame.ftpPanel.getActionMap().get("downAction").setEnabled(false);
		setEnabled(false); // 设置本按钮（断开）不可用
	}
}