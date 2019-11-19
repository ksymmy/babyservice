package com.jqsoft.babyservice.entity.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 提醒消息
 */
@Data
public class RemindNews implements Serializable {
    private Long id;

    private Long examinationId;

    private String title;

    private String context;

    private Byte type; // 类型（0：提前提醒 1：当天签到  2：逾期提醒）'

    private Byte signIn; // '是否已签到（0：未签到 1：已签到）'

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date updateTime;

    private String hospitalName;

    private String newsTime;// 格式（当天：HH:mm  前一天：昨天 HH:mm  昨天之前： XX月XX日 HH:mm 不是今年：XX年XX月XX日 HH:mm）

    private Byte confirm;


}