package com.lec.spring.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    // SavedRequestAwareAuthenticationSuccessHandler#setDefaultTargetUrl()
    // 로그인후 특별히 redirect 할 url 이 없는경우 기본적으로 redirect 할 url(현 상태가 디폴트)
    public CustomLoginSuccessHandler(String defaultTargetUrl){
        setDefaultTargetUrl(defaultTargetUrl);
    }

    //로그인 성공 직후 수행할 동작. 로그인 시간 분석 등에 사용
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        System.out.println("로그인 성공: onAuthenticationSuccess() 호출");
        System.out.println("접속 ip" + getClientIp(request));
        PrincipalDetails userDetail = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("username: " + userDetail.getUsername());
        System.out.println("password: " + userDetail.getPassword());
        System.out.println("authorities" + authentication.getAuthorities().stream().toList());

        //로그인 시간 세션에 저장(로그아웃 시 꺼내서 사용)
        LocalDateTime loginTime = LocalDateTime.now();
        System.out.println("login time: " + loginTime);
        //request에서 세션 뽑아오기
        request.getSession().setAttribute("loginTime", loginTime);

        //로그인 직전 URL로 리다이렉트
        super.onAuthenticationSuccess(request, response, authentication);

        //AuthenticationSuccessHandler(I)
        // └─ SavedRequestAwareAuthenticationSuccessHandler
        //    https://docs.spring.io/spring-security/site/docs/4.0.x/apidocs/org/springframework/security/web/authentication/SavedRequestAwareAuthenticationSuccessHandler.html

    }

    // request 를 한 client ip 가져오기
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
