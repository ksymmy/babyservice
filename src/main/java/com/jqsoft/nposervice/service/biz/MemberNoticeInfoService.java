/**
 * FileName: MemberNoticeInfoService
 * Author:   DR
 * Date:     2019/9/17 11:09
 * Description: 通知公告信息维护
 * History:
 */
package com.jqsoft.nposervice.service.biz;

import com.jqsoft.nposervice.commons.bo.PageBo;
import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.InfoReadedUser;
import com.jqsoft.nposervice.entity.biz.NoticeInfo;
import com.jqsoft.nposervice.entity.biz.UserInfo;
import com.jqsoft.nposervice.mapper.biz.MemberNoticeInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈会员通知公告信息维护〉
 *
 * @author DR
 * @create 2019/9/17
 * @since 1.0.0
 */
@Slf4j
@Service
public class MemberNoticeInfoService {

    @Autowired(required = false)
    public MemberNoticeInfoMapper memberNoticeInfoMapper;

    @Autowired(required = false)
    public MemberUserInfoService memberUserInfoService;

    public RestVo selectNoticeInfo(PageBo<Map<String, Object>> pageBo) {
        List<NoticeInfo> noticeInfos=memberNoticeInfoMapper.selectNoticeInfo(pageBo.getOffset(),pageBo.getSize(),pageBo.getParam());
        return RestVo.SUCCESS(noticeInfos);
    }

    public RestVo selectNoticeInfoByID(String noticeId) {
        return RestVo.SUCCESS(memberNoticeInfoMapper.selectById(noticeId));
    }
    public RestVo updateReceiverState(Map<String, Object> params, UserInfo userInfo) {
        InfoReadedUser infoReadedUser=new InfoReadedUser();
        params.put("userId",userInfo.getId());
        params.put("orgId",userInfo.getOrgId());
        infoReadedUser.setId(CommUtils.getUUID());
        infoReadedUser.setUserId(userInfo.getId());
        infoReadedUser.setInfoId((String) params.get("infoId"));
        infoReadedUser.setUserName(userInfo.getName());
        memberNoticeInfoMapper.updateReceiverState(infoReadedUser);
        return RestVo.SUCCESS();
    }

    /**
     * 获取会员通知公告 附件信息
     * @return
     */
    public RestVo selectNoticeFileInfoByNoticeId(String noticeId) {
        return RestVo.SUCCESS(memberNoticeInfoMapper.selectNoticeFileInfoByNoticeId(noticeId));
    }


}
