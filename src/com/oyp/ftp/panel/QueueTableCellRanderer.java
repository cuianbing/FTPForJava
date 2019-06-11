package com.oyp.ftp.panel;

import java.awt.Component;

import com.oyp.ftp.utils.ProgressArg;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/**
 *  ��Ⱦ�ϴ����ض��б���������Ⱦ��   ����ʾ�������Ĺ���
 */
public class QueueTableCellRanderer extends JProgressBar implements
		TableCellRenderer {
	private static final long serialVersionUID = 1L;

	public QueueTableCellRanderer() {
		setStringPainted(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof ProgressArg) {
			ProgressArg arg = (ProgressArg) value;
			setMinimum(arg.getMin());
			setMaximum(arg.getMax());
			setValue(arg.getValue());
			table.setRowSelectionInterval(row, row);
			table.setColumnSelectionInterval(column, column);
		}
		if (getValue() < getMaximum())
			return this;
		else {
			if (getMaximum() == -1)
				return new JLabel();
			return new JLabel("���");
		}
	}
}
