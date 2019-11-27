package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ExaminationInfoMapper {
    int deleteByPrimaryKey(@Param("id") Long id);

    int insert(ExaminationInfo record);

    int insertSelective(ExaminationInfo record);

    ExaminationInfo selectByPrimaryKey(@Param("id") Long id);

    int updateByPrimaryKeySelective(ExaminationInfo record);

    int updateByPrimaryKey(ExaminationInfo record);

    List<ExaminationInfo> myExaminationInfo(@Param("babyId") Long babyId);

    void batchInsert(List<ExaminationInfo> infos);

    List<ExaminationInfo> getNeedRemindExaminationInfoList(@Param("offset") int offset,
                                                           @Param("size") int size);

    void examinationConfirm(@Param("examinationId") Long examinationId);

    Map<String, Object> applyDelay(@Param("examinationId") Long examinationId);

    void confirmDelay(@Param("examinationId") Long examinationId,
                      @Param("delayDate") Date delayDate,
                      @Param("delayReason") String delayReason);

    void signIn(@Param("examinationId") Long examinationId);

    void deleteByBabyId(@Param("babyId") Long babyId);

    int updateDingTimes(@Param("examId") List<Long> examId);
}