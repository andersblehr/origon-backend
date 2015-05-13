package co.origon.api.aux;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import co.origon.api.model.OMember;


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
            message.setFrom(new InternetAddress(kFromAddress));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.setSubject(subject);
            message.setText(text);
            
            Transport.send(message);
        } catch (MessagingException e) {
            OLog.log().warning(m.meta() + String.format("Caught exception: %s", e.getMessage()));
            OLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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

        if (m.isLive()) {
            if (language.equals(kLanguageNorwegianBokmal)) {
                availabilityInfo = String.format("Origon er tilgjengelig for iOS 7 og senere (iPhone, iPad og iPod touch). " +
                                                 "Last ned Origon fra App Store og registrer deg med %s for å komme i gang. " +
                                                 "(Origon er foreløpig ikke tilgjengelig for Android eller Windows Phone.)", registrationEmail);
            } else {
                availabilityInfo = String.format("Origon is available on iOS 7 and later (iPhone, iPad and iPod touch). " +
                                                 "Download Origon from the App Store and register with %s to get going. " +
                                                 "(Origon is currently not available on Android or Windows Phone.)", registrationEmail);
            }
        } else {
            if (language.equals(kLanguageNorwegianBokmal)) {
                availabilityInfo = String.format("Origon vil snart være tilgjengelig for iOS 7 og senere (iPhone, iPad og iPod touch). " +
                                                 "Når det skjer, kan du laste ned Origon fra App Store og registrere deg med %s for å komme i gang. " +
                                                 "(Origon kommer inntil videre ikke til å være tilgjengelig for Android eller Windows Phone.)", registrationEmail);
            } else {
                availabilityInfo = String.format("Origon will soon be available on iOS 7 and later (iPhone, iPad and iPod touch). " +
                                                 "When it is, you can download Origon from the App Store and register with %s to get going. " +
                                                 "(Origon will not be available on Android or Windows Phone this time around.)", registrationEmail);
            }
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
    
    
    public void sendInvitation(String invitedEmail)
    {
        OMemberProxy memberProxy = m.getMemberProxy();
        
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(invitedEmail, "Du har blitt lagt til i en liste på Origon",
                    String.format("Hei!\n" +
                                  "\n" +
                                  "%s (%s) har lagt deg til i en liste på Origon, " + origonSlogan(kLanguageNorwegianBokmal) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageNorwegianBokmal, invitedEmail) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal) +
                                  "\n" +
                                  "Hi!\n" +
                                  "\n" +
                                  "%s (%s) has added you to a list on Origon, " + origonSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitedEmail) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  memberProxy.memberName, memberProxy.proxyId, memberProxy.memberName, memberProxy.proxyId));
        } else {
            sendEmail(invitedEmail, "You have been added to a list on Origon",
                    String.format("Hi!\n" +
                                  "\n" +
                                  "%s (%s) has added you to a list on Origon, " + origonSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitedEmail) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  memberProxy.memberName, memberProxy.proxyId));
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
                                  "%s (%s) har endret epostadressen din på Origon fra %s til denne adressen. Origon er " + origonSlogan(kLanguageNorwegianBokmal) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageNorwegianBokmal, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal) +
                                  "\n" +
                                  "\n" +
                                  "%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from %s to this address. Origon is " + origonSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  givenName, memberProxy.memberName, memberProxy.proxyId, oldEmail, givenName, memberProxy.memberName, memberProxy.proxyId, oldEmail));
            sendEmail(oldEmail, "Epostadressen din har blitt endret på Origon",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) har endret epostadressen din på Origon fra denne adressen til %s. Origon er " + origonSlogan(kLanguageNorwegianBokmal) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageNorwegianBokmal, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal) +
                                  "\n" +
                                  "\n" +
                                  "%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from this address to %s. Origon is " + origonSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  givenName, memberProxy.memberName, memberProxy.proxyId, invitee.email, givenName, memberProxy.memberName, memberProxy.proxyId, invitee.email));
        } else {
            sendEmail(invitee.email, "Your email address has been changed on Origon",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from %s to this address. Origon is " + origonSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origonAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageEnglish),
                                  invitee.name.split(" ")[0], memberProxy.memberName, memberProxy.proxyId, oldEmail));
            sendEmail(oldEmail, "Your email address has been changed on Origon",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origon from this address to %s. Origon is " + origonSlogan(kLanguageEnglish) + ".\n" +
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
            sendEmail(m.getEmail(), "Complete your registration with Origon",
                    String.format("Velkommen til Origon!\n" +
                                  "\n" +
                                  "Du er nesten klar til å begynne å bruke Origon, " + origonSlogan(kLanguageNorwegianBokmal) + ". " +
                                  "Alt som gjenstår, er å gå tilbake til Origon, oppgi aktiveringskoden under og fylle ut evt manglende opplysninger.\n" +
                                  "\n" +
                                  "  Aktiveringskode: %s\n" +
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
                                  "You are almost ready to start using Origon, " + origonSlogan(kLanguageEnglish) + ". " +
                                  "All that remains is to return to Origon, enter the activation code below and fill out any missing information.\n" +
                                  "\n" +
                                  "  Activation code: %s\n" +
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
                                  "  Aktiveringskode: %s\n" +
                                  "\n" +
                                  origonBestRegards(kLanguageNorwegianBokmal),
                                  m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Activate your new email address for use with Origon",
                    String.format("Please enter the following code to activate your new email address for use with Origon.\n" +
                                  "\n" +
                                  "  Activation code: %s\n" +
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
