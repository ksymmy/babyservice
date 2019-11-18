package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.RemindNews;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RemindNewsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RemindNews record);

    int insertSelective(RemindNews record);

    RemindNews selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RemindNews record);

    int updateByPrimaryKey(RemindNews record);

    void batchInsert(@Param("remindNewsList") List<RemindNews> remindNewsList);

    List<RemindNews> remindNewsList(int offset, int size, Long userId);

    void deleteByBabyId(Long babyId);
}