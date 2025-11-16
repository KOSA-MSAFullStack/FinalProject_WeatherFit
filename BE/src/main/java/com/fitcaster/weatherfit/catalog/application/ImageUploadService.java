// ImageUploadService.java                                                                                                                                                                    │
// 이미지 업로드 서비스

package com.fitcaster.weatherfit.catalog.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

// * author: 김기성
@Service
public class ImageUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 이미지 서버의 로컬 파일 시스템에 업로드, 저장된 이미지의 웹 경로 반환
     *
     * @param image 업로드할 이미지 파일
     * @return 저장된 이미지의 웹 접근 경로 (예: /uploads/xxxxxxxx-xxxx.webp)
     * @throws IOException 이미지 처리 중 오류 발생 시
     */
    // itemCode를 인자로 받도록 변경
    public String uploadImage(MultipartFile image, String itemCode) throws IOException {
        if (image == null || image.isEmpty()) {
            return null;
        }

        logger.info("Image upload path configured: {}", uploadPath); // 디버깅용 로그

        String fileExtension = "";
        String originalFilename = image.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 파일명을 itemCode로 사용 (공백 제거)
        String cleanedItemCode = itemCode != null ? itemCode.trim() : "";
        String fileName = cleanedItemCode + fileExtension;

        File uploadDir = new File(uploadPath);
        logger.info("Resolved upload directory: {}", uploadDir.getAbsolutePath()); // 디버깅용 로그
        if (!uploadDir.exists()) {
            logger.warn("Upload directory does not exist, attempting to create: {}", uploadDir.getAbsolutePath()); // 디버깅용 로그
            uploadDir.mkdirs();
        }
        
        String filePath = Paths.get(uploadPath, fileName).toString(); // fileName 사용
        File dest = new File(filePath);

        logger.info("MultipartFile details - Original Filename: {}, Size: {} bytes, Content Type: {}", // 디버깅용 로그
                image.getOriginalFilename(), image.getSize(), image.getContentType());
        logger.info("Destination file path: {}", dest.getAbsolutePath()); // 디버깅용 로그
        logger.info("Destination directory exists: {}, isWritable: {}", uploadDir.exists(), uploadDir.canWrite()); // 디버깅용 로그
        logger.info("Destination file exists (before transfer): {}", dest.exists()); // 디버깅용 로그

        try {
            Files.copy(image.getInputStream(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Image successfully transferred to: {}", dest.getAbsolutePath()); // 디버깅용 로그
        } catch (IOException e) {
            logger.error("Failed to transfer image to {}: {}", dest.getAbsolutePath(), e.getMessage(), e); // 디버깅용 로그
            throw e;
        }

        return "/uploads/" + fileName; // fileName 사용
    }
}
