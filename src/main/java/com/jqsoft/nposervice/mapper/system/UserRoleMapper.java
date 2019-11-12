package com.jqsoft.nposervice.mapper.system;

import com.jqsoft.nposervice.entity.system.UserRole;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper {
    void insertUserRole(UserRole entity);
    
    List<UserRole> selectUserRoleByUserId(String userId);
    
    void updateRoleIdByUserIdAndCurUserType(Map<String,Object> params);
    
    int deleteUserRoleByUserId (String userId);
}
