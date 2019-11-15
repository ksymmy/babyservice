package com.jqsoft.babyservice.entity.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(value="handler")
public class BabyInfo implements Serializable {

    private Long id;

    private String corpid;

    private Long parentId;

    private String name;

    private Byte sex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date birthday;

    private String avatar;

    private Long fatherId;

    private String fatherName;

    private String fatherMobile;

    private Long motherId;

    private String motherName;

    private String motherMobile;

    private Byte state;

    private Date createTime;

    private Date updateTime;

    private String signIn;

    private List<ExaminationInfo> examinationInfos;


}