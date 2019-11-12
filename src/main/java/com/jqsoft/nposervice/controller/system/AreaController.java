package com.jqsoft.nposervice.controller.system;

import com.jqsoft.nposervice.commons.interceptor.LoginCheck;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.system.Area;
import com.jqsoft.nposervice.service.system.AreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 地区信息controller
 */
@LoginCheck
@RestController
@RequestMapping("/area")
public class AreaController {

    @Autowired
    public AreaService areaService;

    /**
     * 通过区划编码获得所属区划
     * @param code 当前区划
     */
    @GetMapping("selectAreaByPCode")
    public RestVo<List<Area>> selectAreaByPCode(String code){
        if(StringUtils.isBlank(code)){
            // 最顶级的区划编码
            code = "000000000000";
        }
        return areaService.selectAreaByPCode(code);
    }

}
