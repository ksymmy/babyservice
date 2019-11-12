/**
 * FileName: MemberMeetingController
 * Author:   DR
 * Date:     2019/9/18 19:16
 * Description: 会员会议管理
 * History:
 */
package com.jqsoft.nposervice.controller.biz;

import com.jqsoft.nposervice.commons.bo.PageBo;
import com.jqsoft.nposervice.commons.interceptor.AuthCheck;
import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.controller.system.BaseController;
import com.jqsoft.nposervice.entity.biz.ParticipantsCheck;
import com.jqsoft.nposervice.service.biz.MemberMeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RestController
@AuthCheck
@RequestMapping("memberMeetingInfo")
public class MemberMeetingController extends BaseController{

    @Autowired
    public MemberMeetingService memberMeetingService;

    /**
     * 获取会议信息 分页
     * @param pageBo
     * @return
     */
    @RequestMapping("/getMeetingInfo")
    public RestVo getMeetingInfo(@RequestBody PageBo<Map<String, Object>> pageBo){
        pageBo.getParam().put("userId",this.getUserId());
        return memberMeetingService.getMeetingInfo(pageBo);
    }

    /**
     * 通过ID获取会议信息
     * @param meetingId
     * @return
     */
    @RequestMapping("/getMeetingInfoById")
    public RestVo getMeetingInfoById(@RequestParam String meetingId){
        return memberMeetingService.getMeetingInfoById(meetingId,this.getUserId());
    }

    /**
     * 获取参会人员信息
     * @param params
     * @return
     */
    @RequestMapping("/getParticipantsInfo")
    public RestVo getParticipantsInfo(@RequestParam Map<String, Object> params){
        return memberMeetingService.getParticipantsInfo(params);
    }

    /**
     * 报名
     * @return
     */
    @RequestMapping("/setMeetingState")
    public RestVo setMeetingState(@RequestBody ParticipantsCheck participantsCheck){
        participantsCheck.setUserId(this.getUserId());
        return memberMeetingService.setMeetingState(participantsCheck,this.getUserInfo());
    }

}
