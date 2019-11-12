package com.jqsoft.nposervice.entity.system;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * ztree 插件的节点
 * 
 * @author wangjie
 *   
 */
@Data
@NoArgsConstructor
public class TreeNode {
	
	
	public static boolean  NODE_OPEN = true;
	public static boolean  NODE_CLOSE = false;
	
	private String id;
	private String pid;
	private String name;
	private String title;
	private int isHidden;
	private boolean open = NODE_OPEN;
	private String target;
	private String url;
	private int checked;
}