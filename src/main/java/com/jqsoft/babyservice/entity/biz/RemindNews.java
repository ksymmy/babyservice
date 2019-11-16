package com.jqsoft.babyservice.entity.biz;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 提醒消息
 */
@Data
public class RemindNews implements Serializable {
    private Long id;

    private String corpid;

    private Long examinationId;

    private String title;

    private String context;

    private Byte type;

    private Byte signIn;

    private Byte confirm;

    private Date createTime;

    private Date updateTime;


}