package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.HospitalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: created by ksymmy@163.com at 2019/11/29 9:15
 * @desc: 各医院信息
 */
@Slf4j
@RestController
@RequestMapping("/hospital")
public class HospitalController extends BaseController {

    @Resource
    private HospitalService hospitalService;

}
