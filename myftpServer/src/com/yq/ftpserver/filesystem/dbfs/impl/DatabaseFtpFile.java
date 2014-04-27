package com.yq.ftpserver.filesystem.dbfs.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.ftpserver.filesystem.nativefs.impl.NameEqualsFileFilter;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yq.ftpserver.filesystem.dbfs.DatabaseFileSystemFactory;

public class DatabaseFtpFile implements FtpFile {

    private final Logger LOG = LoggerFactory.getLogger(DatabaseFtpFile.class);

    // the file name with respect to the user root.
    // The path separator character will be '/' and
    // it will always begin with '/'.
    
	private DatabaseFileSystemFunc dbfsfc;
    private String fileNameWithPath;
    private User user;
    private File file;
       
    public DatabaseFtpFile(final String fileNameWithPath,final User user)
    {   
        this.fileNameWithPath = DatabaseFileSystemFunc.normalize(fileNameWithPath);
        this.user = user;
        this.dbfsfc = new DatabaseFileSystemFunc(user);	
		if(isFile())
			file = dbfsfc.getFileFromDisk(fileNameWithPath);
    }
      

    /**
     * Get full name.
     */
    @Override
    public String getAbsolutePath() {
    	return fileNameWithPath;
    }

    /**
     * Get short name.
     */
    @Override
    public String getName() {

        // root - the short name will be '/'
        if (fileNameWithPath.equals(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR)) {
            return DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR;
        }

        // strip the last '/'
        String[] directories = fileNameWithPath.split(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR);
        return directories[directories.length-1];
    }

    /**
     * Is a hidden file?
     */
    public boolean isHidden() {
    	return false;
    }

    /**
     * Is it a directory?
     */
    public boolean isDirectory() {
        return dbfsfc.isDirectory(fileNameWithPath);
    }

    /**
     * Is it a file?
     */
    public boolean isFile() {
    	if(!doesExist())
    		return false;
    	else
    		return !dbfsfc.isDirectory(fileNameWithPath);
    }

    /**
     * Does this file exists?
     */
    public boolean doesExist() {
    	return dbfsfc.doesExist(fileNameWithPath);
    }

    /**
     * Get file size.
     */
    public long getSize() {
    	if(isDirectory())
    		return 0;
    	else
    		return file.length();
    }

    /**
     * Get file owner.
     */
    public String getOwnerName() {
        return user.getName();
    }

    /**
     * Get group name
     */
    public String getGroupName() {
        return "group";
    }

    /**
     * Get link count
     */
    public int getLinkCount() {
        //TODO  	    	
    	return isDirectory()?3:1;
    }

