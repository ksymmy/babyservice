package com.jqsoft.nposervice.mapper.system;

import com.jqsoft.nposervice.entity.system.Dict;
import net.jqsoft.persist.mybatisplus.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * mapper-字典
 * </p>
 *
 * @author wangjie
 */
@Mapper
public interface DictMapper extends SuperMapper<Dict> {

    List<Dict> getDictListByCode(@Param("code") String code);

    List<Dict> getDictListById(String id);
}