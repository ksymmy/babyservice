package com.jqsoft.nposervice.service.biz;

import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.MeetingNoticeTemplate;
import com.jqsoft.nposervice.mapper.biz.MeetingNoticeTemplateMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/9/20 14:11
 * @desc:
 */
@Service
public class MeetingNoticeTemplateService {

    @Resource
    private MeetingNoticeTemplateMapper templateMapper;

    public RestVo selectList(String orgId) {
        return RestVo.SUCCESS(templateMapper.selectList(orgId));
    }

    public RestVo insert(MeetingNoticeTemplate template) {
        return RestVo.SUCCESS(templateMapper.insert(template));
    }

    public int queryUnique(Map<String, Object> params) {
        return templateMapper.queryUnique(params);
    }
}
