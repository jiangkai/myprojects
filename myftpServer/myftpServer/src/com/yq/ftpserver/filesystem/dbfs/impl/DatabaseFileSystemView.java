package com.yq.ftpserver.filesystem.dbfs.impl;

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yq.ftpserver.database.DataSource;

public class DatabaseFileSystemView implements FileSystemView  {

	
    private final Logger LOG = LoggerFactory.getLogger(DatabaseFileSystemView.class);
	
	private DataSource dataSource = DataSource.getDataSource();
	private User user;
	private boolean caseInsensitive;
	
	private int currentDirId;
	private int homeDirId;
	
	
	
	
	public DatabaseFileSystemView(User u, boolean cI) {
		user = u;
		caseInsensitive = cI;
	}

	public DatabaseFileSystemView(User u)
	{
		this(u,false);
	}
	
	
	@Override
	public FtpFile getHomeDirectory() throws FtpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FtpFile getWorkingDirectory() throws FtpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean changeWorkingDirectory(String dir) throws FtpException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FtpFile getFile(String file) throws FtpException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRandomAccessible() throws FtpException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
