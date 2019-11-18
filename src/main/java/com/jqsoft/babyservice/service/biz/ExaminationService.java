package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import com.jqsoft.babyservice.mapper.biz.ExaminationInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 家长端-确认可以正常体检
     * @param newsId
     * @return
     */
    @RequestMapping("examinationConfirm")
    public RestVo examinationConfirm(Long newsId){
        if (null == newsId) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        examinationInfoMapper.examinationConfirmByNewsId(newsId);
        return RestVo.SUCCESS();
    }

    /**
     * 家长端-申请延期
     * @param newsId
     * @return
     */
    public RestVo applyDelay(Long newsId){
        if (null == newsId) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        Map<String,String> map = examinationInfoMapper.applyDelay(newsId);
        String examinationType = map.get("examinationType");
        if (StringUtils.isNotBlank(examinationType)){
            if ("1".equals(examinationType)) {
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
    public RestVo confirmDelay(Long examinationId, Date delayDate, String delayReason){
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
//        isWorkTime

        // 是否在医院不上班时间


        return RestVo.SUCCESS();
    }
}

