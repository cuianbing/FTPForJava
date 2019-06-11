package com.oyp.ftp;

import org.apache.commons.net.ftp.FTPClient;

public class MainStatic {
	public static FTPClient ftpClient;//ftp全局变量
	public static String SERVER_CHARSET = "ISO-8859-1";//服务端编码
	public static String LOCAL_CHARSET = "GBK";;//本地编码格式
}
