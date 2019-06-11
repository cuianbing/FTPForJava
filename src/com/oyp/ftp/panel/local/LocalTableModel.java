package com.oyp.ftp.panel.local;

/**
 * ���ر��ģ�� ok
 * @author cuian
 *
 */
class LocalTableModel extends javax.swing.table.DefaultTableModel {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	Class[] types = new Class[] { java.lang.Object.class,
			java.lang.String.class, java.lang.String.class };
	boolean[] canEdit = new boolean[] { false, false, false };

	LocalTableModel() {
		super(new Object[][] {}, new String[] { "�ļ���", "��С", "����" });
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int columnIndex) {
		return types[columnIndex];
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return canEdit[columnIndex];
	}
}