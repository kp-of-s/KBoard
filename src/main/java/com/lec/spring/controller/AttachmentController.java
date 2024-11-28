package com.lec.spring.controller;

import com.lec.spring.domain.Attachment;
import com.lec.spring.service.AttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController //파일 다운로드, 데이터 response. 컨트롤러+리스폰스바디
public class AttachmentController {

    @Value("${app.upload.path}")  // org.springframework.beans.factory.annotation.Value
    private String uploadDir;


    private final AttachmentService attachmentService;


    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
        System.out.println(getClass().getName() + "() 생성");
    }


    // 파일 다운로드
    // id: 첨부파일의 id
    // ResponseEntity<T> 를 사용하여
    // '직접' Response data 를 구성

    @RequestMapping("/board/download")
    public ResponseEntity<?> download(Long id) {
        if(id == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        Attachment file = attachmentService.findById(id);
        if(file == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        String sourceName = file.getSourcename();
        String filename = file.getFilename();

        String path = new File(uploadDir, filename).getAbsolutePath(); //저장파일 결대경로

        try{
        //파일 유형 추출
            String mimeType = Files.probeContentType(Paths.get(path));

        //지정되지 않은 유형 처리
            if(mimeType == null){
                mimeType = "application/octet-stream"; // 일련의 8bit 스트림 타입.  유형이 알려지지 않은 파일에 대한 형식 지정
            }

        //Response body 준비
            Path filePath = Paths.get(path);
            Resource resource
                    //리소스   < 인풋스트림 < 저장 파일
                    = new InputStreamResource(Files.newInputStream(filePath));

        //Response head 세팅
            HttpHeaders headers = new HttpHeaders();
            // ↓ 원본 파일 이름(sourceName) 으로 다운로드 하게 하기위한 세팅
            //   반.드.시 URL 인코딩해야 함
            // ex) Content-Disposition: filename="filename.jpg"
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename(URLEncoder.encode(sourceName, "utf-8"))
                            .build());
            headers.setCacheControl("no-cache");
            headers.setContentType(MediaType.parseMediaType(mimeType)); //유형지정

        //Response entity 리턴
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IOException e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); //500에러
        }
    }

}