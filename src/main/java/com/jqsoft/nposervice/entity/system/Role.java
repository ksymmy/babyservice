/**
 * Copyright (c) 2006-2015 jqsoft.net
 */
package com.jqsoft.nposervice.entity.system;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

/**
 * 
 * 实体-角色
 * @author wangjie
 *
 */
@Data
@TableName("t_role")
@NoArgsConstructor
public class Role extends SuperEntity<String> {
	
	private static final long serialVersionUID = 1L;

    private String  code; // 角色编码
    private String  name;// 角色名称
    private String  pid;// 父ID
    private String  remark;// 角色描述
    private Integer status;// 状态^0正常1停用-1删除
    
}