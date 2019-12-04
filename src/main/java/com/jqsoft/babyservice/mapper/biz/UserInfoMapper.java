package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserInfoMapper {
    int deleteByPrimaryKey(@Param("id") Long id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(@Param("id") Long id);

    UserInfo selectByCorpIdAndMobile(@Param("mobile") String mobile,
                                     @Param("corpid") String corpid);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    UserInfo selectByCorpIdAndUserid(@Param("corpid") String corpid,
                                     @Param("userid") String userid);

    UserInfo getUserInfoByMobile(@Param("mobile") String mobile);

    int deleteByUserid(@Param("userid") String userid);
}