package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.utils.DateUtil;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.RemindNews;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.mapper.biz.RemindNewsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 提醒消息service
 */
@Slf4j
@Service
public class RemindNewsService {

    @Resource
    private RemindNewsMapper remindNewsMapper;

    @Value("${hospitalName}")
    public String hospitalName;

    @Value("${welcomeNewsTitle}")
    public String welcomeNewsTitle;

    @Value("${welcomeNewsContext}")
    public String welcomeNewsContext;

    /**
     * 家长端-获取我的消息列表
     * @param pageBo
     * @param userId
     * @return
     */
    public RestVo remindNewsList(PageBo<Map<String, Object>> pageBo, Long userId){
        if (null == pageBo || null == userId) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        List<RemindNews> remindNewsList = remindNewsMapper.remindNewsList(pageBo.getOffset(), pageBo.getSize(), userId);
        if (CollectionUtils.isNotEmpty(remindNewsList)) {
            for (RemindNews news : remindNewsList) {
                news.setHospitalName(hospitalName);
                // 格式化消息日期
                news.setNewsTime(DateUtil.formatDdNewsDate(news.getCreateTime()));
            }
        }
        return RestVo.SUCCESS(remindNewsList);
    }

    /**
     * 初始化家长端欢迎信息
     * @param userInfo
     */
    public void addWelcomeNews(UserInfo userInfo){
        if (null != userInfo && 0 == userInfo.getAdmin()) {
            RemindNews news = remindNewsMapper.getWelcomeNewsByUserId(userInfo.getId());
            if (null == news) {
                Date now = new Date();
                news = new RemindNews();
                news.setUserId(userInfo.getId());
                news.setCreateTime(now);
                news.setUpdateTime(now);
                news.setTitle(welcomeNewsTitle);
                news.setContext(welcomeNewsContext);
                news.setNewsType((byte)-1);
                remindNewsMapper.insert(news);
            }
        }
    }
}
