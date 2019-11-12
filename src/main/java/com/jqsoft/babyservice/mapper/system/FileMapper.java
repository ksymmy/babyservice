package com.jqsoft.babyservice.mapper.system;

import com.jqsoft.babyservice.entity.system.FileEntity;
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
public interface FileMapper extends SuperMapper<FileEntity> {

    List<FileEntity> selectByRecordIdAndType(@Param("recordId") String recordid, @Param("type") Byte type);

    void saveBatch(List<FileEntity> files);
}