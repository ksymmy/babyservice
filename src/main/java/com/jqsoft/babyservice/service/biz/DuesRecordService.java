package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.DuesRecord;
import com.jqsoft.babyservice.mapper.biz.DuesRecordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/9/16 8:12
 * @desc:
 */
@Service
public class DuesRecordService {

    @Resource
    private DuesRecordMapper duesRecordMapper;

    public RestVo queryDuesRecordList(PageBo<Map<String, Object>> pageBo, String orgId) {
        return RestVo.SUCCESS(duesRecordMapper.queryDuesRecordList(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(), orgId));
    }

    public RestVo dues(DuesRecord entity) {
        return RestVo.SUCCESS(duesRecordMapper.insert(entity));
    }

    public RestVo notice(PageBo<Map<String, Object>> pageBo, String orgId) {
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, 30);
//        Date warningDate = cal.getTime();
//        System.out.println(warningDate);
        return RestVo.SUCCESS(duesRecordMapper.notice(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(), orgId));
    }

    public RestVo getMaxEndDate(String userId) {
        return RestVo.SUCCESS(duesRecordMapper.getMaxEndDate(userId));
    }
}
