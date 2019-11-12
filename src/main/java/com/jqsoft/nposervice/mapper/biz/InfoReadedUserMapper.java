package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.InfoReadedUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InfoReadedUserMapper {
    int deleteByPrimaryKey(String id);

    int insert(InfoReadedUser record);

    int insertSelective(InfoReadedUser record);

    InfoReadedUser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(InfoReadedUser record);

    int updateByPrimaryKey(InfoReadedUser record);

    int deleteByInfoId(String InfoId);
}