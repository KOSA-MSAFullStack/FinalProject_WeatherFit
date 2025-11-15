//package com.fitcaster.weatherfit.common.config.image;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 실제 경로
//        String uploadPath = "file:///C:/Users/82107/Desktop/FinalProject_WeatherFit/uploads/";
//
//        // 프로젝트 실행 경로 기준 ./uploads/ 폴더를
//        // http://localhost:8080/uploads/** 로 매핑
//        registry.addResourceHandler("/uploads/**") // 브라우저에서 접근할 URL 패턴
//                .addResourceLocations("file:../uploads/");      // 실제 서버의 폴더 위치 (프로젝트 루트 기준)
////                .addResourceLocations(uploadPath);      // 실제 서버의 폴더 위치 (프로젝트 루트 기준)
//    }
//}
