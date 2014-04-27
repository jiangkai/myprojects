package com.yq.ftpserver.filesystem.dbfs.impl;

public class DatabaseFileSystemDirectory{

	String directoryPath;
	String username;
	
	public DatabaseFileSystemDirectory(String un,String pathname) {
		username = un;
		directoryPath = pathname;
	}
	
	
	public boolean isFile()
	{
		return false;
	}
	
	public boolean isDirectory()
	{
		return false;
	}

	
	public boolean exists()
	{
		return false;
	}
	
	public boolean mkdirs()
	{
		return false;
	}
}
