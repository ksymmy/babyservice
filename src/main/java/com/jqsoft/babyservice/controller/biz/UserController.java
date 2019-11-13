package com.jqsoft.babyservice.controller.biz;

import com.jqsoft.babyservice.commons.interceptor.AdminCheck;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @AdminCheck
    @RequestMapping("/list")
    public RestVo selectList(int currentPageIndex, int pageSize) {
        HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("currentPageIndex", currentPageIndex);
            put("pageSize", pageSize);
        }};
        return null;
    }


}
