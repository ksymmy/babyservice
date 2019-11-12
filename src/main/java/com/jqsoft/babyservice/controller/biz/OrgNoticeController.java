/**
 * FileName: OrgNoticeController
 * Author:   DR
 * Date:     2019/9/21 15:30
 * Description: 组织通知公告
 * History:
 */
package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.interceptor.AuthCheck;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.NoticeInfo;
import com.jqsoft.babyservice.service.biz.MemberNoticeInfoService;
import com.jqsoft.babyservice.service.biz.OrgNoticeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 〈组织通知公告〉
 *
 * @author DR
 * @create 2019/9/21
 * @since 1.0.0
 */
@Slf4j
@RestController
@AuthCheck
@RequestMapping("orgNotice")
public class OrgNoticeController extends BaseController {

    @Autowired
    public OrgNoticeService orgNoticeService;
    @Autowired
    public MemberNoticeInfoService memberNoticeInfoService;


    /**
     * 生成组织通知公告 ID
     * @return
     */
    @RequestMapping("/getId")
    public RestVo getId(){
        return RestVo.SUCCESS(CommUtils.getUUID());
    }
    /**
     * 获取组织通知公告信息
     * @param pageBo
     * @return
     */
    @RequestMapping("/getNoticeInfo")
    public RestVo getNoticeInfo(@RequestBody PageBo<Map<String, Object>> pageBo){
//        pageBo.getParam().put("orgId",this.getOrgId());
        log.info(pageBo.getParam().toString());
        return orgNoticeService.selectNoticeInfoByUserId(pageBo,this.getOrgId());
    }

    /**
     * 通过ID 获取组织通知公告信息
     * @param noticeId
     * @return
     */
    @RequestMapping("/getNoticeInfoById")
    public RestVo getNoticeInfoById(@RequestParam String noticeId){
        return orgNoticeService.getNoticeInfoById(noticeId);
    }


    /**
     * 发布组织通知公告信息
     * @param params
     * @return
     */
    @RequestMapping("/publishNotice")
    public RestVo publishNotice(@RequestParam Map<String, Object> params){
        params.put("orgId",this.getOrgId());
        return orgNoticeService.publishNoticeById(params);
    }

    /**
     * 保存组织通知公告信息
     * @return
     */
    @RequestMapping("/saveNotice")
    public RestVo saveNotice(@RequestBody NoticeInfo noticeInfo){
        noticeInfo.setOrgId(this.getOrgId());
        return orgNoticeService.saveNotice(noticeInfo);
    }

    /**
     * 删除组织通知公告信息
     * @return
     */
    @RequestMapping("/deleteNotice")
    public RestVo deleteNotice(@RequestParam("noticeId") String id){
        return orgNoticeService.deleteNotice(id,this.getOrgId());
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
}
