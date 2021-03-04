package jp.comfycolor.hibicomi.utils.mail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.comfycolor.hibicomi.bean.setting.SettingBean.MailBean;

public class MailUtils {

	public static Logger logger = LoggerFactory.getLogger(MailUtils.class);

	//エンコード指定
	private static final String ENCODE = "ISO-2022-JP";

	private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	/**
	 * 処理開始
	 *
	 * @param bean
	 * @param site
	 */
	public static void sendStart(String execType, MailBean mail, LocalDateTime now) {

		String subject = "ひびこみ処理開始 ["+ execType +"]";
		String body = "ひびこみ処理開始: "+ execType + "\n" + DT_FORMATTER.format(now);

		send(mail, subject, body);
	}


	/**
	 * 処理成功完了
	 *
	 * @param bean
	 * @param site
	 */
	public static void sendSuccess(String execType, MailBean mail, LocalDateTime now) {

		String subject = "ひびこみ処理完了 ["+ execType +"]";
		String body = "ひびこみ処理完了: "+ execType
				+ "\n開始時刻："+ DT_FORMATTER.format(now)
				+ "\n終了時刻："+ DT_FORMATTER.format(LocalDateTime.now());

		send(mail, subject, body);
	}

	/**
	 * 例外発生メール
	 *
	 * @param bean
	 * @param site
	 */
	public static void sendException(String execType, MailBean mail, Exception e) {
		// try-with-resources
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw);){
			// 例外出力
			e.printStackTrace(pw);

			String subject = "["+ execType +"]【エラー】例外発生";
			String body = "例外が発生しました。\n\n"+ sw.toString();

			send(mail, subject, body);
		} catch (Exception e2) {
			logger.error("sendException例外", e2);
		}
	}


	//ここからメール送付に必要なSMTP,SSL認証などの設定

	public static void send(MailBean bean, String subject, String body) {
		final Properties props = new Properties();

		props.put("mail.smtp.host","smtp.gmail.com");

        //GmailのSMTPを使う場合
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		//propsに設定した情報を使用して、sessionの作成
		final Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(bean.getId(), bean.getPasswd());
			}
		});


		// ここからメッセージ内容の設定。上記で作成したsessionを引数に渡す。
		final MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(
					bean.getFromAddress(), bean.getFromName(), ENCODE));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					bean.getToAddress(), bean.getToName(), ENCODE));

			// メールのSubject
			message.setSubject(subject, ENCODE);

			// メール本文
			message.setText(body, ENCODE);

			logger.debug("subject: "+ subject);

			// 送信日付
			message.setSentDate(new Date());
		} catch (MessagingException e) {
			logger.error("メール作成失敗", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("メール作成失敗", e);
		}

		// メール送信。
		try {
			Transport.send(message);
		} catch (AuthenticationFailedException e) {
			// 認証失敗
			logger.error("認証失敗", e);
		} catch (MessagingException e) {
			logger.error("smtpサーバへの接続失敗", e);
		} catch (Exception e) {
			logger.error("メール失敗全般", e);
		}
	}
}
