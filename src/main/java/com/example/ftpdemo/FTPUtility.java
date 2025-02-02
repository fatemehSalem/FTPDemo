package com.example.ftpdemo;


import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import java.security.cert.X509Certificate;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;

@Service
public class FTPUtility {
    private static final String SERVER = "localhost";
    private static final int PORT = 21; // Default FTP port
    private static final String USER = "samaUser";
    private static final String PASSWORD = "samaUser@123";

    public boolean uploadFileD() {
        boolean done = false;
        try {

            TrustManager[] trustAllCertificates = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());


            FTPSClient ftps = new FTPSClient();
           ftps.setTrustManager(trustAllCertificates[0]);
          //  ftps.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            ftps.setRemoteVerificationEnabled(false);
            ftps.setEnabledSessionCreation(true);


            ftps.connect(SERVER, PORT);
            System.out.println("Connected to FTP server");

            ftps.login(USER, PASSWORD);
            System.out.println("Logged in");



            ftps.execPBSZ(0);
            ftps.execPROT("P");
            ftps.enterLocalPassiveMode();
            ftps.setFileType(FTPSClient.BINARY_FILE_TYPE);
            System.out.println(ftps.getReplyString());


            String remoteFile = "demo.xlsx";

            ClassPathResource resource = new ClassPathResource("demo.xlsx");

            ftps.printWorkingDirectory();

            try (InputStream inputStream = resource.getInputStream()) {
                done = ftps.storeFile(remoteFile, inputStream);
                System.out.println("Upload success: " + done + " Reply: " + ftps.getReplyString());
            }

          /*  long fileSize = getFileSize(ftps, "demo.xlsx");
            if(fileSize != 0) {
                InputStream is = retrieveFileStream(ftps, "demo.xlsx");
                byte[] fileBytes = downloadFile(is, fileSize);
                if (fileBytes == null) return false;
            }*/
            if (done) {
                System.out.println("File uploaded successfully.");
            } else {
                System.out.println("Failed to upload the file.");
            }

            ftps.logout();
            ftps.disconnect();
            System.out.println("Disconnected");

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return done;
    }

    public boolean uploadFile() {

        String protocol = "TLSv1.2";
        FTPSClient ftpsClient = new FTPSClient( protocol,false);
        ftpsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        boolean done = false;

        try {
            ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
            ftpsClient.setRemoteVerificationEnabled(false);
            ftpsClient.setEnabledSessionCreation(true);

            ftpsClient.connect(SERVER, PORT);
            System.out.println("Connected to server.");

            if (ftpsClient.login(USER, PASSWORD)) {
                System.out.println("Logged in successfully.");
            } else {
                System.err.println("Login failed.");
                return false;
            }

            ftpsClient.execPBSZ(0);
            ftpsClient.execPROT("P");
            ftpsClient.enterLocalPassiveMode();
            ftpsClient.setFileType(FTPSClient.BINARY_FILE_TYPE);

            // File upload
            String remoteFile = "/demo.xlsx";
            ClassPathResource resource = new ClassPathResource("demo.xlsx");

            try (InputStream inputStream = resource.getInputStream()) {
                done = ftpsClient.storeFile(remoteFile, inputStream);
            }


        /*    long fileSize = getFileSize(ftpsClient, "demo.xlsx");
            if(fileSize != 0) {
                InputStream is = retrieveFileStream(ftpsClient, "demo.xlsx");
                byte[] fileBytes = downloadFile(is, fileSize);
                if (fileBytes == null) return false;
            }*/
            if (done) {
                System.out.println("File uploaded successfully.");
            } else {
                System.out.println("Failed to upload the file.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftpsClient.isConnected()) {
                    ftpsClient.logout();
                    ftpsClient.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return done;
    }
    private byte[] downloadFile(InputStream is, long fileSize)
            throws Exception {
        byte[] buffer = new byte[(int) fileSize];
        if (is.read(buffer, 0, buffer.length) == -1) {
            return null;
        }
        return buffer; // <-- Here is your file's contents !!!
    }
    private InputStream retrieveFileStream(FTPSClient ftp, String filePath)
            throws Exception {
        InputStream is = ftp.retrieveFileStream(filePath);
        int reply = ftp.getReplyCode();
        if (is == null
                || (!FTPReply.isPositivePreliminary(reply)
                && !FTPReply.isPositiveCompletion(reply))) {
            throw new Exception(ftp.getReplyString());
        }
        return is;
    }
        private long getFileSize(FTPSClient ftp, String filePath) throws Exception {
            long fileSize = 0;
            ftp.changeWorkingDirectory("/FTP");
            FTPFile[] files = ftp.listFiles(filePath);
            for (FTPFile file : files) {
                System.out.println(file.getName() + " : " + file.getSize());
            }

            int replyCode = ftp.getReplyCode();
            System.out.println("Reply Code: " + replyCode);
            if (files.length == 1 && files[0].isFile()) {
                fileSize = files[0].getSize();
            }
            return fileSize;
        }

    public void uploadFile2() {
        FTPClient ftpClient = new FTPClient();
        try {
            // Connect to FTP Server
            ftpClient.connect(SERVER, PORT);
            int replyCode = ftpClient.getReplyCode();
            if (!ftpClient.login(USER, PASSWORD)) {
                throw new RuntimeException("FTP login failed");
            }
            System.out.println("Connected to FTP server.");

            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            String localFile = "src/main/resources/ftpFile.txt";
            String remoteFile = "/ftpFile.txt";
            InputStream inputStream = new FileInputStream(localFile);
            boolean done = ftpClient.storeFile(remoteFile, inputStream);
            inputStream.close();

            if (done) {
                System.out.println("File uploaded successfully!");
            } else {
                System.err.println("File upload failed.");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
