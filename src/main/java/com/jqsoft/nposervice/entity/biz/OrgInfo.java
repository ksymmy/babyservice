package com.jqsoft.nposervice.entity.biz;

import java.sql.Timestamp;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

/**
 * 组织信息
 */
@Data
@TableName("biz_org_info")
@NoArgsConstructor
public class OrgInfo extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;
  
    //授权方企业id
    private String corpid;

    //企业logo
    private String corpLogoUrl;

    //授权方企业名称
    private String corpName;

    //企业所属行业
    private String industry;

    //企业是否认证
    private Boolean isAuthenticated;

    //企业认证等级，0：未认证，1：高级认证，2：中级认证，3：初级认证
    private Long authLevel;

    //授权企业所在省份
    private String corpProvince;

    //授权企业所在城市
    private String corpCity;

    private String creditCode;

    private String orgTypeName;

    private String orgType;

    private String industryName;

    private String provinceId;

    private String cityId;

    private String countyId;

    private String detail;

    private String address;

    private String postalCode;

    private String email;

    private Date establishDate;

    private String phone;
    //
    private Timestamp createTime;

    //
    private Timestamp updateTime;
    
    private String agentId;

    @TableField(exist = false)
    private String establishDateStr;

}