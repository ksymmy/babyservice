package com.jqsoft.nposervice.controller.biz;

import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.interceptor.AuthCheck;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.controller.system.BaseController;
import com.jqsoft.nposervice.entity.biz.DuesStandard;
import com.jqsoft.nposervice.service.biz.DuesStandardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import static com.jqsoft.nposervice.commons.constant.ResultMsg.STANDARDMESS_MORE;
import static com.jqsoft.nposervice.commons.constant.ResultMsg.STANDARDMESS_ONE;

@Slf4j
@RestController
@AuthCheck
@RequestMapping("duesStandard")
public class DuesStandardController extends BaseController {

    @Autowired
    public DuesStandardService duesStandardService;

    @Autowired
    public RedisUtils redisUtils;

    @PostMapping("/saveOrUpdate")
    public RestVo saveOrUpdate(@RequestBody DuesStandard entity) {
        String orgId = this.getOrgId();
        entity.setOrgId(orgId);
        if (StringUtils.isBlank(entity.getId())) {
            HashMap<String, Object> params = new HashMap<String, Object>() {{
                put("job", entity.getJob());
                put("orgId", entity.getOrgId());
            }};
            List<DuesStandard> dataList = duesStandardService.selectByType(params);
            if (dataList.size() > 0) {
                return RestVo.FAIL(STANDARDMESS_ONE);
            }
        } else {
            HashMap<String, Object> params = new HashMap<String, Object>() {{
                put("job", entity.getJob());
                put("orgId", entity.getOrgId());
            }};
            List<DuesStandard> dataList = duesStandardService.selectByType(params);
            if (dataList.size() > 1) {
                return RestVo.FAIL(STANDARDMESS_MORE);
            } else if (dataList.size() == 1) {
                if (!entity.getId().equals(dataList.get(0).getId())) {
                    return RestVo.FAIL(STANDARDMESS_ONE);
                }
            }
        }
        log.info(String.valueOf(entity));
        RestVo restVo = duesStandardService.saveOrUpdate(entity);
        return restVo;
    }

    @RequestMapping("/list")
    public RestVo selectList(int currentPageIndex, int pageSize,Integer type) {
        String orgId = this.getOrgId();
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("orgId", orgId);
            put("type", type);
            put("currentPageIndex", currentPageIndex);
            put("pageSize", pageSize);
        }};
        RestVo restVo = duesStandardService.selectList(params);
        return restVo;
    }

    @RequestMapping("/selectInfo")
    public RestVo selectInfo(String id) {
        log.info(String.valueOf(id));
        RestVo restVo = duesStandardService.selectInfo(id);
        log.info(String.valueOf(restVo));
        return restVo;
    }


    @RequestMapping("/deleteByid")
    public RestVo deleteByid(String id) {
        duesStandardService.deleteById(id);
        return RestVo.SUCCESS();
    }
}
