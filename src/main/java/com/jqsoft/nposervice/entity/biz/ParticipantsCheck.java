package com.jqsoft.nposervice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 会议参加人员审核表
 */
@Data
@TableName("biz_participants_check")
@NoArgsConstructor
public class ParticipantsCheck extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String meetingId;

    private String userId;

    private Byte state;

    private String processInstanceId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8", locale = "zh")
    private Timestamp createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8", locale = "zh")
    private Timestamp updateTime;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String corpId;
}