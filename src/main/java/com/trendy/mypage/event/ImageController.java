package com.trendy.mypage.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 저장 경로
            Path path = Paths.get(uploadDir, file.getOriginalFilename());
            File destination = path.toFile();

            // 디렉토리가 없으면 생성
            if (!destination.getParentFile().exists()) {
                destination.getParentFile().mkdirs();
            }

            // 파일 저장
            file.transferTo(destination);

            // 반환 URL
            return "/images/" + file.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error uploading file: " + e.getMessage();
        }
    }
}
