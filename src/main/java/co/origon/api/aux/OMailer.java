package co.origon.api.aux;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletResponse;

import co.origon.api.model.OMember;
import co.origon.api.model.OMembership;
import co.origon.api.model.OOrigo;


public class OMailer
{
    private static final String kFromAddress = "minion@origon.co";
    
    private static final String kLanguageEnglish = "en";
    private static final String kLanguageGerman = "de";
    private static final String kLanguageNorwegian = "nb";
    
    private OMeta m;
    
    
    private void sendEmail(String recipientAddress, String subject, String text)
    {
        Message message = new MimeMessage(Session.getInstance(new Properties()));
        
        try {
            InternetAddress sender = new InternetAddress(kFromAddress);
            sender.setPersonal("Origon");
            
            message.setFrom(sender);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
            message.setText(text);
            
            Transport.send(message);
            
            OLog.log().fine(m.meta() + String.format("Sent email with subject '%s' to %s", subject, recipientAddress));
            //OLog.log().fine(m.meta() + String.format("Sent email with subject '%s' and following body to %s:\n\n%s", subject, recipientAddress, text));
        } catch (Exception e) {
            OLog.log().warning(m.meta() + String.format("Caught exception: %s", e.getMessage()));
            OLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    
    private String availabilityInfo(String language, String registrationEmail)
    {
        String availabilityInfo = null;

        if (language.equals(kLanguageNorwegian)) {
            availabilityInfo = String.format("Origon er tilgjengelig for iOS 7 og senere (iPhone, iPad og iPod touch). " +
                                             "Last ned Origon fra App Store og registrer deg med %s for å komme i gang. " +
                                             "(Origon er foreløpig ikke tilgjengelig for Android eller Windows Phone.)", registrationEmail);
        } else if (language.equals(kLanguageGerman)) {
            availabilityInfo = String.format("Origon steht für iOS 7 und höher zur Verfügung (iPhone, iPad und iPod touch). " +
                                             "Zum Losfahren, Origon im App Store herunterladen und dich mit %s registrieren. " +
                                             "(Origon unterstützt zur Zeit nicht Android oder Windows Phone.)", registrationEmail);
        } else {
            availabilityInfo = String.format("Origon is available on iOS 7 and later (iPhone, iPad and iPod touch). " +
                                             "Download Origon from the App Store and register with %s to get going. " +
                                             "(Origon is currently not available on Android or Windows Phone.)", registrationEmail);
        }
        
        return availabilityInfo;
    }
    
    
    private String bestRegards(String language)
    {
        String bestRegards = null;
        
        if (language.equals(kLanguageNorwegian)) {
            bestRegards = "Med vennlig hilsen Origon-teamet\n" +
                          "http://origon.co\n";
        } else if (language.equals(kLanguageGerman)) {
            bestRegards = "Mit freundlichen Grüßen,\n" +
                          "das Origon-Team\n" +
                          "http://origon.co\n";
        } else {
            bestRegards = "Best regards,\n" +
                          "The Origon team\n" +
                          "http://origon.co\n";
        }
        
        return bestRegards;
    }
    
    
    private String invitationSubject(String language, OOrigo origo)
    {
        String invitationSubject = null;
        
        if (language.equals(kLanguageNorwegian)) {
            if (origo == null) {
                invitationSubject = "Epostadressen din er lagt inn på Origon";
            } else if (origo.isPrivate()) {
                invitationSubject = "Du har blitt lagt til i en privat liste på Origon";
            } else if (origo.isResidence()) {
                invitationSubject = "Husstanden din har blitt lagt inn på Origon";
            } else {
                invitationSubject = String.format("Du har blitt lagt inn i lista \"%s\" på Origon", origo.name);
            }
        } else if (language.equals(kLanguageGerman)) {
            if (origo == null) {
                invitationSubject = "Deine E-Mail-Adresse ist in Origon eingetragen worden";
            } else if (origo.isPrivate()) {
                invitationSubject = "Du bist in eine private Liste bei Origon eingetragen worden";
            } else if (origo.isResidence()) {
                invitationSubject = "Dein Haushalt ist bei Origon eingetragen worden";
            } else {
                invitationSubject = String.format("Du bist in die Liste \"%s\" bei Origon eingetragen worden", origo.name);
            }
        } else {
            if (origo == null) {
                invitationSubject = "Your email address has been added on Origon";
            } else if (origo.isPrivate()) {
                invitationSubject = "You have been added to a private list on Origon";
            } else if (origo.isResidence()) {
                invitationSubject = "Your household has been added on Origon";
            } else {
                invitationSubject = String.format("You have been added to the list '%s' on Origon", origo.name);
            }
        }
        
        return invitationSubject;
    }
    
    
    private String invitationBody(String language, OMembership membership, OOrigo origo)
    {
        String invitationBody = null;
        OMemberProxy memberProxy = m.getMemberProxy();
        
        if (language.equals(kLanguageNorwegian)) {
            if (origo == null) {
                invitationBody = String.format("%s (%s) har lagt inn epostadressen din på Origon", memberProxy.memberName, memberProxy.proxyId);
            } else if (origo.isPrivate()) {
                invitationBody = "Du har blitt lagt til i en privat liste på Origon";
            } else if (origo.isResidence()) {
                invitationBody = String.format("%s (%s) har lagt inn husstanden din på Origon", memberProxy.memberName, memberProxy.proxyId);
            } else if (membership.isAssociate() && !origo.isForMinors) {
                invitationBody = String.format("%s (%s) har lagt deg inn som assossiert medlem av lista \"%s\" på Origon", memberProxy.memberName, memberProxy.proxyId, origo.name);
            } else {
                invitationBody = String.format("%s (%s) har lagt deg inn i lista \"%s\" på Origon", memberProxy.memberName, memberProxy.proxyId, origo.name);
            }
        } else if (language.equals(kLanguageGerman)) {
            if (origo == null) {
                invitationBody = String.format("%s (%s) hat deine E-Mail-Adresse in Origon eingetragen", memberProxy.memberName, memberProxy.proxyId);
            } else if (origo.isPrivate()) {
                invitationBody = "Du bist in eine private Liste bei Origon eingetragen worden";
            } else if (origo.isResidence()) {
                invitationBody = String.format("%s (%s) hat deinen Haushalt bei Origon eingetragen", memberProxy.memberName, memberProxy.proxyId);
            } else if (membership.isAssociate() && !origo.isForMinors) {
                invitationBody = String.format("%s (%s) har dich als assoziiertes Mitglied der Liste \"%s\" bei Origon eingetragen", memberProxy.memberName, memberProxy.proxyId, origo.name);
            } else {
                invitationBody = String.format("%s (%s) hat dich in die Liste \"%s\" bei Origon eingetragen", memberProxy.memberName, memberProxy.proxyId, origo.name);
            }
        } else {
            if (origo == null) {
                invitationBody = String.format("%s (%s) has added your email address on Origon", memberProxy.memberName, memberProxy.proxyId);
            } else if (origo.isPrivate()) {
                invitationBody = "You have been added to a private list on Origon";
            } else if (origo.isResidence()) {
                invitationBody = String.format("%s (%s) has added your household on Origon", memberProxy.memberName, memberProxy.proxyId);
            } else if (membership.isAssociate() && !origo.isForMinors) {
                invitationBody = String.format("%s (%s) has added you as an associate member of the list '%s' on Origon", memberProxy.memberName, memberProxy.proxyId, origo.name);
            } else {
                invitationBody = String.format("%s (%s) has added you to the list '%s' on Origon", memberProxy.memberName, memberProxy.proxyId, origo.name);
            }
        }
        
        return invitationBody;
    }
    
    
    private String invitationText(String language, OMembership membership, OOrigo origo)
    {
        String invitationText = null;
        
        if (language.equals(kLanguageNorwegian)) {
            invitationText =
                    String.format("Hei!\n" +
                                  "\n" +
                                  invitationBody(kLanguageNorwegian, membership, origo) + ".\n" +
                                  "\n" +
                                  availabilityInfo(kLanguageNorwegian, membership.member.email) + "\n" +
                                  "\n" +
                                  bestRegards(kLanguageNorwegian));
        } else if (language.equals(kLanguageGerman)) {
            invitationText =
                    String.format("Hallo!\n" +
                                  "\n" +
                                  invitationBody(kLanguageGerman, membership, origo) + ".\n" +
                                  "\n" +
                                  availabilityInfo(kLanguageGerman, membership.member.email) + "\n" +
                                  "\n" +
                                  bestRegards(kLanguageGerman));
        } else {
            invitationText =
                    String.format("Hi!\n" +
                                  "\n" +
                                  invitationBody(kLanguageEnglish, membership, origo) + ".\n" +
                                  "\n" +
                                  availabilityInfo(kLanguageEnglish, membership.member.email) + "\n" +
                                  "\n" +
                                  bestRegards(kLanguageEnglish));
        }
        
        return invitationText;
    }
    
    
    private String emailChangeNotificationText(String language, OMemberProxy inviterProxy, OMember invitee, String oldEmail)
    {
        String emailChangeNotificationText = null;
        String inviteeGivenName = invitee.name.split(" ")[0];
        
        if (language.equals(kLanguageNorwegian)) {
            emailChangeNotificationText =
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) har endret epostadressen din på Origon fra %s til %s.\n" +
                                  "\n" +
                                  availabilityInfo(kLanguageNorwegian, invitee.email) + "\n" +
                                  "\n" +
                                  bestRegards(kLanguageNorwegian),
                                  inviteeGivenName, inviterProxy.memberName, inviterProxy.proxyId, oldEmail, invitee.email);
        } else if (language.equals(kLanguageGerman)) {
            emailChangeNotificationText =
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) hat deine E-Mail-Adresse bei Origon von %s auf %s geändert.\n" +
                                  "\n" +
                                  availabilityInfo(kLanguageGerman, invitee.email) + "\n" +
                                  "\n" +
                                  bestRegards(kLanguageGerman),
                                  inviteeGivenName, inviterProxy.memberName, inviterProxy.proxyId, oldEmail, invitee.email);
        } else {
            emailChangeNotificationText = 
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from %s to %s.\n" +
                                  "\n" +
                                  availabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  bestRegards(kLanguageEnglish),
                                  inviteeGivenName, inviterProxy.memberName, inviterProxy.proxyId, oldEmail, invitee.email);
        }

        return emailChangeNotificationText;
    }
    
    
    public OMailer(OMeta m)
    {
        this.m = m;
    }
    
    
    public void sendInvitation(String email)
    {
        String invitationText = invitationText(m.getLanguage(), null, null);
        
        if (!m.getLanguage().equals(kLanguageEnglish)) {
            invitationText = invitationText + "\n\n" + invitationText(kLanguageEnglish, null, null);
        }
        
        sendEmail(email, invitationSubject(m.getLanguage(), null), invitationText);
    }
    
    
    public void sendInvitation(OMembership membership, OOrigo origo)
    {
        String invitationText = invitationText(m.getLanguage(), membership, origo);
        
        if (!m.getLanguage().equals(kLanguageEnglish)) {
            invitationText = invitationText + "\n\n" + invitationText(kLanguageEnglish, membership, origo);
        }
        
        sendEmail(membership.member.email, invitationSubject(m.getLanguage(), origo), invitationText);
    }
    
    
    public void sendEmailChangeNotification(OMember invitee, String oldEmail)
    {
        String emailChangeNotificationSubject = null;
        
        if (m.getLanguage().equals(kLanguageNorwegian)) {
            emailChangeNotificationSubject = "Epostadressen din på Origon har blitt endret";
        } else if (m.getLanguage().equals(kLanguageGerman)) {
            emailChangeNotificationSubject = "Deine E-Mail-Adresse bei Origon ist geändert worden";
        } else {
            emailChangeNotificationSubject = "Your email address on Origon has been changed";
        }
        
        String emailChangeNotificationText = emailChangeNotificationText(m.getLanguage(), m.getMemberProxy(), invitee, oldEmail);

        if (!m.getLanguage().equals(kLanguageEnglish)) {
            emailChangeNotificationText = emailChangeNotificationText + "\n\n" + emailChangeNotificationText(kLanguageEnglish, m.getMemberProxy(), invitee, oldEmail);
        }
        
        sendEmail(invitee.email, emailChangeNotificationSubject, emailChangeNotificationText);
        sendEmail(oldEmail, emailChangeNotificationSubject, emailChangeNotificationText);
    }
    
    
    public void sendRegistrationEmail()
    {
        if (m.getLanguage().equals(kLanguageNorwegian)) {
            sendEmail(m.getEmail(), "Fullfør registreringen på Origon",
                    String.format("Velkommen til Origon!\n" +
                                  "\n" +
                                  "Alt som gjenstår før du kan begynne å bruke Origon, er å oppgi aktiveringskoden under og fylle ut evt manglende opplysninger.\n" +
                                  "\n" +
                                  "    Aktiveringskode: %s\n" +
                                  "\n" +
                                  "Merk: Opplysningene dine tilhører deg og bare deg. Vi kommer aldri til å selge dem videre eller bruke dem til noe annet enn det du kan " +
                                  "se med egne øyne i Origon.\n" +
                                  "\n" +
                                  bestRegards(kLanguageNorwegian),
                                  m.getActivationCode()));
        } else if (m.getLanguage().equals(kLanguageGerman)) {
            sendEmail(m.getEmail(), "Bitte deine Registrierung bei Origon vollenden",
                    String.format("Willkommen bei Origon!\n" +
                                  "\n" +
                                  "Bevor du Origon nutzen kannst, musst du den folgenden Aktivierungscode eingeben und alle eventuell fehlenden Informationen nachtragen." +
                                  "\n" +
                                  "    Aktivierungscode: %s\n" +
                                  "\n" +
                                  "Bitte beachten: Deine Informationen gehören dir und nur dir. Wir werden sie nie weiterverkaufen oder für andere Zwecke nutzen, als was du dir" +
                                  "mit eigenen Augen in Origon anschauen kannst." +
                                  "\n" +
                                  bestRegards(kLanguageGerman),
                                  m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Complete your registration with Origon",
                    String.format("Welcome to Origon!\n" +
                                  "\n" +
                                  "All that remains before you can start using Origon, is to enter the activation code below and fill out any missing information.\n" +
                                  "\n" +
                                  "    Activation code: %s\n" +
                                  "\n" +
                                  "Please note: Your information belongs to you and you alone. We will never sell it nor use it for anything besides what you can " +
                                  "see with your own eyes in Origon.\n" +
                                  "\n" +
                                  bestRegards(kLanguageEnglish),
                                  m.getActivationCode()));
        }
    }
    
    
    public void sendEmailActivationCode()
    {
        if (m.getLanguage().equals(kLanguageNorwegian)) {
            sendEmail(m.getEmail(), "Aktiver epostadressen din for bruk i Origon",
                    String.format("Oppgi følgende kode for å aktivere epostadressen %s for bruk i Origon.\n" +
                                  "\n" +
                                  "    Aktiveringskode: %s\n" +
                                  "\n" +
                                  bestRegards(kLanguageNorwegian),
                                  m.getEmail(), m.getActivationCode()));
        } else if (m.getLanguage().equals(kLanguageGerman)) {
            sendEmail(m.getEmail(), "Bitte deine E-Mail-Adresse für Gebrauch bei Origon aktivieren",
                    String.format("Bitte den folgenden Code eingeben, um die E-Mail-Adresse %s für Gebrauch bei Origon zu aktivieren.\n" +
                                  "\n" +
                                  "    Aktivierungscode: %s\n" +
                                  "\n" +
                                  bestRegards(kLanguageGerman),
                                  m.getEmail(), m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Activate your email address for use with Origon",
                    String.format("Please enter the following code to activate the email address %s for use with Origon.\n" +
                                  "\n" +
                                  "    Activation code: %s\n" +
                                  "\n" +
                                  bestRegards(kLanguageEnglish),
                                  m.getEmail(), m.getActivationCode()));
        }
        
        OLog.log().fine(m.meta() + "Sent email activation code to user.");
    }
    
    
    public void sendPasswordResetEmail()
    {
        if (m.getLanguage().equals(kLanguageNorwegian)) {
            sendEmail(m.getEmail(), "Passordet ditt har blitt tilbakestilt",
                    String.format("Ditt midlertidige passord er: %s.\n" +
                                  "\n" +
                                  "Så snart du har logget på Origon, bør du gå til Innstillinger og endre passordet til noe bare du vet om.\n" +
                                  "\n" +
                                  bestRegards(kLanguageNorwegian),
                                  m.getActivationCode()));
        } else if (m.getLanguage().equals(kLanguageGerman)) {
            sendEmail(m.getEmail(), "Dein Passwort ist zurückgestellt worden",
                    String.format("Dein temporäres Passwort ist: %s.\n" +
                                  "\n" +
                                  "Wenn du dich bei Origon eingeloggt hast, bitte unter Einstellungen ein Passwort wählen, das nur du kennst." +
                                  "\n" +
                                  bestRegards(kLanguageGerman),
                                  m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Your password has been reset",
                    String.format("Your temporary passord is: %s.\n" +
                                  "\n" +
                                  "Once you have logged in to Origon, you should go to Settings and change the password to something only you know.\n" +
                                  "\n" +
                                  bestRegards(kLanguageEnglish),
                                  m.getActivationCode()));
        }
        
        OLog.log().fine(m.meta() + "Sent new password to user.");
    }
}
