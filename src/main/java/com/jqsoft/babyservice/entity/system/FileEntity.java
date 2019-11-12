package com.jqsoft.babyservice.entity.system;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import net.jqsoft.persist.common.entity.SuperEntity;

import java.sql.Timestamp;

/**
 * 实体-文件
 *
 * @author wangjie
 */
@Data
@TableName("t_file")
public class FileEntity extends SuperEntity<String> {

    private static final long serialVersionUID = 1L;

    private String app;// 系统名称
    private String name;// 文件名称(不包含后缀)
    private String extension; // 文件类型（后缀）
    private String path; // 文件存储地址
    @TableField(value = "f_size")
    private long fileSize; // 文件大小
    private Timestamp uploadTime; // 上传日期
    private String md5; // MD5校验码
    private String remark; // 文件描述
    private String recordId;//文件外键
    private Byte type;//文件类型
    private String spaceId;//钉盘spaceId
    private String fileId;//钉盘文件id

    public FileEntity() {
    }

    public FileEntity(String id,String app, String name, String extension, String path, long fileSize, Timestamp uploadTime, String recordId, Byte type, String spaceId, String fileId) {
        this.setId(id);
        this.app = app;
        this.name = name;
        this.extension = extension;
        this.path = path;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
        this.recordId = recordId;
        this.type = type;
        this.spaceId = spaceId;
        this.fileId = fileId;
    }
}