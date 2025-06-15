package com.SpringProject.kharidoMat.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    public static String saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(uploadDir + "/" + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }

        return fileName;
    }
}
