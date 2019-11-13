package com.jqsoft.babyservice.controller.biz;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.interceptor.AdminCheck;
import com.jqsoft.babyservice.commons.interceptor.UserCheck;
import com.jqsoft.babyservice.commons.interceptor.SubmitTarget;
import com.jqsoft.babyservice.commons.utils.CommUtils;
import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.controller.system.BaseController;
import com.jqsoft.babyservice.service.biz.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Auther: luohongbing
 * @Date: 2019/9/10 10:56
 */
@Slf4j
@RestController
@RequestMapping("demo")
public class DemoController extends BaseController {

    @Autowired
    public DemoService demoService;


    @Autowired
    public RedisUtils redisUtils;

//    @Autowired
//    public OSSUtils ossUtils;

    @AdminCheck
    @GetMapping("getData")
    public RestVo getData(@RequestParam(value = "id", required = true) String id) {
        log.info("获取数据 入参 id:{}", id);
        RestVo restVo = demoService.getData(id);
        log.info("获取数据 出参:{}", restVo);

        // Redis使用示例：
        redisUtils.add(RedisKey.ORG_INFO.getKey("123"), new JSONObject());
        return restVo;
    }

    @UserCheck
    @GetMapping("getData1")
    public RestVo getData1(@RequestParam(value = "id", required = true) String id) {
        log.info("获取数据 入参 id:{}", id);
        RestVo restVo = demoService.getData(id);
        log.info("获取数据 出参:{}", restVo);

        // Redis使用示例：
        redisUtils.add(RedisKey.ORG_INFO.getKey("123"), new JSONObject());
        return restVo;
    }

    @GetMapping("login")
    public RestVo login(@RequestParam(value = "userId", required = true) String userId) {
        String tokenKey = RedisKey.LOGIN_TOKEN.getKey(userId);
        String token = (String) redisUtils.get(tokenKey);
        if (StringUtils.isNotEmpty(token)) {
            return RestVo.SUCCESS(token);
        }
        token = CommUtils.getUUID();
        String userIdKey = RedisKey.LOGIN_USERID.getKey(token);
        redisUtils.add(tokenKey, token, RedisUtils.tokenExpire, TimeUnit.MINUTES);
        redisUtils.add(userIdKey, userId, RedisUtils.tokenExpire, TimeUnit.MINUTES);
        return RestVo.SUCCESS(token);
    }

    @UserCheck
    @GetMapping("getUserId")
    public RestVo getUserId(@RequestParam(value = "token", required = true) String token) {
        return RestVo.SUCCESS(this.getUserId());
    }


//    @GetMapping("upload")
//    public RestVo upload() {
//        String url = "C:\\Users\\JQ001\\Desktop\\图片\\logo.png";
//        String fileName = DateUtil.formatDate(new Date(), DateUtil.FMT_1) + "/" + CommUtils.getUUID() + ".png";
//        ossUtils.upload(url, fileName);
//        return RestVo.SUCCESS(ossUtils.fmtAccessUrl(fileName));
//    }

