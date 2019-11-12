package com.jqsoft.nposervice.commons.constant;

public enum ResultMsg {

    SUCCESS("0000", "操作成功"),
    FAIL("9999", "操作失败"),
    ERROR("4444", "系统异常"),


    TOKEN_IS_NULL("0001","token为空"),
    NOT_LOGIN("0002","未登录"),
    USER_UNCHECK("0003", "用户未审核"),
    USER_CHECK_FAIL("0004", "用户审核不通过"),
    USER_IS_STOPED("0005", "用户被停用"),
    NOT_AUTH("0006", "无此访问权限"),

    LOGIN_FAIL("0007","登录失败"),
    NOT_PARAM("0008", "缺少参数"),
    ILLEGAL_PARAM_FORMAT("0009", "非法参数格式"),
    FILE_IS_NULL("0010", "空文件"),
    TEMPLATE_HAD_EXIST("0011","模板名称已存在"),
    
    DD_GETUSERID_FAIL("0012","调钉钉接口获取userid失败"),
	DD_GETUSERINFO_FAIL("0013","调钉钉接口获取用户信息失败"),
    STANDARDMESS_ONE("0014","当前会员类型已存在"),
    STANDARDMESS_MORE("0015","当前会员存在多条"),

    MAIL_SEND_FAIL("0016","邮件发送失败"),
    OPERATE_FREQUENT("0017","操作太频繁,请稍后再试"),
    DD_GET_SPACEID_FAIL("0018","获取企业钉盘spaceId失败"),
    DD_GRANT_FAIL("0019","企业钉盘授权失败"),
    DD_GETCORPINFO_FAIL("0020","调用钉钉接口获取企业信息失败"),
    ;

    private String code;
    private String name;

    ResultMsg(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
