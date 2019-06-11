package com.oyp.ftp.panel.ftp;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.oyp.ftp.MainStatic;

/**
 * 刷新按钮的动作处理器
 */
class RefreshAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FtpPanel ftpPanel;

	/**
	 * 构造方法
	 * 
	 * @param ftpPanel
	 *            - FTP资源管理面板
	 * @param name
	 *            - 动作的名称
	 * @param icon
	 *            - 动作的图标
	 */
	public RefreshAction(FtpPanel ftpPanel, String name, Icon icon) {
		super(name, icon); // 调用父类构造方法
		this.ftpPanel = ftpPanel; // 赋值FTP管理面板的引用
	}

	/**
	 * 刷新按钮的动作处理器动作的事件处理方法
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ftpPanel.refreshCurrentFolder(MainStatic.ftpClient.printWorkingDirectory());
		} catch (IOException e1) {
			e1.printStackTrace();
		} // 调用刷新FTP资源列表的方法
	}
}