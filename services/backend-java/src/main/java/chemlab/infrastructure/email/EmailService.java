package chemlab.infrastructure.email;

import org.springframework.stereotype.Service;

//import com.sun.mail.smtp.SMTPTransport;
import jakarta.mail.Transport;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import static chemlab.infrastructure.email.EmailConstant.*;

@Service
public class EmailService {
    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", SMTP_SERVER);
        properties.put("mail.smtp.port", DEFAULT_PORT);
        return Session.getInstance(properties);
    }

    public void sendNewPasswordEmail(String firstName, String password, String email) {
        try {
            MimeMessage message = new MimeMessage(getEmailSession());
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
            message.setSubject(EMAIL_SUBJECT);
            message.setText("Hello " + firstName + ", here is your password: " + password + " <---.", "UTF-8"); // as "text/plain"
            message.setSentDate(new Date());
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

//    private Message createEmail(String firstName, String password, String email) throws MessagingException {
//        Message message = new MimeMessage(getEmailSession());
//        message.setFrom(new InternetAddress(FROM_EMAIL));
//        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
//        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(CC_EMAIL, false));
//        message.setSubject(EMAIL_SUBJECT);
//        message.setText("Hello " + firstName + ", \n\nYour new account password is: " + password +
//                "\n\n The Support Team");
//        message.setSentDate(new Date());
//        message.saveChanges();
//        return message;
//    }
}