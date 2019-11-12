package com.jqsoft.nposervice.mapper.system;

import com.jqsoft.nposervice.entity.system.Resources;
import net.jqsoft.persist.mybatisplus.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * mapper-资源
 * </p>
 * 
 * @author wangjie
 *
 */
@Mapper
public interface ResourcesMapper{

    List<String> getAllResourcesJumpByUserId(@Param("userId") String userId);
}