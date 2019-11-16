package com.jqsoft.babyservice.commons.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * 钉钉消息
 */
@Data
public class DdMessage implements Serializable {

    private String title;
    private String context;
    private String userid;

}
