package com.scolaapp.api.strings;


public class ScStrings
{
    // Grammar snippets
    public String strPhoneDefinite;
    public String str_iPodDefinite;
    public String str_iPadDefinite;
    public String strPhonePossessive;
    public String str_iPodPossessive;
    public String str_iPadPossessive;
    public String strThisPhone;
    public String strThis_iPod;
    public String strThis_iPad;
    
    // Error & alert messages
    public String strNoInternetError;
    public String strServerErrorAlert;
    public String strInvalidNameAlert;
    public String strInvalidEmailAlert;
    public String strInvalidPasswordAlert;
    public String strEmailSentAlertTitle;
    public String strEmailSentAlert;
    public String strEmailSentToInviteeAlertTitle;
    public String strEmailSentToInviteeAlert;
    public String strPasswordsDoNotMatchAlert;
    public String strRegistrationCodesDoNotMatchAlert;
    public String strUserExistsMustLogInAlert;
    public String strNotLoggedInAlert;
    public String strNoAddressAlert;
    public String strInvalidDateOfBirthAlert;
    public String strNoPhoneNumberAlert;
    
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
    
    // Auth view
    public String strUserIntentionPrompt;
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
    public String strProvideAddressUserHelp;
    public String strVerifyAddressUserHelp;
    public String strAddressLine1Prompt;
    public String strAddressLine2Prompt;
    public String strPostCodeAndCityPrompt;
    public String strDateOfBirthUserHelp;
    public String strDateOfBirthPrompt;
    public String strDateOfBirthClickHerePrompt;
    
    // Registration view 2
    public String strGenderUserHelp;
    public String strMobilePhoneUserHelp;
    public String strMobilePhonePrompt;
    public String strLandlineUserHelp;
    public String strLandlinePrompt;
    public String strFemaleAdult;
    public String strFemaleMinor;
    public String strMaleAdult;
    public String strMaleMinor;
    
