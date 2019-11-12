package com.jqsoft.nposervice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 会费缴纳记录
 */
@Data
@TableName("biz_dues_record")
@NoArgsConstructor
public class DuesRecord extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String orgId;

    private String userId;

    private Byte years;

    private BigDecimal money;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date endDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date payDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Timestamp createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Timestamp updateTime;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private Byte state; //0 未缴费,-1:未到预警期内,1 在预警期内 黑色字体显示,2 过了预警期 红色字体显示
}