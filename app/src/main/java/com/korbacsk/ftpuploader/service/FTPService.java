package com.korbacsk.ftpuploader.service;

import com.korbacsk.ftpuploader.helper.Debug;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;

public class FTPService {
    private final String host;
    private final String username;
    private final String password;

    private FTPClient ftpClient;

    public FTPService(String host, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
    }

    public String connect() {
        String error = null;
        boolean connected = false;
        try {
            ftpClient = new FTPClient();
            ftpClient.setConnectTimeout(10 * 1000);
            ftpClient.connect(InetAddress.getByName(this.host));
            connected = ftpClient.login(this.username, this.password);
            Debug.LogMessage("FTPService - connect, connection status:" + String.valueOf(connected));
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                /*FTPFile[] mFileArray = mFtpClient.listFiles();
                Log.e("Size", String.valueOf(mFileArray.length));*/
            }
        } catch (Exception e) {
            Debug.LogError(e);
            error = e.getLocalizedMessage();

        } finally {
            return error;
        }

    }

    public String uploadFile(File uploadFile, String remotePath) {
        String error = null;
        try {
            FileInputStream srcFileStream = new FileInputStream(uploadFile);
            boolean status = ftpClient.storeFile(uploadFile.getName(),
                    srcFileStream);
            Debug.LogMessage("FTPService - uploadFile, status:" + String.valueOf(status));
            srcFileStream.close();
        } catch (Exception e) {
            Debug.LogError(e);
            error = e.getLocalizedMessage();
        } finally {
            return error;
        }
    }

}
