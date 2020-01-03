package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.mapper.biz.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserService {
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private RedisUtils redisUtils;


    public int insertSelective(UserInfo userInfo) {
        return userInfoMapper.insertSelective(userInfo);
    }

    public int insert(UserInfo userInfo) {
        return userInfoMapper.insert(userInfo);
    }

    public int updateByPrimaryKeySelective(UserInfo userInfo) {
        String key = RedisKey.LOGIN_CORP_USER.getKey(userInfo.getCorpid(), userInfo.getUserid());
        redisUtils.remove(key);
        redisUtils.add(key, userInfo);
        return userInfoMapper.updateByPrimaryKeySelective(userInfo);
    }

    public UserInfo selectByPrimaryKey(Long id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    public UserInfo selectByCorpIdAndUserid(String corpid, String userid) {
        String key = RedisKey.LOGIN_CORP_USER.getKey(corpid, userid);
        UserInfo userInfo = (UserInfo) redisUtils.get(key);
        if (null != userInfo) {
            return userInfo;
        }
        userInfo = userInfoMapper.selectByCorpIdAndUserid(corpid, userid);
        if (null != userInfo) {
            redisUtils.add(key, userInfo);
        }
        return userInfo;
    }

    public int deleteByUserid(String userid) {
        return userInfoMapper.deleteByUserid(userid);
    }

    public UserInfo selectByCorpIdAndMobile(String mobile, String corpid) {
        return userInfoMapper.selectByCorpIdAndMobile(mobile, corpid);
    }
}

