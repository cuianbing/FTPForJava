
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
 * FTP服务器面板按钮
 * @author cuian
 *
 */
public class FtpPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;
	private javax.swing.JButton createFolderButton;
	private javax.swing.JButton delButton;
	private javax.swing.JButton downButton;
	javax.swing.JTable ftpDiskTable;
	public javax.swing.JLabel ftpSelFilePathLabel;//地址栏
	private javax.swing.JScrollPane scrollPane;
	private javax.swing.JToolBar toolBar;
	private javax.swing.JButton refreshButton;
	private javax.swing.JButton renameButton;
	FTPClientFrame frame = null;
	Queue<Object[]> queue = new LinkedList<Object[]>();//下载队列
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
		actionMap.put("createFolderAction", new CreateFolderAction(this, "创建文件夹", null));
		actionMap.put("delAction", new DelFileAction(this, "删除", null));
		actionMap.put("refreshAction", new RefreshAction(this, "刷新", null));
		actionMap.put("renameAction", new RenameAction(this, "重命名", null));
		actionMap.put("downAction", new DownAction(this, "下载", null));

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

		setBorder(javax.swing.BorderFactory.createTitledBorder(null, "远程", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.ABOVE_TOP));
		setLayout(new java.awt.GridBagLayout());

		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		delButton.setText("删除");
		delButton.setFocusable(false);
		delButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		delButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		delButton.setAction(actionMap.get("delAction"));
		toolBar.add(delButton);

		renameButton.setText("重命名");
		renameButton.setFocusable(false);
		renameButton.setAction(actionMap.get("renameAction"));
		toolBar.add(renameButton);

		createFolderButton.setText("新文件夹");
		createFolderButton.setFocusable(false);
		createFolderButton.setAction(actionMap.get("createFolderAction"));
		toolBar.add(createFolderButton);

		downButton.setText("下载");
		downButton.setFocusable(false);
		downButton.setAction(actionMap.get("downAction"));
		toolBar.add(downButton);

		refreshButton.setText("刷新");
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
		// 设置渲染本地资源和FTP资源表格组件的渲染器
		ftpDiskTable.getColumnModel().getColumn(0).setCellRenderer(FTPTableCellRanderer.getCellRanderer());
		// RowSorter 的一个实现，它使用 TableModel 提供排序和过滤操作。
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(ftpDiskTable.getModel());
		TableStringConverter converter = new TableConverter();
		// 设置负责将值从模型转换为字符串的对象。
		sorter.setStringConverter(converter);
		// 设置 RowSorter。RowSorter 用于提供对 JTable 的排序和过滤。
		ftpDiskTable.setRowSorter(sorter);
		/**
		 * 颠倒指定列的排序顺序。调用此方法时，由子类提供具体行为。 通常，如果指定列已经是主要排序列，则此方法将升序变为降序（或将降序变为升序）；
		 * 否则，使指定列成为主要排序列，并使用升序排序顺序。如果指定列不可排序，则此方法没有任何效果。
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
	 * 表格单击或双击事件的处理方法。
	 * 
	 * cuianbing  ok
	 */
	private void ftpDiskTableMouseClicked(java.awt.event.MouseEvent evt) {
		int selectedRow = ftpDiskTable.getSelectedRow();
		Object value = ftpDiskTable.getValueAt(selectedRow, 0);
		if (value instanceof FtpFile) {
			FtpFile selFile = (FtpFile) value;
			String fileName = selFile.getName();
			System.out.println("文件路径：" + selFile);
			// String absolutePath = selFile.getAbsolutePath();
			if (evt.getClickCount() >= 2) { // 双击鼠标
				if (selFile.isDirectory()) {
					try {
						if (ftpSelFilePathLabel.getText().equals("/") && fileName.equals(".")) {

						} else {
							if (fileName.equals(".")) {//去除上一个路径
								String aaa = ftpSelFilePathLabel.getText().substring(0, ftpSelFilePathLabel.getText().lastIndexOf("/"));
								String substring = aaa.substring(0, aaa.lastIndexOf("/") + 1);
								System.out.println("//去除上一个路径 "+substring);
								ftpSelFilePathLabel.setText(substring);
								MainStatic.ftpClient.changeWorkingDirectory(new String(substring.getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
								// 列出目录下文件以及文件夹
								listFtpFiles(MainStatic.ftpClient.listFiles(new String(substring.getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET)));
							} else {
								ftpSelFilePathLabel
										.setText(ftpSelFilePathLabel.getText() + "" + selFile.getName()+"/");
								// 切换到目录
								System.out.println(ftpSelFilePathLabel.getText());
								MainStatic.ftpClient.changeWorkingDirectory(new String(ftpSelFilePathLabel.getText().getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
								// 列出目录下文件以及文件夹
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
	 * 读取FTP文件到表格的方法
	 * 
	 * @param list
	 * 
	 */
	public synchronized void listFtpFiles(final FTPFile[] list) {
		// 获取表格的数据模型
		final DefaultTableModel model = (DefaultTableModel) ftpDiskTable.getModel();
		model.setRowCount(0);
		// 创建一个线程类
		Runnable runnable = new Runnable() {
			public synchronized void run() {
				ftpDiskTable.clearSelection();
				{
					String pwd = "/";
					if(list == null || list.length == 0) {
						model.addRow(new Object[] { new FtpFile(".", pwd, true), "", "" }); // 添加“.”符号
					}else{
						pwd = list[0].getLink();
						model.addRow(new Object[] { new FtpFile(".", pwd, true), "", "" }); // 添加“.”符号
						for (FTPFile file : list) {
							/** 文件名 */
							String fileName = file.getName();
							/** 文件日期 */
							Date time = file.getTimestamp().getTime();
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String dateStr = formatter.format(time);
							/** 文件大小 **/
							long fileSize = file.getSize();
							String fileSizeStr = String.valueOf(fileSize);
							/** 是否为文件夹 **/
							boolean isD = false;
							boolean isF = true;
							if (file.isDirectory()) {
								isD = true;
								isF = false;
								fileSizeStr = "文件夹";
							}
							FtpFile ftpFile = new FtpFile();
							// 将FTP目录信息初始化到FTP文件对象中
							ftpFile.setLastDate(dateStr);// 最后修改日期
							ftpFile.setSize(fileSizeStr);// 文件大小
							ftpFile.setName(fileName);// 文件名称
							ftpFile.setPath(pwd);// 路径
							ftpFile.setDirectory(isD);
							ftpFile.setFile(isF);
							// 将文件信息添加到表格中
							model.addRow(new Object[] { ftpFile, ftpFile.getSize(), dateStr });
						}
					}
				}
			}
		};
		if (SwingUtilities.isEventDispatchThread()) // 启动线程对象
			runnable.run();
		else
			SwingUtilities.invokeLater(runnable);
	}

	/**
	 * 设置FTP连接，并启动下载队列线程的方法
	 * 
	 * @throws FtpProtocolException
	 */
	public void setFtpClient(FTPClient ftpClient) throws FtpProtocolException {
		startDownThread();
	}

	/**
	 * 刷新FTP资源管理面板的当前文件夹
	 * ok
	 */
	public void refreshCurrentFolder(String name) {
		try {
			FTPFile[] list = MainStatic.ftpClient.listFiles(new String(name.getBytes(MainStatic.LOCAL_CHARSET),MainStatic.SERVER_CHARSET));
			// 获取服务器文件列表
			listFtpFiles(list); // 调用解析方法
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始下载队列线程
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
	 * 停止下载队列线程
	 */
	public void stopDownThread() {
		if (thread != null) {
			thread.stopThread();
		}
	}

	/**
	 * 获得当前工作路径
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
	 * 清除FTP资源表格内容的方法
	 */
	public void clearTable() {
		FtpTableModel model = (FtpTableModel) ftpDiskTable.getModel();
		model.setRowCount(0);
	}
}
