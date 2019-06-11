package com.oyp.ftp.utils;

/**
 *  FTP�ļ���JavaBean��
 */
public class FtpFile implements FileInterface {
	private String name = ""; // �ļ�����
	private String path = ""; // ·��
	protected boolean directory; // �Ƿ��ļ���
	private boolean file; // �Ƿ��ļ�
	private String lastDate; // ����޸�����
	private String size; // �ļ���С
	private long longSize; // �ļ���С�ĳ���������

	/**
	 * Ĭ�ϵĹ��췽��
	 */
	public FtpFile() {
	}

	/**
	 * �Զ���Ĺ��췽��
	 * 
	 * @param name
	 *            �ļ���
	 * @param path
	 *            ·��
	 * @param directory
	 *            �Ƿ��ļ���
	 */
	public FtpFile(String name, String path, boolean directory) {
		this.name = name; // ��ʼ���������
		this.path = path;
		this.directory = directory;
		if(directory) {
			this.file = false;
		}else {
			this.file = true;
		}
		
	}

	public String getSize() {
		return size;
	}

	/**
	 * �����ļ���С�ķ���
	 * 
	 * @param nsize
	 *            �ļ���С���ַ�����ʾ
	 */
	public void setSize(String nsize) {
		String size;
		if(nsize.equals("�ļ���")) {
			size = nsize;
		}else {
			double parseLong = Long.parseLong(nsize) / (1024.0 * 1024); //MB
			size = String.format("%.2f", parseLong) + "MB";
		}
		
		this.size = size;
	}

	
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public void setFile(boolean file) {
		this.file = file;
	}

	public String getLastDate() {
		return lastDate;
	}

	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}

	public boolean isFile() {
		return file;
	}

	public boolean isDirectory() {
		return directory;
	}

	/**
	 * ��ø��ļ��ľ���·��
	 * 
	 * @return ·��
	 */
	public String getAbsolutePath() {
		if (path.lastIndexOf('/') == path.length() - 1)
			return path + name;
		else
			return path + "" + name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ��дtoString�������������˸������������ʾ������
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	public long getLongSize() {
		return longSize;
	}
}
