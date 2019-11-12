package com.jqsoft.nposervice.entity.system;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

/**
 * 
 * 角色-资源
 * @author wangjie
 *
 */
@Data
@TableName("t_role_res")
@NoArgsConstructor
public class RoleRes extends SuperEntity<String> {
	
	private static final long serialVersionUID = 1L;

	private String roleId;// 角色编号
	private String resId;// 菜单编号
	
}