    // Generic Scola strings
    public String strMyPlace;
    public String strOurPlace;
    public String strMyMessageBoard;
    public String strOurMessageBoard;
    
    
    private void setGrammarSnippets(String language)
    {
        if ("nb".equals(language)) {
            strPhoneDefinite                    = "telefonen";
            str_iPodDefinite                    = "iPod'en";
            str_iPadDefinite                    = "iPad'en";
            strPhonePossessive                  = "telefonens";
            str_iPodPossessive                  = "iPod'ens";
            str_iPadPossessive                  = "iPad'ens";
            strThisPhone                        = "denne telefonen";
            strThis_iPod                        = "denne iPod'en";
            strThis_iPad                        = "denne iPad'en";
        } else {
            strPhoneDefinite                    = "the phone";
            str_iPodDefinite                    = "the iPod";
            str_iPadDefinite                    = "the iPad";
            strPhonePossessive                  = "the phone's";
            str_iPodPossessive                  = "the iPod's";
            str_iPadPossessive                  = "the iPad's";
            strThisPhone                        = "this phone";
            strThis_iPod                        = "this iPod";
            strThis_iPad                        = "this iPad";
        }
    }
    
    
    private void setErrorAndAlertMessages(String language) 
    {
        if ("nb".equals(language)) {
            strNoInternetError                  = "Ingen internettforbindelse.";
            strServerErrorAlert                 = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            strInvalidNameAlert                 = "Du må oppgi fullt navn som i signaturen din";
            strInvalidEmailAlert                = "Du må oppgi en gyldig epost-adresse";
            strInvalidPasswordAlert             = "Passordet må inneholde minimum %d tegn";
            strEmailSentAlertTitle              = "Registreringskode sendt";
            strEmailSentAlert                   = "En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strEmailSentToInviteeAlertTitle     = "Velkommen til Scola!";
            strEmailSentToInviteeAlert          = "Takk for at du tar imot scola-invitasjonen(e) du har mottatt. En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strPasswordsDoNotMatchAlert         = "Passordet stemmer ikke med det du oppga tidligere. Du kan prøve igjen, eller du kan gå tilbake og start på nytt.";
            strRegistrationCodesDoNotMatchAlert = "Registreringskoden stemmer ikke med den du har mottatt på epost. Du kan prøve igjen, eller du kan gå tilbake og start på nytt.";
            strUserExistsMustLogInAlert         = "Det ser ut som du prøver å logge inn. Du blir nå tatt til innloggingsdialogen.";
            strNotLoggedInAlert                 = "Feil epost-adresse eller passord. Vennligst prøv igjen.";
            strNoAddressAlert                   = "Du må oppgi en adresse. Det holder at ett av feltene er utfylt.";
            strNoPhoneNumberAlert               = "Du må oppgi minst ett telefonnummer.";
            strInvalidDateOfBirthAlert          = "Du har oppgitt en ugyldig fødselsdato. Du må angi en dato som gjør deg mellom 5 og 110 år gammel.";
        } else {
            strNoInternetError                  = "No internet connection.";
            strServerErrorAlert                 = "An error has occurred. Please try again later. [%d: \"%@\"]";
            strInvalidNameAlert                 = "Please provide your full name, as in your signature";
            strInvalidEmailAlert                = "Please provide a valid email address";
            strInvalidPasswordAlert             = "The password must contain minimum %d characters";
            strEmailSentAlertTitle              = "Registration code sent";
            strEmailSentAlert                   = "An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strEmailSentToInviteeAlertTitle     = "Welcome to Scola!";
            strEmailSentToInviteeAlert          = "Thank you for accepting the scola invitation(s) you have received. An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strPasswordsDoNotMatchAlert         = "The password does not match the one you entered before. You can try again, or you can go back and start over.";
            strRegistrationCodesDoNotMatchAlert = "The registration code does not match the one you have received by email. You can try again, or you can go back and start over.";
            strUserExistsMustLogInAlert         = "It looks like you are trying to log in. You will now be taken to the login dialog.";
            strNotLoggedInAlert                 = "Incorrent email or password. Please try again.";
            strNoAddressAlert                   = "Please provide an address. It is sufficient to fill in one of the fields.";
            strNoPhoneNumberAlert               = "You must provide at least one phone number.";
            strInvalidDateOfBirthAlert          = "You have provided an invalid date of birth. Please provide a date that puts your age between 5 and 110 years.";
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
    
    
    private void setAuthViewStrings(String language)
    {
        if ("nb".equals(language)) {
            strUserIntentionPrompt              = "Velkommen til Scola!";
            strUserIntentionLogin               = "Jeg vil logge inn";
            strUserIntentionRegistration        = "Jeg vil bli medlem";
            strUserHelpNew                      = "For å bli Scola-medlem må du oppgi:";
            strUserHelpMember                   = "Logg inn om du allerede er Scola-medlem:";
            strNamePrompt                       = "Fullt navn som i signaturen din";
            strEmailPrompt                      = "Epost-adressen din";
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
            strUserIntentionPrompt              = "Welcome to Scola!";
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
            strProvideAddressUserHelp           = "Hva er adressen din?";
            strVerifyAddressUserHelp            = "Verifiser adressen din:";
            strAddressLine1Prompt               = "Adresselinje 1";
            strAddressLine2Prompt               = "Adresselinje 2";
            strPostCodeAndCityPrompt            = "Postnummer og poststed";
            strDateOfBirthUserHelp              = "Når ble du født?";
            strDateOfBirthPrompt                = "Bruk datohjulene til å angi fødselsdato";
            strDateOfBirthClickHerePrompt       = "Fødselsdatoen din";
        } else {
            strProvideAddressUserHelp           = "What is your home address?";
            strVerifyAddressUserHelp            = "Verify your home address:";
            strAddressLine1Prompt               = "Address line 1";
            strAddressLine2Prompt               = "Address line 2";
            strPostCodeAndCityPrompt            = "Postal code and city/town";
            strDateOfBirthUserHelp              = "When were you born?";
            strDateOfBirthPrompt                = "Use the date wheels to enter your birth date";
            strDateOfBirthClickHerePrompt       = "Your birth date";
        }
    }
    
    
    private void setRegistrationView2Strings(String language)
    {
        if ("nb".equals(language)) {
            strGenderUserHelp                   = "Er du %@ eller %@?";
            strFemaleAdult                      = "Kvinne";
            strFemaleMinor                      = "Jente";
            strMaleAdult                        = "Mann";
            strMaleMinor                        = "Gutt";
            strMobilePhoneUserHelp              = "Hva er mobilnummeret ditt?";
            strMobilePhonePrompt                = "Mobilnummeret ditt";
            strLandlineUserHelp                 = "Har du fasttelefon?";
            strLandlinePrompt                   = "Fasttelefonnummeret ditt";
        } else {
            strGenderUserHelp                   = "Are you a %@ or a %@?";
            strFemaleAdult                      = "Woman";
            strFemaleMinor                      = "Girl";
            strMaleAdult                        = "Man";
            strMaleMinor                        = "Boy";
            strMobilePhoneUserHelp              = "What is your mobile number?";
            strMobilePhonePrompt                = "Your mobile number";
            strLandlineUserHelp                 = "Have you got a landline?";
            strLandlinePrompt                   = "Your landline number";
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
        setGrammarSnippets(language);
        setErrorAndAlertMessages(language);
        setButtonTitles(language);
        
        setAuthViewStrings(language);
        setRegistrationView1Strings(language);
        setRegistrationView2Strings(language);
        setGenericScolaStrings(language);
    }
}
