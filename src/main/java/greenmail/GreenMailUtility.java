package greenmail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

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

			for (int i = 0, n = messages.length; i < n; i++) {
				Message message = messages[i];
				System.out.println("---------------------------------");
				System.out.println("Email Number " + (i + 1));
				System.out.println("Subject: " + message.getSubject());
				System.out.println("From: " + message.getFrom()[0]);
				System.out.println("Text: " + message.getContent().toString());
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

	private static void retrieveEmails() throws MessagingException {
		System.out.println("Getting emails...");
		try {
			ServerSetup imapConfig = new ServerSetup(3143, "auto-tester.bp-3.lan", "imap");
			System.out.println("Prepare server config, protocol: " + imapConfig.getProtocol() + ", port: "
					+ imapConfig.getPort() + ", host: " + imapConfig.getBindAddress());
			GreenMail greenMailIMAP = new GreenMail();
			System.out.println("Obtain imap server: " + greenMailIMAP.getImap());
			System.out.println("Obtain pop3 server: " + greenMailIMAP.getPop3());
			Session imapSession = greenMailIMAP.getImap().createSession();
			System.out.println("Obtain imap session: " + imapSession.getClass());
			Store store = imapSession.getStore("imap");
			// store.connect("auto-tester.bp-3.lan", "user2@", "pwd2");
			System.out.println("Obtain imap store: " + store);
			System.out.println("Received messages: " + greenMailIMAP.getReceivedMessages().length);

			// Folder inbox = store.getFolder("INBOX");
			// inbox.open(Folder.READ_ONLY);
			// Message msgReceived = inbox.getMessage(1);
			// System.out.println(msgReceived.getSubject());
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
