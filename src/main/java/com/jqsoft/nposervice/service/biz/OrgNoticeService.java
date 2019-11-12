/**
 * FileName: OrgNoticeService
 * Author:   DR
 * Date:     2019/9/21 15:33
 * Description: 组织通知公告
 * History:
 */
package com.jqsoft.nposervice.service.biz;

import com.jqsoft.nposervice.commons.bo.PageBo;
import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.utils.OSSUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.NoticeInfo;
import com.jqsoft.nposervice.entity.system.FileEntity;
import com.jqsoft.nposervice.mapper.biz.InfoReadedUserMapper;
import com.jqsoft.nposervice.mapper.biz.MemberNoticeInfoMapper;
import com.jqsoft.nposervice.mapper.biz.OrgNoticeMapper;
import com.jqsoft.nposervice.service.system.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈组织通知公告〉
 *
 * @author DR
 * @create 2019/9/21
 * @since 1.0.0
 */
@Slf4j
@Service
public class OrgNoticeService {

    @Autowired(required = false)
    public OrgNoticeMapper orgNoticeMapper;

    @Autowired(required = false)
    public MemberNoticeInfoMapper memberNoticeInfoMapper;

    @Resource
    private FileService fileService;

    @Resource
    private OSSUtils ossUtils;

    @Resource
    private InfoReadedUserMapper infoReadedUserMapper;
    /**
     * 获取组织通知公告信息
     * @param pageBo
     * @return
     */
    public RestVo selectNoticeInfoByUserId(PageBo<Map<String, Object>> pageBo, String orgId) {
        return RestVo.SUCCESS(orgNoticeMapper.selectNoticeById(pageBo.getOffset(), pageBo.getSize(), pageBo.getParam(),orgId));
    }

    /**
     * 通过ID 获取组织通知公告信息
     * @param noticeId
     * @return
     */
    public RestVo getNoticeInfoById(String noticeId) {
        return RestVo.SUCCESS(orgNoticeMapper.selectById(noticeId));
    }

    /**
     * 发布组织通知公告信息
     * @param params
     * @return
     */
    public RestVo publishNoticeById(Map<String, Object> params) {
        if("1".equals(params.get("state"))){
            params.put("publish",new Timestamp(System.currentTimeMillis()));
        }else{
            params.put("publish",null);
            infoReadedUserMapper.deleteByInfoId((String) params.get("noticeId"));
        }

        return RestVo.SUCCESS(orgNoticeMapper.publishNoticeById(params));
    }
    /**
     * 保存组织通知公告信息
     * @return
     */
    public RestVo saveNotice(NoticeInfo noticeInfo) {
        Timestamp ts=new Timestamp(System.currentTimeMillis());
        Map<String, Object> params=new HashMap<>();
        noticeInfo.setUpdateTime(ts);
        if(noticeInfo.getState() == 1){
            noticeInfo.setPublish(ts);
        }
        noticeInfo.setCreateTime(ts);
        //新增
        return RestVo.SUCCESS( orgNoticeMapper.saveNotice(noticeInfo));//返回新的参数

    }
    /**
     * 删除组织通知公告信息
     * @return
     */
    public RestVo deleteNotice(String id, String orgId) {
        Map<String, Object> params=new HashMap<>();
        params.put("orgId",orgId);
        orgNoticeMapper.deleteNotice(id);
        List<FileEntity> fileEntityList= memberNoticeInfoMapper.selectNoticeFileInfoByNoticeId(id);
        orgNoticeMapper.deleteFile(id);
        for(FileEntity f:fileEntityList){
            ossUtils.delete(f.getPath());
        }
        return RestVo.SUCCESS();
    }

}
