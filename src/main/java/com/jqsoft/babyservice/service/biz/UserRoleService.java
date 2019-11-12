package com.jqsoft.babyservice.service.biz;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jqsoft.babyservice.mapper.system.UserRoleMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserRoleService {
	@Autowired
    private UserRoleMapper userRoleMapper;
	
	public void updateRoleIdByUserIdAndCurUserType (String userId,String curUserType) {
		if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(curUserType) ){
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userId", userId);
			if("0".equals(curUserType) || "1".equals(curUserType)){		//组织管理员
				params.put("roleId", "61af6388d38142bd9baa0362c1c2b9fc");
			}else if ("2".equals(curUserType) || "3".equals(curUserType)) {	//组织会员
				params.put("roleId", "023d8fc384c94377b95d3da6c74d5260");
			}
			userRoleMapper.updateRoleIdByUserIdAndCurUserType(params);
		}
		return;
	}
	
	
}
