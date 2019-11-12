package com.jqsoft.nposervice.controller.system;

import com.jqsoft.nposervice.commons.interceptor.LoginCheck;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.service.system.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据字典controller
 */
@LoginCheck
@RestController
@RequestMapping("/dict")
public class DictController {

	@Autowired
	DictService dictService;

	/**
	 * 根据数据字典code获取字典信息
	 * @param code
	 * @return
	 */
	@GetMapping("getDictListByCode")
	public RestVo getDictListByCode(@Param("code") String code){
		return dictService.getDictListByCode(code);
	}

}