package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.Job.RemindNewsJob;
import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.interceptor.AdminCheck;
import com.jqsoft.babyservice.commons.interceptor.ParentCheck;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.BabyInfo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.BabyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    //******************************************* 医生端接口 *************************************************************

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
    @GetMapping("/overduelistcount")
    public RestVo overListCount(Integer overdueStart, Integer overdueEnd, Integer dingTimes, @RequestHeader("corpid") String corpid) {
        return babyService.overListCount(overdueStart, overdueEnd, dingTimes, corpid);
    }

    /**
     * 医生端-逾期列表
     *
     * @param pageBo 查询参数
     */
    @PostMapping("/overduelist")
    public RestVo overdueList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.overdueList(pageBo, corpid);
    }

    /**
     * 获取DING 对象的userid
     *
     * @param overdueStart 逾期时间起
     * @param overdueEnd   逾期时间止
     * @param dingTimes    钉次数
     * @param age          月龄
     * @param corpid       corpid
     */
    @GetMapping("/overdueDingUserid")
    public RestVo overdueDingUserid(Integer overdueStart, Integer overdueEnd, Integer dingTimes, Integer age, @RequestHeader("corpid") String corpid) {
        return babyService.overdueDingUserid(overdueStart, overdueEnd, dingTimes, age, corpid);
    }


    /**
     * 医生端-儿童档案
     *
     * @param babyid babyid
     */
    @GetMapping("/babyinfo")
    public RestVo babyInfo(Long babyid) {
        return babyService.getBabyInfo(babyid);
    }

    /**
     * 医生端-儿童档案-父母信息
     *
     * @param babyid babyid
     */
    @Deprecated
    @GetMapping("/babyparentinfo")
    public RestVo babyParentInfo(Long babyid) {
        return babyService.getBabyParentInfo(babyid);
    }


    /**
     * 医生端-取消逾期提醒
     *
     * @param examid 体检项目id
     */
    @Transactional
    @PostMapping("/cancelremind")
    public RestVo cancelRemind(Long examid) {
        return babyService.cancelRemind(examid);
    }

    /**
     * 医生端-明日体检通知
     *
     * @param pageBo:
     * @param corpid:
     */
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
    @PostMapping("/allbabyslist")
    public RestVo allBabysList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.allBabysList(pageBo, corpid);
    }

    /**
     * 医生端-取消儿童管理
     *
     * @param id:
     */
    @Transactional
    @PostMapping("/cancelbaby")
    public RestVo cancelBaby(Long id) {
        return babyService.cancelBaby(id);
    }


    //******************************************* 医生端接口 *************************************************************

    /**
     * 家长端--获取我的宝宝信息
     *
     * @return
     */
    @ParentCheck
    @RequestMapping("myBabys")
    public RestVo myBabys() {
        UserInfo user = this.getUser();
        return babyService.myBabys(user.getId(), user.getMobile());
    }

    /**
     * 家长端-删除宝宝信息
     *
     * @param id
     * @return
     */
    @ParentCheck
    @RequestMapping("delBaby")
    public RestVo delBabyInfo(Long id) {
        UserInfo user = this.getUser();
        return babyService.delBabyInfo(id, user.getId(), user.getMobile());
    }

    /**
     * 家长端-保存宝宝信息
     *
     * @param babyInfo
     * @return
     */
    @ParentCheck
    @RequestMapping("addBabyInfo")
    public RestVo addBabyInfo(@RequestBody BabyInfo babyInfo) {
        return babyService.addBabyInfo(babyInfo, this.getUser().getId(), this.getDdCorpid());
    }

    @RequestMapping("test")
    public void test(){
        remindNewsJob.remindNewsJob();
    }
}
