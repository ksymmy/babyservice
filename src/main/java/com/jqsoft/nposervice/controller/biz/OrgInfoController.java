package com.jqsoft.nposervice.controller.biz;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jqsoft.nposervice.commons.interceptor.AuthCheck;
import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.controller.system.BaseController;
import com.jqsoft.nposervice.entity.biz.OrgInfo;
import com.jqsoft.nposervice.service.biz.OrgInfoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("orgInfoController")
public class OrgInfoController extends BaseController {
    @Autowired
    public OrgInfoService orgInfoService;

    @Autowired
    public RedisUtils redisUtils;


    @AuthCheck
    @RequestMapping("/selectOrgInfo")
    public RestVo selectOrgInfo() {
        String orgId = this.getOrgId();
        log.info(String.valueOf(orgId));
        RestVo restVo = orgInfoService.selectOrgInfo(orgId);
        log.info(String.valueOf(restVo));
        return restVo;
    }

    @AuthCheck
    @PostMapping("/saveOrUpdate")
    public RestVo saveOrUpdate(@RequestBody OrgInfo entity) {
        if(StringUtils.isNotBlank(entity.getEstablishDateStr())){
            entity.setEstablishDate(CommUtils.convertStringToDate(entity.getEstablishDateStr(),null));
        }
        log.info(String.valueOf(entity));
        RestVo restVo = orgInfoService.saveOrUpdate(entity);
        return restVo;
    }

}
