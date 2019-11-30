package com.jqsoft.babyservice.entity.biz;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class HospitalInfo implements Serializable {
    private Long id;

    private String name;

    private String mobile;

    private String address;

    private String corpid;

    private String appKey;

    private String appSecret;

    private String agentId;

    private Date createTime;

    private Date updateTime;

}