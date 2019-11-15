package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.BabyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public BabyService babyService;

    /**
     * 医生端-首页统计
     *
     * @return
     */
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
    public RestVo overListCount(int overdueStart, int overdueEnd, int dingTimes, @RequestHeader("corpid") String corpid) {
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
     * 医生端-儿童档案-父母信息
     *
     * @param babyid babyid
     */
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
    @PostMapping("/tomorrownoticelist")
    public RestVo tomorrowNoticeList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.tomorrowNoticeList(pageBo, corpid);
    }

    /**
     * 医生端-明日体检-延后一天
     *
     * @param examId 体检id
     */
    @PostMapping("/delayoneday")
    public RestVo delayOneDay(Long examId) {
        return babyService.delayOneDay(examId);
    }

}
