package com.jqsoft.nposervice.controller.biz;

import com.jqsoft.nposervice.commons.interceptor.AuthCheck;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.controller.system.BaseController;
import com.jqsoft.nposervice.entity.biz.MeetingInfo;
import com.jqsoft.nposervice.service.biz.MeetingService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * @author: created by ksymmy@163.com at 2019/9/16 10:54
 * @desc: 会议管理
 */
@AuthCheck
@RestController
@RequestMapping("/meeting")
public class MeetingController extends BaseController {
    @Resource
    private MeetingService meetingService;

    /**
     * 会议列表
     *
     * @param name  name
     * @param group 年月分组
     * @return
     */
    @GetMapping("/list")
    private RestVo list(@RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "group", required = false) String group) {
        String orgId = this.getOrgId();
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("corpid", orgId);
            put("name", name);
            put("group", group);
        }};
        return meetingService.queryMeetingList(params);
    }

    /**
     * 创建会议
     *
     * @param meetingInfo 会议实体
     * @return RestVo
     */
    @PostMapping("/add")
    private RestVo addMeeting(@RequestBody MeetingInfo meetingInfo) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        meetingInfo.setCreateTime(now);
        meetingInfo.setUpdateTime(now);
        meetingInfo.setOrgId(this.getOrgId());
        return meetingService.addMeeting(meetingInfo);
    }

    /**
     * 会议更新
     *
     * @param meetingInfo
     * @return
     */
    @PostMapping("/update")
    private RestVo updateMeeting(@RequestBody MeetingInfo meetingInfo) {
        meetingInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return meetingService.updateMeeting(meetingInfo);
    }

    /**
     * 删除会议
     *
     * @param meetingid
     * @return
     */
    @PostMapping("/delete")
    private RestVo delete(@RequestParam String meetingid) {
        return meetingService.delete(meetingid);
    }

    /**
     * 会议详情
     *
     * @param meetingid       会议id
     * @param setParticipants 是否设置参会人字段:不传就是false
     * @return
     */
    @GetMapping("/get")
    private RestVo get(@RequestParam String meetingid, @RequestParam(value = "setParticipants", required = false) boolean setParticipants) {
        RestVo restVo = meetingService.get(meetingid);
        if (setParticipants) {
            MeetingInfo meetingInfo = (MeetingInfo) restVo.getData();
            meetingService.setParticipants(meetingInfo);
            restVo.setData(meetingInfo);
        }
        return restVo;
    }
}
