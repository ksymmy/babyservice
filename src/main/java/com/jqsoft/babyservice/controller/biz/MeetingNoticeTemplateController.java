package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.interceptor.AuthCheck;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.MeetingNoticeTemplate;
import com.jqsoft.babyservice.service.biz.MeetingNoticeTemplateService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: created by ksymmy@163.com at 2019/9/20 14:10
 * @desc:
 */
@AuthCheck
@RestController
@RequestMapping("/meetingnoticetemplate")
public class MeetingNoticeTemplateController extends BaseController {

    @Resource
    private MeetingNoticeTemplateService templateService;

    /**
     * 模板列表
     *
     * @return
     */
    @GetMapping("/list")
    private RestVo list() {
        return templateService.selectList(this.getOrgId());
    }

    /**
     * 添加模板
     *
     * @param template
     * @return
     */
    @PostMapping("/add")
    private RestVo add(@RequestBody MeetingNoticeTemplate template) {
        String orgId = this.getOrgId();
        template.setOrgId(orgId);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("orgId", orgId);
            put("name", template.getName());
        }};
        if (1 == templateService.queryUnique(params)) {
            return RestVo.FAIL(ResultMsg.TEMPLATE_HAD_EXIST);
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        template.setId(CommUtils.getUUID());
        template.setCreateTime(now);
        template.setUpdateTime(now);
        return templateService.insert(template);
    }
}
