
package com.oyp.ftp.utils;

import java.io.File;
public class DiskFile extends java.io.File implements FileInterface {

	private static final long serialVersionUID = 1L;

	public DiskFile() {
		super(".");
	}

	public DiskFile(File theFile) {
		super(theFile.toURI());
	}

	@Override
	public String toString() {
		return getName();
	}
}
