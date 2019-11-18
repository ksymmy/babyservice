package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.utils.LunarSolarConverter;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.BabyInfo;
import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import com.jqsoft.babyservice.mapper.biz.BabyInfoMapper;
import com.jqsoft.babyservice.mapper.biz.ExaminationInfoMapper;
import com.jqsoft.babyservice.mapper.biz.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BabyService {

    @Resource
    private BabyInfoMapper babyInfoMapper;

    @Resource
    private ExaminationInfoMapper examinationInfoMapper;

    @Resource
    private WorkTimeService workTimeService;

    @Resource
    private UserInfoMapper userInfoMapper;

    public RestVo overListCount(int overdueStart, int overdueEnd, int dingTimes, String corpid) {
        return RestVo.SUCCESS(babyInfoMapper.overListCount(overdueStart, overdueEnd, dingTimes, corpid));
    }


    public RestVo overdueList(PageBo<Map<String, Object>> pageBo, String corpid) {
        return RestVo.SUCCESS(babyInfoMapper.overdueList(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(), corpid));
    }

    public RestVo getBabyParentInfo(Long id) {
        return RestVo.SUCCESS(babyInfoMapper.getBabyParentInfo(id));
    }

    public RestVo tomorrowExaminationBabysList(PageBo<Map<String, Object>> pageBo, String corpid) {
        return RestVo.SUCCESS(babyInfoMapper.tomorrowExaminationBabysList(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(), corpid));
    }

    public RestVo changeDateBabysList(PageBo<Map<String, Object>> pageBo, String corpid) {
        return RestVo.SUCCESS(babyInfoMapper.changeDateBabysList(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(), corpid));
    }

    public RestVo allBabysList(PageBo<Map<String, Object>> pageBo, String corpid) {
        return RestVo.SUCCESS(babyInfoMapper.allBabysList(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(), corpid));
    }

    /**
     * 医生端-首页统计
     *
     * @return
     */
    public RestVo indexCount(String corpid) {
        Map<String, String> dataMap = new HashMap<>();

        // 逾期人数统计
        Map<String, String> map1 = babyInfoMapper.overdueCount(corpid);
        dataMap.put("overdueDays7", map1.get("overdueDays7"));
        dataMap.put("overdueDays14", map1.get("overdueDays14"));
        dataMap.put("overdueDays21", map1.get("overdueDays21"));

        // 明日体检通知人数统计
        Map<String, String> map2 = babyInfoMapper.tomorrowExaminationBabysCount(corpid);
        dataMap.put("tomorrowExaminationBabys", map2.get("total"));

        // 申请改期人数统计
        Map<String, String> map3 = babyInfoMapper.changeDateBabysCount(corpid);
        dataMap.put("changeDateBabys", map3.get("total"));

        // 总儿童人数统计
        Map<String, String> map4 = babyInfoMapper.allBabysCount(corpid);
        dataMap.put("allBabys", map4.get("total"));

        return RestVo.SUCCESS(dataMap);
    }

    public RestVo cancelRemind(Long id) {
        ExaminationInfo entity = new ExaminationInfo();
        entity.setId(id);
        entity.setSignIn((byte) 1);
        return RestVo.SUCCESS(examinationInfoMapper.updateByPrimaryKeySelective(entity));
    }

    public RestVo delayOneDay(Long id, String corpid) {
        ExaminationInfo entity = examinationInfoMapper.selectByPrimaryKey(id);
        entity.setExaminationDate(this.getExaminationDate(corpid, entity.getExaminationDate()));
        return RestVo.SUCCESS(examinationInfoMapper.updateByPrimaryKeySelective(entity));
    }

    public RestVo cancelBaby(Long id) {
        BabyInfo babyInfo = new BabyInfo();
        babyInfo.setId(id);
        babyInfo.setState((byte) 0);
        return RestVo.SUCCESS(babyInfoMapper.updateByPrimaryKeySelective(babyInfo));
    }

    public RestVo getBabyInfo(Long babyid) {
        return RestVo.SUCCESS(babyInfoMapper.selectByPrimaryKey(babyid));
    }

    /**
     * 医生端-获取我的宝宝信息
     * @param parentId 必填
     * @param mobile 可不填
     * @return
     */
    public RestVo myBabys(Long parentId, String mobile) {
        List<BabyInfo> babyInfos = babyInfoMapper.myBabys(parentId, mobile);
        if (CollectionUtils.isNotEmpty(babyInfos)) {
            int size = babyInfos.size();
            for (int i = 0; i < size; i++) {
                BabyInfo baby = babyInfos.get(i);
                List<ExaminationInfo> examinationInfos = baby.getExaminationInfos();
                if (CollectionUtils.isNotEmpty(examinationInfos)) {
                    Map<Byte, Byte> map = new HashMap<>();
                    for (ExaminationInfo examinationInfo : examinationInfos) {
                        if (null != examinationInfo.getExaminationType()) {
                            map.put(examinationInfo.getExaminationType(), examinationInfo.getSignIn());
                        }
                    }
                    Object[] signIn = {0, 0, 0, 0, 0, 0, 0, 0, 0};
                    byte[] examinationType = {1, 3, 6, 8, 12, 18, 24, 30, 36};
                    for (int j = 0; j < examinationType.length; j++) {
                        if (map.containsKey(examinationType[j])) {
                            signIn[j] = map.get(examinationType[j]);
                        }
                    }
                    baby.setSignIn(StringUtils.join(signIn, ","));
                    baby.setExaminationInfos(null);
                } else {
                    baby.setSignIn("0,0,0,0,0,0,0,0,0");
                }
            }
        }
        return RestVo.SUCCESS(babyInfos);
    }

    /**
     * 家长端-删除宝宝信息
     *
     * @param babyId
     * @return
     */
    public RestVo delBabyInfo(Long babyId, Long parentId, String mobile) {
        if (null == babyId) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        BabyInfo babyInfo = babyInfoMapper.selectByPrimaryKey(babyId);
        if (null == babyInfo) {
            return RestVo.FAIL(ResultMsg.DATA_NOT_EXISTS);
        }
        // 判断删除的宝宝是否为当前操作人的子女
        if (!(null != babyInfo.getParentId() && parentId == babyInfo.getParentId().longValue())
                || (StringUtils.isNotBlank(babyInfo.getFatherMobile()) && StringUtils.isNotBlank(mobile) && mobile.equals(babyInfo.getFatherMobile()))
                || (StringUtils.isNotBlank(babyInfo.getMotherMobile()) && StringUtils.isNotBlank(mobile) && mobile.equals(babyInfo.getMotherMobile()))) {
            return RestVo.FAIL(ResultMsg.NO_AUTH_DEL_BABY);
        }

        babyInfoMapper.deleteByPrimaryKey(babyId);
        return RestVo.SUCCESS();
    }

    /**
     * 家长端-保存宝宝信息
     *
     * @param babyInfo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RestVo addBabyInfo(BabyInfo babyInfo, Long parentId, String corpid) {
        if (null == babyInfo || null == babyInfo.getBirthday()) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        babyInfo.setParentId(parentId);
        babyInfo.setCorpid(corpid);
        babyInfo.setState((byte) 1);
        babyInfo.setId(null);
        Date now = new Date();
        babyInfo.setCreateTime(now);
        babyInfo.setUpdateTime(now);

        // 父母手机号如果已填写则需要绑定（用手机号直接关联）
        /*if (StringUtils.isNotBlank(babyInfo.getFatherMobile())) {
            UserInfo userInfo = userInfoMapper.getUserInfoByMobile(babyInfo.getFatherMobile());
            babyInfo.setFatherId(null != userInfo ? userInfo.getId() : null);
        }
        if (StringUtils.isNotBlank(babyInfo.getMotherMobile())) {
            UserInfo userInfo = userInfoMapper.getUserInfoByMobile(babyInfo.getMotherMobile());
            babyInfo.setMotherId(null != userInfo ? userInfo.getId() : null);
        }*/

        babyInfoMapper.insert(babyInfo);

        // 生成体检计划
        byte[] examinationType = {1, 3, 6, 8, 12, 18, 24, 30, 36};
        for (int j = 0; j < examinationType.length; j++) {
            ExaminationInfo info = new ExaminationInfo();
            info.setBabyId(babyInfo.getId());
            info.setExaminationType(examinationType[j]);
            info.setExaminationDate(this.getExaminationDate(corpid, DateUtils.addMonths(babyInfo.getBirthday(), examinationType[j])));
            String item = "健康体检";
            item = examinationType[j] == 1 ? ("满月" + item) : (examinationType[j] + "月龄" + item);
            info.setExaminationItem(item);
            info.setDingTimes((short) 0);
            info.setSignIn((byte) 0);
            info.setConfirm((byte) 0);
            info.setCreateTime(now);
            info.setUpdateTime(now);
            examinationInfoMapper.insert(info);
        }

        return RestVo.SUCCESS();
    }


    /**
     * 判断当前日期是否为有效工作日，如果不是则获取下一个有效工作日: 1.不是法定节假日 2.在医院工作时间
     *
     * @param corpid:corpid
     * @param date:延期的日期
     */
    public Date getExaminationDate(String corpid, Date date) {
        if (null == date || date.before(new Date())) {
            return null;
        }

        // 判断是否是节假日或者不在医院工作时间
        int i = 0;// 往后判断30天
        while (LunarSolarConverter.isHolidays(date) || !(workTimeService.isWorkTime(corpid, date))) {
            if (++i >= 30) {
                return null;
            }
            date = DateUtils.addDays(date, 1);
        }

        return date;
    }



}
