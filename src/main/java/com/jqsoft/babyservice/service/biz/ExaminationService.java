package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.utils.LunarSolarConverter;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.BabyInfo;
import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.mapper.biz.ExaminationInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * 体检计划service
 */
@Slf4j
@Service
public class ExaminationService {

    @Resource
    private ExaminationInfoMapper examinationInfoMapper;

    @Resource
    private WorkTimeService workTimeService;

    @Resource
    private BabyService babyService;

    /**
     * 家长端-确认可以正常体检
     * @param examinationId
     * @return
     */
    @RequestMapping("examinationConfirm")
    public RestVo examinationConfirm(Long examinationId, UserInfo userInfo){
        if (null == examinationId || null == userInfo) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        BabyInfo babyInfo = babyService.getBabyInfoByExaminationId(examinationId);
        if (null == babyInfo) {
            return RestVo.FAIL(ResultMsg.BABY_NOT_EXISTS);
        }

        // 判断是否为宝宝家长
        if (!babyService.isBabyParent(babyInfo, userInfo)) {
            return RestVo.FAIL(ResultMsg.NOT_BABY_PARENT);
        }
        examinationInfoMapper.examinationConfirm(examinationId);
        return RestVo.SUCCESS();
    }

    /**
     * 家长端-签到
     * @param examinationId
     * @return
     */
    public RestVo signIn(Long examinationId, UserInfo userInfo){
        if (null == examinationId || null == userInfo) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        BabyInfo babyInfo = babyService.getBabyInfoByExaminationId(examinationId);
        if (null == babyInfo) {
            return RestVo.FAIL(ResultMsg.BABY_NOT_EXISTS);
        }

        // 判断是否为宝宝家长
        if (!babyService.isBabyParent(babyInfo, userInfo)) {
            return RestVo.FAIL(ResultMsg.NOT_BABY_PARENT);
        }

        examinationInfoMapper.signIn(examinationId);
        return RestVo.SUCCESS();
    }

    /**
     * 家长端-申请延期
     * @param examinationId
     * @return
     */
    public RestVo applyDelay(Long examinationId, UserInfo userInfo){
        if (null == examinationId || null == userInfo) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        BabyInfo babyInfo = babyService.getBabyInfoByExaminationId(examinationId);
        if (null == babyInfo) {
            return RestVo.FAIL(ResultMsg.BABY_NOT_EXISTS);
        }

        // 判断是否为宝宝家长
        if (!babyService.isBabyParent(babyInfo, userInfo)) {
            return RestVo.FAIL(ResultMsg.NOT_BABY_PARENT);
        }

        Map map = examinationInfoMapper.applyDelay(examinationId);
        Object examinationType = map.get("examinationType");
        if (null != examinationType){
            if (examinationType.equals(1)) {
                map.put("examinationType","满月健康体检");
            } else {
                map.put("examinationType", examinationType + "月龄健康体检");
            }
        }
        return RestVo.SUCCESS(map);
    }

    /**
     * 家长端-确定延期
     * @param examinationId
     * @param delayDate
     * @param delayReason
     * @return
     */
    public RestVo confirmDelay(UserInfo userInfo, Long examinationId, Date delayDate, String delayReason){
        if (null == examinationId || null == userInfo || null == delayDate) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        BabyInfo babyInfo = babyService.getBabyInfoByExaminationId(examinationId);
        if (null == babyInfo) {
            return RestVo.FAIL(ResultMsg.BABY_NOT_EXISTS);
        }

        // 判断是否为宝宝家长
        if (!babyService.isBabyParent(babyInfo, userInfo)) {
            return RestVo.FAIL(ResultMsg.NOT_BABY_PARENT);
        }

        Date now = new Date();
        // 不能延期当天及以前时间
        if (DateUtils.isSameDay(delayDate, now) || delayDate.before(now)) {
            return RestVo.FAIL(ResultMsg.BEFORE_TIME);
        }

        ExaminationInfo examinationInfo = examinationInfoMapper.selectByPrimaryKey(examinationId);
        if (null == examinationInfo) {
            return RestVo.FAIL(ResultMsg.DATA_NOT_EXISTS);
        }

        // 判断延期日期是否在一个月以内
        Date maxDate = DateUtils.addMonths(examinationInfo.getExaminationDate(), 1);
        if (delayDate.after(maxDate)) {
            return RestVo.FAIL(ResultMsg.NOT_IN_ONE_MONTH);
        }

        // 是否是法定节假日
        boolean isHolidays = LunarSolarConverter.isHolidays(delayDate);
        if (isHolidays) {
            return RestVo.FAIL(ResultMsg.IN_HOLIDAYS);
        }

        // 是否在医院不上班时间
        boolean isWorkTime = workTimeService.isWorkTime(userInfo.getCorpid(),delayDate);
        if (!isWorkTime) {
            return RestVo.FAIL(ResultMsg.NOT_IN_WORK_TIME);
        }

        examinationInfoMapper.confirmDelay(examinationId, delayDate, delayReason);

        return RestVo.SUCCESS();
    }

    /**
     * 校验延期日期是否有效
     * @param corpid
     * @param examinationId
     * @param delayDate
     * @return
     */
    /*public RestVo checkDelayDate(String corpid, Long examinationId, Date delayDate){
        if (null == examinationId || null == delayDate) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }

        ExaminationInfo examinationInfo = examinationInfoMapper.selectByPrimaryKey(examinationId);
        if (null == examinationInfo) {
            return RestVo.FAIL(ResultMsg.DATA_NOT_EXISTS);
        }

        // 判断延期日期是否在一个月以内
        Date maxDate = DateUtils.addMonths(examinationInfo.getExaminationDate(), 1);
        if (delayDate.after(maxDate)) {
            return RestVo.FAIL(ResultMsg.NOT_IN_ONE_MONTH);
        }

        // 是否是法定节假日
        boolean isHolidays = LunarSolarConverter.isHolidays(delayDate);
        if (!isHolidays) {
            return RestVo.FAIL(ResultMsg.IN_HOLIDAYS);
        }

        // 是否在医院不上班时间
        boolean isWorkTime = workTimeService.isWorkTime(corpid,delayDate);
        if (!isWorkTime) {
            return RestVo.FAIL(ResultMsg.NOT_IN_WORK_TIME);
        }

        return RestVo.SUCCESS();
    }*/
}

