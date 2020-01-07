package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.HospitalNoticeTemplateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: created by ksymmy@163.com at 2020/1/6 19:47
 * @desc: 月龄体检通知内容配置表
 * 表biz_hospital_notice_template中corpid列='corpid'的为默认配置
 */
@RestController
@RequestMapping("/hospitalnoticetemp")
public class HospitalNoticeTemplateController extends BaseController {

    @Resource
    private HospitalNoticeTemplateService hospitalNoticeTemplateService;

    /**
     * 获取企业通知模板
     */
    @RequestMapping("/get")
    public RestVo getTemplate() {
        return hospitalNoticeTemplateService.selectByCorpid(this.getDdCorpid());
    }

    /**
     * 更新企业通知模板(按月龄)
     *
     * @param et   月龄
     * @param text 通知内容
     */
    @RequestMapping("/update")
    public RestVo update(@RequestParam("examinationType") String et, @RequestParam("text") String text) {
        return hospitalNoticeTemplateService.updateByCorpid(this.getDdCorpid(), et, text);
    }
}