    @SubmitTarget
    @GetMapping("sendMail")
    public RestVo sendMail() {
        String[] res = {"1053122492@qq.com", "1053122492@qq.com"};
       /* try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
//        mailSenderService.sendMailBatch(res,"测试标题","测试内容");
        return RestVo.SUCCESS();
    }


    @RequestMapping("alipay")
    public String alipay(String out_trade_no,
                         String subject,
                         String total_amount,
                         String body) throws UnsupportedEncodingException {
        // 商户appid
        String APPID = "2016101400680732";
        // 私钥 pkcs8格式的
        String RSA_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCVMORf28nTAtzgcLrbloc27AnTE1Z5YBYWq3IU8yZ94AFY8oqRmm" +
                "1d/grmTRE2T23h1JO2VrWHL6FeXe7+Pd0/XutdF7P9zn8R0722tDKu8V0FDCQasDyF3xv+17p4jCKfn7iURX3/K1XEd7Zvbd1db+aJyIidzsU78fzLr560w52KnFM" +
                "InnAbkykY8/E+R0/j1CCXIHntZQ0WXBuYIDs5i7Z3sslRhGNmAyCIAX7vnqJ3rR0IYLfsl6WB7uoQXvYXTUVRCgHNBdc0IC6Y4nIEpiqd5GtdzJkEwiBAD1jsFC4WrliN" +
                "LES2WXzug+s6eqZsFf4uFaur2SAHZnXcrExZAgMBAAECggEAX3i5PIq3HfygAlTBjxJGFyR3qQaf+O4/VGSEjdmIEVdPJW+Ys1KimFqg+P3qfNnZlIHnwPC+G/OkDXKJJV" +
                "PLiOkiLyGjAlGpyWu55Pht0rwMbdV2KUoMgRLkpHHDF6993K5mfSP3XFqL+T0ulyI0Xb3lRtj4bBxM5W6W+nW5Vy0390DNEm2xWRdfZy8n/p6cVQ9soZLE3jgF6kWoU9YqJd" +
                "zknpVoid9cZPSTpxUX9WpheXob/CyUQB8VnY5IDWwCCKPKlOeJfb3RN3+XFRoDYnPOYmGJ1c1PrIFGzOJWm0GL/H+apV1qZOjqJSe3IPCCSpfbvuTnqLYzpY/7GlQBAQKBgQDY" +
                "JZVp6xKJiMK1ua8qJ3WUxwxi0ti2hftA6Pn/IheTpAQwh5wtGGnWdZVypH3ya98BoqlO33VAdaTsVr8c+Zn0OuG9FcVTJfxcF3h7IhpCVNIIPq8EZKnFIqbiO85UzmMOuVexmvzG" +
                "vfdLkzCyti9TulGy57mqI91jEwU+qt0/oQKBgQCwsueR9ano6aWXOvhiPQ7KYXvA0AvtnSka/22VTp3Z6Yo34CssmgmZCquPFul2dqjGcLobjuKpdYYaNsRPbPJptMfz+haDtVr6H" +
                "8s0fpH6lyq6Yyoe23Nt8JE9IvsuoJ+8jqTSBdvRiI+D83CwjMsZTAEc/rrZIWYNFBlUYeuxuQKBgBlTdgleGOLymlXutt6KlxViqUnfCLOCSWlWowxI81hCTJdfHGaQSL5vMp6nNXa" +
                "1OwEXDR2Hw1ZVMojatTPw7yAV0iN0rJVMiyBDQ8OxwD/rbly284zhOp81qzYw6cE4GZU9FFBkJ6Al3BcH3RnjjPITj8UVr8SGxUb5kJDCNoBhAoGAXcy7DUqiBdhyZROS42OSRHD" +
                "3ZuWKT3sMZR6PJ6FyPkmxa/P+N1EWIz380JrTGGXpNl6FDTgOrvKKW8QS9i7ZiK/FLYjnBsfxIbJbBuThzWsN7Z/SrkrEvenkz9WQDbKK2WfHqw4dIOAjf2JtwesbrbQhKyk2Oyl1" +
                "qR+wSpEos1ECgYBpX8zFU1na6L2P5UZw4aZBiAPNS9ElYY18tavvKbCqcQBysByHVESNzoR8eE50P2I9AtzZJp3EMH/XhGj4R75/vYnmKtzXwALw024FG6+mU+x0uhtuRYtNEa9rVowuePJZBDRk7HIDF7cvcgyKhzuYUgIKfX0758Z5vTrpsa41xQ==";
        // 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
        String notify_url = "http://www.baidu.com";
        // 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
        String return_url = "http://www.baidu.com";
        // 请求网关地址
        String URL = "https://openapi.alipaydev.com/gateway.do";
        // 编码
        String CHARSET = "UTF-8";
        // 返回格式
        String FORMAT = "json";
        // 支付宝公钥
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2QbiZXkHbWGg0vbutpCNnRcCiIwaHKEufIwcbgp4PT8fHRVPP+EFDCKqERa14T2ueapUrDUhxdTFd" +
                "XS72o0bUof7csRhpYLrSHmCeIg45+gEvOyFjnO3LWFwTV+koXK/5AHuBW7F4RF+18KoampojqfFfbdw3ioMiPVFbkJ4/+WC+scfSi7uh1j+OvTqNdfm0meTnZAe5NakWdanPeL46q2eDESS" +
                "o0QG2bfbfbWiROg4nqOlOcBf8HPM1vGwCi8koI093DM52l3CJ663ZMaXkZBI2N0PbR1HxI61Z2pUge7Xq+6+/8wUgK3yKuTuzmhsrbgawMn1e01/qiTalrNOKQIDAQAB";
        // RSA2
        String SIGNTYPE = "RSA2";

        // 商户订单号，商户网站订单系统中唯一订单号，必填
//        out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//        // 订单名称，必填
//        subject = new String(request.getParameter("subject").getBytes("ISO-8859-1"),"UTF-8");
//        // 付款金额，必填
//        total_amount=new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
//        // 商品描述，可空
//        body = new String(request.getParameter("body").getBytes("ISO-8859-1"),"UTF-8");
//         超时时间 可空
//        String timeout_express="30m";
        // 销售产品码 必填
        String product_code = "QUICK_WAP_WAY";
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(URL, APPID, RSA_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(out_trade_no);
        model.setSubject(subject);
        model.setTotalAmount(total_amount);
        model.setBody(body);
//        model.setTimeoutExpress(timeout_express);
        model.setProductCode(product_code);
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(notify_url);
        // 设置同步地址
        alipay_request.setReturnUrl(return_url);
//        AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest,"GET");
        // form表单生产
        String form = "";
        try {
            // 调用SDK生成表单
            form = client.pageExecute(alipay_request).getBody();
            System.out.println("===form:" + form);
            return form;
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return form;
    }

}
