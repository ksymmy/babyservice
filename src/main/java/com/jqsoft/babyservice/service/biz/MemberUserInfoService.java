/**
 * FileName: MemberUserInfoService
 * Author:   Administrator
 * Date:     2019/9/11 13:39
 * Description: 会员信息维护
 * History:
 */
package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.mapper.biz.UserInfoMapper;
import com.jqsoft.babyservice.mapper.system.DictMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈会员信息维护〉
 *
 * @author DR
 * @create 2019/9/11
 * @since 1.0.0
 */
@Service
public class MemberUserInfoService {

    @Autowired(required = false)
    public UserInfoMapper userInfoMapper;
    @Autowired(required = false)
    private DictMapper dictMapper;
    @Resource
    private RedisUtils redisUtils;


    /**
     * 个人会员信息  保存
     *
     * @param userInfo
     * @return
     */
    public RestVo updateByPID(UserInfo userInfo) {
        if (StringUtils.isBlank(userInfo.getName()) && StringUtils.isBlank(userInfo.getPhome()) && StringUtils.isBlank(userInfo.getEmail())
                && StringUtils.isBlank(userInfo.getIdnumber()) && StringUtils.isBlank(userInfo.getIdtype())) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        String key = RedisKey.USER_INFO.getKey(userInfo.getId());
        UserInfo userInfoK = (UserInfo) redisUtils.get(key);
        if (null != userInfoK) {
            redisUtils.remove(RedisKey.USER_INFO.getKey(userInfo.getId()));
        }
        userInfoMapper.updateByPID(userInfo);
        return RestVo.SUCCESS();
    }

    /**
     * 单位会员信息 保存
     *
     * @param userInfo
     * @return
     */
    public RestVo updateByCID(UserInfo userInfo) {
        if (StringUtils.isBlank(userInfo.getName()) && StringUtils.isBlank(userInfo.getPhome()) && StringUtils.isBlank(userInfo.getEmail())
                && StringUtils.isBlank(userInfo.getIdnumber()) && StringUtils.isBlank(userInfo.getProvinceId()) && StringUtils.isBlank(userInfo.getCityId())
                && StringUtils.isBlank(userInfo.getCountyId()) && StringUtils.isBlank(userInfo.getDetail()) && (userInfo.getEstablishDate() != null)
                && StringUtils.isBlank(userInfo.getIdtype()) && StringUtils.isBlank(userInfo.getOrgType()) && StringUtils.isBlank(userInfo.getIndustry())
                && StringUtils.isBlank(userInfo.getAddress())) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        } else if (CommUtils.checkPhone(userInfo.getPhome()) && CommUtils.checkEmail(userInfo.getEmail()) && CommUtils.checkIdnumbe(userInfo.getIdnumber())) {
            return RestVo.FAIL(ResultMsg.ILLEGAL_PARAM_FORMAT);
        }
        String key = RedisKey.USER_INFO.getKey(userInfo.getId());
        UserInfo userInfoK = (UserInfo) redisUtils.get(key);
        if (null != userInfoK) {
            redisUtils.remove(RedisKey.USER_INFO.getKey(userInfo.getId()));
        }
        userInfoMapper.updateByCID(userInfo);
        return RestVo.SUCCESS();
    }

    /**
     * 会员信息 获取 (个人/单位)
     *
     * @param id
     * @return
     */
    public RestVo<UserInfo> findByID(String id) {
        String key = RedisKey.USER_INFO.getKey(id);
        UserInfo userInfo = (UserInfo) redisUtils.get(key);
        if (null == userInfo) {
            userInfo = userInfoMapper.findByID(id);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            userInfo.setEstablishDateStr(userInfo.getEstablishDate() == null ? "" : simpleDateFormat.format(userInfo.getEstablishDate()));
            redisUtils.add(key, userInfo);
        }
        return RestVo.SUCCESS(userInfo);
    }


    public RestVo queryMemByCorpId(String corpid) {
        return RestVo.SUCCESS(userInfoMapper.queryMemByCorpId(corpid));
    }

    public RestVo queryDuesStand(HashMap<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>() {{
            put("money", userInfoMapper.queryDuesStand(params));
            put("jobName", dictMapper.selectById((String) params.get("type")).getName());

        }};
        return RestVo.SUCCESS(result);
    }
}
