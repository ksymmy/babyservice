package com.jqsoft.babyservice.entity.biz;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 消息已读用户
 */
@Data
@NoArgsConstructor
public class InfoReadedUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String infoId;

    private String userId;

    private String userName;

    private Timestamp readTime;


}