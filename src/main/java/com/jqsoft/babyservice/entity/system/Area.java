/**
 * Copyright (c) 2019-2025 jqsoft.net
 */
package com.jqsoft.babyservice.entity.system;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Area extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String id;

    /**
     * 区划编码
     */
    @TableId(type = IdType.UUID, value = "code")
    private String code;
    /**
     * 区划名称
     */
    private String name;
    /**
     * 长编码
     */
    private String longCode;
    /**
     * 长名称
     */
    private String longName;
    /**
     * 父编码
     */
    private String pCode;
    /**
     * 父名称
     */
    private String pName;
    /**
     * 区划等级
     */
    private String areaLevel;
    /**
     * 助记码
     */
    private String mnemonicCode;
    /**
     * 状态
     */
    private String status;
    /**
     * 备注1
     */
    private String remark;
    /**
     * 备注2
     */
    private String remark2;
    /**
     * 备注3
     */
    private String remark3;
    /**
     * 是否启用
     */
    private String isEnabled;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}


