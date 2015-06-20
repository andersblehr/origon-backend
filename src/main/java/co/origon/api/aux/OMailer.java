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
import co.origon.api.model.OOrigo;


public class OMailer
{
    private static final String kFromAddress = "minion@origon.co";
    
    private static final String kLanguageEnglish = "en";
    private static final String kLanguageNorwegianBokmal = "nb";
    
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
    
    
    private String origonInvitationSubject(String language, OOrigo origo)
    {
        String invitationSubject = null;
        
        if (language.equals(kLanguageNorwegianBokmal)) {
            if (origo == null) {
                invitationSubject = "Epostadressen din er lagt inn på Origon";
            } else if (origo.isPrivate()) {
                invitationSubject = "Du har blitt lagt til i en privat liste på Origon";
            } else if (origo.isResidence()) {
                invitationSubject = "Husstanden din har blitt lagt inn på Origon";
            } else {
                invitationSubject = String.format("Du har blitt lagt inn i lista \"%s\" på Origon", origo.name);
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
    
    
    private String origonInvitationPreamble(String language, OOrigo origo)
    {
        String invitationPreamble = null;
        OMemberProxy memberProxy = m.getMemberProxy();
        
        if (language.equals(kLanguageNorwegianBokmal)) {
            if (origo == null) {
                invitationPreamble = String.format("%s (%s) har lagt inn epostadressen din på Origon", memberProxy.memberName, memberProxy.proxyId);
            } else if (origo.isPrivate()) {
                invitationPreamble = "Du har blitt lagt til i en privat liste på Origon";
            } else if (origo.isResidence()) {
                invitationPreamble = String.format("%s (%s) har lagt inn husstanden din på Origon", memberProxy.memberName, memberProxy.proxyId);
            } else {
                invitationPreamble = String.format("%s (%s) har lagt deg inn i lista \"%s\" på Origon", memberProxy.memberName, memberProxy.proxyId, origo.name);
            }
        } else {
            if (origo == null) {
                invitationPreamble = String.format("%s (%s) has added your email address on Origon", memberProxy.memberName, memberProxy.proxyId);
            } else if (origo.isPrivate()) {
                invitationPreamble = "You have been added to a private list on Origon";
            } else if (origo.isResidence()) {
                invitationPreamble = String.format("%s (%s) has added your household on Origon", memberProxy.memberName, memberProxy.proxyId);
            } else {
                invitationPreamble = String.format("%s (%s) has added you to the list '%s' on Origon", memberProxy.memberName, memberProxy.proxyId, origo.name);
            }
        }
        
        return invitationPreamble;
    }
    
    
    private String origonSlogan(String language)
    {
        String slogan = null;

        if (language.equals(kLanguageNorwegianBokmal)) {
            slogan = "mobil-appen som gir deg fullt eierskap til din egen kontaktinformasjon " +
                     "og knytter deg tettere sammen med menneskene du allerede har med å gjøre i det daglige";
        } else {
            slogan = "the mobile app that gives you full ownership of your own contact information " +
                     "and connects you more tightly with the people you already cross paths with in your daily routine";
        }
        
        return slogan;
    }
    
    
    private String origonAvailabilityInfo(String language, String registrationEmail)
    {
        String availabilityInfo = null;

        if (language.equals(kLanguageNorwegianBokmal)) {
            availabilityInfo = String.format("Origon er tilgjengelig for iOS 7 og senere (iPhone, iPad og iPod touch). " +
                                             "Last ned Origon fra App Store og registrer deg med %s for å komme i gang. " +
                                             "(Origon er foreløpig ikke tilgjengelig for Android eller Windows Phone.)", registrationEmail);
        } else {
            availabilityInfo = String.format("Origon is available on iOS 7 and later (iPhone, iPad and iPod touch). " +
                                             "Download Origon from the App Store and register with %s to get going. " +
                                             "(Origon is currently not available on Android or Windows Phone.)", registrationEmail);
        }
        
        return availabilityInfo;
    }
    
    
    private String origonBestRegards(String language)
    {
        String bestRegards = null;
        
        if (language.equals(kLanguageNorwegianBokmal)) {
            bestRegards = "Med vennlig hilsen Origon-teamet\n" +
                          "http://origon.co\n";
        } else {
            bestRegards = "Best regards,\n" +
                          "The Origon team\n" +
                          "http://origon.co\n";
        }
        
        return bestRegards;
    }
    
    
    public OMailer(OMeta m)
    {
        this.m = m;
    }
    
    
    public void sendInvitation(String invitedEmail, OOrigo origo)
    {
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(invitedEmail, origonInvitationSubject(kLanguageNorwegianBokmal, origo),
                    String.format("Hei!\n" +
                                  "\n" +
                                  origonInvitationPreamble(kLanguageNorwegianBokmal, origo) + ", " + origonSlogan(kLanguageNorwegianBokmal) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageNorwegianBokmal, invitedEmail) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal) +
                                  "\n" +
                                  "\n" +
                                  "Hi!\n" +
                                  "\n" +
                                  origonInvitationPreamble(kLanguageEnglish, origo) + ", " + origonSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitedEmail) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish)));
        } else {
            sendEmail(invitedEmail, origonInvitationSubject(kLanguageEnglish, origo),
                    String.format("Hi!\n" +
                                  "\n" +
                                  origonInvitationPreamble(kLanguageEnglish, origo) + ", " + origonSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitedEmail) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish)));
        }
    }
    
    
    public void sendEmailChangeNotification(OMember invitee, String oldEmail)
    {
        OMemberProxy memberProxy = m.getMemberProxy();
        
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            String givenName = invitee.name.split(" ")[0];
            
            sendEmail(invitee.email, "Epostadressen din har blitt endret på Origon",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) har endret epostadressen din på Origon fra %s til denne adressen.\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageNorwegianBokmal, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal) +
                                  "\n" +
                                  "\n" +
                                  "%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from %s to this address.\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  givenName, memberProxy.memberName, memberProxy.proxyId, oldEmail, givenName, memberProxy.memberName, memberProxy.proxyId, oldEmail));
            sendEmail(oldEmail, "Epostadressen din har blitt endret på Origon",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) har endret epostadressen din på Origon fra denne adressen til %s.\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageNorwegianBokmal, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal) +
                                  "\n" +
                                  "\n" +
                                  "%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from this address to %s.\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  givenName, memberProxy.memberName, memberProxy.proxyId, invitee.email, givenName, memberProxy.memberName, memberProxy.proxyId, invitee.email));
        } else {
            sendEmail(invitee.email, "Your email address has been changed on Origon",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from %s to this address.\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  invitee.name.split(" ")[0], memberProxy.memberName, memberProxy.proxyId, oldEmail));
            sendEmail(oldEmail, "Your email address has been changed on Origon",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from this address to %s.\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  invitee.name.split(" ")[0], memberProxy.memberName, memberProxy.proxyId, invitee.email));
        }
    }
    
    
    public void sendRegistrationEmail()
    {
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(m.getEmail(), "Fullfør registreringen på Origon",
                    String.format("Velkommen til Origon!\n" +
                                  "\n" +
                                  "Alt som gjenstår før du kan begynne å bruke Origon, er å oppgi aktiveringskoden under og fylle ut evt manglende opplysninger.\n" +
                                  "\n" +
                                  "    Aktiveringskode: %s\n" +
                                  "\n" +
                                  "Merk: Opplysningene du vil bli bedt om å oppgi (eller som kanskje allerede har blitt oppgitt om du mottok en invitasjon) " +
                                  "er dine og bare dine. Vi kommer aldri til å selge dem videre eller bruke dem til noe annet enn det du kan se med egne øyne i Origon.\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal),
                                  m.getActivationCode()));

        } else {
            sendEmail(m.getEmail(), "Complete your registration with Origon",
                    String.format("Welcome to Origon!\n" +
                                  "\n" +
                                  "All that remains before you can start using Origon, is to enter the activation code below and fill out any missing information.\n" +
                                  "\n" +
                                  "    Activation code: %s\n" +
                                  "\n" +
                                  "Please note: The information you will be asked to enter (or that may have already been entered if you received an invitation) " +
                                  "is yours and yours alone. We will never sell it nor use it for anything except what you can see with your own eyes in Origon.\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  m.getActivationCode()));
        }
    }
    
    
    public void sendEmailActivationCode()
    {
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(m.getEmail(), "Aktiver den nye epostadressen din for bruk i Origon",
                    String.format("Oppgi følgende kode for å aktivere den nye epostadressen din for bruk i Origon.\n" +
                                  "\n" +
                                  "    Aktiveringskode: %s\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal),
                                  m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Activate your new email address for use with Origon",
                    String.format("Please enter the following code to activate your new email address for use with Origon.\n" +
                                  "\n" +
                                  "    Activation code: %s\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  m.getActivationCode()));
        }
        
        OLog.log().fine(m.meta() + "Sent email activation code to user.");
    }
    
    
    public void sendPasswordResetEmail()
    {
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(m.getEmail(), "Passordet ditt har blitt tilbakestilt",
                    String.format("Ditt midlertidige passord er %s.\n" +
                                  "\n" +
                                  "Så snart du har logget på Origon, bør du gå til Innstillinger->Bytt passord og endre det til noe bare du vet om.\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal),
                                  m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Your password has been reset",
                    String.format("Your temporary passord is %s.\n" +
                                  "\n" +
                                  "As soon as you have logged in to Origon, you should go to Settings->Change password and change it to something only you know.\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  m.getActivationCode()));
        }
        
        OLog.log().fine(m.meta() + "Sent new password to user.");
    }
}
