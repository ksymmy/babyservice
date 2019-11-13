package com.jqsoft.babyservice.entity.biz;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BabyInfo {
    private Long id;

    private String corpid;

    private String name;

    private Date birthday;

    private String avatar;

    private Long father_id;

    private Long mother_id;

    private Byte state;

    private Date create_time;

    private Date update_time;

}