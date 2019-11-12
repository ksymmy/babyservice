package com.jqsoft.nposervice.mapper.biz;

import org.apache.ibatis.annotations.Mapper;

import com.jqsoft.nposervice.entity.biz.OrgInfo;

@Mapper
public interface OrgInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(OrgInfo record);

    int insertSelective(OrgInfo record);

    OrgInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OrgInfo record);

    int updateByPrimaryKey(OrgInfo record);
    
    //通过授权企业id查询企业信息
    OrgInfo selectByCorpId(String corpId);
}