package greenmail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

public class GreenMailUtility {

	public static void main(String[] args) throws IOException {
		try {
			// retrieveEmails();

			String host = "auto-tester.bp-3.lan";// change accordingly
			String mailStoreType = "pop3";
			String username = "user2";// change accordingly
			String password = "pwd2";// change accordingly
			int port = 110;

			simpleFetchMails(host, port, mailStoreType, username, password);
			deleteAllEmails(host, username, password);

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void simpleFetchMails(String host, int port, String mailStoreType, String user, String password)
			throws MessagingException, IOException {
		try {
			// Get a Session
			// create properties field
			Properties properties = new Properties();

			properties.put("mail.pop3.host", host);
			properties.put("mail.pop3.port", String.valueOf(port));
			properties.put("mail.pop3.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);

			// Create pop3 Store object and connect with pop server.
			Store store = emailSession.getStore(mailStoreType);
			store.connect(host, user, password);

			// create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();
			System.out.println("messages.length---" + messages.length);
			List<File> attachments = new ArrayList<File>();

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				System.out.println("---------------------------------");
				System.out.println("Email Number " + (i + 1));
				System.out.println("Subject: " + message.getSubject());
				System.out.println("From: " + message.getFrom()[0]);
				System.out.println("Text: " + getTextFromMessage(message));

				// Attachment
				Multipart multipart = (Multipart) message.getContent();
				System.out.println("Multipart: " + multipart.getContentType());
				for (int j = 0; j < multipart.getCount(); j++) {
					BodyPart bodyPart = multipart.getBodyPart(j);
					if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
							&& StringUtils.isBlank(bodyPart.getFileName())) {
						continue; // dealing with attachments only
					}
					InputStream is = bodyPart.getInputStream();
					System.out.println("Body part " + j + ": " + is.toString());
					File f = new File("C:/temp/" + bodyPart.getFileName());
					FileOutputStream fos = new FileOutputStream(f);
					byte[] buf = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buf)) != -1) {
						fos.write(buf, 0, bytesRead);
					}
					fos.close();
					attachments.add(f);

				}
				System.out.println("Attachment: " + attachments.size());

			}

			// close the store and folder objects
			emailFolder.close(false);
			store.close();

		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteAllEmails(String host, String user, String password) {
		try {
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "pop3");
			properties.put("mail.pop3s.host", host);
			properties.put("mail.pop3s.port", "110");
			properties.put("mail.pop3.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);
			// create the POP3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3");
			store.connect(host, user, password);
			// create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_WRITE);
			// retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				message.setFlag(Flags.Flag.DELETED, true);
			}
			// expunges the folder to remove messages which are marked deleted
			emailFolder.close(true);
			store.close();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private static String getTextFromMessage(Message message) throws MessagingException, IOException {
		String result = "";
		if (message.isMimeType("text/plain")) {
			result = message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			result = getTextFromMimeMultipart(mimeMultipart);
		}
		return result;
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		int count = mimeMultipart.getCount();
		for (int i = 0; i < count; i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				result = result + "\n" + bodyPart.getContent();
				break; // without break same text appears twice in my tests
			} else if (bodyPart.isMimeType("text/html")) {
				String html = (String) bodyPart.getContent();
				result = result + "\n" + Jsoup.parse(html).text();
			} else if (bodyPart.getContent() instanceof MimeMultipart) {
				result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
			}
		}
		return result;
	}

}
