package com.jqsoft.babyservice.entity.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class ExaminationInfo implements Serializable {
    private Long id;

    private String corpid;

    private Long babyId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date examinationDate;

    private Byte examinationType;

    private String examinationItem;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date oldExaminationDate;

    private String changeDateReason;

    private Short overdueDays;

    private Short dingTimes;

    private Byte signIn;

    private Date createTime;

    private Date updateTime;


}