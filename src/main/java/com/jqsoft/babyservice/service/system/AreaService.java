/**
 * Copyright (c) 2019-2025 jqsoft.net
 */
package com.jqsoft.babyservice.service.system;

import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.system.Area;
import com.jqsoft.babyservice.mapper.system.AreaMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AreaService
 *
 * @author generator
 * @version 1.0
 */
@Slf4j
@Service
public class AreaService {

    @Autowired(required = false)
    public AreaMapper areaMapper;

    public RestVo<List<Area>> selectAreaByPCode(String code) {
        List<Area> areaList = areaMapper.selectAreaByPCode(code);
        return RestVo.SUCCESS(areaList);
    }

    public Area selectAreaByCode(String code) {
        return areaMapper.selectAreaByCode(code);
    }

    public String getAreaName(String code) {
        Area entity = selectAreaByCode(code);
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }


   /* @Override
    public Area selectAreaByOrgId(String orgId) throws ManagerException {
        try {
            return this.baseMapper.selectAreaByOrgId(orgId);
        } catch (Exception e) {
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error(stackTraceElements[0].getClassName() + "类调用" + stackTraceElements[0].getMethodName() + "方法时在第" + stackTraceElements[0].getLineNumber() + "行发生异常！");
            e.printStackTrace();
            throw new ManagerException("100200", new String[]{"区域信息获取失败！"});
        }

    }*/

  /*  @Override
    public List<Area> queryAllPAreaByCode(String code) throws ManagerException {
        try {
            return this.baseMapper.queryAllPArea(code);
        } catch (Exception e) {
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error(stackTraceElements[0].getClassName() + "类调用" + stackTraceElements[0].getMethodName() + "方法时在第" + stackTraceElements[0].getLineNumber() + "行发生异常！");
            e.printStackTrace();
            throw new ManagerException("100200", new String[]{"根据区划编码查询其所有的父级区划！"});
        }
    }
*/
   /* @Override
    public PageResult<Area> selectAreaPage(Page<Area> page, Map<String, Object> params) throws ManagerException {
        try {
            return page.setRecords(this.baseMapper.queryAreaPage(page, params));
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error(stackTraceElements[0].getClassName() + "类调用" + stackTraceElements[0].getMethodName() + "方法时在第" + stackTraceElements[0].getLineNumber() + "行发生异常！");
            throw new ManagerException("100200", new String[]{"查询行政区划列表异常"});
        }
    }*/

   /* @Override
        public Boolean updateIsEnable(String code,String isEnabled) throws ManagerException {
        // 禁用启用参数
        boolean flag = false;
        User user = (User) ShiroUtils.getUser();
        Map<String,Object> param = new HashMap<>();
        param.put("code",code);
        param.put("updator",user.getId());
        param.put("updatorName",user.getUpdatorName());
        param.put("updateTime", new Date());
        if(ENABLE.equals(isEnabled)){
            param.put("isEnabled", UNABLE);
        }else{
            flag = true;
            param.put("isEnabled", ENABLE);
        }
         this.baseMapper.updateIsEnable(param);
        return flag;
    }*/


    /*@Override
    public List<Area> queryAreaByCodes(String[] codes) throws ManagerException {
        return this.baseMapper.queryAreaByCodes(codes);
    }*/

    /*@Override
    public String getAreaNameByAreaId (String areaId) throws ManagerException{
        Area area = this.selectOne(QueryWrapper.build().eq("code",areaId));
        return area.getName();
    }*/

}
