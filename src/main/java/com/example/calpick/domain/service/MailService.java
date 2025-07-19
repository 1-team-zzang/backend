package com.example.calpick.domain.service;

import com.example.calpick.domain.entity.Notification;
import com.example.calpick.domain.entity.enums.NotificationStatus;
import com.example.calpick.global.exception.CalPickException;
import com.example.calpick.global.exception.ErrorCode;
import com.example.calpick.domain.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender javaMailSender;
    private final NotificationRepository notificationRepository;



    private final TransactionTemplate transactionTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public MimeMessage createMail(String sendEmail,String senderName,String title,String date, String url,String type) throws MessagingException {

        MailMessageForm.MailMessage mail = null;

        if(type.equals("REQUEST")){
            mail = MailMessageForm.createRequestMessage(senderName,title,date,url);
        }else if(type.equals("ACCEPT")){
            mail = MailMessageForm.createConfirmMessage(senderName,title,date,url);
        }else if(type.equals("REJECT")){
            mail = MailMessageForm.createRejectMessage(senderName,title,date,url);
        }

        MimeMessage message = javaMailSender.createMimeMessage();


        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, sendEmail);
        message.setSubject(mail.getTitle()); //메일 제목
        String body = mail.getContent();

        message.setText(body, "UTF-8", "html");

        return message;
    }

    @Async("mailExecutor")
    public void sendSimpleMessageAsync(String senderEmail,String senderName,String title,Long notificationId,String date, String url,String type) throws Exception{

        MimeMessage message = createMail(senderEmail, senderName,title, date,url,type); // 메일 생성
        try {
            javaMailSender.send(message); // 메일 발송
            transactionTemplate.executeWithoutResult(status -> {
                updateNotificationStatus(notificationId, NotificationStatus.SUCCESS);
            });


        } catch (Exception e) {
            log.error("메일 발송 실패 - notificationId: {}",notificationId,e);
            transactionTemplate.executeWithoutResult(status -> {
                updateNotificationStatus(notificationId, NotificationStatus.SUCCESS);
            });

        }
    }
    public void updateNotificationStatus(Long notificationId, NotificationStatus status) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CalPickException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.setNotificationStatus(status);
    }
}
