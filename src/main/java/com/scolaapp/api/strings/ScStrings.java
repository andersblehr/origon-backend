package com.scolaapp.api.strings;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ScStrings")
public class ScStrings
{
    // Alert messages
    public String strInternalServerError;
    public String strInvalidNameAlert;
    public String strInvalidEmailAlert;
    public String strInvalidPasswordAlert;
    public String strInvalidScolaShortnameAlert;
    public String strPasswordsDoNotMatchAlert;
    public String strRegistrationCodesDoNotMatchAlert;
    public String strScolaNotFoundAlert;
    public String strInvitationNotFoundAlert;
    public String strUserExistsAlertTitle;
    public String strUserExistsButNotLoggedInAlert;
    public String strUserExistsAndLoggedInAlert;
    
    // Button titles
    public String strOK;
    public String strCancel;
    public String strHaveAccess;
    public String strHaveCode;
    public String strLater;
    public String strTryAgain;
    public String strGoBack;
    public String strContinue;
    
    // Auth view
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
    public String strPleaseWait;
    public String strUserHelpCompleteRegistration;
    public String strEmailSentPopUpTitle;
    public String strEmailSentPopUpMessage;
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
            strInternalServerError              = "Det har oppstått en feil. Vennligst prøv igjen senere.";
            strInvalidNameAlert                 = "Vennligst oppgi fullt navn som i signaturen din";
            strInvalidEmailAlert                = "Vennligst oppgi en gyldig epost-adresse";
            strInvalidPasswordAlert             = "Passordet må inneholde minimum %d tegn";
            strInvalidScolaShortnameAlert       = "Scola-kortnavnet er for kort. Vennligst oppgi kortnavnet nøyaktig slik det står i invitasjonen.";
            strPasswordsDoNotMatchAlert         = "Passordet stemmer ikke med det du oppga tidligere. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            strRegistrationCodesDoNotMatchAlert = "Registreringskoden stemmer ikke med den du har mottatt på epost. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            strScolaNotFoundAlert        = "Det finnes ingen scola med kortnavn \"%@\". Vennligst oppgi kortnavnet nøyaktig slik det står i invitasjonen.";
            strInvitationNotFoundAlert    = "Det er ikke registrert en invitasjon for %@ til scola med kortnavn \"%@\". Vennligst oppgi navnet ditt nøyaktig slik det står i invitasjonen. (Du kan korrigere navnet ditt senere om det er feilstavet.)";
            strUserExistsAlertTitle             = "Allerede medlem";
            strUserExistsButNotLoggedInAlert    = "Du er allerede Scola-medlem, men passordet du har oppgitt stemmer ikke med det som er registrert hos oss. Vennligst oppgi passordet på nytt.";
            strUserExistsAndLoggedInAlert       = "Du er allerede Scola-medlem og er nå logget inn.";
            
            // Button titles
            strOK                               = "OK";
            strCancel                           = "Avbryt";
            strHaveAccess                       = "Har tilgang";
            strHaveCode                         = "Har kode";
            strLater                            = "Nei, ikke nå";
            strTryAgain                         = "Prøv igjen";
            strGoBack                           = "Gå tilbake";
            strContinue                         = "Fortsett";
            
