package com.jqsoft.nposervice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

import java.util.Date;

/**
 * 流程信息
 */
@Data
@NoArgsConstructor
public class WorkflowInfo extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String orgId;

    private String flowName;

    private String flowCode;

    private String processCode;

    private Date createTime;


}