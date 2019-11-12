package com.jqsoft.nposervice.entity.system;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.jqsoft.persist.common.entity.SuperEntity;

import java.util.List;

/**
 * 
 * 实体-资源
 * @author wangjie
 *
 */
@Data
@TableName("t_resources")
@NoArgsConstructor
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Resources extends SuperEntity<String> {

	private static final long serialVersionUID = 1L;
	
	public static final String ROOT = "root";
	public static final int TYPE_MENU = 1;
	public static final int TYPE_FUNCTION = 2;
	
	private String  name; // 资源编码
    private String  title;// 资源名称
    private String  icon;// 图标
    private String  jump;// 访问地址
    private String pid;// 父ID
    private String  type;// 资源类型^1菜单，2功能
    private String  status;// 是否启用^0正常1不可用
    private Integer sort;// 序号
    private String  remark;// 资源描述
    
    @TableField(exist=false)
    private boolean spread = true;// layuiadmin 需要的字段，非持久
    @TableField(exist=false)
    private Integer children;// 子节点数量，非持久
    @TableField(exist=false)
    private List<Resources>  list;// 子节点，非持久
   
}