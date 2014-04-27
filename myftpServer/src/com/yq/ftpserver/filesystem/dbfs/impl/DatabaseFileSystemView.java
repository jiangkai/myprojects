package com.yq.ftpserver.filesystem.dbfs.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.ftpserver.filesystem.nativefs.impl.NativeFtpFile;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yq.ftpserver.filesystem.dbfs.DatabaseFileSystemFactory;

public class DatabaseFileSystemView implements FileSystemView  {

    private final Logger LOG = LoggerFactory.getLogger(DatabaseFileSystemView.class);
	
	private DatabaseFileSystemFunc dbfsfc;
	private User user;
//	private boolean caseInsensitive;
	
	private static String local_disk_rootdir;
	
	private String currentDirPath;
		
	public DatabaseFileSystemView(User u, boolean cI) {
		user = u;
//		caseInsensitive = cI;
		dbfsfc = new DatabaseFileSystemFunc(u);
		currentDirPath = DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR;
	
		Properties prop = new Properties();
		InputStream in = getClass().getResourceAsStream("/test/resources/myFtpServer.properties"); 
		try {
			prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		local_disk_rootdir = prop.getProperty("myftpserver.localdisk.rootdir");
	}

	public DatabaseFileSystemView(User u)
	{
		this(u,false);
	}
	
	
	@Override
	public FtpFile getHomeDirectory() throws FtpException {
		return dbfsfc.getHomeDirectory();
	}

	@Override
	public FtpFile getWorkingDirectory() throws FtpException {
		return getFile(currentDirPath);
		//return dbfsfc.getFtpFile(currentDirPath,true);
	}

	@Override
	public boolean changeWorkingDirectory(String dir) throws FtpException {
        String physicalName = NativeFtpFile.getPhysicalName(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR,
        		currentDirPath, dir, false);
		
		FtpFile directory= getFile(physicalName);
		if(directory!=null)
		{
			currentDirPath = physicalName;
			return true;
		}
		else
			return false;
	}

	@Override
	public FtpFile getFile(String file) throws FtpException {

        // get actual file object
        String physicalName = NativeFtpFile.getPhysicalName(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR,
        		currentDirPath, file, false);
 //       File fileObj = new File(physicalName);

        // strip the root directory and return
//        String userFileName = physicalName.substring(rootDir.length() - 1);
//        return new NativeFtpFile(userFileName, fileObj, user);
        return new DatabaseFtpFile(physicalName,user);

	}

	@Override
	public boolean isRandomAccessible() throws FtpException {
		return true;
	}

	@Override
	public void dispose() {		
	}

	public static String getTheNextPhysicalFileName()
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		String theNextPhysicalFileName = local_disk_rootdir+"/"+df.format(new Date());
		return theNextPhysicalFileName;
	}
	

	
}
