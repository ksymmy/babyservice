package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.mapper.biz.BabyInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class BabyService {

    @Resource
    private BabyInfoMapper babyInfoMapper;

    public RestVo overdueList(PageBo<Map<String, Object>> pageBo, String corpid) {
        return RestVo.SUCCESS(babyInfoMapper.overdueList(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(), corpid));
    }

    public RestVo getBabyParentInfo(Long id) {
        return RestVo.SUCCESS(babyInfoMapper.getBabyParentInfo(id));
    }
}
