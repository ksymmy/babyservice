package com.jqsoft.nposervice.commons.constant;

/**
 * @Description:
 * @Auther: luohongbing
 * @Date: 2019/9/11 15:37
 */
public enum RedisKey {

    LOGIN_TOKEN("LOGIN:TOKEN:%s"),
    LOGIN_USERID("LOGIN:USERID:%s"),
    ORG_ID("ORG:ID:%s"),
    LOGIN_CORPACCESSTOKEN("LOGIN:CORPACCESSTOKEN:%s"),

    ORG_INFO("ORG:INFO:%s"),
    USER_INFO("USER:INFO:%s"),
    USER_RESOURCES_JUMP("USER:RESOURCES:JUMP:%s"),
    DICT_TYLE("DICT:TYPE:%s"),
    REPEAT_SUBMIT("REPEAT_SUBMIT:%s:%s"),

    MEETING_INFO("MEETING_INFO:%s"),
    MEETING_NOTICE_INFO("MEETING_NOTICE_INFO:%s"),

    SUITE_TICKET("SUITE_TICKET:%s"),

    DING_PAN_GRANT("DING_PAN_GRANT:%s_%s_%s_%s"),//corpId_userId_grantType_fileId

    DINGTALK_SPACE_ID("DINGTALK_SPACE_ID:%s");//corpId

    RedisKey(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    public String getKey(String... keys) {
        return String.format(key, keys);
    }
}
