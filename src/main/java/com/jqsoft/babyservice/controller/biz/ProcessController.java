package com.jqsoft.babyservice.controller.biz;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.google.common.collect.Lists;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.utils.DateUtil;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.dd.DdCallBack;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.MeetingInfo;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.entity.system.Dict;
import com.jqsoft.babyservice.service.biz.OrgInfoService;
import com.jqsoft.babyservice.service.biz.UserService;
import com.jqsoft.babyservice.service.system.DictService;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: created by ksymmy@163.com at 2019/10/17 16:41
 * @desc: 智能审批
 */
@RestController
@RequestMapping("/process")
public class ProcessController extends BaseController {
    @Resource
    private DdCallBack callBack;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private OrgInfoService orgInfoService;
    @Resource
    private UserService userService;
    @Resource
    DictService dictService;

    /**
     * 创建/修改审批模板
     *
     * @param corpId      corpId
     * @param processCode processCode
     */
    @PostMapping("/save")
    public RestVo signUpDemo(String corpId, String processCode) {

        String corpAccessToken = getAccessToken(corpId);

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/save");
        OapiProcessSaveRequest request = new OapiProcessSaveRequest();
        OapiProcessSaveRequest.SaveProcessRequest saveProcessRequest = new OapiProcessSaveRequest.SaveProcessRequest();
        saveProcessRequest.setDisableFormEdit(false);
//        saveProcessRequest.setFakeMode(true);
        saveProcessRequest.setName("会议报名7");
        if (StringUtils.isNotBlank(processCode)) {//审批模板的唯一码。在本接口中，如不传，表示新建一个模板。如传值了，表示更新所传值对应的审批模板
            saveProcessRequest.setProcessCode(processCode);
        }
        saveProcessRequest.setAgentid(getAgentid(corpId));

        // 注意，每种表单组件，对应的componentName是固定的，参照一下示例代码
        List<OapiProcessSaveRequest.FormComponentVo> formComponentList = Lists.newArrayList();

        // 会议类型
        OapiProcessSaveRequest.FormComponentVo meetingType = new OapiProcessSaveRequest.FormComponentVo();
        meetingType.setComponentName("TextField");
        OapiProcessSaveRequest.FormComponentPropVo meetingTypeProp = new OapiProcessSaveRequest.FormComponentPropVo();
        meetingTypeProp.setRequired(true);
        meetingTypeProp.setLabel("会议类型");
        meetingTypeProp.setPlaceholder("请输入");
        meetingTypeProp.setId("TextField-J78F050R");
        meetingType.setProps(meetingTypeProp);
        formComponentList.add(meetingType);

        // 会议主题
        OapiProcessSaveRequest.FormComponentVo meetingName = new OapiProcessSaveRequest.FormComponentVo();
        meetingName.setComponentName("TextareaField");
        OapiProcessSaveRequest.FormComponentPropVo meetingNameProp = new OapiProcessSaveRequest.FormComponentPropVo();
        meetingNameProp.setRequired(true);
        meetingNameProp.setLabel("会议主题");
        meetingNameProp.setPlaceholder("请输入");
        meetingNameProp.setId("TextareaField-J78F050S");
        meetingName.setProps(meetingNameProp);
        formComponentList.add(meetingName);

        //日期区间
        OapiProcessSaveRequest.FormComponentVo dateRangeComponent = new OapiProcessSaveRequest.FormComponentVo();
        dateRangeComponent.setComponentName("DDDateRangeField");
        OapiProcessSaveRequest.FormComponentPropVo dateRangeComponentProp = new OapiProcessSaveRequest.FormComponentPropVo();
        dateRangeComponentProp.setRequired(true);
        dateRangeComponentProp.setLabel(JSON.toJSONString(Arrays.asList("会议开始", "会议结束")));
        dateRangeComponentProp.setPlaceholder("请选择");
        dateRangeComponentProp.setUnit("小时"); // 小时或天
        dateRangeComponentProp.setId("DDDateRangeField-J78F057Q");
        dateRangeComponent.setProps(dateRangeComponentProp);
        formComponentList.add(dateRangeComponent);

        // 会议地点
        OapiProcessSaveRequest.FormComponentVo meetingAddress = new OapiProcessSaveRequest.FormComponentVo();
        meetingAddress.setComponentName("TextField");
        OapiProcessSaveRequest.FormComponentPropVo meetingAddressProp = new OapiProcessSaveRequest.FormComponentPropVo();
        meetingAddressProp.setRequired(true);
        meetingAddressProp.setLabel("会议地点");
        meetingAddressProp.setPlaceholder("请输入");
        meetingAddressProp.setId("TextField-J78F051R");
        meetingAddress.setProps(meetingAddressProp);
        formComponentList.add(meetingAddress);

        // 主办方
        OapiProcessSaveRequest.FormComponentVo meetingSponsor = new OapiProcessSaveRequest.FormComponentVo();
        meetingSponsor.setComponentName("TextField");
        OapiProcessSaveRequest.FormComponentPropVo meetingSponsorProp = new OapiProcessSaveRequest.FormComponentPropVo();
        meetingSponsorProp.setRequired(true);
        meetingSponsorProp.setLabel("主办方");
        meetingSponsorProp.setPlaceholder("请输入");
        meetingSponsorProp.setId("TextField-J78F052R");
        meetingSponsor.setProps(meetingSponsorProp);
        formComponentList.add(meetingSponsor);

        // 会议内容
        OapiProcessSaveRequest.FormComponentVo meetingContext = new OapiProcessSaveRequest.FormComponentVo();
        meetingContext.setComponentName("TextareaField");
        OapiProcessSaveRequest.FormComponentPropVo meetingContextProp = new OapiProcessSaveRequest.FormComponentPropVo();
        meetingContextProp.setRequired(true);
        meetingContextProp.setLabel("会议内容");
        meetingContextProp.setPlaceholder("请输入");
        meetingContextProp.setId("TextareaField-J78F051S");
        meetingContext.setProps(meetingContextProp);
        formComponentList.add(meetingContext);

//        // 附件
//        OapiProcessSaveRequest.FormComponentVo attachmentComponent = new OapiProcessSaveRequest.FormComponentVo();
//        attachmentComponent.setComponentName("DDAttachment");
//        OapiProcessSaveRequest.FormComponentPropVo attachmentComponentProp = new OapiProcessSaveRequest.FormComponentPropVo();
//        attachmentComponentProp.setRequired(true);
//        attachmentComponentProp.setLabel("附件");
//        attachmentComponentProp.setId("DDAttachment-J78F0572");
//        attachmentComponent.setProps(attachmentComponentProp);
//        formComponentList.add(attachmentComponent);

        // 内部联系人
        OapiProcessSaveRequest.FormComponentVo innerContactComponent = new OapiProcessSaveRequest.FormComponentVo();
        innerContactComponent.setComponentName("InnerContactField");
        OapiProcessSaveRequest.FormComponentPropVo innerContactComponentProp = new OapiProcessSaveRequest.FormComponentPropVo();
        innerContactComponentProp.setRequired(true);
        innerContactComponentProp.setLabel("申请人");
        innerContactComponentProp.setChoice(0L); // 是否支持多选 "1" or "0"
        innerContactComponentProp.setId("InnerContactField-J78F0574");
        innerContactComponent.setProps(innerContactComponentProp);
        formComponentList.add(innerContactComponent);

//        // 单行文本框
//        OapiProcessSaveRequest.FormComponentVo singleInput = new OapiProcessSaveRequest.FormComponentVo();
//        singleInput.setComponentName("TextField");
//        OapiProcessSaveRequest.FormComponentPropVo singleInputProp = new OapiProcessSaveRequest.FormComponentPropVo();
//        singleInputProp.setRequired(true);
//        singleInputProp.setLabel("报名人");
//        singleInputProp.setPlaceholder("请输入");
//        singleInputProp.setId("TextField-J78F056R");
//        singleInput.setProps(singleInputProp);
//        formComponentList.add(singleInput);

        // 日期
        OapiProcessSaveRequest.FormComponentVo dateComponent = new OapiProcessSaveRequest.FormComponentVo();
        dateComponent.setComponentName("DDDateField");
        OapiProcessSaveRequest.FormComponentPropVo dateComponentProp = new OapiProcessSaveRequest.FormComponentPropVo();
        dateComponentProp.setRequired(true);
        dateComponentProp.setLabel("报名时间");
        dateComponentProp.setPlaceholder("请选择");
        dateComponentProp.setUnit("小时"); // 小时或天
        dateComponentProp.setId("DDDateField-J8MTJZVE");
        dateComponent.setProps(dateComponentProp);
        formComponentList.add(dateComponent);

        saveProcessRequest.setFormComponentList(formComponentList);
        request.setSaveProcessRequest(saveProcessRequest);

        OapiProcessSaveResponse response = null;
        try {
            response = client.execute(request, corpAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(JSON.toJSONString(response));
        return RestVo.SUCCESS(response);
    }

    /**
     * 查询已设置为条件得表单组件
     *
     * @param corpId      corpId
     * @param processCode processCode
     */
    @PostMapping("/condition")
    public RestVo condition(String corpId, String processCode) {
        String corpAccessToken = getAccessToken(corpId);
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/form/condition/list");
        OapiProcessFormConditionListRequest req = new OapiProcessFormConditionListRequest();
        OapiProcessFormConditionListRequest.QureyProcessRequest obj1 = new OapiProcessFormConditionListRequest.QureyProcessRequest();
        obj1.setAgentid(getAgentid(corpId));
        obj1.setProcessCode(processCode);
        req.setRequest(obj1);
        OapiProcessFormConditionListResponse rsp = null;
        try {
            rsp = client.execute(req, corpAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp);
    }

    /**
     * 创建流程实例
     *
     * @param corpId      corpId
     * @param processCode processCode
     */
    @PostMapping("/createprocessinstance")
    public RestVo createProcessInstance(String corpId, String processCode, String meetingId) {
        String corpAccessToken = getAccessToken(corpId);
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/create");
        OapiProcessinstanceCreateRequest request = new OapiProcessinstanceCreateRequest();
        request.setProcessCode(processCode);
        List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValues = new ArrayList<>();//new ArrayList<FormComponentValueVo>();
        MeetingInfo meetingInfo = userService.queryByID(meetingId);
        Dict dict = dictService.queryByID(meetingInfo.getType());
        UserInfo userInfo = this.getUserInfo();
        // 附件
        OapiProcessinstanceCreateRequest.FormComponentValueVo attachmentComponent = new OapiProcessinstanceCreateRequest.FormComponentValueVo();

        /*// qiye
        JSONObject attachmentJson = new JSONObject();
        attachmentJson.put("fileId", "6433971134");
        attachmentJson.put("fileName", "IMG_2644.JPG");
        attachmentJson.put("fileType", "jpg");
        attachmentJson.put("spaceId", "540150983");
        attachmentJson.put("fileSize", "1110333");*/

//        JSONObject attachmentJson = new JSONObject();
//        attachmentJson.put("fileId", "9051579598");
//        attachmentJson.put("fileName", "Screenshot_20190929_150612_com.alibaba.android.rimet_[B@f31c2d3(2)");
//        attachmentJson.put("fileType", "jpg");
//        attachmentJson.put("spaceId", "2011770766");
//        attachmentJson.put("fileSize", "40873");
//        JSONObject attachmentJson = new JSONObject();
//        attachmentJson.put("fileId", "6433971140");
//        attachmentJson.put("fileName", "2644.JPG");
//        attachmentJson.put("fileType", "jpg");
//        attachmentJson.put("spaceId", "1635477658");
//        attachmentJson.put("fileSize", "333");

//        JSONArray array = new JSONArray();
//        array.add(attachmentJson);
//        attachmentComponent.setValue(array.toJSONString());
//        attachmentComponent.setName("附件");
//        formComponentValues.add(attachmentComponent);

        // 会议类型
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo1 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo1.setName("会议类型");
        vo1.setValue(dict.getName());
        formComponentValues.add(vo1);

        //会议主题
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo2 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo2.setName("会议主题");
        vo2.setValue(meetingInfo.getName());
        formComponentValues.add(vo2);

        //会议时间
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo3 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo3.setName(JSON.toJSONString(Arrays.asList("会议开始", "会议结束")));
        String[] arr = new String[]{DateUtil.formatDate(meetingInfo.getMeetingStart(), "yyyy-MM-dd HH:mm"),
                DateUtil.formatDate(meetingInfo.getMeetingEnd(), "yyyy-MM-dd HH:mm")};
        vo3.setValue(JSON.toJSONString(arr));
        formComponentValues.add(vo3);

        //会议地点
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo4.setName("会议地点");
        vo4.setValue(meetingInfo.getAddress());
        formComponentValues.add(vo4);

        //主办方
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo5 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo5.setName("主办方");
        vo5.setValue(meetingInfo.getSponsor());
        formComponentValues.add(vo5);

        //会议内容
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo6 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo6.setName("会议内容");
        vo6.setValue(meetingInfo.getContext());
        formComponentValues.add(vo6);


        String[] arr2 = new String[]{userInfo.getUserid()};
        // 申请人
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo11 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo11.setName("申请人");
        vo11.setValue(JSON.toJSONString(arr2));
        formComponentValues.add(vo11);

//        // 单行输入框
//        OapiProcessinstanceCreateRequest.FormComponentValueVo vo12 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
//        vo12.setName("报名人");
//        vo12.setValue("报名人的姓名");
//        formComponentValues.add(vo12);

        // 日期
        OapiProcessinstanceCreateRequest.FormComponentValueVo vo8 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
        vo8.setName("报名时间");
        vo8.setValue(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm"));
        formComponentValues.add(vo8);

        List<UserInfo> userInfoList = userService.queryUserByOrgID(userInfo.getOrgId(), meetingInfo.getCreateUserId());
        List<String> list = new ArrayList<>();
        for (UserInfo user : userInfoList) {
            if (!userInfo.getUserid().equals(user.getUserid())) list.add(user.getUserid());
        }

        request.setFormComponentValues(formComponentValues);
//        request.setApprovers("2709666825648829,manager9448");//审批人
        OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo approverVo = new OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo();
        approverVo.setUserIds(list);
        approverVo.setTaskActionType("OR");
        List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> app = new ArrayList<>();
        app.add(approverVo);
        request.setApproversV2(app);
        request.setOriginatorUserId(userInfo.getUserid());//发起人
        request.setCcList("");
        System.out.println(request.getCcList());
        request.setCcPosition("START_FINISH");
        request.setDeptId(-1L);
        request.setAgentId(getAgentid(corpId));
        OapiProcessinstanceCreateResponse response = null;
        try {
            response = client.execute(request, corpAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        System.out.println(JSON.toJSONString(response));
        return RestVo.SUCCESS(response);
    }

    /**
     * 获取访问凭证
     *
     * @param corpId corpId
     */
    private String getAccessToken(String corpId) {
        String corpAccessToken = (String) redisUtils.get(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId));
        if (StringUtils.isBlank(corpAccessToken)) {
            corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
            //把corpAccessToken放入缓存中,设置失效时间110分钟（钉钉设置corpAccessToken的失效时间为120分钟），如果缓存中有就重置失效时间
            redisUtils.add(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId), corpAccessToken, 110, TimeUnit.MINUTES);
        }
        return corpAccessToken;
    }

    /**
     * 获取agentId
     *
     * @param corpId corpId
     */
    private Long getAgentid(String corpId) {
        return Long.valueOf(orgInfoService.selectByCorpId(corpId).getAgentId());
    }

    /**
     * 创建或者更新自定义工作流模板
     *
     * @param corpId      corpId
     * @param processCode processCode 为空是创建,非空为更新
     */
    @PostMapping("/createCustomTemplate")
    public RestVo createCustomTemplate(String corpId, String processCode) {

        String corpAccessToken = getAccessToken(corpId);

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/save");

        OapiProcessSaveRequest request = new OapiProcessSaveRequest();
        OapiProcessSaveRequest.SaveProcessRequest saveProcessRequest = new OapiProcessSaveRequest.SaveProcessRequest();
        saveProcessRequest.setDisableFormEdit(true);
        saveProcessRequest.setName("自定义会议报名流程");
        saveProcessRequest.setDescription("自定义的模板");
        if (StringUtils.isNotBlank(processCode)) {
            saveProcessRequest.setProcessCode(processCode);
        }
        saveProcessRequest.setAgentid(getAgentid(corpId));
        saveProcessRequest.setFakeMode(true);

        // 注意，每种表单组件，对应的componentName是固定的，参照一下示例代码
        List<OapiProcessSaveRequest.FormComponentVo> formComponentList = Lists.newArrayList();

        // 单行文本框
        OapiProcessSaveRequest.FormComponentVo singleInput = new OapiProcessSaveRequest.FormComponentVo();
        singleInput.setComponentName("TextField");
        OapiProcessSaveRequest.FormComponentPropVo singleInputProp = new OapiProcessSaveRequest.FormComponentPropVo();
        singleInputProp.setRequired(true);
        singleInputProp.setLabel("会议名称");
        singleInputProp.setPlaceholder("请输入");
        singleInputProp.setId("TextField-J78F056R");
        singleInput.setProps(singleInputProp);
        formComponentList.add(singleInput);

        // 多行文本框
        OapiProcessSaveRequest.FormComponentVo multipleInput = new OapiProcessSaveRequest.FormComponentVo();
        multipleInput.setComponentName("TextareaField");
        OapiProcessSaveRequest.FormComponentPropVo multipleInputProp = new OapiProcessSaveRequest.FormComponentPropVo();
        multipleInputProp.setRequired(true);
        multipleInputProp.setLabel("会议内容");
        multipleInputProp.setPlaceholder("请输入");
        multipleInputProp.setId("TextareaField-J78F056S");
        multipleInput.setProps(multipleInputProp);
        formComponentList.add(multipleInput);


        // 日期
        OapiProcessSaveRequest.FormComponentVo dateComponent = new OapiProcessSaveRequest.FormComponentVo();
        dateComponent.setComponentName("DDDateField");
        OapiProcessSaveRequest.FormComponentPropVo dateComponentProp = new OapiProcessSaveRequest.FormComponentPropVo();
        dateComponentProp.setRequired(true);
        dateComponentProp.setLabel("报名时间");
        dateComponentProp.setPlaceholder("请选择");
        dateComponentProp.setUnit("小时"); // 小时或天
        dateComponentProp.setId("DDDateField-J8MTJZVE");
        dateComponent.setProps(dateComponentProp);
        formComponentList.add(dateComponent);

        // 日期区间
        OapiProcessSaveRequest.FormComponentVo dateRangeComponent = new OapiProcessSaveRequest.FormComponentVo();
        dateRangeComponent.setComponentName("DDDateRangeField");
        OapiProcessSaveRequest.FormComponentPropVo dateRangeComponentProp = new OapiProcessSaveRequest.FormComponentPropVo();
        dateRangeComponentProp.setRequired(true);
        dateRangeComponentProp.setLabel(JSON.toJSONString(Arrays.asList("会议开始", "会议结束")));
        dateRangeComponentProp.setPlaceholder("请选择");
        dateRangeComponentProp.setUnit("小时"); // 小时或天
        dateRangeComponentProp.setId("DDDateRangeField-J78F057Q");
        dateRangeComponent.setProps(dateRangeComponentProp);
        formComponentList.add(dateRangeComponent);

        saveProcessRequest.setFormComponentList(formComponentList);
        request.setSaveProcessRequest(saveProcessRequest);

        OapiProcessSaveResponse response = null;
        try {
            response = client.execute(request, corpAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(JSON.toJSONString(response));
        return RestVo.SUCCESS(response);
    }

    /**
     * 删除为企业创建的工作流模板
     *
     * @param corpId      corpId
     * @param processCode processCode
     */
    @PostMapping("/deleteCustomTemplate")
    public RestVo deleteCustomTemplate(String corpId, String processCode) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/delete");
        OapiProcessDeleteRequest req = new OapiProcessDeleteRequest();
        OapiProcessDeleteRequest.DeleteProcessRequest obj1 = new OapiProcessDeleteRequest.DeleteProcessRequest();
        obj1.setAgentid(getAgentid(corpId));
        obj1.setProcessCode(processCode);
        req.setRequest(obj1);
        OapiProcessDeleteResponse rsp = null;
        try {
            rsp = client.execute(req, getAccessToken(corpId));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp.getBody());
    }

    /**
     * 创建自定义流程实例
     *
     * @param corpId      corpId
     * @param processCode processCode
     */
    @PostMapping("/createCustomProcessInstance")
    public RestVo createCustomProcessInstance(String corpId, String processCode) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/workrecord/create");
        OapiProcessWorkrecordCreateRequest req = new OapiProcessWorkrecordCreateRequest();
        OapiProcessWorkrecordCreateRequest.SaveFakeProcessInstanceRequest obj1 = new OapiProcessWorkrecordCreateRequest.SaveFakeProcessInstanceRequest();
        obj1.setAgentid(getAgentid(corpId));
        obj1.setProcessCode(processCode);
        obj1.setOriginatorUserId("172241440630905872");
        obj1.setTitle("我是标题,你看到了没");
        List<OapiProcessWorkrecordCreateRequest.FormComponentValueVo> list3 = new ArrayList<>();

        OapiProcessWorkrecordCreateRequest.FormComponentValueVo obj4 = new OapiProcessWorkrecordCreateRequest.FormComponentValueVo();
        obj4.setName("会议名称");
        obj4.setValue("会议名称哈哈哈");
        obj4.setExtValue("ext111");
        list3.add(obj4);

        OapiProcessWorkrecordCreateRequest.FormComponentValueVo obj5 = new OapiProcessWorkrecordCreateRequest.FormComponentValueVo();
        obj5.setName("会议内容");
        obj5.setValue("会议内容哈哈哈22");
        obj5.setExtValue("ext222");
        list3.add(obj5);

        OapiProcessWorkrecordCreateRequest.FormComponentValueVo obj6 = new OapiProcessWorkrecordCreateRequest.FormComponentValueVo();
        obj6.setName("报名时间");
        obj6.setValue(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm"));
        obj6.setExtValue(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm"));
        list3.add(obj6);

        OapiProcessWorkrecordCreateRequest.FormComponentValueVo obj7 = new OapiProcessWorkrecordCreateRequest.FormComponentValueVo();
        obj7.setName(JSON.toJSONString(Arrays.asList("会议开始", "会议结束")));
        String[] arr = new String[]{DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm"),
                DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm")};
        obj7.setValue(JSON.toJSONString(arr));
        obj7.setExtValue(JSON.toJSONString(arr));
        list3.add(obj7);

        obj1.setFormComponentValues(list3);
        obj1.setUrl("http://ksymmy.vaiwan.com/ddProcess/taskList?userId=123");
        req.setRequest(obj1);
        OapiProcessWorkrecordCreateResponse rsp = null;
        try {
            rsp = client.execute(req, getAccessToken(corpId));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp.getBody());
    }

    /**
     * 创建待办事项
     *
     * @param corpId            corpId
     * @param processInstanceId processInstanceId
     * @param activityId        activityId
     */
    @PostMapping("/createCustomProcessTask")
    public RestVo createCustomProcessTask(String corpId, String processInstanceId, String activityId) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/workrecord/task/create");
        OapiProcessWorkrecordTaskCreateRequest req = new OapiProcessWorkrecordTaskCreateRequest();
        OapiProcessWorkrecordTaskCreateRequest.SaveTaskRequest obj1 = new OapiProcessWorkrecordTaskCreateRequest.SaveTaskRequest();
        obj1.setAgentid(getAgentid(corpId));
        obj1.setProcessInstanceId(processInstanceId);
        if (StringUtils.isNotBlank(activityId)) {
//        obj1.setActivityId(activityId);
        }
        List<OapiProcessWorkrecordTaskCreateRequest.TaskTopVo> list3 = new ArrayList<>();
        OapiProcessWorkrecordTaskCreateRequest.TaskTopVo obj4 = new OapiProcessWorkrecordTaskCreateRequest.TaskTopVo();
        list3.add(obj4);
        obj4.setUserid("012224366432353450");
        obj4.setUrl("http://ksymmy.vaiwan.com/ddProcess/audit?userId=1");
        OapiProcessWorkrecordTaskCreateRequest.TaskTopVo obj5 = new OapiProcessWorkrecordTaskCreateRequest.TaskTopVo();
        list3.add(obj5);
        obj5.setUserid("2709666825648829");
        obj5.setUrl("http://ksymmy.vaiwan.com/ddProcess/audit?userId=2");
        obj1.setTasks(list3);
        req.setRequest(obj1);
        OapiProcessWorkrecordTaskCreateResponse rsp = null;
        try {
            rsp = client.execute(req, this.getAccessToken(corpId));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp.getBody());
    }

    /**
     * 更新流程实例的状态
     *
     * @param corpId            corpId
     * @param processInstanceId processInstanceId
     */
    @PostMapping("/updateCustomProcessStatus")
    public RestVo updateCustomProcessStatus(String corpId, String processInstanceId) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/workrecord/update");
        OapiProcessWorkrecordUpdateRequest req = new OapiProcessWorkrecordUpdateRequest();
        OapiProcessWorkrecordUpdateRequest.UpdateProcessInstanceRequest obj1 = new OapiProcessWorkrecordUpdateRequest.UpdateProcessInstanceRequest();
        obj1.setAgentid(getAgentid(corpId));
        obj1.setProcessInstanceId(processInstanceId);
        obj1.setStatus("COMPLETED");//实例状态，分为COMPLETED, TERMINATED
        obj1.setResult("agree");
        req.setRequest(obj1);
        OapiProcessWorkrecordUpdateResponse rsp = null;
        try {
            rsp = client.execute(req, this.getAccessToken(corpId));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp.getBody());
    }

    /**
     * 更新待办任务的状态
     *
     * @param corpId            corpId
     * @param processInstanceId processInstanceId
     * @param taskId            taskId
     */
    @PostMapping("/updateCustomProcessTaskStatus")
    public RestVo updateCustomProcessTaskStatus(String corpId, String processInstanceId, Long taskId) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/workrecord/task/update");
        OapiProcessWorkrecordTaskUpdateRequest req = new OapiProcessWorkrecordTaskUpdateRequest();
        OapiProcessWorkrecordTaskUpdateRequest.UpdateTaskRequest obj1 = new OapiProcessWorkrecordTaskUpdateRequest.UpdateTaskRequest();
        obj1.setAgentid(getAgentid(corpId));
        obj1.setProcessInstanceId(processInstanceId);
        List<OapiProcessWorkrecordTaskUpdateRequest.TaskTopVo> list3 = new ArrayList<>();
        OapiProcessWorkrecordTaskUpdateRequest.TaskTopVo obj4 = new OapiProcessWorkrecordTaskUpdateRequest.TaskTopVo();
        list3.add(obj4);
        obj4.setTaskId(taskId);
        obj4.setStatus("COMPLETED");//任务状态，分为CANCELED和COMPLETED
        obj4.setResult("AGREE");//任务结果，分为agree和refuse
        obj1.setTasks(list3);
        req.setRequest(obj1);
        OapiProcessWorkrecordTaskUpdateResponse rsp = null;
        try {
            rsp = client.execute(req, this.getAccessToken(corpId));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp.getBody());
    }

    /**
     * 批量取消代办
     *
     * @param corpId            corpId
     * @param processInstanceId processInstanceId
     * @param activityId        任务组id，需要在调用创建任务接口时，主动设置该值
     */
    @PostMapping("/customProcessTaskgroupCancel")
    public RestVo customProcessTaskgroupCancel(String corpId, String processInstanceId, String activityId) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/workrecord/taskgroup/cancel");
        OapiProcessWorkrecordTaskgroupCancelRequest req = new OapiProcessWorkrecordTaskgroupCancelRequest();
        OapiProcessWorkrecordTaskgroupCancelRequest.UpdateTaskRequest obj1 = new OapiProcessWorkrecordTaskgroupCancelRequest.UpdateTaskRequest();
        obj1.setAgentid(getAgentid(corpId));
        obj1.setProcessInstanceId(processInstanceId);
        obj1.setActivityId(activityId);
        req.setRequest(obj1);
        OapiProcessWorkrecordTaskgroupCancelResponse rsp = null;
        try {
            rsp = client.execute(req, this.getAccessToken(corpId));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp.getBody());
    }

    /**
     * 查询用户待办任务
     *
     * @param corpId corpId
     * @param userId userId
     */
    @PostMapping("/customProcessTaskQuery")
    public RestVo customProcessTaskQuery(String corpId, String userId) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/process/workrecord/task/query");
        OapiProcessWorkrecordTaskQueryRequest req = new OapiProcessWorkrecordTaskQueryRequest();
        req.setUserid(userId);
        req.setOffset(0L);
        req.setCount(50L);
        req.setStatus(0L);
        OapiProcessWorkrecordTaskQueryResponse rsp = null;
        try {
            rsp = client.execute(req, this.getAccessToken(corpId));
        } catch (ApiException e) {
            e.printStackTrace();
        }
        System.out.println(rsp.getBody());
        return RestVo.SUCCESS(rsp.getBody());
    }


}
