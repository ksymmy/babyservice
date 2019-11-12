package com.jqsoft.nposervice.commons.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;

@Component
public class OSSUtils {

	@Value("${oss.url}")
	private String url;

	@Value("${oss.key}")
	private String key;

	@Value("${oss.secret}")
	private String secret;

	@Value("${oss.bucket}")
	private String bucket;

	@Value("${oss.endpoint}")
	private String endpoint;


	/**
	 * 获取阿里云OSS客户端对象
	 *
	 * @return
	 */
	@Bean
	public OSSClient ossClient(){
		return new OSSClient(url, key, secret);
	}

	/**
	 * 向阿里云的OSS存储中上传文件
	 *
	 * @param sourceFilePath 源文件地址
	 * @param targetFilePath 保存文件地址
	 * @return
	 */
	public boolean upload(String sourceFilePath, String targetFilePath){
		try{
			// 创建OSSClient实例
			OSSClient client = ossClient();

			//将图片路径转化为文件
			File file = new File(sourceFilePath);

			//将文件传给输入流
			InputStream ins = new FileInputStream(file);

			//创建上传Object的Metadata
			PutObjectResult putResult = client.putObject(bucket, targetFilePath, ins);

			ins.close();

			//解析结果
			String etag = putResult.getETag();

			// 关闭client
			client.shutdown();
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

    /**
     * 向阿里云的OSS存储中上传文件
     *
     * @param ins
     * @param targetFilePath 保存文件地址
     * @return
     */
    public boolean upload(InputStream ins, String targetFilePath){
        try{
            // 创建OSSClient实例
            OSSClient client = ossClient();

            //创建上传Object的Metadata
            PutObjectResult putResult = client.putObject(bucket, targetFilePath, ins);

            ins.close();

            //解析结果
            String etag = putResult.getETag();

            // 关闭client
            client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

	/**
	 * 向阿里云的OSS存储中上传Base64文件
	 *
	 * @param base64Data
	 * @param targetFilePath 保存文件地址
	 * @return
	 */
	public boolean uploadBase64(String base64Data, String targetFilePath){
		try{
			// 创建OSSClient实例
			OSSClient client = ossClient();

			// 图片数据解码
			byte[] asBytes = Base64.getDecoder().decode(base64Data.getBytes());

			//创建上传Object的Metadata
			PutObjectResult putResult = client.putObject(bucket, targetFilePath, new ByteArrayInputStream(asBytes));

			//解析结果
			String etag = putResult.getETag();

			// 关闭client
			client.shutdown();
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * 阿里云的OSS存储中文件访问路径
	 *
	 * @param filePath
	 * @return
	 */
	public String fmtAccessUrl(String filePath){

		if(CommUtils.isNull(filePath)){
			return "";
		}
		String imageUrl = endpoint + filePath;

		return imageUrl;
	}

	/**
	 * 从阿里云的OSS存储中下载文件
	 *
	 * @param filePath
	 * @param saveName
	 * @return
	 */
	public boolean download(String filePath, String saveName){
		try{
			// 创建OSSClient实例
			OSSClient client = ossClient();

			// 将图片路径转化为文件
			File file = new File(filePath);

			// 下载object到文件
			client.getObject(new GetObjectRequest(bucket, saveName), file);

			// 关闭client
			client.shutdown();
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * 从阿里云的OSS存储中删除文件
	 *
	 * @param filePath
	 * @return
	 */
	public boolean delete(String filePath){
		try{
			// 创建OSSClient实例
			OSSClient client = ossClient();

			// 下载object到文件
			client.deleteObject(bucket, filePath);

			// 关闭client
			client.shutdown();
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * 判断文件是否存在
	 * @param filePath
	 * @return
	 */
	public boolean isExist(String filePath) {
		boolean found = false;
		try{
			// 创建OSSClient实例
			OSSClient client = ossClient();

			// 判断文件是否存在
			found = client.doesObjectExist(bucket, filePath);

			// 关闭client
			client.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return found;
	}
}