    /**
     * Get last modified time.
     */
    public long getLastModified(){
    	return dbfsfc.getLastModified(fileNameWithPath);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setLastModified(long time) {  	
    	dbfsfc.setLashModified(fileNameWithPath,time);   	
       	return true;
    }

    /**
     * Check read permission.
     */
    public boolean isReadable() {
    	return dbfsfc.isReadable(fileNameWithPath);
    }

    /**
     * Check file write permission.
     */
    public boolean isWritable() {    	
    	return dbfsfc.isWritable(fileNameWithPath);
    }

    /**
     * Has delete permission.
     */
    public boolean isRemovable() {

        // root cannot be deleted
        if ("/".equals(fileNameWithPath)) {
            return false;
        }
        
    	return dbfsfc.isRemovable(fileNameWithPath);        
    }

    /**
     * Delete file.
     */
    public boolean delete() {
        boolean retVal = false;
        if (isRemovable()) {

        	if (isDirectory()) {
        		dbfsfc.deleteDirectory(fileNameWithPath);
        		retVal=true;
			}else if (isFile()) {
				
				dbfsfc.deleteFile(fileNameWithPath);
				dbfsfc.deleteDirectory(fileNameWithPath);
				
				retVal=true;
			}
        }
        return retVal;
    }

    /**
     * Move file object.
     */
    public boolean move(final FtpFile dest) {  
    	boolean retVal=false;
    	if (isWritable()) {
			if (isDirectory()) {
				dbfsfc.renameDirectory(dest,fileNameWithPath);
				retVal=true;
			}else if (isFile()) {
				dbfsfc.renameFile(dest,fileNameWithPath);
				retVal=true;
			}
		}
    	
    	return retVal;
    }

    /**
     * Create directory.
     */
    public boolean mkdir() {
        boolean retVal = false;
        if (isWritable()) {
            retVal = dbfsfc.createDirectory(fileNameWithPath);
        }
        return retVal;
    }

    /**
     * Get the physical file object.
     */
    public File getPhysicalFile(){
        return file;
    }

    /**
     * List files. If not a directory or does not exist, null will be returned.
     */
    public List<FtpFile> listFiles() {

        // is a directory
        if (isFile())
            return null;
        else
        {
    		List<String> files = dbfsfc.getSubFiles(fileNameWithPath);

    		List<FtpFile> ftpFiles = new ArrayList<FtpFile>();

    		for (String file : files)
    				ftpFiles.add(new DatabaseFtpFile(file, user));

    		return ftpFiles;
        }
    }

    /**
     * Create output stream for writing.
     */
    public OutputStream createOutputStream(final long offset)
            throws IOException {
    	
        // permission check
        if (!isWritable()) {
            throw new IOException("No write permission : " + file.getName());
        }

        if(!doesExist())
        	file = dbfsfc.createPhysicalFile(fileNameWithPath);
        
        // create output stream
        final RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.setLength(offset);
        raf.seek(offset);

        // The IBM jre needs to have both the stream and the random access file
        // objects closed to actually close the file
        return new FileOutputStream(raf.getFD()) {
            @Override
            public void close() throws IOException {
                super.close();
                raf.close();
            }
        };
    }

    /**
     * Create input stream for reading.
     */
    public InputStream createInputStream(final long offset) throws IOException {

    	if(isDirectory())
    		throw new IOException("Can not read a directory : " + getName());
    	
    	
        // permission check
        if (!isReadable()) {
            throw new IOException("No read permission : " + file.getName());
        }

        // move to the appropriate offset and create input stream
        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.seek(offset);

        // The IBM jre needs to have both the stream and the random access file
        // objects closed to actually close the file
        return new FileInputStream(raf.getFD()) {
            @Override
            public void close() throws IOException {
                super.close();
                raf.close();
            }
        };
    }

    /**
     * Normalize separate character. Separate character should be '/' always.
     */
    public final static String normalizeSeparateChar(final String pathName) {
        String normalizedPathName = pathName.replace(File.separatorChar, '/');
        normalizedPathName = normalizedPathName.replace('\\', '/');
        return normalizedPathName;
    }

    /**
     * Get the physical canonical file name. It works like
     * File.getCanonicalPath().
     * 
     * @param rootDir
     *            The root directory.
     * @param currDir
     *            The current directory. It will always be with respect to the
     *            root directory.
     * @param fileNameWithPath
     *            The input file name.
     * @return The return string will always begin with the root directory. It
     *         will never be null.
     */
    public final static String getPhysicalName(final String rootDir,
            final String currDir, final String fileNameWithPath) {
        return getPhysicalName(rootDir, currDir, fileNameWithPath, false);
    }

    public final static String getPhysicalName(final String rootDir,
            final String currDir, final String fileNameWithPath,
            final boolean caseInsensitive) {

        // get the starting directory
        String normalizedRootDir = normalizeSeparateChar(rootDir);
        if (normalizedRootDir.charAt(normalizedRootDir.length() - 1) != '/') {
            normalizedRootDir += '/';
        }

        String normalizedFileName = normalizeSeparateChar(fileNameWithPath);
        String resArg;
        String normalizedCurrDir = currDir;
        if (normalizedFileName.charAt(0) != '/') {
            if (normalizedCurrDir == null) {
                normalizedCurrDir = "/";
            }
            if (normalizedCurrDir.length() == 0) {
                normalizedCurrDir = "/";
            }

            normalizedCurrDir = normalizeSeparateChar(normalizedCurrDir);

            if (normalizedCurrDir.charAt(0) != '/') {
                normalizedCurrDir = '/' + normalizedCurrDir;
            }
            if (normalizedCurrDir.charAt(normalizedCurrDir.length() - 1) != '/') {
                normalizedCurrDir += '/';
            }

            resArg = normalizedRootDir + normalizedCurrDir.substring(1);
        } else {
            resArg = normalizedRootDir;
        }

        // strip last '/'
        if (resArg.charAt(resArg.length() - 1) == '/') {
            resArg = resArg.substring(0, resArg.length() - 1);
        }

        // replace ., ~ and ..
        // in this loop resArg will never end with '/'
        StringTokenizer st = new StringTokenizer(normalizedFileName, "/");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();

            // . => current directory
            if (tok.equals(".")) {
                continue;
            }

            // .. => parent directory (if not root)
            if (tok.equals("..")) {
                if (resArg.startsWith(normalizedRootDir)) {
                    int slashIndex = resArg.lastIndexOf('/');
                    if (slashIndex != -1) {
                        resArg = resArg.substring(0, slashIndex);
                    }
                }
                continue;
            }

            // ~ => home directory (in this case the root directory)
            if (tok.equals("~")) {
                resArg = normalizedRootDir.substring(0, normalizedRootDir
                        .length() - 1);
                continue;
            }

            if (caseInsensitive) {
                File[] matches = new File(resArg)
                        .listFiles(new NameEqualsFileFilter(tok, true));

                if (matches != null && matches.length > 0) {
                    tok = matches[0].getName();
                }
            }

            resArg = resArg + '/' + tok;
        }

        // add last slash if necessary
        if ((resArg.length()) + 1 == normalizedRootDir.length()) {
            resArg += '/';
        }

        // final check
        if (!resArg.regionMatches(0, normalizedRootDir, 0, normalizedRootDir
                .length())) {
            resArg = normalizedRootDir;
        }

        return resArg;
    }

    /**
     * Implements equals by comparing getCanonicalPath() for the underlying file instabnce.
     * Ignores the fileNameWithPath and User fields
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DatabaseFtpFile) {
            String thisCanonicalPath;
            String otherCanonicalPath;
            thisCanonicalPath = getAbsolutePath();
			otherCanonicalPath = ((DatabaseFtpFile)obj).getAbsolutePath();
			return thisCanonicalPath.equals(otherCanonicalPath);
        }
        return false;
    }


	@Override
	public int hashCode() {
		try {
			return file.getCanonicalPath().hashCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}


	public String getFileNameWithPath() {
		return fileNameWithPath;
	}
	public void setFileNameWithPath(String fileNameWithPath) {
		this.fileNameWithPath = fileNameWithPath;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}	
}

