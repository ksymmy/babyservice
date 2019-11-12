/**
 * FileName: MemberMeetingService
 * Author:   DR
 * Date:     2019/9/18 19:19
 * Description: 会员会议管理
 * History:
 */
package com.jqsoft.babyservice.service.biz;

import com.dingtalk.api.response.OapiProcessSaveResponse;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.biz.MeetingInfo;
import com.jqsoft.babyservice.entity.biz.ParticipantsCheck;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.entity.biz.WorkflowInfo;
import com.jqsoft.babyservice.mapper.biz.MemberMeetingMapper;
import com.jqsoft.babyservice.mapper.biz.WorkflowInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 〈会员会议管理〉
 *
 * @author DR
 * @create 2019/9/18
 * @since 1.0.0
 */
@Slf4j
@Service
public class MemberMeetingService {

    private static final String flowCode = "PROC-E95B-45CA-AD9A";

    @Autowired(required = false)
    public MemberMeetingMapper memberMeetingMapper;

    @Autowired(required = false)
    public WorkflowInfoMapper workflowInfoMapper;

    @Resource
    private ProcessService processService;


    /**
     * 获取会议信息
     *
     * @param pageBo
     * @return
     */
    public RestVo getMeetingInfo(PageBo<Map<String, Object>> pageBo) {
        long time = System.currentTimeMillis();
        List<MeetingInfo> list = memberMeetingMapper.getMeetingInfo(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam());
        for (MeetingInfo m : list) {
            if (m.getMeetingEnd().getTime() < time) {
                m.setCheckState("4");//会议结束
            } else {
                if (m.getEnrollEnd().getTime() > time && m.getEnrollStart().getTime() < time) {
                    m.setEnrollState("1");// 1显示报名按钮
                }
            }


        }
        return RestVo.SUCCESS(list);

    }

    public RestVo getParticipantsInfo(Map<String, Object> params) {
        return RestVo.SUCCESS(memberMeetingMapper.getParticipantsInfo(params));
    }

    /**
     * 报名参会
     *
     * @return
     */
    public RestVo setMeetingState(ParticipantsCheck participantsCheck, UserInfo userInfo) {
        OapiProcessinstanceCreateResponse response = null;
        MeetingInfo meetingInfo = memberMeetingMapper.selectById(participantsCheck.getMeetingId());
        if (participantsCheck.getState() == -1) {
            memberMeetingMapper.deleteMeetingState(participantsCheck);
        } else {
            long time = System.currentTimeMillis();
            if (meetingInfo.getEnrollStart().getTime() <= time && meetingInfo.getEnrollEnd().getTime() >= time) {
                // 报名工作流
                try {
                    response = this.signUpDD(participantsCheck.getMeetingId(), userInfo, participantsCheck.getCorpId());
                    ParticipantsCheck pc = memberMeetingMapper.selectParticipantsCheck(participantsCheck.getMeetingId(), participantsCheck.getUserId());
                    if (pc != null) {
                        memberMeetingMapper.updateParticipantsCheck(participantsCheck.getState(), pc.getId());
                        return RestVo.SUCCESS();
                    }
                    participantsCheck.setId(CommUtils.getUUID());
                    participantsCheck.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    participantsCheck.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    participantsCheck.setProcessInstanceId(response.getProcessInstanceId());
                    memberMeetingMapper.setMeetingState(participantsCheck);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return RestVo.FAIL(ResultMsg.FAIL);
            }
        }
        return RestVo.SUCCESS(response);
    }

    public RestVo getMeetingInfoById(String meetingId, String userId) {
        MeetingInfo meetingInfo = memberMeetingMapper.selectMeetingById(meetingId, userId);
        long time = System.currentTimeMillis();
        if (meetingInfo.getMeetingEnd().getTime() < time) {
            meetingInfo.setCheckState("4");//会议结束
        } else {
            if (meetingInfo.getEnrollEnd().getTime() > time && meetingInfo.getEnrollStart().getTime() < time) {
                meetingInfo.setEnrollState("1");// 1显示报名按钮
            }
        }
        return RestVo.SUCCESS(meetingInfo);
    }

    /**
     * 报名智能工作流
     *
     * @param MeetingId
     * @param userInfo
     * @param corpId
     * @return
     * @throws Exception
     */
    public OapiProcessinstanceCreateResponse signUpDD(String MeetingId, UserInfo userInfo, String corpId) throws Exception {
        String processCode = null;
        WorkflowInfo workflowInfo = workflowInfoMapper.selectWorkflowInfo(userInfo.getOrgId(), flowCode);
        if (workflowInfo == null) {
            OapiProcessSaveResponse oapiProcessSaveResponse = processService.signUpDemo(corpId, processCode);
            processCode = oapiProcessSaveResponse.getResult().getProcessCode();
            WorkflowInfo workflow = new WorkflowInfo();
            workflow.setId(CommUtils.getUUID());
            workflow.setFlowName("会议报名申请模板");
            workflow.setFlowCode(flowCode);
            workflow.setOrgId(userInfo.getOrgId());
            workflow.setProcessCode(processCode);
            workflow.setCreateTime(new Timestamp(System.currentTimeMillis()));
            workflowInfoMapper.save(workflow);
        } else {
            processCode = workflowInfo.getProcessCode();
        }
        return processService.createProcessInstance(corpId, processCode, MeetingId, userInfo);

    }
}
