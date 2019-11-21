package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.interceptor.AdminCheck;
import com.jqsoft.babyservice.commons.interceptor.ParentCheck;
import com.jqsoft.babyservice.commons.utils.DdUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.BabyInfo;
import com.jqsoft.babyservice.entity.biz.ExaminationInfo;
import com.jqsoft.babyservice.job.RemindNewsJob;
import com.jqsoft.babyservice.service.biz.BabyService;
import com.jqsoft.babyservice.service.biz.ExaminationService;
import com.jqsoft.babyservice.service.biz.RemindNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: baby控制器
 * @Auther: luohongbing
 * @Date: 2019/11/13 18:59
 */
@Slf4j
@RestController
@RequestMapping("/baby")
public class BabyController extends BaseController {

    @Resource
    public BabyService babyService;

    @Resource
    private RemindNewsJob remindNewsJob;

    @Resource
    private DdUtils ddUtils;

    @Resource
    private ExaminationService examinationService;

    @Resource
    private RemindNewsService remindNewsService;

    @Value("${hospitalName}")
    public String hospitalName;

    //******************************************* 医生端接口 *****************************************************

    /**
     * 医生端-首页统计
     *
     * @return
     */
    @AdminCheck
    @RequestMapping("indexCount")
    public RestVo indexCount() {
        RestVo restVo = babyService.indexCount(this.getDdCorpid());
        return restVo;
    }

    /**
     * 医生端-逾期列表-逾期数量(按月龄分组)
     *
     * @param overdueStart 逾期时间起
     * @param overdueEnd   逾期时间止
     * @param dingTimes    钉次数
     */
    @AdminCheck
    @GetMapping("/overduelistcount")
    public RestVo overListCount(Integer overdueStart, Integer overdueEnd, Integer dingTimes, @RequestHeader("corpid") String corpid) {
        return babyService.overListCount(overdueStart, overdueEnd, dingTimes, corpid);
    }

