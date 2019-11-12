package com.jqsoft.babyservice.entity.system;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

/**
 * 
 * 实体-字典
 * @author wangjie
 *
 */
@Data
@TableName("t_dict")
@NoArgsConstructor
public class Dict  extends SuperEntity<String>  {
	
	private static final long serialVersionUID = 1L;

	public static final String ROOT = "root";

	private String content;// 值
	private String code;// 编码
	private String name;// 名称
	private String pid;// 父ID
	private String remark;// 描述
	private Integer sort;// 序号
}