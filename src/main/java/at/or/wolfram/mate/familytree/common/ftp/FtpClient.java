package at.or.wolfram.mate.familytree.common.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// from: https://www.baeldung.com/java-ftp-client
public class FtpClient {
	 
    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;
    
    private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);
 
    public FtpClient(String server, int port, String user, String password ) {
    	this.server = server;
    	this.port = port;
    	this.user = user;
    	this.password = password;
    }
    
    public void open() throws IOException {
        ftp = new FTPClient();
 
        ftp.addProtocolCommandListener(new ProtocolCommandListener() {
			
			@Override
			public void protocolReplyReceived(ProtocolCommandEvent event) {
				logger.debug("Command received: '{}', Message: '{}'", event.getCommand(), event.getMessage());
			}
			
			@Override
			public void protocolCommandSent(ProtocolCommandEvent event) {
				logger.debug("Command sent: '{}', Message: '{}'", event.getCommand(), event.getMessage());
			}
		});
 
        ftp.connect(server, port);
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
 
        ftp.login(user, password);
    }
 
    public void downloadFile(String source, String destination) throws IOException {
        FileOutputStream out = new FileOutputStream(destination);
        ftp.retrieveFile(source, out);
    }
    
    public void putFileToPath(File file, String path) throws IOException {
        ftp.storeFile(path, new FileInputStream(file));
    }
    
    public void deleteFileFromPath(String path) throws IOException {
    	ftp.deleteFile(path);
    }
    
    public Collection<String> listFiles(String path) throws IOException {
        FTPFile[] files = ftp.listFiles(path);
        return Arrays.stream(files)
          .map(FTPFile::getName)
          .collect(Collectors.toList());
    }
    
    public void close() throws IOException {
        ftp.disconnect();
    }
}