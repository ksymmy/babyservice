package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.NoticeReceiver;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeReceiverMapper {
    int deleteByPrimaryKey(String id);

    int insert(NoticeReceiver record);

    int insertSelective(NoticeReceiver record);

    NoticeReceiver selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(NoticeReceiver record);

    int updateByPrimaryKey(NoticeReceiver record);
}