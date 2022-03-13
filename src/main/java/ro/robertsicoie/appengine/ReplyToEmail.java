package ro.robertsicoie.appengine;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

public class ReplyToEmail {
    public  boolean sendEmailStatus=false;
    public SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
    private static final Logger logger = Logger.getLogger("ReplyToEmail");
    public static String from = "prasanna.pujari66@gmail.com";
    public static String pass ="xyboiirowplwsbrn";
    // Recipient's email ID needs to be mentioned.
    public static String host = "smtp.gmail.com";
    public static ReplyToEmail replyToEmail=new ReplyToEmail();

    public static  Session session;

    public String  toEmail;
    public Folder inbox ;
    public Message[] messages;
    public String subject;

   /* public static void main(String[] args) {
       // ba68fc71-073b-4da1-994a-4fd915d66c49

       *//* ReplyToEmail replyToEmail=new ReplyToEmail();
        replyToEmail.callEmail();*//*
    }*/

    public boolean callEmail(){
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        // Get the default Session object.
        session = Session.getDefaultInstance(properties);
        // create the imap store object and connect to the imap server

        try{
            Store store = session.getStore("imaps");
            store.connect(host, from, pass);
           /* while (!replyToEmail.sendEmailStatus) {*/
                replyToEmail.checkInbox(store);
           /* }
            if(replyToEmail.sendEmailStatus){*/
                store.close();
            //}
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }

        return  replyToEmail.sendEmailStatus;
    }
    public  void   checkInbox(Store store ){
        // Sender's email ID needs to be mentioned
       // System.out.println("----"+formatter.format(Calendar.getInstance().getTime())+"-----------");
        try{


            // create the inbox object and open it
            inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);
            messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            logger.info(formatter.format(Calendar.getInstance().getTime())+":-messages.length---" + messages.length);
            if(messages.length!=0){
                Message message = messages[messages.length-1];
                try {
                    toEmail=message.getFrom()[0].toString().toLowerCase(Locale.ROOT);
                    subject=message.getSubject().toLowerCase(Locale.ROOT);
                    logger.info(subject);
                    if(toEmail.contains("prasanna.kmr11@gmail.com")){
                        if(!subject.contains("south")&&!subject.contains("re:")){
                            if(subject.contains("stockdale")||subject.contains("thomaston")){
                                sendEmail(message);
                            }else{
                                String content=getTextFromMessage(message).toLowerCase(Locale.ROOT);
                                System.out.println("Text: " + content);
                                if(!content.contains("south") &&(content.contains("stockdale")||content.contains("thomaston"))){
                                    sendEmail(message);
                                }
                            }
                        }
                    }
                    message.setFlag(Flags.Flag.SEEN, true);
                }catch (IOException exception){
                }
            }
           //inbox.close(false);
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public  String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }
    private  void sendEmail( Message message){

        try{
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage=(MimeMessage) mimeMessage.reply(false);
            // Set From: header field of the header.
            mimeMessage.setFrom(new InternetAddress(from));
            // Set To: header field of the header.
            mimeMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
            //mimeMessage.setReplyTo(message.getReplyTo());
            // Set Subject: header field
            mimeMessage.setSubject("RE: "+ message.getSubject());
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Interested");
            // Create a multi-part to combine the parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            // Create and fill part for the forwarded content
            messageBodyPart = new MimeBodyPart();
            messageBodyPart.setDataHandler(message.getDataHandler());
            // Add part to multi part
            multipart.addBodyPart(messageBodyPart);
            // Associate multi-part with message
            mimeMessage.setContent(multipart);
            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
            replyToEmail.sendEmailStatus=true;
            logger.info("Email sent time:- "+formatter.format(Calendar.getInstance().getTime()));
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

}