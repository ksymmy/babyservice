package com.jqsoft.babyservice.entity.biz;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class BabyInfo implements Serializable {
    private Long id;

    private String corpid;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date birthday;

    private String avatar;

    private Long father_id;

    private Long mother_id;

    private Byte state;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date create_time;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8", locale = "zh")
    private Date update_time;

}