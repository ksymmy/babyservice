package com.jqsoft.babyservice.entity.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class BabyInfo implements Serializable {

    private Long id;

    private String corpid;

    private String name;

    private Byte sex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date birthday;

    private String avatar;

    private Long fatherId;

    private Long motherId;

    private Byte state;

    private Date createTime;

    private Date updateTime;


}