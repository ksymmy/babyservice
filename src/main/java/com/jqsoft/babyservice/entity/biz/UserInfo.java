package com.jqsoft.babyservice.entity.biz;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用户信息
 */
@Data
@TableName("biz_user_info")
@NoArgsConstructor
public class UserInfo extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String orgId;

    //授权方企业id
    private String corpId;

    private String userid;

    private String unionid;

    private String name;

    private Byte active;

    private String orderInDepts;

    private Byte isadmin;

    private Byte isboss;

    private Byte ishide;

    private String isleader;

    private Byte isSenior;

    private String department;

    private String position;

    private String avatar;

    private String jobnumber;

    private Date hireddate;

    private String idtype;

    private String idnumber;

    private String phome;

    private String postalCode;

    private String email;

    private String orgType;

    private String industry;

    private String provinceId;

    private String cityId;

    private String countyId;

    private String detail;

    private String address;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date establishDate;

    private Byte status;

    //审核时选择的用户类型
    private Byte userType;
    
    //当前用户类型
    private Byte curUserType;

    private String job;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    private String userToken;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date endDate;


    @TableField(exist = false)
    private String createTimeStr;

    public String getCreateTimeStr() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return createTime == null ? "" : simpleDateFormat.format(this.createTime);
    }

    @TableField(exist = false)
    private String establishDateStr;


    private String creditCode;

    private String enterpriseName;

    private String enterprisePhone;

    private String enterpriseEmail;

    private String enterprisePostalCode;



}