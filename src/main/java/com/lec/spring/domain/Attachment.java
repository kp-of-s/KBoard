package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {
    public Long id;
    private Long Post_id;       //어느 글의 첨부파일?
    public String sourcename;   //원본 파일명
    public String filename;     //저장된 파일명
    private boolean isImage;    //이미지 파일 맞나
}
