package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.BabyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BabyInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BabyInfo record);

    int insertSelective(BabyInfo record);

    BabyInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BabyInfo record);

    int updateByPrimaryKey(BabyInfo record);

    List<Map> overListCount(Integer overdueStart, Integer overdueEnd, Integer dingTimes, String corpid);

    List<String> overdueDingUserid(Integer overdueStart, Integer overdueEnd, Integer dingTimes, Integer age, String corpid);

    List<Long> overdueDingExamids(Integer overdueStart, Integer overdueEnd, Integer dingTimes, Integer age, String corpid);

    List<Map> overdueList(@Param("offset") Integer offset,
                          @Param("size") Integer size,
                          @Param("param") Map param,
                          @Param("corpid") String corpid);

    List<Map> tomorrowExaminationBabysList(@Param("offset") Integer offset,
                                           @Param("size") Integer size,
                                           @Param("param") Map param,
                                           @Param("corpid") String corpid);

    List<Map> changeDateBabysList(@Param("offset") Integer offset,
                                  @Param("size") Integer size,
                                  @Param("param") Map param,
                                  @Param("corpid") String corpid);

    List<Map> allBabysList(@Param("offset") Integer offset,
                           @Param("size") Integer size,
                           @Param("param") Map param,
                           @Param("corpid") String corpid);

    Map getBabyParentInfo(Long id);

    Map<String, String> overdueCount(@Param("corpid") String corpid);

    Map<String, String> tomorrowExaminationBabysCount(@Param("corpid") String corpid);

    Map<String, String> changeDateBabysCount(@Param("corpid") String corpid);

    Map<String, String> allBabysCount(@Param("corpid") String corpid);

    List<BabyInfo> myBabys(@Param("parentId") Long parentId);

    BabyInfo getBabyInfoByExaminationId(Long examinationId);

}