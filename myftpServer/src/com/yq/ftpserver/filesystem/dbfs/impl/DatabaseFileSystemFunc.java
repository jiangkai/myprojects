package com.yq.ftpserver.filesystem.dbfs.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.yq.ftpserver.database.DataSource;
import com.yq.ftpserver.filesystem.dbfs.DatabaseFileSystemFactory;

public class DatabaseFileSystemFunc {

	User user;
	DataSource dataSource = DataSource.getDataSource();

	private final Logger LOG = LoggerFactory
			.getLogger(DatabaseFileSystemFunc.class);

	public DatabaseFileSystemFunc(User u) {
		user = u;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static String normalize(String orignalPath) {
		// orignalPath.replaceAll("\\",
		// DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR);
		if (orignalPath.length() == 0)
			return DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR;
		// if(orignalPath.startsWith("."))
		// orignalPath = orignalPath.substring(1,orignalPath.length());
		if (!orignalPath.equals(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR)
				&& orignalPath
						.endsWith(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR))
			return orignalPath.substring(0, orignalPath.length() - 1);
		else
			return orignalPath;
	}

	public static String getParentPath(String path) {
		path = normalize(path);
		if (path.equals(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR))
			return "";
		else {
			int index = path
					.lastIndexOf(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR);
			if (index == 0)
				return DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR;
			else
				return path.substring(0, index);
		}
	}

	public boolean doesExist(String path) {
		path = normalize(path);
		String sql = "select * from "
				+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + path + "' and username='" + user.getName()
				+ "'";
		return !dataSource.isResultEmpty(sql);
	}

	public void addDirectoryToParentDirectory(String parent,
			String directoryPath) {
		directoryPath = normalize(directoryPath);
		String subDirectories = getSubDirectories(parent);
		String newSubDir = subDirectories
				+ DatabaseFileSystemFactory.DIRECTORY_SEPERATOR_INDB
				+ directoryPath;

		String sql = "update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " set subfiles='" + newSubDir + "' where username='"
				+ user.getName() + "' and path='" + parent + "'";

		dataSource.executeUpdate(sql);
	}
	
	private void deleteDirectoryFromParentDirectory(String parent,
			String directoryPath) {
		// TODO Auto-generated method stub
		directoryPath=normalize(directoryPath);
		String subDirectories = getSubDirectories(parent);
		String newSubDir = subDirectories.substring(0, subDirectories.lastIndexOf(" "));	
		String sql = "update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " set subfiles='" + newSubDir + "' where username='"
				+ user.getName() + "' and path='" + parent + "'";
		dataSource.executeUpdate(sql);
	}

	public String getSubDirectories(String path) {
		path = normalize(path);
		String sql = "select * from "
				+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + path + "' and username='" + user.getName()
				+ "'";

		return (String) dataSource.query(sql, "subfiles");
	}

	public List<String> getSubDirectoriesList(String path) {
		path = normalize(path);
		String result = getSubDirectories(path);
		List<String> subDirectories = new ArrayList<String>();
		String[] paths = result
				.split(DatabaseFileSystemFactory.DIRECTORY_SEPERATOR_INDB);
		for (String subdir : paths)
			subDirectories.add(subdir);
		return subDirectories;
	}

	public void addDirectoryToParentDirectory(String path)
	{
		String parentPath = getParentPath(path);
		addDirectoryToParentDirectory(parentPath,path);
	}
	
	public void deleteDirectoryFromParentDirectory(String path) {
		String parentPath = getParentPath(path);
		deleteDirectoryFromParentDirectory(parentPath,path);
	}
	
	

	public boolean createDirectory(String path) {
		path = normalize(path);
		String parentPath = getParentPath(path);

		if (doesExist(path))
			throw new IllegalArgumentException("Path of " + path
					+ " has been created!");

		if (!doesExist(parentPath))
			throw new IllegalArgumentException("Parent path of " + path
					+ " does not exist!");

		addDirectoryToParentDirectory(parentPath, path);

		String sql = "insert into "
				+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ "(path,username,parentpath,subfiles,isDirectory) values ('"
				+ path + "','" + user.getName() + "','" + getParentPath(path)
				+ "','',1)";

		dataSource.executeUpdate(sql);
		return true;
	}

	public void createHomeDirectory() {
		createDirectory(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR);
	}

	// public boolean createDirectory(String directoryPath)
	// {
	// if(doesExist(directoryPath))
	// return false;
	//
	// String sql =
	// "insert into "+DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
	// +"(path,username,parentpath,subfiles,isDirectory) values ('"
	// +directoryPath+"','"
	// +user.getName()+"','"+getParentPath(directoryPath)+"','',1)";
	// dataSource.executeUpdate(sql);
	//
	// List<String> subFiles=getSubFiles(directoryPath);
	//
	// return true;
	// }

	public boolean fileExist(String filePath) {
		filePath = normalize(filePath);
		String sql = "select * from "
				+ DatabaseFileSystemFactory.FILE_TABLE_NAME + " where dbpath='"
				+ filePath + "' and username='" + user.getName() + "'";

		return !dataSource.isResultEmpty(sql);
	}

	public File getFileFromDisk(String pathWithName) {
		pathWithName = normalize(pathWithName);
		if (!fileExist(pathWithName))
			throw new IllegalArgumentException("File does not exist:"
					+ pathWithName);

		String sql = "select * from "
				+ DatabaseFileSystemFactory.FILE_TABLE_NAME + " where dbpath='"
				+ pathWithName + "' and username='" + user.getName() + "'";
		String diskPath = (String) dataSource.query(sql, "physicalpath");

		return new File(diskPath);
	}

	public FtpFile getHomeDirectory() {
		if (!doesExist(DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR))
			createHomeDirectory();

		return new DatabaseFtpFile(
				DatabaseFileSystemFactory.DIRECTORY_DECOLLATOR, user);
	}

	public long getDirectoryLastModified(String directoryPath) {
		return 0;
	}

	public List<String> getSubFiles(String directoryPath) {
		directoryPath = normalize(directoryPath);
		if (doesExist(directoryPath)) {
			String sql = "select * from "
					+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " where path='" + directoryPath + "' and username='"
					+ user.getName() + "'";

			String result = (String) dataSource.query(sql, "subfiles");

			if (result == null || result.equals(""))
				return new ArrayList<String>();
			else {
				String[] files = result
						.split(DatabaseFileSystemFactory.FILE_SEPERATOR_INDB);
				List<String> fileList = new ArrayList<String>();
				for(String file:files)
					if(StringUtils.hasText(file))
						fileList.add(file);
				return fileList;
			}
		}
		return null;
	}

	public boolean isDirectory(String filePath) {
		filePath = normalize(filePath);

		String sql = "select isDirectory from "
				+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + filePath + "' and username='"
				+ user.getName() + "'";

		int isDirectory = (Integer) dataSource.query(sql, "isDirectory");

		if (isDirectory == 1){
			return true;
		}else{
			return false;
		}
	}

	public long getLastModified(String path) {
		return 0;
	}

	public static void main(String[] args) {
		// User user = new User();
	}

	public void setLashModified(String fileNameWithPath, long time) {
		// TODO Auto-generated method stub

	}

	public boolean isReadable(String fileNameWithPath) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isWritable(String fileNameWithPath) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isRemovable(String fileNameWithPath) {
		// TODO Auto-generated method stub
		return true;
	}

	public void deleteDirectory(String fileNameWithPath) {
		// TODO Auto-generated method stub
		if (isDirectory(fileNameWithPath)) {
			String subfiles= "select subfiles from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " where path='" + fileNameWithPath + "' and username='"
							+ user.getName() + "'" ;
			String subfile=(String) dataSource.query(subfiles, "subfiles");
			
			String sql=" delete from "+DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " where path='" + fileNameWithPath + "' and username='"
					+ user.getName() + "'";
			dataSource.executeUpdate(sql);
		    deleteDirectoryFromParentDirectory(fileNameWithPath);
			
			
			if (!subfile.equals(" ")) {
				String subfileString[]=subfile.split(" ");
				for (int i = 0; i < subfileString.length; i++) {
					if(!subfileString[i].equals(" ")) {
						if (isDirectory(subfileString[i])) {
							deleteDirectory(subfileString[i]);	
						}else {
							deleteFile(subfileString[i]);
						}
				}
			}
		}		
			
	}
}



	public void deleteFile(String fileNameWithPath) {
		// TODO Auto-generated method stub
		

		String localpathString= " select physicalpath from "+DatabaseFileSystemFactory.FILE_TABLE_NAME
				+" where dbpath='" + fileNameWithPath + "' and username='"
						+ user.getName() + "'";
		
		
		File localFile= new File(localpathString);
		localFile.delete();
	
		String sql=" delete from "+DatabaseFileSystemFactory.FILE_TABLE_NAME
				+ " where dbpath='" + fileNameWithPath + "' and username='"
				+ user.getName() + "'";
		dataSource.executeUpdate(sql);
		
		

	}

	public File createPhysicalFile(String fileNameWithPath) {

		String physicalFileName = DatabaseFileSystemView.getTheNextPhysicalFileName();
		File file = new File(physicalFileName);

		String sql = "insert into "
				+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ "(path,username,parentpath,subfiles,isDirectory) values ('"
				+ fileNameWithPath + "','" + user.getName() + "','"
				+ getParentPath(fileNameWithPath) + "','',0)";
		dataSource.executeUpdate(sql);

		sql = "insert into " + DatabaseFileSystemFactory.FILE_TABLE_NAME 
				+ "(username,dbpath,physicalpath) values('"
				+user.getName()+"','"+fileNameWithPath+"','"
				+physicalFileName+"')";
		dataSource.executeUpdate(sql);

		addDirectoryToParentDirectory(fileNameWithPath);
		return file;
	}
	

	public void renameFile(FtpFile dest, String fileNameWithPath) {
		// TODO Auto-generated method stub
		String destPath=dest.getAbsolutePath();
		
		String sql = "select parentpath from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
		+ " where path='" + fileNameWithPath + "' and username='"
		+ user.getName() + "'" ; 
		String parentpath = (String) dataSource.query(sql, "parentpath");
		
		String subfiles = "select subfiles from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + parentpath + "' and username='"
				+ user.getName() + "'" ;
		String subfileString=(String) dataSource.query(subfiles, "subfiles");
		
		String newStr=renameSubDirectory(subfileString,fileNameWithPath,destPath);
		
						
		sql= "update " + DatabaseFileSystemFactory.FILE_TABLE_NAME
				+ " set dbpath='" + destPath + "' where username='"
				+ user.getName() + "' and dbpath='" + fileNameWithPath + "'";
		dataSource.executeUpdate(sql);
		
		sql="update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " set path='" + destPath + "' where username='"
				+ user.getName() + "' and path='" + fileNameWithPath + "'";
		dataSource.executeUpdate(sql);
		

		sql= "update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " set subfiles='" + newStr + "' where username='"
				+ user.getName() + "' and path='" + parentpath + "'";
		dataSource.executeUpdate(sql);
	}

	public String renameSubDirectory(String subfileString, String fileNameWithPath, String destPath) {
		// TODO Auto-generated method stub
		String subfile[]=subfileString.split(" ");
		String subdirectory = null;
		for (int i = 0; i < subfile.length; i++) {
			if (subfile[i].equals(fileNameWithPath)) {
				subfile[i]=destPath;
			}	
			subdirectory=subfile[0];
			for (int j = 1; j < subfile.length; j++) {
				subdirectory = subdirectory+" "+subfile[j];	
			}
					
		}
		
		return subdirectory;
	}

	public void renameDirectory(FtpFile dest, String fileNameWithPath) {
		// TODO Auto-generated method stub
		if (isDirectory(fileNameWithPath)) {
			String destPath=dest.getAbsolutePath();
			
			
			
			String sql = "select parentpath from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " where path='" + fileNameWithPath + "' and username='"
					+ user.getName() + "'" ; 
			String parentpath = (String) dataSource.query(sql, "parentpath");
			
			sql= "select subfiles from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " where path='" + fileNameWithPath + "' and username='"
					+ user.getName() + "'" ; 
			String subfiles=(String) dataSource.query(sql, "subfiles");
			
			sql= "select subfiles from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " where path='" + parentpath+ "' and username='"
					+ user.getName() + "'" ; 
			String parentsubdir=(String) dataSource.query(sql, "subfiles");
			
			
			String newsubdir = renameSubDirectory(parentsubdir, fileNameWithPath, destPath);

			//update the related parent directory
			String updateparentdir= "update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " set subfiles='" + newsubdir + "' where username='"
					+ user.getName() + "' and path='" + parentpath + "'";
			dataSource.executeUpdate(updateparentdir);
			
		
		
			
			//更新该目录的子目录
			//String newsubdirString=subfiles.replaceAll(fileNameWithPath, destPath);
			if (!subfiles.equals(" ")) {
				String subfileString[]= subfiles.split(" ");
				for (int i = 0; i < subfileString.length; i++) {

				}
				
				
				
				
			    String newsubdirString=null;
				for (int i = 0; i < subfileString.length; i++) {
					String str=subfileString[i].substring(0, fileNameWithPath.length());
					str=destPath;
					subfileString[i]=str+subfileString[i].substring(fileNameWithPath.length(),subfileString[i].length() );
					newsubdirString=subfileString[0];
					for (int j = 1; j < subfileString.length; j++) {
						newsubdirString=newsubdirString+ " "+subfileString[i];
					}

					String updatesubdir="update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
							+ " set subfiles='" + newsubdirString + "' where username='"
							+ user.getName() + "' and path='" + fileNameWithPath + "'";
					dataSource.executeUpdate(updatesubdir);
				}
				
			}
			
			
			//更新该目录本身
			String updatepath="update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
					+ " set path='" + destPath + "' where username='"
					+ user.getName() + "' and path='" + fileNameWithPath + "'";
			dataSource.executeUpdate(updatepath);
			
			//update the related subdirectory
			if (!subfiles.equals(" ")) {
				String subfilesString [] = subfiles.split(" ");
				for (int i = 0; i < subfilesString.length; i++) {
					renameSubDir(destPath,subfilesString[i],fileNameWithPath);
				}
				
				
			}
			
		}
			
			
}


	public void renameSubDir(String destPath, String path,
			String fileNameWithPath) {
		// TODO Auto-generated method stub
		//查询目录
		String sql = "select path from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + path + "' and username='"
				+ user.getName() + "'" ; 
		String pathdir = (String) dataSource.query(sql, "path");
		//查询父目录
		sql="select parentpath from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + path + "' and username='"
				+ user.getName() + "'" ; 
		String parentpathdir = (String) dataSource.query(sql, "parentpath");
		//查询子目录
		sql="select subfiles from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + path + "' and username='"
				+ user.getName() + "'" ; 
		String subpathdir = (String) dataSource.query(sql, "subfiles");
		
		//查询是否为路径
		sql="select isDirectory from "+ DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " where path='" + path + "' and username='"
				+ user.getName() + "'" ; 
        int  isdir = (Integer) dataSource.query(sql, "isDirectory");
		//update file
		if (isdir==0) {
			String newdir=null;
			String str=pathdir.substring(0, fileNameWithPath.length());
			str=destPath;
			newdir=str+pathdir.subSequence(fileNameWithPath.length(), pathdir.length());
			String updatepath = "update " + DatabaseFileSystemFactory.FILE_TABLE_NAME
					+ " set dbpath='" + newdir+ "' where username='"
					+ user.getName() + "' and dbpath='" + path + "'";
			dataSource.executeUpdate(updatepath);
		}
		
		       //update parentpath
				String newparentdir=parentpathdir.replaceAll(fileNameWithPath, destPath);
				String updateparentpath = "update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
						+ " set parentpath='" + newparentdir+ "' where username='"
						+ user.getName() + "' and path='" + path + "'";
				dataSource.executeUpdate(updateparentpath);
				
				//update subdirpath
				
				if ((!(subpathdir==null)) && (!(subpathdir.equals("")))) {
					String subdir[]=subpathdir.split(" ");
					String newsubdirString=null;
					for (int i = 0; i < subdir.length; i++) {
						if ( !(subdir[i].equals(""))) {
							String str=subdir[i].substring(0, fileNameWithPath.length());
							str=destPath;
							
							subdir[i]=str+subdir[i].substring(fileNameWithPath.length(),subdir[i].length() );
							
							newsubdirString=subdir[0];
							for (int j = 1; j < subdir.length; j++) {
								newsubdirString=newsubdirString+ " "+subdir[i];
							}

							
							renameSubDir(destPath, subpathdir, fileNameWithPath);
							
						} 
//						String updatesubdir="update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
//								+ " set path='" + newsubdirString + "' where username='"
//								+ user.getName() + "' and path='" + subdir[i] + "'";
//						dataSource.executeUpdate(updatesubdir);
						
						String updatesubdirectory="update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
								+ " set subfiles='" + newsubdirString + "' where username='"
								+ user.getName() + "' and path='" + path + "'";
						dataSource.executeUpdate(updatesubdirectory);
						
						}
						
				}
				
				
				
		//update path
		//String newdir=pathdir.replaceAll(fileNameWithPath, destPath);
		String newdir=null;
		String str=pathdir.substring(0, fileNameWithPath.length());
		str=destPath;
		newdir=str+pathdir.subSequence(fileNameWithPath.length(), pathdir.length());
		String updatepath = "update " + DatabaseFileSystemFactory.DIRECTORY_TABLE_NAME
				+ " set path='" + newdir+ "' where username='"
				+ user.getName() + "' and path='" + path + "'";
		dataSource.executeUpdate(updatepath);
		
	}
}
		
	


