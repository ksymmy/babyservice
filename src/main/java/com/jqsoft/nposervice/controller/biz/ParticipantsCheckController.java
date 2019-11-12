package com.jqsoft.nposervice.controller.biz;

import com.jqsoft.nposervice.commons.interceptor.AuthCheck;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.ParticipantsCheck;
import com.jqsoft.nposervice.service.biz.ParticipantsCheckService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;

/**
 * @author: created by ksymmy@163.com at 2019/9/19 15:30
 * @desc: 会议参加人员审核
 */
@AuthCheck
@RestController
@RequestMapping(("/participants"))
public class ParticipantsCheckController {
    @Resource
    private ParticipantsCheckService participantsCheckService;

    /**
     * 会议报名人员
     *
     * @param meetingid
     * @return
     */
    @GetMapping("/list")
    private RestVo participantsList(@RequestParam String meetingid) {
        return participantsCheckService.participantsList(meetingid);
    }

    /**
     * 参加审核人数
     *
     * @param meetingid
     * @return
     */
    //    @AuthCheck
    @GetMapping("/badges")
    private RestVo participantsBadges(@RequestParam String meetingid) {
        return participantsCheckService.participantsBadges(meetingid);
    }

    /**
     * 按主键更新(审核)
     *
     * @param participantsCheck :
     * @return RestVo
     */
    @PostMapping("/update")
    private RestVo updateByPrimaryKeySelective(@RequestBody ParticipantsCheck participantsCheck) {
        participantsCheck.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return participantsCheckService.updateByPrimaryKeySelective(participantsCheck);
    }

}
