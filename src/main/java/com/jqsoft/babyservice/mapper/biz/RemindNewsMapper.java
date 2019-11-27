package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.RemindNews;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RemindNewsMapper {

    int insert(RemindNews record);

    void batchInsert(@Param("remindNewsList") List<RemindNews> remindNewsList);

    List<RemindNews> remindNewsList(@Param("offset") int offset,
                                    @Param("size") int size,
                                    @Param("userId") Long userId);

    void deleteByBabyId(@Param("babyId") Long babyId);

    RemindNews getWelcomeNewsByUserId(@Param("userId") Long userId);
}