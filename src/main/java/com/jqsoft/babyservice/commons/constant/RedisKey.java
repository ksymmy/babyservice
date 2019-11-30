package com.jqsoft.babyservice.commons.constant;

/**
 * @Description:
 * @Auther: luohongbing
 * @Date: 2019/9/11 15:37
 */
public enum RedisKey {

    LOGIN_TOKEN("LOGIN:TOKEN:%s"),
    LOGIN_CORP("LOGIN:CORP:%s"),//corpid:HospitalInfo
    LOGIN_USERID("LOGIN:USERID:%s"),
    LOGIN_CORP_USER("LOGIN:%s:%s"),
    LOGIN_ACCESS_TOKEN("LOGIN:ACCESS-TOKEN:%s"),//corpid:AccessToken
    LOGIN_MOBILE_USERID("LOGIN:MOBILE-USERID:%s"),

    WORK_TIME("WORKTIME:%s"),
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
