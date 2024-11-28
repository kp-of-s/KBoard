package com.lec.spring.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("### 로그아웃 성공 : CustomLogoutSuccessHandler 동작 ###");

        // 로그아웃 시간 남기기
                LocalDateTime logoutTime = LocalDateTime.now();
                System.out.println("로그아웃시간: " + logoutTime);

        // 사용시간 (로그인 ~ 로그아웃) 계산해보기
        LocalDateTime loginTime = (LocalDateTime) request.getSession().getAttribute("loginTime");

        if(loginTime != null) {
            long seconds = loginTime.until(logoutTime, ChronoUnit.SECONDS);
            System.out.println("사용시간: " + seconds + " 초");
        }

        request.getSession().invalidate();

        String redirectUrl = "/user/login?logouthendler";

        //만약 리턴하고자하는 URL이 있을 경우, 해당 URL로 리다렉
        if (request.getParameter("ret_url") != null){
            redirectUrl = request.getParameter("ret_url");
        }

        response.sendRedirect(redirectUrl);
    }
}
