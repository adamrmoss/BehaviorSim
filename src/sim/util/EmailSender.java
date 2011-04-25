/*
 * BehaviorSim - version 1.0 
 * 
 * Copyright (C) 2010 The BehaviorSim Development Team, fasheng@cs.gsu.edu.
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * 
 * Info, Questions, Suggestions & Bugs Report to fasheng@cs.gsu.edu.
 *  
 */

package sim.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * <p>
 * Title: Email sending central class
 * </p>
 * <p>
 * Description: It sends the specified information to the user
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: GSU
 * </p>
 * 
 * @author Fasheng Qiu
 * @version 1.0
 */
public class EmailSender {
	// The sender's email details
	private static final String d_email = "behaviorsim@gmail.com",
			d_password = "0qiufasheng", d_host = "smtp.gmail.com",
			d_port = "465";

	/**
	 * Send the result to behaviorsim@gmail.com
	 * 
	 * @param email
	 *            User's email address
	 * @param bugs
	 *            Target bug information
	 * @return Whether the sending is successful
	 */
	public boolean send(String email, String bugs) throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.user", d_email);
		props.put("mail.smtp.host", d_host);
		props.put("mail.smtp.port", d_port);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", d_port);
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		try {
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getInstance(props, auth);

			MimeMessage msg = new MimeMessage(session);
			msg.setSubject("Bugs of BehaviorSim v1.0");
			msg.setFrom(new InternetAddress(d_email));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					d_email));

			// create and fill the first message part
			MimeBodyPart mbp1 = new MimeBodyPart();
			StringBuffer sb = new StringBuffer();
			if (email != null)
				sb.append("From " + email).append("\n");
			sb.append(bugs);
			mbp1.setText(sb.toString());

			Multipart mp = new MimeMultipart();
			mp.addBodyPart(mbp1);

			msg.setContent(mp);

			Transport.send(msg);
		} catch (Exception mex) {
			mex.printStackTrace();
			MessageUtils.debug(this, "send", mex);
			throw mex;
		}
		return true;
	}

	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(d_email, d_password);
		}
	}
}