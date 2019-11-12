package com.jqsoft.babyservice.commons.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageVo<T> implements Serializable {
	private static final long serialVersionUID = -5668554152671000202L;
	/**
	 * 当前页数
	 */
	private Integer page;

	/**
	 * 每页条数
	 */
	private Integer size;

	/**
	 * 总数
	 */
	private Long total;

	/**
	 * 总页数
	 */
	private Integer pageNum;

	/**
	 * 数据
	 */
	private List<T> datas;
}
