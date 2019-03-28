/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ofpatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASSTPROGRAMMER
 */
public class FileSearch {

    /**
     * @param args the command line arguments
     */
    public static String PATHSEPERATOR = "/";

    public static void recursive(File sourcePath, File destinationPath) throws NoSuchAlgorithmException, IOException {
        File[] source = sourcePath.listFiles();
        File[] destination = destinationPath.listFiles();

        MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");

        for (File file : source) {
            if (!file.isDirectory()) {
                if (!new File(destinationPath.getPath() + PATHSEPERATOR + file.getName()).exists()) {
                    copyFile(file, new File(destinationPath.getPath()));
                    System.out.println("File added: " + file);
                } else if (!getFileChecksum(shaDigest, new File(destinationPath.getPath() + PATHSEPERATOR + file.getName())).
                        equals(getFileChecksum(shaDigest, file))) {
                    copyFile(file, new File(destinationPath.getPath()));

                    System.out.println("File changes: " + file);
                }
            } else if (file.isDirectory()) {
                if (!new File(destinationPath.getPath() + PATHSEPERATOR + file.getName()).exists()) {
                    new File(destinationPath.getPath() + PATHSEPERATOR + file.getName()).mkdirs();
                    recursive(file, new File(destinationPath + PATHSEPERATOR + file.getName()));
                    System.out.println("Directory added: " + file);
                } else {
                    recursive(file, new File(destinationPath + PATHSEPERATOR + file.getName()));
                }
            }
        }
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);
        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    public static void copyFile(File toCopy, File mainDestination)
            throws IOException {
        if (!mainDestination.exists()) {
            mainDestination.mkdirs();
        }
        Path to = Paths.get(mainDestination.getAbsolutePath()
                + File.separatorChar + toCopy.getName());

        Files.copy(toCopy.toPath(), to, StandardCopyOption.REPLACE_EXISTING);
    }
}
