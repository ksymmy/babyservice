package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.mapper.biz.RemindNewsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 提醒消息service
 */
@Slf4j
@Service
public class RemindNewsService {

    @Resource
    private RemindNewsMapper remindNewsMapper;

    @Resource
    private RedisUtils redisUtils;


    /**
     * 家长端-获取我的消息列表
     * @param pageBo
     * @param userId
     * @return
     */
    public RestVo remindNewsList(PageBo<Map<String, Object>> pageBo, Long userId){
        return RestVo.SUCCESS(remindNewsMapper.remindNewsList(pageBo.getOffset(), pageBo.getSize(), userId));
    }



}
