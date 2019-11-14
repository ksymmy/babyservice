package com.jqsoft.babyservice.entity.biz;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfo implements Serializable {
    private Long id;

    private String name;

    private String mobile;

    private String address;

    private Byte active;

    private Byte admin;

    private String corpid;

    private String userid;

    private Date createTime;

    private Date updateTime;

    public UserInfo() {
    }

    public UserInfo(Long id, String name, String mobile, String address, Byte active, Byte admin, String corpid, String userid, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.active = active;
        this.admin = admin;
        this.corpid = corpid;
        this.userid = userid;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}