package com.jqsoft.babyservice.entity.biz;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HospitalNoticeTemplate implements Serializable {
    private Long id;

    private String corpid;

    /**
     * 扩展字段
     */
    private String extra;

    private Date createTime;

    private Date updateTime;

    private String et1;

    private String et3;

    private String et6;

    private String et8;

    private String et12;

    private String et18;

    private String et24;

    private String et30;

    private String et36;

    private String etall;

}