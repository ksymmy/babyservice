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


}