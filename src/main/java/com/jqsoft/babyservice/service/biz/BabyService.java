package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.utils.DateUtil;
import com.jqsoft.babyservice.commons.utils.LunarSolarConverter;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.BabyInfo;
import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.entity.biz.WorkTime;
import com.jqsoft.babyservice.mapper.biz.BabyInfoMapper;
import com.jqsoft.babyservice.mapper.biz.ExaminationInfoMapper;
import com.jqsoft.babyservice.mapper.biz.UserInfoMapper;
import com.jqsoft.babyservice.mapper.biz.WorkTimeMapper;
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
    private WorkTimeMapper workTimeMapper;

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

    public RestVo delayOneDay(Long id) {
//        ExaminationInfo entity = new ExaminationInfo();
//        entity.setId(id);
        return RestVo.SUCCESS();
    }

    public RestVo cancelBaby(Long id) {
        BabyInfo babyInfo = new BabyInfo();
        babyInfo.setId(id);
        babyInfo.setState((byte) 0);
        return RestVo.SUCCESS(babyInfoMapper.updateByPrimaryKeySelective(babyInfo));
    }

    /**
     * 医生端-获取我的宝宝信息
     * @return
     */
    public RestVo myBabys(Long parentId){
        List<BabyInfo> babyInfos = babyInfoMapper.myBabys(parentId);
        if (CollectionUtils.isNotEmpty(babyInfos)) {
            int size = babyInfos.size();
            for (int i = 0;i < size; i++) {
                BabyInfo baby = babyInfos.get(i);
                List<ExaminationInfo> examinationInfos = baby.getExaminationInfos();
                if (CollectionUtils.isNotEmpty(examinationInfos)) {
                    Map<Byte,Byte> map = new HashMap<>();
                    for (ExaminationInfo examinationInfo : examinationInfos) {
                        if (null != examinationInfo.getExaminationType()) {
                            map.put(examinationInfo.getExaminationType(),examinationInfo.getSignIn());
                        }
                    }
                    Object[] signIn = {0,0,0,0,0,0,0,0,0};
                    byte[] examinationType = {1,3,6,8,12,18,24,30,36};
                    for (int j = 0;j < examinationType.length ; j++) {
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
     * @param babyId
     * @return
     */
    public RestVo delBabyInfo(Long babyId, Long parentId){
        if (null == babyId) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        BabyInfo babyInfo = babyInfoMapper.selectByPrimaryKey(babyId);
        if (null == babyInfo) {
            return RestVo.FAIL(ResultMsg.DATA_NOT_EXISTS);
        }
        // 判断删除的宝宝是否为当前操作人的子女
        if (!(null != babyInfo.getParentId() && parentId == babyInfo.getParentId().longValue())
                || (null != babyInfo.getFatherId() && parentId == babyInfo.getFatherId().longValue())
                || (null != babyInfo.getMotherId() && parentId == babyInfo.getMotherId().longValue())) {
            return RestVo.FAIL(ResultMsg.NO_AUTH_DEL_BABY);
        }

        babyInfoMapper.deleteByPrimaryKey(babyId);
        return RestVo.SUCCESS();
    }

    /**
     * 家长端-保存宝宝信息
     * @param babyInfo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public RestVo addBabyInfo(BabyInfo babyInfo, Long parentId, String corpid){
        if (null == babyInfo || null == babyInfo.getBirthday()) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        babyInfo.setParentId(parentId);
        babyInfo.setCorpid(corpid);
        babyInfo.setState((byte)1);
        babyInfo.setId(null);
        babyInfo.setFatherId(null);
        babyInfo.setMotherId(null);
        Date now = new Date();
        babyInfo.setCreateTime(now);
        babyInfo.setUpdateTime(now);

        // 父母手机号如果已填写则需要绑定
        if (StringUtils.isNotBlank(babyInfo.getFatherMobile())) {
            UserInfo userInfo = userInfoMapper.getUserInfoByMobile(babyInfo.getFatherMobile());
            babyInfo.setFatherId(null != userInfo ? userInfo.getId() : null);
        }
        if (StringUtils.isNotBlank(babyInfo.getMotherMobile())) {
            UserInfo userInfo = userInfoMapper.getUserInfoByMobile(babyInfo.getMotherMobile());
            babyInfo.setMotherId(null != userInfo ? userInfo.getId() : null);
        }

        babyInfoMapper.insert(babyInfo);

        // 生成体检计划
        byte[] examinationType = {1,3,6,8,12,18,24,30,36};
        for (int j = 0;j < examinationType.length ; j++) {
            ExaminationInfo info = new ExaminationInfo();
            info.setBabyId(babyInfo.getId());
            info.setExaminationType(examinationType[j]);
            info.setExaminationDate(this.getExaminationDate(corpid, DateUtils.addMonths(babyInfo.getBirthday(),examinationType[j])));
            String item = "健康体检";
            item = examinationType[j] == 1 ? ("满月" + item) : (examinationType[j] + "月龄" + item);
            info.setExaminationItem(item);
            info.setDingTimes((short)0);
            info.setSignIn((byte)0);
            info.setCreateTime(now);
            info.setUpdateTime(now);
            examinationInfoMapper.insert(info);
        }


        return RestVo.SUCCESS();
    }


    public Date getExaminationDate(String corpid, Date date){
        if (null == date) {
            return null;
        }
        WorkTime workTime = workTimeMapper.getWorkTimeByCorpid(corpid);
        if (null == workTime) {
            workTime = new WorkTime();
            workTime.setMonday((byte)1);
            workTime.setTuesday((byte)1);
            workTime.setWednesday((byte)1);
            workTime.setThursday((byte)1);
            workTime.setFriday((byte)1);
            workTime.setSaturday((byte)0);
            workTime.setSunday((byte)0);
        }

        // 判断医院是否7天不上班
        if (workTime.getMonday() == 0
                && workTime.getTuesday() == 0
                && workTime.getWednesday() == 0
                && workTime.getThursday() == 0
                && workTime.getFriday() == 0
                && workTime.getSaturday() == 0
                && workTime.getSunday() == 0) {
            log.info("corpid:{} 医院7天不上班",corpid);
            return null;
        }

        // 判断是否在节假日期间
        int i = 0;// 往后判断30天
        while(LunarSolarConverter.isHolidays(date)){
            if (++i >= 30) {
                return null;
            }
            date = DateUtils.addDays(date,1);
        }

        // 计算日期是星期几
        int day = DateUtil.getDayOfWeek(date);
        // 判断日期是否在医院上班时间，不在则往后延一天继续判断
        while(!((day == 1 && workTime.getMonday() == 1)
                || (day == 2 && workTime.getTuesday() == 1)
                || (day == 3 && workTime.getWednesday() == 1)
                || (day == 4 && workTime.getThursday() == 1)
                || (day == 5 && workTime.getFriday() == 1)
                || (day == 6 && workTime.getSaturday() == 1)
                || (day == 7 && workTime.getSunday() == 1))){
            if (++i >= 30) {
                return null;
            }
            date = DateUtils.addDays(date,1);
            day = DateUtil.getDayOfWeek(date);
        }
        return date;
    }

}
