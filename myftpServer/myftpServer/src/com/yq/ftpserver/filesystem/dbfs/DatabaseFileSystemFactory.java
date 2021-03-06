package com.yq.ftpserver.filesystem.dbfs;

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yq.ftpserver.filesystem.dbfs.impl.DatabaseFileSystemFunc;
import com.yq.ftpserver.filesystem.dbfs.impl.DatabaseFileSystemView;

public class DatabaseFileSystemFactory implements FileSystemFactory  {
    
	
	private final Logger LOG = LoggerFactory.getLogger(DatabaseFileSystemFactory.class);
    
	private boolean createHome;

    private boolean caseInsensitive;

    
    public static final String DIRECTORY_DECOLLATOR = "/";
    
    /**
     * table ddl
     * CREATE TABLE `directory` (
		  `id` int(11) NOT NULL AUTO_INCREMENT,
		  `name` varchar(255) NOT NULL,
		  `username` varchar(255) NOT NULL,
		  `subdirectories` varchar(1024) DEFAULT NULL,
		  `files` varchar(1024) DEFAULT NULL,
		  PRIMARY KEY (`id`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;

		CREATE TABLE `file` (
		  `id` int(11) NOT NULL AUTO_INCREMENT,
		  `name` varchar(255) NOT NULL,
		  `username` varchar(255) NOT NULL,
		  `dbpath` varchar(255) NOT NULL,
		  `phsicalpath` varchar(255) DEFAULT NULL,
		  `type` varchar(255) DEFAULT NULL,
		  PRIMARY KEY (`id`)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8;*/   
    public static final String DIRECTORY_TABLE_NAME = "directory";
    public static final String FILE_TABLE_NAME = "file";
    
    
    
    private DatabaseFileSystemFunc dbFsFunc = new DatabaseFileSystemFunc();
    /**
     * Should the home directories be created automatically
     * @return true if the file system will create the home directory if not available
     */
    public boolean isCreateHome() {
        return createHome;
    }

    /**
     * Set if the home directories be created automatically
     * @param createHome true if the file system will create the home directory if not available
     */

    public void setCreateHome(boolean createHome) {
        this.createHome = createHome;
    }

    /**
     * Is this file system case insensitive. 
     * Enabling might cause problems when working against case-sensitive file systems, like on Linux
     * @return true if this file system is case insensitive
     */
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    /**
     * Should this file system be case insensitive. 
     * Enabling might cause problems when working against case-sensitive file systems, like on Linux
     * @param caseInsensitive true if this file system should be case insensitive
     */
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * Create the appropriate user file system view.
     */
    public FileSystemView createFileSystemView(User user) throws FtpException {
        synchronized (user) {
            // create home if does not exist
            if (createHome)
            	dbFsFunc.createDirectoryForUser(user, DIRECTORY_DECOLLATOR);

            FileSystemView fsView = new DatabaseFileSystemView(user,caseInsensitive);
            return fsView;
        }
    }


}
