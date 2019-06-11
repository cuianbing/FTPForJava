package com.oyp.ftp.panel.local;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
/**
 *  ˢ�±�����Դ�б�Ķ���������
 * @author cuian
 *
 */
class RefreshAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private LocalPanel localPanel; // ������Դ������������

	/**
	 * ���췽��
	 * 
	 * @param localPanel
	 *            ������Դ�������
	 * @param name
	 *            ����������
	 * @param icon
	 *            ������ͼ��
	 */
	public RefreshAction(LocalPanel localPanel, String name, Icon icon) {
		super(name, icon); // ִ�и���Ĺ��췽��
		this.localPanel = localPanel; // ��ֵ������Դ������������
	}

	/**
	 * ˢ�±�����Դ�б�Ķ������������¼�������
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.localPanel.refreshCurrentFolder(); // ���ù�������ˢ�·���
	}
}