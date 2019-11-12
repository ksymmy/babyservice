package com.jqsoft.nposervice.controller.biz;

import com.jqsoft.nposervice.commons.bo.PageBo;
import com.jqsoft.nposervice.commons.interceptor.AuthCheck;
import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.controller.system.BaseController;
import com.jqsoft.nposervice.entity.biz.DuesRecord;
import com.jqsoft.nposervice.service.biz.DuesRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/9/12 17:25
 * @desc: 会费管理
 */
@AuthCheck
@RestController
@RequestMapping("/duesrecord")
public class DuesRecordController extends BaseController {

    @Resource
    private DuesRecordService duesRecordService;

    /**
     * 会费管理列表
     *
     * @param pageBo pageBo
     * @return RestVo
     */
    @PostMapping("/list")
    private RestVo list(@RequestBody PageBo<Map<String, Object>> pageBo) {
        return duesRecordService.queryDuesRecordList(pageBo, this.getOrgId());
    }

    /**
     * 获取会员缴费最后期限
     *
     * @param userId userId
     */
    @GetMapping("/getMaxEndDate")
    private RestVo getMaxEndDate(String userId) {
        return duesRecordService.getMaxEndDate(userId);
    }

    /**
     * 收取会费
     *
     * @param duesRecord 会费实体
     * @return RestVo
     */
    @PostMapping("/add")
    private RestVo add(@RequestBody DuesRecord duesRecord) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        duesRecord.setCreateTime(now);
        duesRecord.setUpdateTime(now);
        duesRecord.setId(CommUtils.getUUID());
        duesRecord.setOrgId(this.getOrgId());
        return duesRecordService.dues(duesRecord);
    }

    /**
     * 会费提醒
     *
     * @return RestVo
     */
    @PostMapping("/notice")
    private RestVo notice(@RequestBody PageBo<Map<String, Object>> pageBo) {
        return duesRecordService.notice(pageBo, this.getOrgId());
    }
}
