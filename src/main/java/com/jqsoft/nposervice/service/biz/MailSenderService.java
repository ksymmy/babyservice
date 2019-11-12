package com.jqsoft.nposervice.service.biz;

import com.jqsoft.nposervice.commons.constant.ResultMsg;
import com.jqsoft.nposervice.commons.vo.RestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @Description:
 * @Auther: luohongbing
 * @Date: 2019/9/16 10:00
 */
@Slf4j
@Service
public class MailSenderService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.nick}")
    private String nick;


    /**
     *
     * @param receiver 接收人邮箱
     * @param subject 主题
     * @param text 内容
     * @return
     */
    public RestVo sendMail(String receiver, String subject, String text){
        log.info("普通邮件发送 入参 receiver:{} subject:{} text:{}", receiver, subject, text);
        if (StringUtils.isBlank(receiver) ||
                StringUtils.isBlank(subject) ||
                StringUtils.isBlank(text)) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }

        try {
            MimeMessage mimeMessage = this.getMimeMessage(nick, receiver, subject, text);
            mailSender.send(mimeMessage);
            log.info("普通邮件发送成功！");
            return RestVo.SUCCESS();
        } catch (Exception e) {
            log.error("普通邮件发送失败:", e);
            return RestVo.FAIL(ResultMsg.MAIL_SEND_FAIL);
        }
    }

    /**
     *
     * @param receivers 接收人邮箱(数组)
     * @param subject 主题
     * @param text 内容
     * @return
     */
    public RestVo sendMailBatch(String[] receivers, String subject, String text){
        log.info("群发邮件发送 入参 receiver:{} subject:{} text:{}", receivers, subject, text);
        if (null== receivers || receivers.length == 0 ||
                StringUtils.isBlank(subject) ||
                StringUtils.isBlank(text)) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }

        int length = receivers.length;
        MimeMessage[] msgs = new MimeMessage[length];
        try {
            for (int i = 0;i<length;i++) {
                MimeMessage mimeMessage = this.getMimeMessage(nick, receivers[i], subject, text);
                msgs[i] = mimeMessage;
            }
            mailSender.send(msgs);
        } catch (Exception e) {
            log.error("群发邮件发送失败:", e);
            return RestVo.FAIL(ResultMsg.MAIL_SEND_FAIL);
        }
        log.info("群发邮件发送成功！");
        return RestVo.SUCCESS();
    }

    /**
     * 生成简单邮件消息对象
     * @param receiver
     * @param subject
     * @param text
     * @return
     */
    private MimeMessage getMimeMessage(String nick, String receiver, String subject, String text)
            throws UnsupportedEncodingException, MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        nick=javax.mail.internet.MimeUtility.encodeText(nick);
        mimeMessage.setFrom(new InternetAddress(nick +" <"+from+">"));
        //设置收信人
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
        //设置邮件主题
        mimeMessage.setSubject(subject);
        //设置邮件正文
        mimeMessage.setText(text);
        return mimeMessage;
    }

    /**
     * (只能发送本地附件)
     * @param to 接收人邮箱
     * @param subject 主题
     * @param text 内容
     * @param filePaths 文件路径数组
     * @return
     */
    public RestVo sendMailWithFile(String to, String subject, String text, String[] filePaths){
        log.info("附件邮件发送 入参 to:{} subject:{} text:{} filePaths:{}", to, subject, text, filePaths);
        if (StringUtils.isBlank(to) ||
                StringUtils.isBlank(subject) ||
                StringUtils.isBlank(text)) {
            return RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        try {
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper= null;
            helper = new MimeMessageHelper(message,true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            //验证文件数据是否为空
            if(null != filePaths && filePaths.length > 0){
                FileSystemResource file=null;
                int length = filePaths.length;
                for (int i = 0; i < length; i++) {
                    //添加附件
                    file=new FileSystemResource(filePaths[i]);
                    helper.addAttachment(filePaths[i].substring(filePaths[i].lastIndexOf(File.separator)), file);
                }
            }
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("附件邮件发送异常：", e);
            return RestVo.ERROR();
        }
        log.info("附件邮件发送成功！");
        return RestVo.SUCCESS();
    }
}
