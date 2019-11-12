package com.jqsoft.nposervice.service.system;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiCspaceGetCustomSpaceRequest;
import com.dingtalk.api.request.OapiCspaceGrantCustomSpaceRequest;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.response.OapiCspaceGetCustomSpaceResponse;
import com.dingtalk.api.response.OapiCspaceGrantCustomSpaceResponse;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.jqsoft.nposervice.commons.constant.RedisKey;
import com.jqsoft.nposervice.commons.constant.ResultMsg;
import com.jqsoft.nposervice.commons.utils.RedisUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.controller.dd.DdCallBack;
import com.jqsoft.nposervice.entity.system.FileEntity;
import com.jqsoft.nposervice.mapper.system.FileMapper;
import com.jqsoft.nposervice.service.biz.OrgInfoService;
import com.taobao.api.ApiException;
import com.taobao.api.FileItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileService {
    @Resource
    private FileMapper fileMapper;
    @Resource
    private DdCallBack callBack;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private OrgInfoService orgInfoService;

    public void save(FileEntity fileEntity) {
        fileMapper.insert(fileEntity);
    }

    public FileEntity get(String id) {
        return fileMapper.selectById(id);
    }

    public RestVo getList(String recordid, Byte type) {
        List<FileEntity> files = fileMapper.selectByRecordIdAndType(recordid, type);
//        files.forEach(file -> file.setPath(endpoint + file.getPath()));
        return RestVo.SUCCESS(files);
    }

    public void delete(FileEntity fileEntity) {
        fileMapper.deleteById(fileEntity.getId());
    }

    public RestVo getDingTalkSpaceId(String corpId) {
        String spaceId = (String) redisUtils.get(RedisKey.DINGTALK_SPACE_ID.getKey(corpId));
        if (StringUtils.isNotBlank(spaceId)) {
            return RestVo.SUCCESS(spaceId);
        }
        String corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
        if (StringUtils.isBlank(corpAccessToken)) {
            corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
            //把corpAccessToken放入缓存中,设置失效时间110分钟（钉钉设置corpAccessToken的失效时间为120分钟），如果缓存中有就重置失效时间
            redisUtils.add(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId), corpAccessToken, 110, TimeUnit.MINUTES);
        }
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/cspace/get_custom_space");
        OapiCspaceGetCustomSpaceRequest request = new OapiCspaceGetCustomSpaceRequest();
        request.setAgentId(orgInfoService.selectByCorpId(corpId).getAgentId());
//        request.setDomain("test");
        request.setHttpMethod("GET");
        OapiCspaceGetCustomSpaceResponse response;
        try {
            response = client.execute(request, corpAccessToken);
            redisUtils.add(RedisKey.DINGTALK_SPACE_ID.getKey(corpId), response.getSpaceid(), 1, TimeUnit.HOURS);
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("获取企业钉盘spaceId失败，企业corpId:{}失败原因：{}", corpId, e);
            return RestVo.FAIL(ResultMsg.DD_GET_SPACEID_FAIL);
        }
        return RestVo.SUCCESS(response.getSpaceid());
    }

    public RestVo grant(String userId, String corpId, String grantType, String fileids) {
        boolean hadGranted = redisUtils.exists(RedisKey.DING_PAN_GRANT.getKey(corpId, userId, grantType, fileids));
        if (hadGranted) {
            return RestVo.SUCCESS();
        }
        String corpAccessToken = (String) redisUtils.get(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId));
        if (StringUtils.isBlank(corpAccessToken)) {
            corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
            //把corpAccessToken放入缓存中,设置失效时间110分钟（钉钉设置corpAccessToken的失效时间为120分钟），如果缓存中有就重置失效时间
            redisUtils.add(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId), corpAccessToken, 110, TimeUnit.MINUTES);
        }
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/cspace/grant_custom_space");
        OapiCspaceGrantCustomSpaceRequest request = new OapiCspaceGrantCustomSpaceRequest();
        request.setAgentId(orgInfoService.selectByCorpId(corpId).getAgentId());
//        request.setDomain("test");
        request.setType(grantType);
        request.setPath("/");
        if (StringUtils.equals("download", grantType)) {
            request.setFileids(fileids);
        }
        request.setUserid(userId);
        request.setDuration(600L);
        request.setHttpMethod("GET");
        OapiCspaceGrantCustomSpaceResponse response;
        try {
            response = client.execute(request, corpAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("企业钉盘授权失败，企业corpId:{},授权类型:{}失败原因：{}", corpId, grantType, e);
            return RestVo.FAIL(ResultMsg.DD_GRANT_FAIL);
        }
        if (StringUtils.equals("0", response.getErrorCode())) {
            redisUtils.add(RedisKey.DING_PAN_GRANT.getKey(corpId, userId, grantType, fileids), 1, 595, TimeUnit.SECONDS);
        }
        return RestVo.SUCCESS(response.getErrcode());
    }

    public void saveBatch(List<FileEntity> files) {
        fileMapper.saveBatch(files);
    }

    public RestVo uploadMedia(String corpId) {
        String corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
        if (StringUtils.isBlank(corpAccessToken)) {
            corpAccessToken = callBack.getCorpAccessToken(corpId).getAccessToken();
            //把corpAccessToken放入缓存中,设置失效时间110分钟（钉钉设置corpAccessToken的失效时间为120分钟），如果缓存中有就重置失效时间
            redisUtils.add(RedisKey.LOGIN_CORPACCESSTOKEN.getKey(corpId), corpAccessToken, 110, TimeUnit.MINUTES);
        }
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/media/upload");
        OapiMediaUploadRequest request = new OapiMediaUploadRequest();
        request.setType("file");
        request.setMedia(new FileItem("C:\\Users\\Administrator\\Desktop\\TIM图片20191023145031.png"));
        OapiMediaUploadResponse response;
        try {
            response = client.execute(request, corpAccessToken);
        } catch (ApiException e) {
            e.printStackTrace();
            return RestVo.FAIL();
        }
        return RestVo.SUCCESS(response);
    }

	/*public Page<FileEntity> selectFilePage(PageRequest<FileEntity> pageRequest) {
		// 单表查询  使用默认的查询方法，可以不写Mapper.xml
		// currentPage(pageRequest.getCurrentPage())的含义为：设定分页查询的当前页
		// pageSize(pageRequest.getPageSize())的含义为：设定每页显示的条目
		// like(StringTools.notEmpty(app),"APP", app) 的含义为：
		// 	        当app不为空时->进行如下查询 -> 字段 APP like '%值app%'
		// like(StringTools.notEmpty(name), "NAME", name) 的含义同上
		// descs("UPLOAD_TIME") 的含义为：
		//     将UPLOAD_TIME加入降序的字段，即按照UPLOAD_TIME 倒序。descs方法可以加入多个降序字段
		String app = StringTools.toString(pageRequest.getCondition("app"));
		String name = StringTools.toString(pageRequest.getCondition("name"));
		return this.selectPage(this.buildQueryWrapper()
								.currentPage(pageRequest.getCurrentPage())
								.pageSize(pageRequest.getPageSize())
								.like(StringTools.notEmpty(app), "APP", app)
								.like(StringTools.notEmpty(name), "NAME", name)
								.descs("UPLOAD_TIME"));
	}*/

}