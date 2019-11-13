package com.jqsoft.babyservice.entity.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date create_time;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8", locale = "zh")
    private Date update_time;

    public UserInfo() {
    }

    public UserInfo(Long id, String name, String mobile, String address, Byte active, Byte admin, String corpid, String userid, Date create_time, Date update_time) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.active = active;
        this.admin = admin;
        this.corpid = corpid;
        this.userid = userid;
        this.create_time = create_time;
        this.update_time = update_time;
    }
}