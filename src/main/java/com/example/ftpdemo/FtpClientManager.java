package com.example.ftpdemo;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class FtpClientManager {
    private FTPClient ftpClient = new FTPClient();

    // Connect to FTP Server
    public void connect(String server, int port, String user, String pass) throws IOException {
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();  // Passive mode to avoid connection issues
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // Ensures correct file transfer
        System.out.println("Connected to " + server);
    }

    // Upload a file to FTP
    public boolean uploadFile(String localFilePath, String remoteFilePath) throws IOException {
        ClassPathResource resource = new ClassPathResource("demo.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            boolean uploaded = ftpClient.storeFile(remoteFilePath, inputStream);
            System.out.println("Upload " + (uploaded ? "successful" : "failed"));
            return uploaded;
        }
    }

    // Get file size on FTP
    public long getFileSize(String remoteFilePath) throws IOException {
        long fileSize = ftpClient.mlistFile(remoteFilePath).getSize();
        System.out.println("File size: " + fileSize + " bytes");
        return fileSize;
    }

    // List files in directory
    public void listFiles(String directory) throws IOException {
        var files = ftpClient.listFiles(directory);
        for (var file : files) {
            System.out.println((file.isDirectory() ? "[DIR] " : "[FILE] ") + file.getName());
        }
    }

    // Download a file from FTP
    public boolean downloadFile(String remoteFilePath) throws IOException {
        String resourcesPath = new File("src/main/resources/" + remoteFilePath).getAbsolutePath();
        File localFile = new File(resourcesPath);
        // Ensure parent directories exist
        localFile.getParentFile().mkdirs();

        try (OutputStream outputStream = new FileOutputStream(localFile)) {
            boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
            System.out.println("Downloaded to resources: " + (success ? "successful" : "failed"));
            return success;
        }
    }

    // Delete a file from FTP
    public boolean deleteFile(String remoteFilePath) throws IOException {
        boolean deleted = ftpClient.deleteFile(remoteFilePath);
        System.out.println("File " + (deleted ? "deleted" : "deletion failed"));
        return deleted;
    }

    // Disconnect from FTP server
    public void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                System.out.println("Disconnected from FTP server.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
