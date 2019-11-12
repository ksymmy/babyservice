package com.jqsoft.nposervice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 公告信息
 */
@Data
@TableName("biz_notice_info")
public class NoticeInfo extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String orgId;

    private String type;

    private String sourceId;

    private String title;

    private String context;

    private Byte state;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publish;

    private Timestamp createTime;

    private Timestamp updateTime;

    @TableField(exist = false)
    private String readState;//0:未读 1:已读

    public NoticeInfo() {
    }

    public NoticeInfo(String id, String orgId, String type, String sourceId, String title, String context, Byte state, Date publish, Timestamp createTime, Timestamp updateTime) {
        this.setId(id);
        this.orgId = orgId;
        this.type = type;
        this.sourceId = sourceId;
        this.title = title;
        this.context = context;
        this.state = state;
        this.publish = publish;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}