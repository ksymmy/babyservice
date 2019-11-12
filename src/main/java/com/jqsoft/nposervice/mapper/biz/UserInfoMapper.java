package com.jqsoft.nposervice.mapper.biz;

import org.apache.ibatis.annotations.Mapper;

import com.jqsoft.nposervice.entity.biz.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(String id);

    int updateByUseridSelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectUserInfo(UserInfo userInfo);

    List<UserInfo> selectUserList(Map<String, Object> params);

    void updateStatusById(UserInfo userInfo);

    List<UserInfo> selectUserListForStatus(Map<String, Object> params);

    void updateByPID(UserInfo userInfo);

    void updateByCID(UserInfo userInfo);

    UserInfo findByID(String id);

    List queryMemByCorpId(String corpid);

    BigDecimal queryDuesStand(HashMap<String, Object> params);

    void updateCurUserType(Map<String,Object> params);

    List<UserInfo> queryUserByOrgID(@Param("orgId") String orgId,
                                    @Param("userId") String userId);
    int deleteUserByUserid (String userid);
}