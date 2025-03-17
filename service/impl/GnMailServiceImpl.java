import org.apache.commons.configuration.CompositeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

	private CompositeConfiguration config;
	
    @Autowired
    private CompositeConfiguration configuration;

	@Autowired
	private AdminDAO adminDAO;

    @Override
    public Object sendMail(AdminDTO adminDTO) throws Exception {

        HashMap<String, Object> body = new HashMap<String, Object>();
        List<AdminDTO> sendList = new ArrayList<AdminDTO>();

        try {
            sendList = adminDAO.selectAdminList(adminDTO);
        } catch (Exception e) {
            e.printStackTrace();
            body.put("result", "발송내용 select 오류");
			return body;
        }

        if (sendList.size() == 0) {
            body.put("result", "발송대상이 없습니다.");
			return body;
        }

        // 그룹웨어 IP
        String server_ip = SystemConstant.getServer_ip;

        // 1. 발신자 정보
        final String sender_id = SystemConstant.getSender_id;
        final String sender_pwd = SystemConstant.getSender_pwd;

        // 2. Property에 SMTP 서버 정보 설정
        Properties prop = new Properties();
        prop.put("mail.smtp.host", SystemConstant.getServer_ip);
        prop.put("mail.smtp.port", SystemConstant.port);
        prop.put("mail.smtp.auth", SystemConstant.mail_smtp_auth);
        prop.put("mail.smtp.debug", SystemConstant.mail_smtp_debug);
        prop.put("mail.smtp.starttls.enable", SystemConstant.mail_smtp_starttls_enable);

        String subject = "";
        String from = sender_id;
        String content = "";

        int resultCnt = 0;

        try {
            for (AdminDTO sendData : sendList) {
                resultCnt++;
                InternetAddress[] receive_id = new InternetAddress[1];
                receive_id[0] = new InternetAddress(sendData.getEmail());

                // 3. SMTP 서버정보와 사용자 정보를 기반으로 Session 클래스의 인스턴스 생성
                Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender_id, sender_pwd);
                    }
                });

                //4. Message 클래스의 객체를 사용하여 수신자와 내용, 제목의 메시지를 작성한다.
                //5. Transport 클래스를 사용하여 작성한 메세지를 전달한다.
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(sender_id, "발신자표시내용", "UTF-8"));
                message.addRecipients(Message.RecipientType.TO, receive_id);
                message.setSubject(subject);
                message.setContent(content, "text/html; charset=utf-8");
                //message.setText(content, true);
                // true는 html을 사용하겠다는 의미입니다.

                Transport.send(message);    // send message
            }
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        body.put("resultCnt", resultCnt);
        body.put("result", "OK");
        
		return body;

    }
}
