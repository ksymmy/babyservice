package com.jqsoft.babyservice.commons.bo;

import lombok.Data;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PageBo<T> implements Serializable {

	@NotNull(message = "起始页为空")
	@Min(value = 1, message = "起始页最小1")
	private Integer page;

	@NotNull(message = "页大小为空")
	@Min(value = 5, message = "页大小最小5")
	@Max(value = 100, message = "页大小最大100")
	private Integer size;

	private T param;

	private Integer offset;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public T getParam() {
		return param;
	}

	public void setParam(T param) {
		this.param = param;
	}

	public Integer getOffset() {
		return (this.getPage() - 1) * this.getSize();
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}
}
