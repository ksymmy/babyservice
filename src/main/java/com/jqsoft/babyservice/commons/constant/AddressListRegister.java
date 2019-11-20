package com.jqsoft.babyservice.commons.constant;

/**
 * @author: created by ksymmy@163.com at 2019/11/19 17:36
 * @desc: 注册事件常量
 */
public class AddressListRegister {
    /**
     * 企业的corpid
     */
    public static final String CORPID = "dinge3e211ad45c983df35c2f4657eb6378f";
    /**
     * 钉钉开放平台上，开发者设置的token
     */
    public static final String TOKEN = "123456";
    /**
     * 数据加密密钥。用于回调数据的加密，长度固定为43个字符，从a-z, A-Z, 0-9共62个字符中选取,您可以随机生成，ISV(服务提供商)推荐使用注册套件时填写的EncodingAESKey
     */
    public static final String AES_KEY = "xxxxxxxxlvdhntotr3x9qhlbytb18zyz5zxxxxxxxxx";

    /**
     * 测试回调接口事件类型
     */
    public static final String CHECK_URL = "check_url";
    /**
     * 通讯录用户增加
     */
    public static final String USER_ADD_ORG = "user_add_org";
    /**
     * 通讯录用户更改
     */
    public static final String USER_MODIFY_ORG = "user_modify_org";
    /**
     * 通讯录用户离职
     */
    public static final String USER_LEAVE_ORG = "user_leave_org";
    /**
     * 加入企业后用户激活
     */
    public static final String USER_ACTIVE_ORG = "user_active_org";
    /**
     * 通讯录用户被设为管理员
     */
    public static final String ORG_ADMIN_ADD = "org_admin_add";
    /**
     * 通讯录用户被取消设置管理员
     */
    public static final String ORG_ADMIN_REMOVE = "org_admin_remove";
    /**
     * 通讯录企业部门创建
     */
    public static final String ORG_DEPT_CREATE = "org_dept_create";
    /**
     * 通讯录企业部门修改
     */
    public static final String ORG_DEPT_MODIFY = "org_dept_modify";
    /**
     * 通讯录企业部门删除
     */
    public static final String ORG_DEPT_REMOVE = "org_dept_remove";
    /**
     * 企业被解散
     */
    public static final String ORG_REMOVE = "org_remove";
    /**
     * 企业信息发生变更
     */
    public static final String ORG_CHANGE = "org_change";
    /**
     * 员工角色信息发生变更
     */
    public static final String LABEL_USER_CHANGE = "label_user_change";
    /**
     * 增加角色或者角色组
     */
    public static final String LABEL_CONF_ADD = "label_conf_add";
    /**
     * 删除角色或者角色组
     */
    public static final String LABEL_CONF_DEL = "label_conf_del";
    /**
     * 修改角色或者角色组
     */
    public static final String LABEL_CONF_MODIFY = "label_conf_modify";
    /**
     * 审批任务开始，结束，转交
     */
    public static final String SEALAPPLY_TASK_CHANGE = "bpms_task_change";
    /**
     * 审批实例开始，结束
     */
    public static final String SEALAPPLY_INSTANCE_CHANGE = "bpms_instance_change";
}
