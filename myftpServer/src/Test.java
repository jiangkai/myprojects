import java.io.File;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

import com.yq.ftpserver.filesystem.dbfs.DatabaseFileSystemFactory;
import com.yq.ftpserver.usermanager.DatabaseUserManagerFactory;


public class Test {
	public static void main(String[] args) throws FtpException
	{
		FtpServerFactory serverFactory = new FtpServerFactory();
		
		ListenerFactory factory = new ListenerFactory();
		serverFactory.addListener("default", factory.createListener());
		
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
//		DatabaseUserManagerFactory userManagerFactory = new DatabaseUserManagerFactory();
		userManagerFactory.setFile(new File("test/resources/users.properties"));
		userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());

		serverFactory.setUserManager(userManagerFactory.createUserManager());
		
//		FileSystemFactory fileSystemFactory = new DatabaseFileSystemFactory();
		
		FileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
		serverFactory.setFileSystem(fileSystemFactory);
		
		FtpServer server = serverFactory.createServer(); 
		server.start();
	}
}
