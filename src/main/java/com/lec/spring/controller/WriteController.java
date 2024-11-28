package com.lec.spring.controller;

import com.lec.spring.domain.Write;
import com.lec.spring.domain.WriteValidator;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class WriteController {

    @RequestMapping("/write")
    public void write(){}

    // BindingResult 객체
    //   DispatcherServlet 에서 제공(자동주입) 해주는 객체
    //   validator 가 유효성 검사를 한 '결과' 가 담긴 객체
    @RequestMapping("/writeOk")
    @ResponseBody
    public String writeOk(@Valid Write write, //@Valid 오류 찾을 객체임을 명시
                          BindingResult result){
        System.out.println("writeOk() : " + write);
//        System.out.println("바인딩 에러 개수: " + result.getErrorCount());  // 바인딩 과정에 발생된 에러의 개수

        showError(result);

        return """
                id: %d<br>
                작성자: %s<br>
                <button onclick="history.back()">돌아가기</button>
                """.formatted(write.getId(), write.getName());
    }

    //바인딩 에러 정보 출력 도우미 메소드
    public void showError(Errors e){
        if(e.hasErrors()){ //에러가 있는가?
            System.out.println("에러 갯수: " + e.getErrorCount());

            List<FieldError> fe = e.getFieldErrors();
            System.out.println("\t[field]\t|[code]");
            for(FieldError ee : fe){
                System.out.println("\t" + ee.getField() + ": " + ee.getCode());
            }
        }else {
            System.out.println("만사형통 잘 굴러감");
        }
    }
    // 이 컨트롤러 클래스의 handler 에서  폼 데이터 를 바인딩할때 검증하는 Validator 객체 지정
    @InitBinder //컨트롤러 생성 될 때, 알아서 호출되도록 등록
    public void initBinder(WebDataBinder binder){
        binder.setValidator(new WriteValidator());
    }
}
