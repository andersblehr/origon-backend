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
    
    // Alert messages
    public String strNoInternetAlertTitle;
    public String strNoInternetAlert;
    public String strInternalServerError;
    public String strInvalidNameAlert;
    public String strInvalidEmailAlert;
    public String strInvalidPasswordAlert;
    public String strEmailAlreadyRegisteredAlert;
    public String strPasswordsDoNotMatchAlert;
    public String strRegistrationCodesDoNotMatchAlert;
    public String strUserExistsAlertTitle;
    public String strUserExistsButNotLoggedInAlert;
    public String strUserExistsAndLoggedInAlert;
    public String strNotLoggedInAlert;
    public String strNoAddressAlert;
    public String strNoDeviceNameAlert;
    public String strNotBornAlert;
    public String strUnrealisticAgeAlert;
    public String strNoMobilePhoneAlert;
    
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
    public String strScolaDescription;
    public String strMembershipPrompt;
    public String strIsMember;
    public String strIsNew;
    public String strUserHelpNew;
    public String strUserHelpMember;
    public String strNamePrompt;
    public String strEmailPrompt;
    public String strNewPasswordPrompt;
    public String strPasswordPrompt;
    public String strPleaseWait;
    public String strUserHelpCompleteRegistration;
    public String strEmailSentPopUpTitle;
    public String strEmailSentPopUpMessage;
    public String strEmailSentToInviteePopUpTitle;
    public String strEmailSentToInviteePopUpMessage;
    public String strSeeYouLaterPopUpTitle;
    public String strSeeYouLaterPopUpMessage;
    public String strWelcomeBackPopUpTitle;
    public String strWelcomeBackPopUpMessage;
    public String strRegistrationCodePrompt;
    public String strRepeatPasswordPrompt;

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
    public String strDeviceNameUserHelp;
    public String strDeviceNamePrompt;
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
    
    
    private void setAlertMessages(String language) 
    {
        if ("nb".equals(language)) {
            strNoInternetAlertTitle             = "Ingen forbindelse";
            strNoInternetAlert                  = "Du er uten internettforbindelse for øyeblikket. Du kan fortsette å bruke Scola, men du vil ikke motta oppdateringer før du har forbindelse igjen.";
            strInternalServerError              = "Det har oppstått en feil. Vennligst prøv igjen senere.";
            strInvalidNameAlert                 = "Vennligst oppgi fullt navn som i signaturen din";
            strInvalidEmailAlert                = "Vennligst oppgi en gyldig epost-adresse";
            strInvalidPasswordAlert             = "Passordet må inneholde minimum %d tegn";
            strEmailAlreadyRegisteredAlert      = "Du er allerede registrert med epost-adressen %@. Vil du logge inn med denne adressen, eller vil du registrere en ny bruker?";
            strPasswordsDoNotMatchAlert         = "Passordet stemmer ikke med det du oppga tidligere. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            strRegistrationCodesDoNotMatchAlert = "Registreringskoden stemmer ikke med den du har mottatt på epost. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            strUserExistsAlertTitle             = "Allerede medlem";
            strUserExistsButNotLoggedInAlert    = "Du er allerede Scola-medlem, men passordet du har oppgitt stemmer ikke med det som er registrert hos oss. Vennligst oppgi passordet på nytt.";
            strUserExistsAndLoggedInAlert       = "Du er allerede Scola-medlem og er nå logget inn.";
            strNotLoggedInAlert                 = "Feil epost-adresse eller passord. Vennligst prøv igjen.";
            strNoAddressAlert                   = "Vennligst oppgi en adresse. Det holder at ett av feltene er utfylt.";
            strNoDeviceNameAlert                = "Du har ikke oppgitt et beskrivende navn til %@. Vil du bruke %@ lagrede navn (\"%@\"), eller vil du oppgi et nytt?";
            strNotBornAlert                     = "Du har oppgitt en fødselsdato i framtiden. Vi beklager veldig, men du må være født for å kunne bruke Scola.";
            strUnrealisticAgeAlert              = "Du har oppgitt at du er %d år gammel. Det er vanskelig å tro. Vennligst oppgi din virkelige fødselsdato.";
            strNoMobilePhoneAlert               = "Vennligst oppgi mobilnummeret ditt, evt. et annet telefonnummer om du ikke har mobiltelefon";
        } else {
            strNoInternetAlertTitle             = "No internet";
            strNoInternetAlert                  = "You have no internet connection at the moment. You can continue to use Scola, but you will not receive any updates until you are online again.";
            strInternalServerError              = "An error has occurred. Please try again later.";
            strInvalidNameAlert                 = "Please provide your full name, as in your signature";
            strInvalidEmailAlert                = "Please provide a valid email address";
            strInvalidPasswordAlert             = "The password must contain minimum %d characters";
            strEmailAlreadyRegisteredAlert      = "You are already registered with the email address %@. Do you want to log in with this address, or do you want to register a new user?";
            strPasswordsDoNotMatchAlert         = "The password does not match the one you entered before, please try again - or go back and start over.";
            strRegistrationCodesDoNotMatchAlert = "The registration code does not match the one you have received by email, please try again - or go back and start over.";
            strUserExistsAlertTitle             = "Already member";
            strUserExistsButNotLoggedInAlert    = "You are already a Scola member, but the password you have provided does not match our records. Please enter your password again.";
            strUserExistsAndLoggedInAlert       = "You are already a Scola member and are now logged in.";
            strNotLoggedInAlert                 = "Wrong email or password. Please try again.";
            strNoAddressAlert                   = "Please provide an address. It is sufficient to fill in one of the fields.";
            strNoDeviceNameAlert                = "You have not provided a descriptive name for %@. Do you want to use %@ configured name ('%@'), or do you want to provide a new name?";
            strNotBornAlert                     = "You have provided a date of birth that's in the future. We are very sorry, but you must have been born in order to use Scola.";
            strUnrealisticAgeAlert              = "According to the date of birth you have provided, you are %d years old. That's hard to believe. Please provide your true date of birth.";
            strNoMobilePhoneAlert               = "Please provide your mobile phone number, or an alternative phone number if you do not have a mobile phone";
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
            strScolaDescription                 = "[subst.] en gruppe mennesker som omgås, samarbeider og/eller er avhengige av hverandre i det daglige.";
            strMembershipPrompt                 = "Er du medlem? Om ikke, ønsker du å bli medlem?";
            strIsMember                         = "Er medlem";
            strIsNew                            = "Vil bli medlem";
            strUserHelpNew                      = "Om du vil bli Scola-medlem, vennligst oppgi:";
            strUserHelpMember                   = "Logg på om du allerede er Scola-medlem:";
            strNamePrompt                       = "Fullt navn som i signaturen din";
            strEmailPrompt                      = "Epost-adressen din";
            strNewPasswordPrompt                = "Et fritt valgt passord";
            strPasswordPrompt                   = "Passordet ditt";
            strPleaseWait                       = "Vennligst vent...";
            strUserHelpCompleteRegistration     = "For å fullføre registreringen, vennligst oppgi:";
            strEmailSentPopUpTitle              = "Registreringskode sendt";
            strEmailSentPopUpMessage            = "En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strEmailSentToInviteePopUpTitle     = "Velkommen til Scola!";
            strEmailSentToInviteePopUpMessage   = "Takk for at du tar imot scola-invitasjonen(e) du har mottatt. En epost med din personlige registreringskode er sendt til %@. Har du tilgang til eposten slik at du kan fortsette nå?";
            strSeeYouLaterPopUpTitle            = "Ser deg senere!";
            strSeeYouLaterPopUpMessage          = "Du kan trygt forlate Scola. Vi fortsetter neste gang du er innom.";
            strWelcomeBackPopUpTitle            = "Velkommen tilbake!";
            strWelcomeBackPopUpMessage          = "Om du har registreringskoden som ble sendt til %@, kan du nå fullføre registreringen. Om ikke, kan du gå tilbake og starte på nytt.";
            strRegistrationCodePrompt           = "Din personlige registreringskode";
            strRepeatPasswordPrompt             = "Samme passord som tidligere";
        } else {
            strScolaDescription                 = "[noun] a group of people who interact, team up, and/or depend on each other in day-to-day activities.";
            strMembershipPrompt                 = "Are you a member? If not, would you like to register?";
            strIsMember                         = "I am a member";
            strIsNew                            = "I want to register";
            strUserHelpNew                      = "To become a Scola member, please provide:";
            strUserHelpMember                   = "Log in if you are already a Scola member:";
            strNamePrompt                       = "Your name as you sign it";
            strEmailPrompt                      = "Your email address";
            strNewPasswordPrompt                = "A password of your choice";
            strPasswordPrompt                   = "Your password";
            strPleaseWait                       = "Please wait...";
            strUserHelpCompleteRegistration     = "To complete your registration, please provide:";
            strEmailSentPopUpTitle              = "Registration code sent";
            strEmailSentPopUpMessage            = "An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strEmailSentToInviteePopUpTitle     = "Welcome to Scola!";
            strEmailSentToInviteePopUpMessage   = "Thank you for accepting the scola invitation(s) you have received. An email with your personal registration code has been sent to %@. Have you got access to your email so that you can continue now?";
            strSeeYouLaterPopUpTitle            = "See you later!";
            strSeeYouLaterPopUpMessage          = "You can safely leave Scola. We'll continue next time your drop by.";
            strWelcomeBackPopUpTitle            = "Welcome back!";
            strWelcomeBackPopUpMessage          = "If you have handy the registration code sent to %@, you can now complete your registration. If not, please go back and start over.";
            strRegistrationCodePrompt           = "Your personal registration code";
            strRepeatPasswordPrompt             = "The same password as before";
        }
    }
    
    
    private void setRegistrationView1Strings(String language)
    {
        if ("nb".equals(language)) {
            strProvideAddressUserHelp           = "Hva er adressen din?";
            strVerifyAddressUserHelp            = "Vennligst verifiser adressen din:";
            strAddressLine1Prompt               = "Adresselinje 1";
            strAddressLine2Prompt               = "Adresselinje 2";
            strPostCodeAndCityPrompt            = "Postnummer og poststed";
            strDateOfBirthUserHelp              = "Når ble du født?";
            strDateOfBirthPrompt                = "Bruk datohjulene til å angi fødselsdato";
            strDateOfBirthClickHerePrompt       = "Fødselsdatoen din";
        } else {
            strProvideAddressUserHelp           = "What is your home address?";
            strVerifyAddressUserHelp            = "Please verify your home address:";
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
            strDeviceNameUserHelp               = "Scola vil referere til %@ som:";
            strDeviceNamePrompt                 = "Et beskrivende navn på %@";
        } else {
            strGenderUserHelp                   = "Are you a %@ or a %@?";
            strFemaleAdult                      = "Woman";
            strFemaleMinor                      = "Girl";
            strMaleAdult                        = "Man";
            strMaleMinor                        = "Boy";
            strMobilePhoneUserHelp              = "What is your mobile number?";
            strMobilePhonePrompt                = "Your mobile number";
            strDeviceNameUserHelp               = "Scola will refer to %@ as:";
            strDeviceNamePrompt                 = "A descriptive name for %@";
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
        setAlertMessages(language);
        setButtonTitles(language);
        
        setAuthViewStrings(language);
        setRegistrationView1Strings(language);
        setRegistrationView2Strings(language);
        setGenericScolaStrings(language);
    }
}
