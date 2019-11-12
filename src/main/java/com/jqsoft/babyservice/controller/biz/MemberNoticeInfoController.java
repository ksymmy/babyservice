/**
 * FileName: MemberNoticeInfoController
 * Author:   DR
 * Date:     2019/9/17 11:06
 * Description: 会员通知公告
 * History:
 */
package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.interceptor.AuthCheck;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.MemberNoticeInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 〈会员通知公告信息维护〉
 *
 * @author DR
 * @create 2019/9/17
 * @since 1.0.0
 */
@Slf4j
@RestController
@AuthCheck
@RequestMapping("memberNoticeInfo")
public class MemberNoticeInfoController extends BaseController{

    @Autowired
    public MemberNoticeInfoService memberNoticeInfoService;

    /**
     * 获取会员通知公告信息
     * @param pageBo
     * @return
     */
    @RequestMapping("/getNoticeInfo")
    public RestVo getNoticeInfo( @RequestBody PageBo<Map<String, Object>> pageBo){
        pageBo.getParam().put("userId",this.getUserId());
        pageBo.getParam().put("orgId",this.getOrgId());
        log.info(pageBo.toString());
        return memberNoticeInfoService.selectNoticeInfo(pageBo);
    }

    /**
     * 通过ID获取会员通知公告信息
     * @param noticeId
     * @return
     */
    @RequestMapping("/getNoticeInfoById")
    public RestVo getNoticeInfoById( @RequestParam String noticeId ){
        log.info(noticeId);
        return memberNoticeInfoService.selectNoticeInfoByID(noticeId);
    }

    /**
     * 获取会员通知公告 附件信息
     * @return
     */
    @RequestMapping("/getNoticeFileInfo")
    public RestVo getNoticeFileInfo( @RequestParam String  noticeId){
        log.info(noticeId);
        return memberNoticeInfoService.selectNoticeFileInfoByNoticeId(noticeId);
    }
    /**
     * 已读读信息状态
     * @return
     */
    @RequestMapping("/updateReceiverState")
    public RestVo updateReceiverState( @RequestParam Map<String, Object> params){
        UserInfo userInfo=this.getUserInfo();
        log.info(params.toString());
        return memberNoticeInfoService.updateReceiverState(params,userInfo);
    }


}
