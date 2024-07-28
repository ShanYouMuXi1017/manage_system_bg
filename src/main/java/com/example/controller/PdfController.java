package com.example.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class PdfController {

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getPdf() throws IOException {
        // 假设PDF文件存储在resources/static目录下
        ClassPathResource pdfFile = new ClassPathResource("static/OperationDocument.pdf");

        // 读取PDF文件内容
        InputStream inputStream = pdfFile.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int bytesRead;
        byte[] data = new byte[1024];

        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        byte[] pdfBytes = buffer.toByteArray();
        inputStream.close();
        buffer.close();

        // 设置HTTP头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", pdfFile.getFilename());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    //@GetMapping("/pdf")
    //public ResponseEntity<ByteArrayResource> downloadPDF() throws IOException, DocumentException {
    //    // 生成 PDF 文件
    //    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //    Document document = new Document();
    //    PdfWriter.getInstance(document, baos);
    //    document.open();
    //    document.add(new Paragraph("Hello, World!"));
    //    document.close();
    //
    //    // 将 PDF 文件以二进制流的形式返回给前端
    //    ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
    //    return ResponseEntity.ok()
    //            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=myfile.pdf")
    //            .contentType(MediaType.APPLICATION_PDF)
    //            .contentLength(resource.contentLength())
    //            .body(resource);
    //}
}
