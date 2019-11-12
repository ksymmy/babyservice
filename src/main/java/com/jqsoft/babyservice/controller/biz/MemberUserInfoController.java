/**
 * FileName: MemberUserInfoController
 * Author:   Administrator
 * Date:     2019/9/11 11:51
 * Description: 会员信息维护
 * History:
 */
package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.interceptor.AuthCheck;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.entity.biz.UserInfo;
import com.jqsoft.babyservice.service.biz.MemberUserInfoService;
import com.jqsoft.babyservice.service.system.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 〈会员信息维护〉
 *
 * @author DR
 * @create 2019/9/11
 * @since 1.0.0
 */
@Slf4j
@RestController
@AuthCheck
@RequestMapping("memberUserInfo")
public class MemberUserInfoController extends BaseController {

    @Autowired
    public MemberUserInfoService memberUserInfoService;

    @Autowired
    public AreaService areaService;

    /**
     * 会员信息 获取 (个人/单位)
     *
     * @return
     */
    @RequestMapping("/findInfo")
    public RestVo findInfo() {
        return RestVo.SUCCESS(this.getUserInfo());
    }

    /**
     * 个人会员信息  保存
     *
     * @param userInfo
     * @return
     */
    @RequestMapping("/pInfoSave")
    public RestVo pInfoSave(@RequestBody UserInfo userInfo) {
        userInfo.setId(this.getUserId());
        log.info(String.valueOf(userInfo));
        return memberUserInfoService.updateByPID(userInfo);
    }

    /**
     * 单位会员信息 保存
     *
     * @param userInfo
     * @return
     */
    @RequestMapping("/cInfoSave")
    public RestVo cInfoSave(@RequestBody UserInfo userInfo) {
        userInfo.setId(this.getUserId());
        log.info(String.valueOf(userInfo));
        return memberUserInfoService.updateByCID(userInfo);
    }


    /**
     * 查询企业会员: user_type:  2：个人会员 3：单位会员
     *
     * @return
     */
    @GetMapping("/queryMemByCorpId")
    public RestVo queryMemByCorpId() {
        return memberUserInfoService.queryMemByCorpId(this.getOrgId());
    }

    /**
     * 会费标准
     *
     * @param type 会员类型
     * @return 会费标准
     */
    @GetMapping("/queryDuesStand")
    public RestVo queryDuesStand(@RequestParam String type) {
        String orgId = this.getOrgId();
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("corpid", orgId);
            put("type", type);
        }};
        return memberUserInfoService.queryDuesStand(params);
    }


    /**
     * 会员信息 获取 (个人/单位)
     *
     * @return
     */
    @RequestMapping("/findInfoById")
    public RestVo findInfoById(String id) {
        RestVo restVo = memberUserInfoService.findByID(id);
        return restVo;
    }

    /**
     * 个人会员信息  保存
     *
     * @param userInfo
     * @return
     */
    @PostMapping("/savePersonal")
    public RestVo savePersonal(@RequestBody UserInfo userInfo) {
        log.info(String.valueOf(userInfo));
        return memberUserInfoService.updateByPID(userInfo);
    }

    /**
     * 单位会员信息 保存
     *
     * @param userInfo
     * @return
     */
    @PostMapping("/saveUnit")
    public RestVo saveUnit(@RequestBody UserInfo userInfo) {
        log.info(String.valueOf(userInfo));
        if (StringUtils.isNotBlank(userInfo.getEstablishDateStr())) {
            userInfo.setEstablishDate(CommUtils.convertStringToDate(userInfo.getEstablishDateStr(), null));
        }
        String address = "";
        if (StringUtils.isNotBlank(userInfo.getProvinceId())) {
            address += areaService.getAreaName(userInfo.getProvinceId());
        }
        if (StringUtils.isNotBlank(userInfo.getCityId())) {
            address += areaService.getAreaName(userInfo.getCityId());
        }
        if (StringUtils.isNotBlank(userInfo.getCountyId())) {
            address += areaService.getAreaName(userInfo.getCountyId());
        }
        address += userInfo.getDetail();
        userInfo.setAddress(address);
        return memberUserInfoService.updateByCID(userInfo);
    }
}
