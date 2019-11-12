package com.jqsoft.nposervice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 公告接收人信息
 */
@Data
@TableName("biz_notice_receiver")
@NoArgsConstructor
public class NoticeReceiver extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String noticeId;

    private String userId;

    private Byte state;

    private Timestamp createTime;

    private Timestamp updateTime;
}