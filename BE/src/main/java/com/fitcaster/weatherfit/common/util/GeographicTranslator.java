package com.fitcaster.weatherfit.common.util;

import org.springframework.stereotype.Component;

/**
 * 위도(lat), 경도(lng)를 기상청 단기예보 API에서 사용하는
 * 격자 좌표(nx, ny)로 변환하는 유틸리티 클래스
 * (기상청에서 공식으로 제공하는 변환식)
 * @author 김경아
 */
@Component
public class GeographicTranslator {

    private static final double RE = 6371.00877; // 지구 반경(km)
    private static final double GRID = 5.0; // 격자 간격(km)
    private static final double SLAT1 = 30.0;
    private static final double SLAT2 = 60.0;
    private static final double OLON = 126.0;
    private static final double OLAT = 38.0;
    private static final double XO = 43;
    private static final double YO = 136;

    public int[] convert(double lat, double lng) {
        double DEGRAD = Math.PI / 180.0;
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lng * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int y = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return new int[]{x, y};
    }
}
