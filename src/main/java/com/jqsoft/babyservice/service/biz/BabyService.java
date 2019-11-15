package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.BabyInfo;
import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import com.jqsoft.babyservice.mapper.biz.BabyInfoMapper;
import com.jqsoft.babyservice.mapper.biz.ExaminationInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class BabyService {

    @Resource
    private BabyInfoMapper babyInfoMapper;
    @Resource
    private ExaminationInfoMapper examinationInfoMapper;

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
}
