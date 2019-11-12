package com.jqsoft.nposervice.service.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.ParticipantsCheck;
import com.jqsoft.nposervice.mapper.biz.ParticipantsCheckMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/9/19 15:32
 * @desc:
 */
@Service
public class ParticipantsCheckService {

    @Resource
    private ParticipantsCheckMapper participantsCheckMapper;

    public RestVo participantsList(String meetingId) {
        return RestVo.SUCCESS(participantsCheckMapper.participantsList(meetingId));
    }

    public RestVo participantsBadges(String meetingId) {
        List<Map> list = participantsCheckMapper.participantsBadges(meetingId);
        Map<Object, Object> bad = Maps.newHashMap();
        list.forEach(map -> {
            bad.put(map.get("state"), map.get("cnt"));
        });
        return RestVo.SUCCESS(bad);
    }

    public RestVo updateByPrimaryKeySelective(ParticipantsCheck participantsCheck) {
        return RestVo.SUCCESS(participantsCheckMapper.updateByPrimaryKeySelective(participantsCheck));
    }

    /**
     * 钉钉云审批结果同步
     *
     * @param data 钉钉云审批结果
     */
    public void updateStateByProcessInstanceId(JSONObject data) {
        DdProcessData bizData = JSON.parseObject(JSON.toJSONString(data), DdProcessData.class);
        if (StringUtils.equalsIgnoreCase("running", bizData.getStatus())) {//运行中的流程不处理
            return;
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("processInstanceId", bizData.getProcessInstanceId());
        if (StringUtils.equalsIgnoreCase("completed", bizData.getStatus())) {//流程已完成
            if (StringUtils.equalsIgnoreCase("agree", bizData.getResult())) {//同意
                params.put("state", 1);
            } else if (StringUtils.equalsIgnoreCase("refuse", bizData.getResult())) {//不同意
                params.put("state", 2);
            }
            participantsCheckMapper.updateStateByProcessInstanceId(params);
        } else if (StringUtils.equalsIgnoreCase("terminated", bizData.getStatus())) {//撤销
            participantsCheckMapper.deleteByProcessInstanceId(bizData.getProcessInstanceId());
        }
    }

    /**
     * 流程的数据:钉钉云审批结果
     */
    @Data
    @NoArgsConstructor
    private final static class DdProcessData {
        private String processInstanceId;
        private List<Object> attachedProcessInstanceIds;
        private String syncAction;//"isv_bpms"，表示一条审批记录的创建或者流程更新。"isv_bpms_cancel"，表示一条审批记录撤销(字段保留待开发)。
        private String businessId;
        private String title;
        private String originatorDeptId;
        private String url;
        private List<Object> operationRecords;
        private String result;
        private String bizAction;
        private List<String> ccUserids;
        private Long createTime;
        private Long finishTime;
        private String originatorUserid;
        private String processCode;
        private List<Object> formValueVOS;
        private List<Object> tasks;
        private String approverUserids;
        private String originatorDeptName;
        private String status;
    }
}
