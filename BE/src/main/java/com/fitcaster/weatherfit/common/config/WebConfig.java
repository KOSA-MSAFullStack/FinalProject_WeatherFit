// WebConfig.java
// 웹 관련 설정

package com.fitcaster.weatherfit.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// * author: 김기성
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 정적 리소스 핸들러 추가, 특정 URL 경로 요청을 파일 시스템의 특정 디렉토리와 매핑
     * 외부에서 업로드된 파일에 접근 가능
     *
     * @param registry 리소스 핸들러를 등록하는 데 사용되는 레지스트리
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /BE/uploads/** 경로로 들어오는 요청을
        // 현재 작업 디렉토리(프로젝트 루트) 아래의 uploadPath(BE/uploads/) 폴더와 매핑
        // 예: /BE/uploads/image.jpg -> file:./BE/uploads/image.jpg
        registry.addResourceHandler("/BE/uploads/**")
                .addResourceLocations("file:./" + uploadPath);
        
        // 하위 호환성 유지 위해 /uploads/** 패턴도 동일하게 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./" + uploadPath);
    }
}
