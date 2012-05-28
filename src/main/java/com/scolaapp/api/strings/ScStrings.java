package com.scolaapp.api.strings;


public class ScStrings
{
    // Error & alert messages
    public String strNoInternetError;
    public String strServerErrorAlert;
    public String strInvalidNameTitle;
    public String strInvalidNameAlert;
    public String strInvalidEmailTitle;
    public String strInvalidEmailAlert;
    public String strInvalidPasswordTitle;
    public String strInvalidPasswordAlert;
    public String strEmailSentAlertTitle;
    public String strEmailSentAlert;
    public String strEmailSentToInviteeTitle;
    public String strEmailSentToInviteeAlert;
    public String strPasswordsDoNotMatchTitle;
    public String strPasswordsDoNotMatchAlert;
    public String strInvalidRegistrationCodeTitle;
    public String strInvalidRegistrationCodeAlert;
    public String strUserExistsMustLogInAlert;
    public String strNotLoggedInAlert;
    public String strNoAddressTitle;
    public String strNoAddressAlert;
    public String strInvalidDateOfBirthTitle;
    public String strInvalidDateOfBirthAlert;
    public String strNoPhoneNumberTitle;
    public String strNoPhoneNumberAlert;
    public String strIncompleteRegistrationTitle;
    public String strIncompleteRegistrationAlert;
    
    // Auth view
    public String strUserIntentionLogin;
    public String strUserIntentionRegistration;
    public String strUserHelpNew;
    public String strUserHelpMember;
    public String strNamePrompt;
    public String strEmailPrompt;
    public String strNewPasswordPrompt;
    public String strPasswordPrompt;
    public String strPleaseWait;
    public String strUserHelpCompleteRegistration;
    public String strSeeYouLaterPopUpTitle;
    public String strSeeYouLaterPopUpMessage;
    public String strWelcomeBackPopUpTitle;
    public String strWelcomeBackPopUpMessage;
    public String strRegistrationCodePrompt;
    public String strRepeatPasswordPrompt;
    public String strScolaDescription;

    // Registration view 1
    public String strRegView1NavItemTitle;
    public String strRegView1BackButtonTitle;
    public String strAddressUserHelp;
    public String strAddressListedUserHelp;
    public String strAddressLine1Prompt;
    public String strAddressLine2Prompt;
    public String strPostCodeAndCityPrompt;
    public String strDateOfBirthUserHelp;
    public String strDateOfBirthListedUserHelp;
    public String strDateOfBirthPrompt;
    public String strDateOfBirthClickHerePrompt;
    
    // Registration view 2
    public String strRegView2NavItemTitle;
    public String strFemale;
    public String strFemaleMinor;
    public String strMale;
    public String strMaleMinor;
    public String strGenderUserHelp;
    public String strMobilePhoneUserHelp;
    public String strMobilePhoneListedUserHelp;
    public String strMobilePhonePrompt;
    public String strLandlineUserHelp;
    public String strLandlineListedUserHelp;
    public String strLandlinePrompt;
    
    // Button titles
    public String strOK;
    public String strCancel;
    public String strLogIn;
    public String strNewUser;
    public String strHaveAccess;
    public String strHaveCode;
    public String strLater;
    public String strTryAgain;
    public String strGoBack;
    public String strContinue;
    public String strNext;
    public String strDone;
    public String strUseConfigured;
    public String strUseNew;
    
