package com.jqsoft.babyservice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 会议通知消息模板
 */
@Data
@NoArgsConstructor
@TableName("biz_meeting_notice_template")
public class MeetingNoticeTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String orgId;

    private String name;

    private String title;

    private String context;

    private Byte sendNotice;

    private Byte sendEmail;

    private Byte sendDingMsg;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Timestamp createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Timestamp updateTime;
}