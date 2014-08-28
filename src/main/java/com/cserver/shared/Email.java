package com.cserver.shared;
import org.apache.commons.mail.*;


public class Email {
	private static final String TAG = "Email";

	public static void sendEmail(String attachPath, String attachDescription, String attachName,
			String dstEmail, String subject, String msg, String srcGmailAccount, String srcSecret) throws EmailException {

			  EmailAttachment attachment = null;
			  if (attachPath != null) {
					attachment = new EmailAttachment();
					attachment.setPath(attachPath);
					attachment.setDisposition(EmailAttachment.ATTACHMENT);
					attachment.setDescription(attachDescription);
					attachment.setName(attachName);
			  }
			  
			  // Create the email message
			  MultiPartEmail email = new MultiPartEmail();
			  email.setHostName("smtp.googlemail.com");
			  email.setSmtpPort(465);
			  email.setAuthentication(srcGmailAccount, srcSecret);
			  email.setSSLOnConnect(true);		  

			  email.addTo(dstEmail);
			  email.setFrom(srcGmailAccount + "@gmail.com");
			  email.setSubject(subject);
			  email.setMsg(msg);
			  
			  // add the attachment
			  if (attachment != null)
				  email.attach(attachment);
			  
			  // send the email
			  SLog.i(TAG, "email sending");
			  email.send();
			  SLog.i(TAG, "email was sent");
	}
}
