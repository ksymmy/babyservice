package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.interceptor.AuthCheck;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.MeetingNotice;
import com.jqsoft.babyservice.service.biz.MeetingNoticeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/9/19 18:13
 * @desc: 会议通知
 */
@AuthCheck
@RestController
@RequestMapping("/meetingnotice")
public class MeetingNoticeController extends BaseController {
    @Resource
    private MeetingNoticeService meetingNoticeService;

    /**
     * 会议通知列表
     *
     * @param meetingid
     * @param name
     * @return
     */
    @GetMapping("/list")
    private RestVo meetingNoticeList(@RequestParam String meetingid, @RequestParam String name) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("meetingId", meetingid);
            put("name", name);
        }};
        return meetingNoticeService.meetingNoticeList(params);
    }

    /**
     * 获取一条会议通知
     *
     * @param noticeid id
     * @return
     */
    @GetMapping("/get")
    private RestVo get(@RequestParam String noticeid) {
        return meetingNoticeService.get(noticeid);
    }

    /**
     * 新增会议通知
     *
     * @param meetingNotice
     * @param corpId
     * @return
     */
    @PostMapping("/add")
    private RestVo addMeetingNotice(@RequestBody MeetingNotice meetingNotice, String corpId) {
        meetingNotice.setId(CommUtils.getUUID());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        meetingNotice.setCreateTime(now);
        meetingNotice.setPublish(now);
        meetingNotice.setPublish(now);
        meetingNotice.setOrgId(this.getOrgId());
        return meetingNoticeService.addMeetingNotice(meetingNotice, corpId);
    }

    /**
     * 发布会议通知
     *
     * @param noticeid 会议通知id
     * @return
     */
    @PostMapping("/release")
    private RestVo release(@RequestParam String noticeid, String corpId) {
        return meetingNoticeService.release(noticeid, corpId);
    }

    /**
     * 删除会议通知
     *
     * @param noticeid
     * @return
     */
    @PostMapping("/delete")
    private RestVo deleteByPrimary(@RequestParam String noticeid) {
        return meetingNoticeService.deleteByPrimaryKey(noticeid);
    }

    /**
     * 更新会议通知
     *
     * @param meetingNotice
     * @param corpId
     * @return
     */
    @PostMapping("/update")
    private RestVo updateByPrimaryKeySelective(@RequestBody MeetingNotice meetingNotice, String corpId) {
        meetingNotice.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return meetingNoticeService.updateByPrimaryKeySelective(meetingNotice, corpId);
    }
}
