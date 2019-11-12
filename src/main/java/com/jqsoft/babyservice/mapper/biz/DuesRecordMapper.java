package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.DuesRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DuesRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(DuesRecord record);

    int insertSelective(DuesRecord record);

    DuesRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DuesRecord record);

    int updateByPrimaryKey(DuesRecord record);

    List<DuesRecord> queryDuesRecordList(@Param("offset") Integer offset,
                                         @Param("size") Integer size,
                                         @Param("param") Map param,
                                         @Param("orgId") String orgId);

    List<DuesRecord> notice(@Param("offset") Integer offset,
                            @Param("size") Integer size,
                            @Param("param") Map param,
                            @Param("orgId") String orgId);

    String getMaxEndDate(String userId);
}