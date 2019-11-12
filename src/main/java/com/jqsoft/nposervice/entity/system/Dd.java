package com.jqsoft.nposervice.entity.system;

import java.math.BigInteger;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@TableName("open_sync_biz_data")
@NoArgsConstructor
public class Dd {
	private BigInteger id;
	private Timestamp gmtCreate;
	private Timestamp gmtModified;
	private String subscribeId;
	private String corpId;
	private String bizId;
	private Long bizType;
	private String bizData;
	private BigInteger openCursor;
	private Long status;
//	private Timestamp beginTime;
//	private Timestamp endTime;
}
