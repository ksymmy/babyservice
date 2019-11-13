package com.jqsoft.babyservice.commons.constant;

/**
 * @Description:
 * @Auther: luohongbing
 * @Date: 2019/9/11 15:37
 */
public enum RedisKey {

    LOGIN_TOKEN("LOGIN:TOKEN:%s"),
    LOGIN_USERID("LOGIN:USERID:%s"),
    LOGIN_CORP_USER("LOGIN:%s:%s"),

    ORG_INFO("ORG:INFO:%s"),
    REPEAT_SUBMIT("REPEAT_SUBMIT:%s:%s");


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
