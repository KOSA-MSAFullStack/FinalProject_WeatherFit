// ImageUploadService.java
// 이미지 업로드 서비스

package com.fitcaster.weatherfit.catalog.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

// * author: 김기성
@Service
public class ImageUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 이미지 서버의 로컬 파일 시스템에 업로드, 저장된 이미지의 웹 경로 반환
     *
     * @param image 업로드할 이미지 파일
     * @return 저장된 이미지의 웹 접근 경로 (예: /uploads/xxxxxxxx-xxxx.webp)
     * @throws IOException 이미지 처리 중 오류 발생 시
     */
    public String uploadImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        // 고유한 파일명 생성
        String originalFilename = image.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 저장 경로 설정
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String filePath = Paths.get(uploadPath, uniqueFileName).toString();
        File dest = new File(filePath);

        // 파일 저장
        image.transferTo(dest);

        // 웹 접근 경로 반환
        return "/uploads/" + uniqueFileName;
    }
}