    // Generic Scola strings
    public String strMyPlace;
    public String strOurPlace;
    public String strMyMessageBoard;
    public String strOurMessageBoard;
    
    
    private void setErrorAndAlertMessages(String language) 
    {
        if ("nb".equals(language)) {
            strNoInternetError                  = "Ingen internettforbindelse.";
            strServerErrorAlert                 = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            strInvalidNameTitle                 = "Ufullstendig navn";
            strInvalidNameAlert                 = "Du må oppgi fullt navn, som i signaturen din";
            strInvalidEmailTitle                = "Ugyldig epostadresse";
            strInvalidEmailAlert                = "Du må oppgi en gyldig epostadresse";
            strInvalidPasswordTitle             = "For kort passord";
            strInvalidPasswordAlert             = "Passordet må inneholde minimum %d tegn";
            strEmailSentAlertTitle              = "Registreringskode sendt";
            strEmailSentAlert                   = "En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strEmailSentToInviteeTitle          = "Velkommen til Scola!";
            strEmailSentToInviteeAlert          = "Takk for at du tar imot scola-invitasjonen(e) du har mottatt. En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strPasswordsDoNotMatchTitle         = "Ugyldig passord";
            strPasswordsDoNotMatchAlert         = "Passordet stemmer ikke med det du oppga tidligere. Du kan prøve igjen, eller du kan gå tilbake og start på nytt.";
            strInvalidRegistrationCodeTitle     = "Ugyldig registreringskode";
            strInvalidRegistrationCodeAlert     = "Registreringskoden stemmer ikke med den du har mottatt på epost. Du kan prøve igjen, eller du kan gå tilbake og start på nytt.";
            strUserExistsMustLogInAlert         = "Det ser ut som du prøver å logge inn. Du blir nå tatt til innloggingsdialogen.";
            strNotLoggedInAlert                 = "Feil epostadresse eller passord. Vennligst prøv igjen.";
            strNoAddressTitle                   = "Adresse mangler";
            strNoAddressAlert                   = "Du må oppgi en adresse. Det holder at ett av adressefeltene er utfylt.";
            strNoPhoneNumberTitle               = "Telefonnummer mangler";
            strNoPhoneNumberAlert               = "Du må oppgi et telefonnummer.";
            strInvalidDateOfBirthTitle          = "Ugyldig fødselsdato";
            strInvalidDateOfBirthAlert          = "Du har oppgitt en ugyldig fødselsdato. Du må være mellom 5 og 110 år gammel for å kunne bruke Scola.";
            strIncompleteRegistrationTitle      = "Ufullstendig registrering";
            strIncompleteRegistrationAlert      = "Du må fullføre registreringen før du kan begynne å bruke Scola.";
        } else {
            strNoInternetError                  = "No internet connection.";
            strServerErrorAlert                 = "An error has occurred. Please try again later. [%d: \"%@\"]";
            strInvalidNameTitle                 = "Incomplete name";
            strInvalidNameAlert                 = "Please provide your full name, as in your signature";
            strInvalidEmailTitle                = "Invalid email address";
            strInvalidEmailAlert                = "Please provide a valid email address";
            strInvalidPasswordTitle             = "Password too short";
            strInvalidPasswordAlert             = "The password must contain minimum %d characters";
            strEmailSentAlertTitle              = "Registration code sent";
            strEmailSentAlert                   = "An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strEmailSentToInviteeTitle          = "Welcome to Scola!";
            strEmailSentToInviteeAlert          = "Thank you for accepting the scola invitation(s) you have received. An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strPasswordsDoNotMatchTitle         = "Invalid password";
            strPasswordsDoNotMatchAlert         = "The password does not match the one you entered before. You can try again, or you can go back and start over.";
            strInvalidRegistrationCodeTitle     = "Invalid registration code";
            strInvalidRegistrationCodeAlert     = "The registration code does not match the one you have received by email. You can try again, or you can go back and start over.";
            strUserExistsMustLogInAlert         = "It looks like you are trying to log in. You will now be taken to the login dialog.";
            strNotLoggedInAlert                 = "Incorrent email or password. Please try again.";
            strNoAddressTitle                   = "Missing address";
            strNoAddressAlert                   = "Please provide an address. It is sufficient to fill in one of the fields.";
            strNoPhoneNumberTitle               = "Missing phone number";
            strNoPhoneNumberAlert               = "You must provide a phone number.";
            strInvalidDateOfBirthTitle          = "Invalid date of birth";
            strInvalidDateOfBirthAlert          = "You have provided an invalid date of birth. You must be between 5 and 110 years old in order to use Scola.";
            strIncompleteRegistrationTitle      = "Incomplete registration";
            strIncompleteRegistrationAlert      = "You must complete your registration before you can start using Scola.";
        }
    }
    
    
    private void setAuthViewStrings(String language)
    {
        if ("nb".equals(language)) {
            strUserIntentionLogin               = "Jeg vil logge inn";
            strUserIntentionRegistration        = "Jeg vil bli medlem";
            strUserHelpNew                      = "For å bli Scola-medlem må du oppgi:";
            strUserHelpMember                   = "Logg inn om du allerede er Scola-medlem:";
            strNamePrompt                       = "Fullt navn som i signaturen din";
            strEmailPrompt                      = "Epostadressen din";
            strNewPasswordPrompt                = "Et fritt valgt passord";
            strPasswordPrompt                   = "Passordet ditt";
            strPleaseWait                       = "Vent litt...";
            strUserHelpCompleteRegistration     = "For å fullføre registreringen må du oppgi:";
            strSeeYouLaterPopUpTitle            = "Ser deg senere!";
            strSeeYouLaterPopUpMessage          = "Du kan trygt forlate Scola. Vi fortsetter neste gang du er innom.";
            strWelcomeBackPopUpTitle            = "Velkommen tilbake!";
            strWelcomeBackPopUpMessage          = "Om du har registreringskoden som ble sendt til %@, kan du nå fullføre registreringen. Om ikke, kan du gå tilbake og få tilsendt en ny registreringskode.";
            strRegistrationCodePrompt           = "Din personlige registreringskode";
            strRepeatPasswordPrompt             = "Samme passord som tidligere";
            strScolaDescription                 = "[subst.] en gruppe mennesker som omgås, samarbeider og/eller er avhengige av hverandre i det daglige.";
        } else {
            strUserIntentionLogin               = "I want to log in";
            strUserIntentionRegistration        = "I want to register";
            strUserHelpNew                      = "To become a Scola member, please provide:";
            strUserHelpMember                   = "Log in if you are already a Scola member:";
            strNamePrompt                       = "Your name as you sign it";
            strEmailPrompt                      = "Your email address";
            strNewPasswordPrompt                = "A password of your choice";
            strPasswordPrompt                   = "Your password";
            strPleaseWait                       = "Please wait...";
            strUserHelpCompleteRegistration     = "To complete your registration, please provide:";
            strSeeYouLaterPopUpTitle            = "See you later!";
            strSeeYouLaterPopUpMessage          = "You can safely leave Scola. We'll continue next time your drop by.";
            strWelcomeBackPopUpTitle            = "Welcome back!";
            strWelcomeBackPopUpMessage          = "If you have handy the registration code sent to %@, you can now complete your registration. If not, you may go back and receive a new registration code.";
            strRegistrationCodePrompt           = "Your personal registration code";
            strRepeatPasswordPrompt             = "The same password as before";
            strScolaDescription                 = "[noun] a group of people who interact, team up, and/or depend on each other in day-to-day activities.";
        }
    }
    
    
    private void setRegistrationView1Strings(String language)
    {
        if ("nb".equals(language)) {
            strRegView1NavItemTitle             = "Adresse, fødselsdato";
            strRegView1BackButtonTitle          = "Adresse";
            strAddressUserHelp                  = "Hva er adressen din?";
            strAddressListedUserHelp            = "Verifiser adressen din:";
            strAddressLine1Prompt               = "Adresselinje 1";
            strAddressLine2Prompt               = "Adresselinje 2";
            strPostCodeAndCityPrompt            = "Postnummer og poststed";
            strDateOfBirthUserHelp              = "Når ble du født?";
            strDateOfBirthListedUserHelp        = "Verifiser fødselsdatoen din:";
            strDateOfBirthPrompt                = "Bruk datohjulene til å angi fødselsdato";
            strDateOfBirthClickHerePrompt       = "Fødselsdatoen din";
        } else {
            strRegView1NavItemTitle             = "Address, birth date";
            strRegView1BackButtonTitle          = "Address";
            strAddressUserHelp                  = "What is your home address?";
            strAddressListedUserHelp            = "Verify your home address:";
            strAddressLine1Prompt               = "Address line 1";
            strAddressLine2Prompt               = "Address line 2";
            strPostCodeAndCityPrompt            = "Postal code and city/town";
            strDateOfBirthUserHelp              = "When were you born?";
            strDateOfBirthListedUserHelp        = "Verify your birth date:";
            strDateOfBirthPrompt                = "Use the date wheels to enter your birth date";
            strDateOfBirthClickHerePrompt       = "Your birth date";
        }
    }
    
    
    private void setRegistrationView2Strings(String language)
    {
        if ("nb".equals(language)) {
            strRegView2NavItemTitle             = "Kjønn, telefon";
            strFemale                           = "Kvinne";
            strFemaleMinor                      = "Jente";
            strMale                             = "Mann";
            strMaleMinor                        = "Gutt";
            strGenderUserHelp                   = "Er du %@ eller %@?";
            strMobilePhoneUserHelp              = "Hva er mobilnummeret ditt?";
            strMobilePhoneListedUserHelp        = "Verifiser mobilnummeret ditt:";
            strMobilePhonePrompt                = "Mobilnummeret ditt";
            strLandlineUserHelp                 = "Har du fasttelefon?";
            strLandlineListedUserHelp           = "Verifiser fasttelefonnummeret ditt:";
            strLandlinePrompt                   = "Fasttelefonnummeret ditt";
        } else {
            strRegView2NavItemTitle             = "Gender, phone";
            strFemale                           = "Woman";
            strFemaleMinor                      = "Girl";
            strMale                             = "Man";
            strMaleMinor                        = "Boy";
            strGenderUserHelp                   = "Are you a %@ or a %@?";
            strMobilePhoneUserHelp              = "What is your mobile phone number?";
            strMobilePhoneListedUserHelp        = "Verify your mobile phone number:";
            strMobilePhonePrompt                = "Your mobile phone number";
            strLandlineUserHelp                 = "Have you got a landline phone?";
            strLandlineListedUserHelp           = "Verify your landline phone number:";
            strLandlinePrompt                   = "Your landline phone number";
        }
    }
    
    
    private void setButtonTitles(String language)
    {
        if ("nb".equals(language)) {
            strOK                               = "OK";
            strCancel                           = "Avbryt";
            strLogIn                            = "Logg inn";
            strNewUser                          = "Ny bruker";
            strHaveAccess                       = "Har tilgang";
            strHaveCode                         = "Har kode";
            strLater                            = "Nei, ikke nå";
            strTryAgain                         = "Prøv igjen";
            strGoBack                           = "Gå tilbake";
            strContinue                         = "Fortsett";
            strNext                             = "Neste";
            strDone                             = "Ferdig";
            strUseConfigured                    = "Lagret navn";
            strUseNew                           = "Nytt navn";
        } else {
            strOK                               = "OK";
            strCancel                           = "Cancel";
            strLogIn                            = "Log in";
            strNewUser                          = "New user";
            strHaveAccess                       = "Have access";
            strHaveCode                         = "Have code";
            strLater                            = "No, not now";
            strTryAgain                         = "Try again";
            strGoBack                           = "Go back";
            strContinue                         = "Continue";
            strNext                             = "Next";
            strDone                             = "Done";
            strUseConfigured                    = "Configured";
            strUseNew                           = "New name";
        }
    }
    
    
    private void setGenericScolaStrings(String language)
    {
        if ("nb".equals(language)) {
            strMyPlace                          = "Hjemme hos meg";
            strOurPlace                         = "Hjemme hos oss";
            strMyMessageBoard                   = "Min oppslagstavle";
            strOurMessageBoard                  = "Vår oppslagstavle";
        } else {
            strMyPlace                          = "My place";
            strOurPlace                         = "Our place";
            strMyMessageBoard                   = "My message board";
            strOurMessageBoard                  = "Our message board";
        }
    }
    
    
    public ScStrings()
    {
    	this("en");
    }
    
    
    public ScStrings(String language)
    {
        setErrorAndAlertMessages(language);
        
        setAuthViewStrings(language);
        setRegistrationView1Strings(language);
        setRegistrationView2Strings(language);
        setGenericScolaStrings(language);
        
        setButtonTitles(language);
    }
}
