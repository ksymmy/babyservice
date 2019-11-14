package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.bo.PageBo;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.BabyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: baby控制器
 * @Auther: luohongbing
 * @Date: 2019/11/13 18:59
 */
@Slf4j
@RestController
@RequestMapping("/baby")
public class BabyController extends BaseController {

    @Autowired
    public BabyService babyService;

    @RequestMapping("indexCount")
    public RestVo indexCount() {
//        select count(1) from biz_baby_info a
//        where a.corpid = '1'
//        and a.state = 1


//        select * from biz_baby_info a
//        where a.corpid = '1'
//        and a.state = 1
//        and exists
//        (select 1 from biz_examination_info b
//        where a.id = b.baby_id
//        and datediff(b.examination_date, curdate()) = 1)
        RestVo restVo = new RestVo();
        Map<String, String> map = new HashMap<>();
        map.put("overdueDays7", "1");
        map.put("overdueDays14", "1");
        map.put("overdueDays21", "1");
        map.put("tomorrowExaminationBabys", "10");
        map.put("changeDateBabys", "3");
        map.put("allBabys", "22");
        restVo.setData(map);
        return restVo;
    }

    /**
     * 医生端-逾期列表
     *
     * @param pageBo 查询参数
     */
    @PostMapping("/overduelist")
    public RestVo overdueList(@RequestBody PageBo<Map<String, Object>> pageBo, @RequestHeader("corpid") String corpid) {
        return babyService.overdueList(pageBo, corpid);
    }

}
