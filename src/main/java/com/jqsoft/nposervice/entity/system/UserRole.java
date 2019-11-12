package com.jqsoft.nposervice.entity.system;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

/**
 * 用户角色
 *
 * @author ZMM
 */
@Data
@TableName("t_user_role")
@NoArgsConstructor
public class UserRole extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String userId;// 用户编号
    private String roleId;// 角色编号

}