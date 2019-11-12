package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.NoticeInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(NoticeInfo record);

    int insertSelective(NoticeInfo record);

    NoticeInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(NoticeInfo record);

    int updateByPrimaryKey(NoticeInfo record);
}