package com.jqsoft.babyservice.entity.biz;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class UserInfo {
    private Long id;

    private String name;

    private String mobile;

    private String adress;

    private Byte active;

    private Byte amdin;

    private String corpid;

    private String userid;

    private Date create_time;

    private Date update_time;

}