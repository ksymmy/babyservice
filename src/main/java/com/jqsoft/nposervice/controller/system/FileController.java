package com.jqsoft.nposervice.controller.system;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.google.common.collect.Lists;
import com.jqsoft.nposervice.commons.constant.ResultMsg;
import com.jqsoft.nposervice.commons.interceptor.LoginCheck;
import com.jqsoft.nposervice.commons.utils.DateUtil;
import com.jqsoft.nposervice.commons.utils.OSSUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.system.FileEntity;
import com.jqsoft.nposervice.service.system.FileService;
import com.taobao.api.FileItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jqsoft.common.lang.FileTools;
import net.jqsoft.common.lang.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * 文件上传下载controller
 */
@Slf4j
@LoginCheck
@RestController
@RequestMapping("/file")
public class FileController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Resource
    private FileService fileService;
    @Resource
    private OSSUtils ossUtils;

    @RequestMapping("/list")
    public String list(Model model) {
        return "/file/list";
    }

    @RequestMapping("/upload")
    public String upload() {
        return "/file/upload";
    }

    /**
     *
     * 重写父类方法、处理分页逻辑
     * 如果不需要分页则无需重写此方法
     * 路径：/file/page的请求地址，会落到此方法
     *
     */
	/*@Override
	protected Page<FileEntity> handlePage(PageRequest<FileEntity> pageRequest) {
		return this.service.selectFilePage(pageRequest);
	}*/

    /**
     * 单个上传，过滤重复文件
     */
    @PostMapping(value = "/upload")
    public RestVo doUpload(@RequestParam("file") MultipartFile file, FileEntity fileEntity) {
        if (null != file && file.getSize() > 0) {
//            String app = request.getParameter("app");
//            String meetingId = request.getParameter("meetingId");
            String name = FileTools.getFileName(file.getOriginalFilename());
            String extension = FileTools.getFileExtension(file.getOriginalFilename());
            long size = file.getSize();
            try {
                String id = StringTools.getUUID();
                String ossPath = DateUtil.formatDate(new Date(), DateUtil.FMT_1) + "/" + id + "." + extension;
//                File dest = new File(this.uploadPath + FileTools.FILE_SEPARATOR + id + "." + extension);
//                if (!dest.getParentFile().exists()) {
//                    dest.getParentFile().mkdirs();
//                }
                //将文件传给输入流后上传OSS
                boolean upload = ossUtils.upload(file.getInputStream(), ossPath);
                if (!upload) {
                    LOGGER.warn(file.getName() + " OSS上传失败");
                    return RestVo.FAIL();
                }
//                file.transferTo(dest);
//                FileEntity fileEntity = new FileEntity();
                fileEntity.setId(id);
//                fileEntity.setApp(app);
                fileEntity.setName(name);
                fileEntity.setExtension(extension);
                fileEntity.setPath(ossPath);
                fileEntity.setFileSize(size);
                fileEntity.setUploadTime(new Timestamp(System.currentTimeMillis()));
                fileService.save(fileEntity);
                return RestVo.SUCCESS(ossUtils.fmtAccessUrl(ossPath));
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.warn(file.getName() + " 上传失败", e);
                return RestVo.SUCCESS();
            }
        }
        return RestVo.FAIL(ResultMsg.FILE_IS_NULL);
    }

    /**
     * 钉盘上传记录
     *
     * @param dingFiles DingFile[]
     * @param recordId  recordId
     * @param type      根据需要传,可不传
     * @param app       app
     */
    @PostMapping(value = "/uploadDingTalk")
    public RestVo uploadDingTalk(@RequestBody DingFile[] dingFiles, String recordId, Byte type, String app) {
        List<FileEntity> files = Lists.newArrayList();
        FileEntity fileEntity;
        for (DingFile dingFile : dingFiles) {
            fileEntity = new FileEntity(StringTools.getUUID(), app, dingFile.getFileName(), dingFile.getFileType(),
                    dingFile.getFileId(), dingFile.getFileSize(), new Timestamp(System.currentTimeMillis()),
                    recordId, type, dingFile.getSpaceId(), dingFile.getFileId());
            files.add(fileEntity);
        }
        fileService.saveBatch(files);
        return RestVo.SUCCESS();
    }

    @RequestMapping("download")
    public void download(@RequestParam("id") String id, HttpServletResponse response) {
        FileEntity fileEntity = fileService.get(id);
        String fileName = fileEntity.getName() + "." + fileEntity.getExtension();
//        response.setHeader("content-type", "application/octet-stream");
//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
//            OutputStream output = response.getOutputStream();
        String accessUrl = ossUtils.fmtAccessUrl(fileEntity.getPath());
        ossUtils.download(accessUrl, fileName);
//            output.write(IoTools.inputStreamToBytes(new FileInputStream(accessUrl)));
//            output.flush();
    }

    /**
     * 查询业务附件
     *
     * @param recordid :
     * @param type     附件类型
     * @return
     */
    @GetMapping("/getlist")
    public RestVo getList(@RequestParam String recordid, @RequestParam(required = false) Byte type) {
        return fileService.getList(recordid, type);
    }

    @RequestMapping("deleteFile")
    public RestVo deleteFile(@RequestBody FileEntity fileEntity) {
        fileService.delete(fileEntity);
        return RestVo.SUCCESS(ossUtils.delete(fileEntity.getPath()));
    }

    /**
     * 获取企业钉盘spaceId
     *
     * @param corpId corpId
     */
    @GetMapping("/getSpace")
    public RestVo getSpace(@RequestParam(value = "corpId", required = false) String corpId) {
        return fileService.getDingTalkSpaceId(corpId);
    }

    /**
     * 企业钉盘授权
     *
     * @param corpId    corpId
     * @param grantType grantType 授权类型 add/download
     * @param fileids   fileids 授权类型为download时不为空
     */
    @GetMapping("/grant")
    public RestVo grant(@RequestParam(value = "corpId", required = false) String corpId,
                        @RequestParam(value = "grantType") String grantType,
                        @RequestParam(value = "fileids", required = false) String fileids) {
        return fileService.grant(this.getUserInfo().getUserid(), corpId, grantType, fileids);
    }

    /**
     * @param corpId corpId
     */
    @GetMapping("/uploadMedia")
    public RestVo uploadMedia(String corpId) {
        return this.fileService.uploadMedia(corpId);
    }

    @Data
    @NoArgsConstructor
    private static class DingFile {
        private String spaceId;
        private String fileId;
        private String fileName;
        private Long fileSize;
        private String fileType = "file";//默认file,防止上传没有格式的文件
    }
}