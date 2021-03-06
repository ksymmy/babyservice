package com.jqsoft.babyservice.commons.constant;

public enum ResultMsg {

    SUCCESS("0000", "操作成功"),
    FAIL("9999", "操作失败"),
    ERROR("4444", "系统异常"),


    CORPID_USERID_IS_NULL("0001","corpid或userid为空"),
    USER_NOT_EXISTS("0002","用户不存在"),
    NOT_AUTH("0003", "无此访问权限"),

    DATA_NOT_EXISTS("0004", "数据不存在"),
    BABY_NOT_EXISTS("0005", "宝宝信息不存在"),
    NOT_BABY_PARENT("0007","不是此宝宝家长"),
    NOT_PARAM("0008", "缺少参数"),
    DELAYED("0009", "每次体检只能改期一次"),


    MAIL_SEND_FAIL("0016","邮件发送失败"),
    OPERATE_FREQUENT("0017","操作太频繁,请稍后再试"),
    DD_GET_SPACEID_FAIL("0018","获取企业钉盘spaceId失败"),
    DD_GRANT_FAIL("0019","企业钉盘授权失败"),
    DD_GETCORPINFO_FAIL("0020","调用钉钉接口获取企业信息失败"),
    NOT_IN_ONE_MONTH("0021","只能改期一个月以内时间"),
    NOT_IN_WORK_TIME("0022","不在医院上班时间"),
    IN_HOLIDAYS("0023","不能选择法定节假日"),
    BEFORE_TIME("0024","不能选择当天或以前时间"),
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