            // Auth view
            strScolaDescription                 = "[subst.] en gruppe mennesker som omgås, samarbeider og/eller er avhengige av hverandre i det daglige.";
            strMembershipPrompt                 = "Allerede medlem? Første gang her? Invitert?";
            strIsNew                            = "Første gang";
            strIsInvited                        = "Invitert";
            strIsMember                         = "Medlem";
            strUserHelpNew                      = "Om du vil bli Scola-medlem, vennligst oppgi:";
            strUserHelpInvited                  = "Om du har mottatt en invitasjon, vennligst oppgi:";
            strUserHelpMember                   = "Logg på om du allerede er Scola-medlem:";
            strNamePrompt                       = "Fullt navn som i signaturen din";
            strNameAsReceivedPrompt             = "Navnet ditt som skrevet i invitasjonen";
            strEmailPrompt                      = "Epost-adressen din";
            strScolaShortnamePrompt             = "Scola-koden oppgitt i invitasjonen";
            strNewPasswordPrompt                = "Et fritt valgt passord";
            strPasswordPrompt                   = "Passordet ditt";
            strPleaseWait                       = "Vennligst vent...";
            strUserHelpCompleteRegistration     = "For å fullføre registreringen, vennligst oppgi:";
            strEmailSentPopUpTitle              = "Registreringskode sendt";
            strEmailSentPopUpMessage            = "En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strSeeYouLaterPopUpTitle            = "Ser deg senere!";
            strSeeYouLaterPopUpMessage          = "Du kan trygt forlate Scola. Vi fortsetter neste gang du er innom.";
            strWelcomeBackPopUpTitle            = "Velkommen tilbake!";
            strWelcomeBackPopUpMessage          = "Om du har registreringskoden som ble sendt til %@, kan du nå fullføre registreringen. Om ikke, kan du gå tilbake og starte på nytt.";
            strRegistrationCodePrompt           = "Din personlige registreringskode";
            strRepeatPasswordPrompt             = "Samme passord som tidligere";
        } else {
            // Alert messages
            strInternalServerError              = "An error has occurred. Please try again later.";
            strInvalidNameAlert                 = "Please provide your full name, as in your signature";
            strInvalidEmailAlert                = "Please provide a valid email address";
            strInvalidPasswordAlert             = "The password must contain minimum %d characters";
            strInvalidScolaShortnameAlert       = "The scola shortname is too short. Please use the exact spelling as provided in the invitation.";
            strPasswordsDoNotMatchAlert         = "The password does not match the one you entered before, please try again - or go back and start over.";
            strRegistrationCodesDoNotMatchAlert = "The registration code does not match the one you have received by email, please try again - or go back and start over.";
            strScolaNotFoundAlert        = "There is no scola with shortname '%@'. Please try again and use the exact spelling as provided in the invitation.";
            strInvitationNotFoundAlert    = "There is no invitation registered for %@ for the scola with shortname '%@'. Please provide your name exactly as it is written in the invitation. (You can correct your name later if it is misspelt.)";
            strUserExistsAlertTitle             = "Already member";
            strUserExistsButNotLoggedInAlert    = "You are already a Scola member, but the password you have provided does not match our records. Please enter your password again.";
            strUserExistsAndLoggedInAlert       = "You are already a Scola member and are now logged in.";
            
            // Button titles
            strOK                               = "OK";
            strCancel                           = "Cancel";
            strHaveAccess                       = "Have access";
            strHaveCode                         = "Have code";
            strLater                            = "No, not now";
            strTryAgain                         = "Try again";
            strGoBack                           = "Go back";
            strContinue                         = "Continue";
           
            // Auth view
            strScolaDescription                 = "[noun] a group of people who interact, team up, and/or depend on each other in day-to-day activities.";
            strMembershipPrompt                 = "Already a member? First time here? Invited?";
            strIsNew                            = "First time";
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
            strPleaseWait                       = "Please wait...";
            strUserHelpCompleteRegistration     = "To complete your registration, please provide:";
            strEmailSentPopUpTitle              = "Registration code sent";
            strEmailSentPopUpMessage            = "An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strSeeYouLaterPopUpTitle            = "See you later!";
            strSeeYouLaterPopUpMessage          = "You can safely leave Scola. We'll continue next time your drop by.";
            strWelcomeBackPopUpTitle            = "Welcome back!";
            strWelcomeBackPopUpMessage          = "If you have handy the registration code sent to %@, you can now complete your registration. If not, please go back and start over.";
            strRegistrationCodePrompt           = "Your personal registration code";
            strRepeatPasswordPrompt             = "The same password as before";
        }
    }
}
