package com.jqsoft.babyservice.service.biz;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.google.common.collect.Lists;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.dd.DdCallBack;
import com.jqsoft.babyservice.entity.biz.MeetingNotice;
import com.jqsoft.babyservice.entity.biz.NoticeInfo;
import com.jqsoft.babyservice.mapper.biz.MeetingNoticeMapper;
import com.jqsoft.babyservice.mapper.biz.NoticeInfoMapper;
import com.jqsoft.babyservice.mapper.biz.ParticipantsCheckMapper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: created by ksymmy@163.com at 2019/9/19 18:14
 * @desc:
 */
@Slf4j
@Service
public class MeetingNoticeService {
    @Resource
    private MeetingNoticeMapper meetingNoticeMapper;
    @Resource
    private MailSenderService mailSenderService;
    @Resource
    private ParticipantsCheckMapper participantsCheckMapper;
    @Resource
    private NoticeInfoMapper noticeInfoMapper;
    @Resource
    private OrgInfoService orgInfoService;
    @Resource
    private DdCallBack callBack;
    @Resource
    private RedisUtils redisUtils;

    public RestVo meetingNoticeList(Map<String, Object> params) {
        return RestVo.SUCCESS(meetingNoticeMapper.meetingNoticeList(params));
    }

    public RestVo deleteByPrimaryKey(String noticeid) {
        int delete = meetingNoticeMapper.deleteByPrimaryKey(noticeid);
        if (1 == delete)
            redisUtils.remove(RedisKey.MEETING_NOTICE_INFO.getKey(noticeid));
        return RestVo.SUCCESS(delete);
    }

    public RestVo updateByPrimaryKeySelective(MeetingNotice meetingNotice, String corpId) {
        int update = meetingNoticeMapper.updateByPrimaryKeySelective(meetingNotice);
        if (1 == update) {
            redisUtils.remove(RedisKey.MEETING_NOTICE_INFO.getKey(meetingNotice.getId()));
        }
        if (1 == meetingNotice.getState()) {
            this.sendMsg(meetingNotice, corpId);
        }
        return RestVo.SUCCESS(update);
    }

    public RestVo addMeetingNotice(MeetingNotice meetingNotice, String corpId) {
        int insert = meetingNoticeMapper.insert(meetingNotice);
        if (1 == meetingNotice.getState()) {
            this.sendMsg(meetingNotice, corpId);
        }
        return RestVo.SUCCESS(insert);
    }

    /**
     * 发送站内信和邮件或者钉钉工作通知
     *
     * @param meetingNotice
     */
    private void sendMsg(MeetingNotice meetingNotice, String corpId) {
        List<Map> participants;
        List<String> participantsEmails, participantsDDUserId = Lists.newArrayList();
        if (meetingNotice.getSendNotice() + meetingNotice.getSendEmail() + meetingNotice.getSendDingMsg() > 0) {
            participants = this.getHadRegParticipants(meetingNotice.getMeetingId());
            participantsEmails = Lists.newArrayList();
            participants.forEach(map -> {
                participantsEmails.add((String) map.get("email"));
                participantsDDUserId.add((String) map.get("ddUserId"));

            });
            if (meetingNotice.getSendDingMsg() == 1) {
                //发送Ding工作通知
                this.sendDingMsg(corpId, meetingNotice, StringUtils.join(participantsDDUserId.toArray(), ","));
            }
            if (meetingNotice.getSendNotice() == 1) {
                //发送站内信
                this.sendNotice(meetingNotice);
            }
            if (meetingNotice.getSendEmail() == 1) {
                //发送邮件
                this.sendEmail(meetingNotice, participantsEmails);
            }
        }
    }


    /**
     * 发送Email
     *
     * @param meetingNotice
     * @param participantsEmails
     */
    private void sendEmail(MeetingNotice meetingNotice, List<String> participantsEmails) {
        if (CollectionUtils.isEmpty(participantsEmails)) return;
        //获取会员(报名成功的)
        String[] receivers = new String[participantsEmails.size()];
        mailSenderService.sendMailBatch(participantsEmails.toArray(receivers), meetingNotice.getTitle(), meetingNotice.getContext());
    }


    /**
     * 发送站内信
     *
     * @param meetingNotice
     */
    private void sendNotice(MeetingNotice meetingNotice) {
        noticeInfoMapper.insert(new NoticeInfo(CommUtils.getUUID(), meetingNotice.getOrgId(), "2", meetingNotice.getMeetingId(), meetingNotice.getTitle(),
                meetingNotice.getContext(), (byte) 1, new Date(), meetingNotice.getCreateTime(), meetingNotice.getUpdateTime()));
    }

    /**
     * 获取某会议报名成功的会员
     *
     * @param meetingId:
     */
    private List<Map> getHadRegParticipants(String meetingId) {
        return participantsCheckMapper.getHadRegParticipants(meetingId);
    }

    /**
     * 发布会议通知
     *
     * @param noticeid
     * @return
     */
    public RestVo release(String noticeid, String corpId) {
        MeetingNotice meetingNotice = meetingNoticeMapper.selectByPrimaryKey(noticeid);
        meetingNotice.setState((byte) 1);
        meetingNotice.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        int update = meetingNoticeMapper.updateByPrimaryKeySelective(meetingNotice);
        if (1 == update) {
            redisUtils.remove(RedisKey.MEETING_NOTICE_INFO.getKey(meetingNotice.getId()));
            this.sendMsg(meetingNotice, corpId);
        }
        return RestVo.SUCCESS(update);
    }

    public RestVo get(String noticeid) {
        String key = RedisKey.MEETING_NOTICE_INFO.getKey(noticeid);
        MeetingNotice meetingNotice = (MeetingNotice) redisUtils.get(key);
        if (null == meetingNotice) {
            meetingNotice = meetingNoticeMapper.selectByPrimaryKey(noticeid);
            redisUtils.add(key, meetingNotice);
        }
        return RestVo.SUCCESS(meetingNotice);
    }

    /**
     * 发送Ding工作通知
     *
     * @param corpId        corpId
     * @param meetingNotice meetingNotice
     * @param userIdList    userIdList
     * @return
     */
    private void sendDingMsg(String corpId, MeetingNotice meetingNotice, String userIdList) {
        if (StringUtils.isBlank(userIdList)) return;
        //获取accessToken
        String corpAccessToken = (String) redisUtils.get(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId));
        if (StringUtils.isBlank(corpAccessToken)) {
            corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
            //把corpAccessToken放入缓存中,设置失效时间110分钟（钉钉设置corpAccessToken的失效时间为120分钟），如果缓存中有就重置失效时间
            redisUtils.add(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId), corpAccessToken, 110, TimeUnit.MINUTES);
        }
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(userIdList);//012224366432353450
        request.setAgentId(Long.valueOf(orgInfoService.selectByCorpId(corpId).getAgentId()));
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("action_card");
        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
        msg.getActionCard().setTitle("会议通知");
        msg.getActionCard().setMarkdown("##### <font size=3 font color=#2673CC font face=\"黑体\">" + StringUtils.substring(meetingNotice.getTitle(), 0, 20) + "</font> \n " + StringUtils.substring(meetingNotice.getContext(), 0, 910));
        msg.getActionCard().setSingleTitle("查看详情");
        msg.getActionCard().setSingleUrl("eapp://pages/org/meetingManage/notice/detail/detail?noticeid=" + meetingNotice.getId());
        request.setMsg(msg);
        try {
            client.execute(request, corpAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("钉钉工作通知发送失败,失败原因:{}", e.getSubErrMsg());
        }
    }
}
