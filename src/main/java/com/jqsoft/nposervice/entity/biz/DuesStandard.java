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
 * 会费缴纳标准
 */
@Data
@TableName("biz_dues_standard")
@NoArgsConstructor
public class DuesStandard extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String orgId;

    private Byte type;

    private String job;

    private BigDecimal money;

    @TableField(exist = false)
    private String moneyStr;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    private Timestamp updateTime;

    @TableField(exist = false)
    private String createTimeStr;
}