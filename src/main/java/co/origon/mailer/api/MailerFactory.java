package co.origon.mailer.api;

public interface MailerFactory {
    Mailer mailer(String languageCode);
}
