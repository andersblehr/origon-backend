package com.origoapp.api.aux;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
//import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import com.origoapp.api.model.OMember;


public class OMailer
{
    private static final String kFromAddress = "donotreply@origoapp.com";
    
    private static final String kLanguageEnglish = "en";
    private static final String kLanguageNorwegianBokmal = "nb";
    
    private OMeta m;
    
    
    private void sendEmail(String recipientAddress, String subject, String text)
    {
        Message message = new MimeMessage(Session.getInstance(new Properties()));
        
        try {
            message.setFrom(new InternetAddress(kFromAddress));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress("ablehr@gmail.com"));  // TODO: For testing only, remove and uncomment preceding when done
            message.setSubject(subject);
            message.setText(text);
            
            OLog.log().fine(m.meta() + String.format("Sending email to %s with body:\n%s", recipientAddress, text));
            
            //Transport.send(message);
        } catch (MessagingException e) {
            OLog.throwWebApplicationException(e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    
    private String origoSlogan(String language)
    {
        String slogan = null;

        if (language.equals(kLanguageNorwegianBokmal)) {
            slogan = "mobil-appen som gir deg full kontroll over din egen kontaktinformasjon " +
                     "og knytter deg tettere sammen med mennesker du allerede har med å gjøre i det daglige";
        } else {
            slogan = "the mobile app that gives you full control over your own contact information " +
                     "and connects you more tightly with people you already cross paths with in your daily routine";
        }
        
        return slogan;
    }
    
    
    private String origoAvailabilityInfo(String language, String registrationEmail)
    {
        String availabilityInfo = null;
        
        if (language.equals(kLanguageNorwegianBokmal)) {
            availabilityInfo = String.format("Origo er tilgjengelig for iOS 7 og senere (iPhone, iPad og iPod touch). " +
                                             "Last ned Origo fra App Store og registrer deg med %s for å komme i gang. " +
                                             "(Origo er foreløpig ikke tilgjengelig for Android eller Windows Phone.)", registrationEmail);
        } else {
            availabilityInfo = String.format("Origo is available on iOS 7 and later (iPhone, iPad and iPod touch). " +
                                             "Download Origo from the App Store and register with %s to get going. " +
                                             "(Origo is currently not available on Android or Windows Phone.)", registrationEmail);
        }
        
        return availabilityInfo;
    }
    
    
    public OMailer(OMeta m)
    {
        this.m = m;
    }
    
    
    public void sendInvitation(String invitedEmail)
    {
        OMemberProxy memberProxy = m.getMemberProxy();
        
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(invitedEmail, "Du har blitt lagt til i en liste på Origo",
                    String.format("Hei!\n" +
                                  "\n" +
                                  "%s (%s) har lagt deg til i en liste på Origo, " + origoSlogan(kLanguageNorwegianBokmal) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageNorwegianBokmal, invitedEmail) + "\n" +
                                  "\n" +
                                  "Med vennlig hilsen,\n" +
                                  "Origo-laget - http://origoapp.com\n" +
                                  "\n" +
                                  "\n" +
                                  "Hi!\n" +
                                  "\n" +
                                  "%s (%s) has added you to a list on Origo, " + origoSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageEnglish, invitedEmail) + "\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com",
                                  memberProxy.memberName, memberProxy.proxyId, memberProxy.memberName, memberProxy.proxyId));
        } else {
            sendEmail(invitedEmail, "You have been added to a list on Origo",
                    String.format("Hi!\n" +
                                  "\n" +
                                  "%s (%s) has added you to a list on Origo, " + origoSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageEnglish, invitedEmail) + "\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com",
                                  memberProxy.memberName, memberProxy.proxyId));
        }
    }
    
    
    public void sendEmailChangeNotification(OMember invitee, String oldEmail)
    {
        OMemberProxy memberProxy = m.getMemberProxy();
        
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            String givenName = invitee.name.split(" ")[0];
            
            sendEmail(invitee.email, "Epostadressen din har blitt endret på Origo",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) har endret epostadressen din på Origo fra %s til denne adressen. Origo er " + origoSlogan(kLanguageNorwegianBokmal) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageNorwegianBokmal, invitee.email) + "\n" +
                                  "\n" +
                                  "Med vennlig hilsen,\n" +
                                  "Origo-laget - http://origoapp.com\n" +
                                  "\n" +
                                  "\n" +
                                  "%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origo from %s to this address. Origo is " + origoSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com",
                                  givenName, memberProxy.memberName, memberProxy.proxyId, oldEmail, givenName, memberProxy.memberName, memberProxy.proxyId, oldEmail));
            sendEmail(oldEmail, "Epostadressen din har blitt endret på Origo",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) har endret epostadressen din på Origo fra denne adressen til %s. Origo er " + origoSlogan(kLanguageNorwegianBokmal) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageNorwegianBokmal, invitee.email) + "\n" +
                                  "\n" +
                                  "Med vennlig hilsen,\n" +
                                  "Origo-laget - http://origoapp.com\n" +
                                  "\n" +
                                  "\n" +
                                  "%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origo from this address to %s. Origo is " + origoSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com",
                                  givenName, memberProxy.memberName, memberProxy.proxyId, invitee.email, givenName, memberProxy.memberName, memberProxy.proxyId, invitee.email));
        } else {
            sendEmail(invitee.email, "Your email address has been changed on Origo",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origo from %s to this address. Origo is " + origoSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com",
                                  invitee.name.split(" ")[0], memberProxy.memberName, memberProxy.proxyId, oldEmail));
            sendEmail(oldEmail, "Your email address has been changed on Origo",
                    String.format("%s,\n" +
                                  "\n" +
                                  "%s (%s) has changed your email address on Origo from this address to %s. Origo is " + origoSlogan(kLanguageEnglish) + ".\n" +
                                  "\n" +
                                  origoAvailabilityInfo(kLanguageEnglish, invitee.email) + "\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com",
                                  invitee.name.split(" ")[0], memberProxy.memberName, memberProxy.proxyId, invitee.email));
        }
    }
    
    
    public void sendRegistrationEmail()
    {
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(m.getEmail(), "Complete your registration with Origo",
                    String.format("Velkommen til Origo!\n" +
                                  "\n" +
                                  "Du er nesten klar til å begynne å bruke Origo, " + origoSlogan(kLanguageNorwegianBokmal) + ". " +
                                  "Alt som gjenstår, er å gå tilbake til Origo, oppgi aktiveringskoden under og fylle ut evt manglende opplysninger.\n" +
                                  "\n" +
                                  "  Aktiveringskode: %s\n" +
                                  "\n" +
                                  "Merk: Opplysningene du vil bli bedt om å oppgi (eller som kanskje allerede har blitt oppgitt om du mottok en invitasjon) " +
                                  "er dine og bare dine. Vi kommer aldri til å selge dem videre eller bruke dem til noe annet enn det du kan se med egne øyne i Origo.\n" +
                                  "\n" +
                                  "Med vennlig hilsen,\n" +
                                  "Origo-laget - http://origoapp.com", m.getActivationCode()));

        } else {
            sendEmail(m.getEmail(), "Complete your registration with Origo",
                    String.format("Welcome to Origo!\n" +
                                  "\n" +
                                  "You are almost ready to start using Origo, " + origoSlogan(kLanguageEnglish) + ". " +
                                  "All that remains is to return to Origo, enter the activation code below and fill out any missing information.\n" +
                                  "\n" +
                                  "  Activation code: %s\n" +
                                  "\n" +
                                  "Please note: The information you will be asked to enter (or that may have already been entered if you received an invitation) " +
                                  "is yours and yours alone. We will never sell it nor use it for anything except what you can see with your own eyes in Origo.\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com", m.getActivationCode()));
        }
    }
    
    
    public void sendEmailActivationCode()
    {
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(m.getEmail(), "Aktiver den nye epostadressen din for bruk i Origo",
                    String.format("Oppgi følgende kode for å aktivere den nye epostadressen din for bruk i Origo.\n" +
                                  "\n" +
                                  "  Aktiveringskode: %s\n" +
                                  "\n" +
                                  "Med vennlig hilsen,\n" +
                                  "Origo-laget - http://origoapp.com", m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Activate your new email address for use with Origo",
                    String.format("Please enter the following code to activate your new email address for use with Origo.\n" +
                                  "\n" +
                                  "  Activation code: %s\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com", m.getActivationCode()));
        }
        
        OLog.log().fine(m.meta() + "Sent email activation code to user.");
    }
    
    
    public void sendPasswordResetEmail()
    {
        if (m.getLanguage().equals(kLanguageNorwegianBokmal)) {
            sendEmail(m.getEmail(), "Passordet ditt har blitt tilbakestilt",
                    String.format("Ditt midlertidige passord er %s.\n" +
                                  "\n" +
                                  "Så snart du har logget på Origo, bør du gå til Innstillinger->Bytt passord og endre det til noe bare du vet om.\n" +
                                  "\n" +
                                  "Med vennlig hilsen,\n" +
                                  "Origo-laget - http://origoapp.com", m.getActivationCode()));
        } else {
            sendEmail(m.getEmail(), "Your password has been reset",
                    String.format("Your temporary passord is %s.\n" +
                                  "\n" +
                                  "As soon as you have logged in to Origo, you should go to Settings->Change password and change it to something only you know.\n" +
                                  "\n" +
                                  "Best regards,\n" +
                                  "The Origo team - http://origoapp.com", m.getActivationCode()));
        }
        
        OLog.log().fine(m.meta() + "Sent new password to user.");
    }
}
