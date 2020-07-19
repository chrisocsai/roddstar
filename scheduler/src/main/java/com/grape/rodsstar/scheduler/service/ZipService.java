package com.grape.rodsstar.scheduler.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    public byte[] zipMultipleFile(Map<String,String> srcFilesContent) throws IOException {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream();  ZipOutputStream zipOut = new ZipOutputStream(baos)){
            for (String fileName : srcFilesContent.keySet()) {
                ByteArrayInputStream bais = new ByteArrayInputStream(srcFilesContent.get(fileName).getBytes());
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = bais.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                bais.close();
            }
            zipOut.finish();
            zipOut.close();
            return baos.toByteArray();
        }
    }
}
