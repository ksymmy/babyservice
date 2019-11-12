package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import net.jqsoft.common.domain.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Auther: luohongbing
 * @Date: 2019/9/11 10:25
 */
@Slf4j
@Service
public class DemoService {

    public RestVo getData(String id) {
        if (StringUtils.isBlank(id)) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        if ("1".equals(id)) {
            return RestVo.SUCCESS();
        } else {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
    }
}
