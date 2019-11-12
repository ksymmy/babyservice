package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.WorkflowInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WorkflowInfoMapper {

    int deleteByPrimaryKey(String id);

    int insert(WorkflowInfo record);

    int insertSelective(WorkflowInfo record);

    WorkflowInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(WorkflowInfo record);

    int updateByPrimaryKey(WorkflowInfo record);

    WorkflowInfo selectWorkflowInfo(@Param("orgId") String orgId,
                                    @Param("flowCode") String flowCode);

    void save(WorkflowInfo workflow);
}