package com.scolaapp.api.strings;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ScStrings")
public class ScStrings
{
    // Alert messages
    public String strInvalidNameAlert;
    public String strInvalidEmailAlert;
    public String strInvalidPasswordAlert;
    public String strInvalidInvitationCodeAlert;
    public String strPasswordsDoNotMatchAlert;
    public String strRegistrationCodesDoNotMatchAlert;
    
    // Generic strings
    public String strOK;
    public String strCancel;
    public String strTryAgain;
    public String strGoBack;
    public String strPleaseWait;
    
    // Root view
    public String strScolaDescription;
    public String strMembershipPrompt;
    public String strIsNew;
    public String strIsInvited;
    public String strIsMember;
    public String strUserHelpNew;
    public String strUserHelpInvited;
    public String strUserHelpMember;
    public String strNamePrompt;
    public String strNameAsReceivedPrompt;
    public String strEmailPrompt;
    public String strScolaShortnamePrompt;
    public String strNewPasswordPrompt;
    public String strPasswordPrompt;
    public String strUserHelpCompleteRegistration;
    public String strEmailSentPopUpTitle;
    public String strEmailSentPopUpMessage;
    public String strContinue;
    public String strLater;
    public String strSeeYouLaterPopUpTitle;
    public String strSeeYouLaterPopUpMessage;
    public String strWelcomeBackPopUpTitle;
    public String strWelcomeBackPopUpMessage;
    public String strRegistrationCodePrompt;
    public String strRepeatPasswordPrompt;

    
    public ScStrings()
    {
    	this("en");
    }
    
    
    public ScStrings(String language)
    {
        if ("nb".equals(language)) {
            // Alert messages
            strInvalidNameAlert                 = "Vennligst oppgi fullt navn som i signaturen din";
            strInvalidEmailAlert                = "Vennligst oppgi en gyldig epost-adresse";
            strInvalidPasswordAlert             = "Passordet må inneholde minimum %d tegn";
            strInvalidInvitationCodeAlert       = "Invitasjonskoden er for kort, vennligst sjekk om du har skrevet den riktig";
            strPasswordsDoNotMatchAlert         = "Passordet stemmer ikke med det du oppga tidligere. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            strRegistrationCodesDoNotMatchAlert = "Registreringskoden stemmer ikke med den du har mottatt på epost. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            
            // Generic strings
            strOK                               = "OK";
            strCancel                           = "Avbryt";
            strTryAgain                         = "Prøv igjen";
            strGoBack                           = "Gå tilbake";
            strPleaseWait                       = "Vennligst vent...";
            
            // Root view UI strings
            strScolaDescription                 = "[subst.] en gruppe mennesker som omgås, samarbeider og/eller er avhengige av hverandre i det daglige.";
            strMembershipPrompt                 = "Er du ny her? Invitert? Allerede medlem?";
            strIsNew                            = "Ny her";
            strIsInvited                        = "Invitert";
            strIsMember                         = "Medlem";
            strUserHelpNew                      = "Om du vil bli Scola-medlem, vennligst oppgi:";
            strUserHelpInvited                  = "Om du har mottatt en invitasjon, vennligst oppgi:";
            strUserHelpMember                   = "Logg på om du allerede er Scola-medlem:";
            strNamePrompt                       = "Fullt navn som i signaturen din";
            strNameAsReceivedPrompt             = "Navnet ditt som skrevet i invitasjonen";
            strEmailPrompt                      = "Epost-adressen din";
            strScolaShortnamePrompt             = "Scola-koden fra invitasjonen";
            strNewPasswordPrompt                = "Et fritt valgt passord";
            strPasswordPrompt                   = "Passordet ditt";
            strUserHelpCompleteRegistration     = "For å fullføre registreringen, vennligst oppgi:";
            strEmailSentPopUpTitle              = "Registreringskode sendt";
            strEmailSentPopUpMessage            = "En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strContinue                         = "Fortsett";     
            strLater                            = "Nei, ikke nå";
            strSeeYouLaterPopUpTitle            = "Ser deg senere!";
            strSeeYouLaterPopUpMessage          = "Du kan trygt forlate Scola. Vi fortsetter neste gang du er innom.";
            strWelcomeBackPopUpTitle            = "Velkommen tilbake!";
            strWelcomeBackPopUpMessage          = "Om du har registreringskoden som ble sendt til %@, kan du nå fullføre registreringen.";
            strRegistrationCodePrompt           = "Din personlige registreringskode";
            strRepeatPasswordPrompt             = "Samme passord som tidligere";
        } else {
            // Alert messages
            strInvalidNameAlert                 = "Please provide your full name, as in your signature";
            strInvalidEmailAlert                = "Please provide a valid email address";
            strInvalidPasswordAlert             = "The password must contain minimum %d characters";
            strInvalidInvitationCodeAlert       = "The invitation code is too short, please verify that you have written it correctly";
            strPasswordsDoNotMatchAlert         = "The password does not match the one you entered before, please try again - or go back and start over.";
            strRegistrationCodesDoNotMatchAlert = "The registration code does not match the one you have received by email, please try again - or go back and start over.";
            
            // Generic strings
            strOK                               = "OK";
            strCancel                           = "Cancel";
            strTryAgain                         = "Try again";
            strGoBack                           = "Go back";
            strPleaseWait                       = "Please wait...";
            
            // Root view
            strScolaDescription                 = "[noun] a group of people who interact, team up, and/or depend on each other in day-to-day activities.";
            strMembershipPrompt                 = "Are you new here? Invited? Already a member?";
            strIsNew                            = "New here";
            strIsInvited                        = "Invited";
            strIsMember                         = "Member";
            strUserHelpNew                      = "To become a Scola member, please provide:";
            strUserHelpInvited                  = "If you have received an invitation, please provide:";
            strUserHelpMember                   = "Log in if you're already a Scola member:";
            strNamePrompt                       = "Your name as you sign it";
            strNameAsReceivedPrompt             = "Your name as written in the invitation";
            strEmailPrompt                      = "Your email address";
            strScolaShortnamePrompt             = "The scola code from the invitation";
            strNewPasswordPrompt                = "A password of your choice";
            strPasswordPrompt                   = "Your password";
            strUserHelpCompleteRegistration     = "To complete your registration, please provide:";
            strEmailSentPopUpTitle              = "Registration code sent";
            strEmailSentPopUpMessage            = "An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strContinue                         = "Continue";
            strLater                            = "No, not now";
            strSeeYouLaterPopUpTitle            = "See you later!";
            strSeeYouLaterPopUpMessage          = "You can safely leave Scola. We'll continue next time your drop by.";
            strWelcomeBackPopUpTitle            = "Welcome back!";
            strWelcomeBackPopUpMessage          = "If you have handy the registration code sent to %@, you can now complete your registration.";
            strRegistrationCodePrompt           = "Your personal registration code";
            strRepeatPasswordPrompt             = "The same password as before";
        }
    }
}
