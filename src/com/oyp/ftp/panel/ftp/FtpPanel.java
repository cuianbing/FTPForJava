
package com.oyp.ftp.panel.ftp;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.ActionMap;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.oyp.ftp.FTPClientFrame;
import com.oyp.ftp.MainStatic;
import com.oyp.ftp.panel.FTPTableCellRanderer;
import com.oyp.ftp.utils.FtpFile;

import sun.net.ftp.FtpProtocolException;

/**
 * FTP��������尴ť
 * @author cuian
 *
 */
public class FtpPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton createFolderButton;
	private javax.swing.JButton delButton;
	private javax.swing.JButton downButton;
	javax.swing.JTable ftpDiskTable;
	public javax.swing.JLabel ftpSelFilePathLabel;//��ַ��
	private javax.swing.JScrollPane scrollPane;
	private javax.swing.JToolBar toolBar;
	private javax.swing.JButton refreshButton;
	private javax.swing.JButton renameButton;
	FTPClientFrame frame = null;
	Queue<Object[]> queue = new LinkedList<Object[]>();//���ض���
	private DownThread thread;

	public FtpPanel() {
		initComponents();
	}

	public FtpPanel(FTPClientFrame client_Frame) {
		frame = client_Frame;
		initComponents();
	}

	private void initComponents() {
		ActionMap actionMap = getActionMap();
		actionMap.put("createFolderAction", new CreateFolderAction(this, "�����ļ���", null));
		actionMap.put("delAction", new DelFileAction(this, "ɾ��", null));
		actionMap.put("refreshAction", new RefreshAction(this, "ˢ��", null));
		actionMap.put("renameAction", new RenameAction(this, "������", null));
		actionMap.put("downAction", new DownAction(this, "����", null));

		java.awt.GridBagConstraints gridBagConstraints;

		toolBar = new javax.swing.JToolBar();
		delButton = new javax.swing.JButton();
		renameButton = new javax.swing.JButton();
		createFolderButton = new javax.swing.JButton();
		downButton = new javax.swing.JButton();
		refreshButton = new javax.swing.JButton();
		scrollPane = new JScrollPane();
		ftpDiskTable = new JTable();
		ftpDiskTable.setDragEnabled(true);
		ftpSelFilePathLabel = new javax.swing.JLabel();

		setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Զ��", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.ABOVE_TOP));
		setLayout(new java.awt.GridBagLayout());

		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		delButton.setText("ɾ��");
		delButton.setFocusable(false);
		delButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		delButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		delButton.setAction(actionMap.get("delAction"));
		toolBar.add(delButton);

		renameButton.setText("������");
		renameButton.setFocusable(false);
		renameButton.setAction(actionMap.get("renameAction"));
		toolBar.add(renameButton);

		createFolderButton.setText("���ļ���");
		createFolderButton.setFocusable(false);
		createFolderButton.setAction(actionMap.get("createFolderAction"));
		toolBar.add(createFolderButton);

		downButton.setText("����");
		downButton.setFocusable(false);
		downButton.setAction(actionMap.get("downAction"));
		toolBar.add(downButton);

		refreshButton.setText("ˢ��");
		refreshButton.setFocusable(false);
		refreshButton.setAction(actionMap.get("refreshAction"));
		toolBar.add(refreshButton);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		add(toolBar, gridBagConstraints);

		ftpDiskTable.setModel(new FtpTableModel());
		ftpDiskTable.setShowHorizontalLines(false);
		ftpDiskTable.setShowVerticalLines(false);
		ftpDiskTable.getTableHeader().setReorderingAllowed(false);
		ftpDiskTable.setDoubleBuffered(true);
		ftpDiskTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				ftpDiskTableMouseClicked(evt);
			}
		});
		scrollPane.setViewportView(ftpDiskTable);
		scrollPane.getViewport().setBackground(Color.WHITE);
		// ������Ⱦ������Դ��FTP��Դ����������Ⱦ��
		ftpDiskTable.getColumnModel().getColumn(0).setCellRenderer(FTPTableCellRanderer.getCellRanderer());
		// RowSorter ��һ��ʵ�֣���ʹ�� TableModel �ṩ����͹��˲�����
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(ftpDiskTable.getModel());
		TableStringConverter converter = new TableConverter();
		// ���ø���ֵ��ģ��ת��Ϊ�ַ����Ķ���
		sorter.setStringConverter(converter);
		// ���� RowSorter��RowSorter �����ṩ�� JTable ������͹��ˡ�
		ftpDiskTable.setRowSorter(sorter);
		/**
		 * �ߵ�ָ���е�����˳�򡣵��ô˷���ʱ���������ṩ������Ϊ�� ͨ�������ָ�����Ѿ�����Ҫ�����У���˷����������Ϊ���򣨻򽫽����Ϊ���򣩣�
		 * ����ʹָ���г�Ϊ��Ҫ�����У���ʹ����������˳�����ָ���в���������˷���û���κ�Ч����
		 */
		sorter.toggleSortOrder(0);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(scrollPane, gridBagConstraints);

		ftpSelFilePathLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(ftpSelFilePathLabel, gridBagConstraints);
		ftpSelFilePathLabel.setText("/");
	}

	/**
	 * ��񵥻���˫���¼��Ĵ�������
	 * 
	 * cuianbing  ok
	 */
	private void ftpDiskTableMouseClicked(java.awt.event.MouseEvent evt) {
		int selectedRow = ftpDiskTable.getSelectedRow();
		Object value = ftpDiskTable.getValueAt(selectedRow, 0);
		if (value instanceof FtpFile) {
			FtpFile selFile = (FtpFile) value;
			String fileName = selFile.getName();
			System.out.println("�ļ�·����" + selFile);
			// String absolutePath = selFile.getAbsolutePath();
			if (evt.getClickCount() >= 2) { // ˫�����
				if (selFile.isDirectory()) {
					try {
						if (ftpSelFilePathLabel.getText().equals("/") && fileName.equals(".")) {

						} else {
							if (fileName.equals(".")) {//ȥ����һ��·��
								String aaa = ftpSelFilePathLabel.getText().substring(0, ftpSelFilePathLabel.getText().lastIndexOf("/"));
								String substring = aaa.substring(0, aaa.lastIndexOf("/") + 1);
								System.out.println("//ȥ����һ��·�� "+substring);
								ftpSelFilePathLabel.setText(substring);
								MainStatic.ftpClient.changeWorkingDirectory(new String(substring.getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
								// �г�Ŀ¼���ļ��Լ��ļ���
								listFtpFiles(MainStatic.ftpClient.listFiles(new String(substring.getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET)));
							} else {
								ftpSelFilePathLabel
										.setText(ftpSelFilePathLabel.getText() + "" + selFile.getName()+"/");
								// �л���Ŀ¼
								System.out.println(ftpSelFilePathLabel.getText());
								MainStatic.ftpClient.changeWorkingDirectory(new String(ftpSelFilePathLabel.getText().getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
								// �г�Ŀ¼���ļ��Լ��ļ���
								listFtpFiles(MainStatic.ftpClient.listFiles(new String(ftpSelFilePathLabel.getText().getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET)));
							}

						}

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * ��ȡFTP�ļ������ķ���
	 * 
	 * @param list
	 * 
	 */
	public synchronized void listFtpFiles(final FTPFile[] list) {
		// ��ȡ��������ģ��
		final DefaultTableModel model = (DefaultTableModel) ftpDiskTable.getModel();
		model.setRowCount(0);
		// ����һ���߳���
		Runnable runnable = new Runnable() {
			public synchronized void run() {
				ftpDiskTable.clearSelection();
				{
					String pwd = "/";
					if(list == null || list.length == 0) {
						model.addRow(new Object[] { new FtpFile(".", pwd, true), "", "" }); // ��ӡ�.������
					}else{
						pwd = list[0].getLink();
						model.addRow(new Object[] { new FtpFile(".", pwd, true), "", "" }); // ��ӡ�.������
						for (FTPFile file : list) {
							/** �ļ��� */
							String fileName = file.getName();
							/** �ļ����� */
							Date time = file.getTimestamp().getTime();
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String dateStr = formatter.format(time);
							/** �ļ���С **/
							long fileSize = file.getSize();
							String fileSizeStr = String.valueOf(fileSize);
							/** �Ƿ�Ϊ�ļ��� **/
							boolean isD = false;
							boolean isF = true;
							if (file.isDirectory()) {
								isD = true;
								isF = false;
								fileSizeStr = "�ļ���";
							}
							FtpFile ftpFile = new FtpFile();
							// ��FTPĿ¼��Ϣ��ʼ����FTP�ļ�������
							ftpFile.setLastDate(dateStr);// ����޸�����
							ftpFile.setSize(fileSizeStr);// �ļ���С
							ftpFile.setName(fileName);// �ļ�����
							ftpFile.setPath(pwd);// ·��
							ftpFile.setDirectory(isD);
							ftpFile.setFile(isF);
							// ���ļ���Ϣ��ӵ������
							model.addRow(new Object[] { ftpFile, ftpFile.getSize(), dateStr });
						}
					}
				}
			}
		};
		if (SwingUtilities.isEventDispatchThread()) // �����̶߳���
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	/**
	 * ����FTP���ӣ����������ض����̵߳ķ���
	 * 
	 * @throws FtpProtocolException
	 */
	public void setFtpClient(FTPClient ftpClient) throws FtpProtocolException {
		startDownThread();
	}

	/**
	 * ˢ��FTP��Դ�������ĵ�ǰ�ļ���
	 * ok
	 */
	public void refreshCurrentFolder(String name) {
		try {
			FTPFile[] list = MainStatic.ftpClient.listFiles(new String(name.getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
			// ��ȡ�������ļ��б�
			listFtpFiles(list); // ���ý�������
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ���ض����߳�
	 * @throws FtpProtocolException
	 */
	private void startDownThread(){
		if (thread != null) {
			thread.stopThread();
		}
		thread = new DownThread(this);
		thread.start();
	}

	/**
	 * ֹͣ���ض����߳�
	 */
	public void stopDownThread() {
		if (thread != null) {
			thread.stopThread();
		}
	}

	/**
	 * ��õ�ǰ����·��
	 * 
	 * @return
	 */
	public String getPwd() {
		String pwd = null;
		try {
			pwd = MainStatic.ftpClient.printWorkingDirectory();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pwd;
	}

	public Queue<Object[]> getQueue() {
		return queue;
	}

	/**
	 * ���FTP��Դ������ݵķ���
	 */
	public void clearTable() {
		FtpTableModel model = (FtpTableModel) ftpDiskTable.getModel();
		model.setRowCount(0);
	}
}
