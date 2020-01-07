package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.HospitalNoticeTemplate;
import com.jqsoft.babyservice.mapper.biz.HospitalNoticeTemplateMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: created by ksymmy@163.com at 2020/1/6 19:50
 * @desc:
 */
@Service
public class HospitalNoticeTemplateService {
    @Resource
    private HospitalNoticeTemplateMapper hospitalNoticeTemplateMapper;
    @Resource
    private RedisUtils redisUtils;

    public RestVo selectByCorpid(String corpid) {
        String key = RedisKey.LOGIN_CORP_NOTICE_TEMP.getKey(corpid);
        if (redisUtils.exists(key)) {
            return RestVo.SUCCESS(redisUtils.get(key));
        }
        String baseKey = RedisKey.LOGIN_CORP_NOTICE_TEMP.getKey("corpid");
        if (!redisUtils.exists(baseKey)) {
            redisUtils.add(baseKey, hospitalNoticeTemplateMapper.selectByCorpid("corpid"));
        }
        HospitalNoticeTemplate base = (HospitalNoticeTemplate) redisUtils.get(baseKey);
        HospitalNoticeTemplate value = hospitalNoticeTemplateMapper.selectByCorpid(corpid);
        if (null == value) {
            value = new HospitalNoticeTemplate();
        }
        value.setCorpid(corpid);
        if (StringUtils.isBlank(value.getEt1())) {
            value.setEt1(base.getEt1());
        }
        if (StringUtils.isBlank(value.getEt3())) {
            value.setEt3(base.getEt3());
        }
        if (StringUtils.isBlank(value.getEt6())) {
            value.setEt6(base.getEt6());
        }
        if (StringUtils.isBlank(value.getEt8())) {
            value.setEt8(base.getEt8());
        }
        if (StringUtils.isBlank(value.getEt12())) {
            value.setEt12(base.getEt12());
        }
        if (StringUtils.isBlank(value.getEt18())) {
            value.setEt18(base.getEt18());
        }
        if (StringUtils.isBlank(value.getEt24())) {
            value.setEt24(base.getEt24());
        }
        if (StringUtils.isBlank(value.getEt30())) {
            value.setEt30(base.getEt30());
        }
        if (StringUtils.isBlank(value.getEt36())) {
            value.setEt36(base.getEt36());
        }
        if (StringUtils.isBlank(value.getEtall())) {
            value.setEtall(base.getEtall());
        }
        redisUtils.add(key, value);
        return RestVo.SUCCESS(value);
    }

    public RestVo updateByCorpid(String corpid, String et, String text) {
        hospitalNoticeTemplateMapper.updateByCorpid(corpid, et, text);
        redisUtils.remove(RedisKey.LOGIN_CORP_NOTICE_TEMP.getKey(corpid));
        return RestVo.SUCCESS();
    }
}
