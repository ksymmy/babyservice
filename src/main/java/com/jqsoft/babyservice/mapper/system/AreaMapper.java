package com.jqsoft.babyservice.mapper.system;

import com.jqsoft.babyservice.entity.system.Area;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AreaMapper
 *
 * @author generator
 * @version 1.0
 */
@Mapper
public interface AreaMapper {

    /**
     * 通过当前区划编码的到所属区划
     *
     * @param code 当前区划编码
     * @return java.util.List<net.jqsoft.system.entity.Area>
     * @author melo、lh
     * @createTime 2019-07-17 12:00:47
     */
    List<Area> selectAreaByPCode(@Param("code") String code);

    /**
     * 根据机构ID获取机构所属的区划对象
     * @author zhaols
     * @createTime 2019-03-20 10:12
     * @param orgId 机构Id
     * @return net.jqsoft.system.entity.Area
     */
//    Area selectAreaByOrgId(String orgId);

    /**
     * 根据区划编码查询其所有的父级区划（不包括全国的）
     *
     * @author yangfan
     * @createTime 2019-03-28 05:23:17
     * @param code 区划编码
     * @return java.util.List<net.jqsoft.system.entity.Area>
     */
//    List<Area> queryAllPArea(String code);

    /**
     * 行政区划分页查询
     * @param page 分页对象
     * @param params 分页查询条件
     * @return java.util.List<net.jqsoft.system.entity.Area>
     * @author melo、lh
     * @createTime 2019-07-15 09:28:55
     */
//    List<Area> queryAreaPage(Page<Area> page, Map<String, Object> params);

    /**
     *  禁用启用
     * @param params  参数
     * @return void
     * @author melo、lh
     * @createTime 2019-07-15 17:56:36
     */
//    void updateIsEnable(Map<String, Object> params);


    /**
     * 通过区划编码集合获取行政区划数据
     *
     * @param codes
     * @return java.util.List<net.jqsoft.system.entity.Area>
     * @throws
     * @author sunlei
     * @createTime 2019/8/13 17:11
     */
//    List<Area> queryAreaByCodes(String[] codes);

    Area selectAreaByCode(String code);
}