    /**
     * 医生端-逾期列表
     *
     * @param pageBo 查询参数
     */
    @AdminCheck
    @PostMapping("/overduelist")
    public RestVo overdueList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.overdueList(pageBo, corpid);
    }

    /**
     * 根据手机号获取userid
     *
     * @param mobile:
     */
    @AdminCheck
    @GetMapping("/getuseridbbymobile")
    public RestVo getUseridBbyMobile(String mobile) {
        return babyService.getUseridByMobile(mobile);
    }

    /**
     * 获取DING 对象的userid / examIds
     *
     * @param overdueStart 逾期时间起
     * @param overdueEnd   逾期时间止
     * @param dingTimes    钉次数
     * @param age          月龄
     * @param corpid       corpid
     */
    @AdminCheck
    @GetMapping("/overdueDingUserid")
    public RestVo overdueDingUserid(Integer overdueStart, Integer overdueEnd, Integer dingTimes, Integer age, @RequestHeader("corpid") String corpid) {
        return babyService.overdueDingUserid(overdueStart, overdueEnd, dingTimes, age, corpid);
    }

    /**
     * 医生端-儿童档案
     *
     * @param babyid babyid
     * @param corpid corpid
     */
    @Deprecated
    @AdminCheck
    @GetMapping("/babyinfo")
    public RestVo babyInfo(Long babyid, @RequestHeader("corpid") String corpid) {
        return babyService.getBabyInfo(babyid, corpid);
    }

    /**
     * 医生端-儿童档案-父母信息
     *
     * @param babyid babyid
     */
    @AdminCheck
    @GetMapping("/babyparentinfo")
    public RestVo babyParentInfo(Long babyid) {
        return babyService.getBabyParentInfo(babyid);
    }


    /**
     * 医生端-取消逾期提醒
     *
     * @param examid 体检项目id
     */
    @AdminCheck
    @Transactional
    @PostMapping("/cancelremind")
    public RestVo cancelRemind(Long examid) {
        return babyService.cancelRemind(examid);
    }

    /**
     * 医生端-取消逾期提醒
     *
     * @param entity 体检项目
     */
    @AdminCheck
    @Deprecated
    @Transactional
    @PostMapping("/updateexam")
    public RestVo cancelRemind(@RequestBody ExaminationInfo entity) {
        return babyService.updateexam(entity);
    }

    /**
     * 更新DING次数
     *
     * @param examIds 体检项目id集合
     */
    @AdminCheck
    @Transactional
    @PostMapping("/updatedingtimes")
    public RestVo updateDingTimes(@RequestParam(value = "examIds") List<Long> examIds) {
        return babyService.updateDingTimes(examIds);
    }

    /**
     * 医生端-明日体检通知
     *
     * @param pageBo:
     * @param corpid:
     */
    @AdminCheck
    @PostMapping("/tomorrowexaminationbabyslist")
    public RestVo tomorrowExaminationBabysList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.tomorrowExaminationBabysList(pageBo, corpid);
    }

    /**
     * 医生端-明日体检-延后一天
     *
     * @param examId 体检id
     * @param corpid corpid
     */
    @AdminCheck
    @Transactional
    @PostMapping("/delayoneday")
    public RestVo delayOneDay(Long examId, @RequestHeader("corpid") String corpid) {
        return babyService.delayOneDay(examId, corpid);
    }

    /**
     * 医生端-申请改期列表
     *
     * @param pageBo:
     * @param corpid:
     */
    @AdminCheck
    @PostMapping("/changedatebabyslist")
    public RestVo changeDateBabysList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.changeDateBabysList(pageBo, corpid);
    }

    /**
     * 医生端-总管理儿童
     *
     * @param pageBo:
     * @param corpid:
     */
    @AdminCheck
    @PostMapping("/allbabyslist")
    public RestVo allBabysList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.allBabysList(pageBo, corpid);
    }

    /**
     * 医生端-取消儿童管理
     *
     * @param id:
     */
    @AdminCheck
    @Transactional
    @PostMapping("/cancelbaby")
    public RestVo cancelBaby(Long id) {
        return babyService.cancelBaby(id);
    }


    //***************************************** 家长端接口 ********************************************************

    /**
     * 家长端-获取我的宝宝信息
     *
     * @return
     */
    @ParentCheck
    @RequestMapping("myBabys")
    public RestVo myBabys() {
        return babyService.myBabys(this.getUser());
    }

    /**
     * 家长端-删除宝宝信息
     *
     * @param babyId
     * @return
     */
    @ParentCheck
    @RequestMapping("delBaby")
    public RestVo delBabyInfo(Long babyId) {
        return babyService.delBabyInfo(babyId, this.getUser());
    }

    /**
     * 家长端-生成体检计划日期
     * @param birthday
     * @return
     */
    @ParentCheck
    @RequestMapping("generateExaminationDates")
    public RestVo generateExaminationDates(@DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday){
        return babyService.generateExaminationDates(this.getDdCorpid(), birthday);
    };


    /**
     * 家长端-保存宝宝信息
     *
     * @param babyInfo
     * @return
     */
    @ParentCheck
    @RequestMapping("addBabyInfo")
    public RestVo addBabyInfo(@RequestBody BabyInfo babyInfo) {
        return babyService.addBabyInfo(babyInfo, this.getUser());
    }


    /**
     * 家长端-消息列表
     *
     * @param pageBo
     * @return
     */
    @ParentCheck
    @RequestMapping("remindNewsList")
    public RestVo remindNewsList(@RequestBody PageBo<Map<String, Object>> pageBo) {
        return remindNewsService.remindNewsList(pageBo, this.getUserId());
    }

    /**
     * 家长端-确认可以按时体检
     *
     * @param examinationId
     * @return
     */
    @ParentCheck
    @RequestMapping("examinationConfirm")
    public RestVo examinationConfirm(Long examinationId) {
        return examinationService.examinationConfirm(examinationId, this.getUser());
    }

    /**
     * 家长端-签到
     *
     * @param examinationId
     * @return
     */
    @ParentCheck
    @RequestMapping("signIn")
    public RestVo signIn(Long examinationId) {
        return examinationService.signIn(examinationId, this.getUser());
    }

    /**
     * 家长端-申请延期
     *
     * @param examinationId
     * @return
     */
    @ParentCheck
    @RequestMapping("applyDelay")
    public RestVo applyDelay(Long examinationId) {
        return examinationService.applyDelay(examinationId, this.getUser());
    }

    /**
     * 家长端-确认延期
     *
     * @param
     * @return
     */
    @ParentCheck
    @RequestMapping("confirmDelay")
    public RestVo confirmDelay(Long examinationId, @DateTimeFormat(pattern = "yyyy-MM-dd") Date delayDate, String delayReason) {
        return examinationService.confirmDelay(this.getUser(), examinationId, delayDate, delayReason);
    }

    /**
     * 家长端-获取医院名称
     *
     * @return
     */
    @RequestMapping("getHospitalName")
    public RestVo getHospitalName() {
        return RestVo.SUCCESS(hospitalName);
    }

    @RequestMapping("test")
    public void test(String title, String context, String userid) {
        remindNewsJob.remindNewsJob();
//        ddUtils.sendDdMessage(title,context,userid);
    }
}
