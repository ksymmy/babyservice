package com.jqsoft.babyservice.entity.biz;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ExaminationInfo {
    private Long id;

    private String corpid;

    private Long baby_id;

    private Date examination_date;

    private Byte examination_type;

    private String examination_item;

    private String change_date_reason;

    private Short overdue_days;

    private Short ding_times;

    private Byte sign_in;

    private Date create_time;

    private Date update_time;
}