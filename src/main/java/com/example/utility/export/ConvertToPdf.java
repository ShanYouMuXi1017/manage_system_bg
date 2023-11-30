package com.example.utility.export;

import java.io.*;

public class ConvertToPdf {
    public static Boolean convertWordToPdf(String filePath, String dirPath) throws IOException, InterruptedException {

        String command = "libreoffice --headless --convert-to pdf " + filePath + " --outdir " + dirPath;

        Process process = Runtime.getRuntime().exec(command);

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return true;
        } else {
            System.out.println("Conversion failed with exit code: " + exitCode);
            return false;
        }
    }
}

