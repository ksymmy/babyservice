package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.mapper.biz.UserInfoMapper;
import com.jqsoft.babyservice.mapper.system.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserService {
    @Autowired(required = false)
    private UserInfoMapper userInfoMapper;
    @Autowired(required = false)
    private UserRoleMapper userRoleMapper;
    @Resource
    private RedisUtils redisUtils;

}

