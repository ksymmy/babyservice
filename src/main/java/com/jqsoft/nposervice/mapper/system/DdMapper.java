package com.jqsoft.nposervice.mapper.system;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jqsoft.nposervice.entity.system.Dd;

@Mapper
public interface DdMapper {
	List<Dd> selectDdCloudData ();
	
	List<Dd> selectDdCloudDataMedium ();
	
	int updateStatusById (Map<String,Object> params);
	
	int updateStatusByMediumId (Map<String,Object> params);
}
