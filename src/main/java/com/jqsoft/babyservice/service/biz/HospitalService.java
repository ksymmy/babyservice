package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.entity.biz.HospitalInfo;
import com.jqsoft.babyservice.mapper.biz.HospitalInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: created by ksymmy@163.com at 2019/11/29 10:03
 * @desc:
 */
@Slf4j
@Service
public class HospitalService {
    @Resource
    private HospitalInfoMapper hospitalInfoMapper;
    @Resource
    private RedisUtils redisUtils;

    public HospitalInfo selectBycorpid(String corpid) {
        String key = RedisKey.LOGIN_CORP.getKey(corpid);
        if (StringUtils.isNotBlank(corpid) && redisUtils.exists(key)) {
            return (HospitalInfo) redisUtils.get(key);
        }
        HospitalInfo hospitalInfo = hospitalInfoMapper.selectBycorpid(corpid);
        if (null != hospitalInfo) redisUtils.add(key, hospitalInfo, 7, TimeUnit.DAYS);
        return hospitalInfo;
    }

    public List<HospitalInfo> selectAll() {
        return hospitalInfoMapper.selectAll();
    }
}
