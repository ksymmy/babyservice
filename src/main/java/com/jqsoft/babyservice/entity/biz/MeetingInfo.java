package com.jqsoft.babyservice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.List;

/**
 * 会议信息表
 */
@Data
@TableName("biz_meeting_info")
@NoArgsConstructor
public class MeetingInfo extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String orgId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8", locale = "zh")
    private Timestamp meetingStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8", locale = "zh")
    private Timestamp meetingEnd;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8", locale = "zh")
    private Timestamp enrollStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8", locale = "zh")
    private Timestamp enrollEnd;

    private String provinceId;

    private String cityId;

    private String countyId;

    private String detail;

    private String address;

    private String name;

    private String sponsor;

    private String type;

    private String context;

    private Byte state;

    private String summary;

    private String createUserId;

    private String createUserName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Timestamp createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Timestamp updateTime;
    @TableField(exist = false)
    private String readState;//0:未读 1:已读
    @TableField(exist = false)
    private String checkState;//-1：未报名  0：未审核  1：审核通过  2：审核不通过 3：已取消参会
    @TableField(exist = false)
    private String enrollState;//0 ： 不能报名  1 ：报名
    @TableField(exist = false)
    private int participantNum;//参会人数
    @TableField(exist = false)
    private List<String> participantNames;//参会人姓名(按报名顺序)
    @TableField(exist = false)
    private String canParticipateIds;//可参会人员id
    @TableField(exist = false)
    private String canParticipateNames;//可参会人员姓名
}