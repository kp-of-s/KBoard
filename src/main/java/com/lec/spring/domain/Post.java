package com.lec.spring.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Model 객체 (domain)

/**
 * DTO 객체
 *  : Data Transfer Object 라고도 함.
 *
 *  객체 -> DB
 *  DB -> 객체
 *  request -> 객체
 *  객체 -> response
 *  ..
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;
//    private String user;
    private String subject;
    private String content;
    private LocalDateTime regDate;
    private Long viewCnt;

    private User user; //작성자 (FK)

    //첨부파일 및 댓글까지 읽어오기
    @ToString.Exclude
    @Builder.Default    //lombok 빌더에서 제외. 이 경우 초깃값 쓸라고
    private List<Attachment> fileList = new ArrayList<>();
}
