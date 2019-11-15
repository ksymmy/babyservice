package com.jqsoft.babyservice.entity.biz;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WorkTime implements Serializable {
    private Long id;

    private String corpid;

    private Byte monday;

    private Byte tuesday;

    private Byte wednesday;

    private Byte thursday;

    private Byte friday;

    private Byte saturday;

    private Byte sunday;

    private Date createTime;

    private Date updateTime;


}