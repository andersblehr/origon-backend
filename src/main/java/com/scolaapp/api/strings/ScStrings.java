package com.scolaapp.api.strings;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="ScStrings")
public class ScStrings
{
    // Grammar snippets
    public String strPhoneDeterminate;
    public String str_iPodDeterminate;
    public String str_iPadDeterminate;
    public String strPhonePossessive;
    public String str_iPodPossessive;
    public String str_iPadPossessive;
    public String strThisPhone;
    public String strThis_iPod;
    public String strThis_iPad;
    
    // Alert messages
    public String strServerUnavailableAlert;
    public String strInternalServerError;
    public String strInvalidNameAlert;
    public String strInvalidEmailAlert;
    public String strInvalidPasswordAlert;
    public String strInvalidScolaShortnameAlert;
    public String strPasswordsDoNotMatchAlert;
    public String strRegistrationCodesDoNotMatchAlert;
    public String strScolaInvitationNotFoundAlert;
    public String strUserExistsAlertTitle;
    public String strUserExistsButNotLoggedInAlert;
    public String strUserExistsAndLoggedInAlert;
    public String strNotLoggedInAlert;
    public String strNoAddressAlert;
    public String strNoDeviceNameAlert;
    public String strNotBornAlert;
    public String strUnrealisticAgeAlert;
    
    // Button titles
    public String strOK;
    public String strCancel;
    public String strHaveAccess;
    public String strHaveCode;
    public String strLater;
    public String strTryAgain;
    public String strGoBack;
    public String strContinue;
    public String strSkipThis;
    public String strNext;
    public String strDone;
    public String strUseConfigured;
    public String strUseNew;
    
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

    // Address view
    public String strProvideAddressUserHelp;
    public String strVerifyAddressUserHelp;
    public String strAddressLine1Prompt;
    public String strAddressLine2Prompt;
    public String strPostCodeAndCityPrompt;
    
    // Date of birth view
    public String strDeviceNameUserHelp;
    public String strDeviceNamePrompt;
    public String strGenderUserHelp;
    public String strFemale;
    public String strMale;
    public String strNeutral;
    public String strDateOfBirthUserHelp;
    public String strDateOfBirthPrompt;
    public String strDateOfBirthClickHerePrompt;
    
    
    public ScStrings()
    {
    	this("en");
    }
    
    
    public ScStrings(String language)
    {
        if ("nb".equals(language)) {
            // Grammer snippets
            strPhoneDeterminate                 = "telefonen";
            str_iPodDeterminate                 = "iPod'en";
            str_iPadDeterminate                 = "iPad'en";
            strPhonePossessive                  = "telefonens";
            str_iPodPossessive                  = "iPod'ens";
            str_iPadPossessive                  = "iPad'ens";
            strThisPhone                        = "denne telefonen";
            strThis_iPod                        = "denne iPod'en";
            strThis_iPad                        = "denne iPad'en";
            
            // Alert messages
            strServerUnavailableAlert           = "Scola-tjenesten er dessverre utilgjengelig akkurat nå. Vennligst prøv igjen senere.";
            strInternalServerError              = "Det har oppstått en feil. Vennligst prøv igjen senere.";
            strInvalidNameAlert                 = "Vennligst oppgi fullt navn som i signaturen din";
            strInvalidEmailAlert                = "Vennligst oppgi en gyldig epost-adresse";
            strInvalidPasswordAlert             = "Passordet må inneholde minimum %d tegn";
            strInvalidScolaShortnameAlert       = "Scola-kortnavnet er for kort. Vennligst oppgi kortnavnet nøyaktig slik det står i invitasjonen.";
            strPasswordsDoNotMatchAlert         = "Passordet stemmer ikke med det du oppga tidligere. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            strRegistrationCodesDoNotMatchAlert = "Registreringskoden stemmer ikke med den du har mottatt på epost. Vennligst prøv igjen - eller gå tilbake og start på nytt.";
            strScolaInvitationNotFoundAlert     = "Det finnes ingen invitasjon til '%@'-scolaen for navnet du har oppgitt. Vennligst oppgi både ditt eget navn og scola-kortnavnet nøyaktig slik de står skrevet i invitasjonen. (Du kan korrigere navnet ditt senere om det er feilstavet.)";
            strUserExistsAlertTitle             = "Allerede medlem";
            strUserExistsButNotLoggedInAlert    = "Du er allerede Scola-medlem, men passordet du har oppgitt stemmer ikke med det som er registrert hos oss. Vennligst oppgi passordet på nytt.";
            strUserExistsAndLoggedInAlert       = "Du er allerede Scola-medlem og er nå logget inn.";
            strNotLoggedInAlert                 = "Feil epost-adresse eller passord. Vennligst prøv igjen.";
            strNoAddressAlert                   = "Vennligst oppgi en adresse. Det holder at ett av feltene er utfylt.";
            strNoDeviceNameAlert                = "Du har ikke oppgitt et beskrivende navn til %@. Vil du bruke %@ lagrede navn (\"%@\"), eller vil du oppgi et nytt?";
            strNotBornAlert                     = "Du har oppgitt en fødselsdato i framtiden. Vi beklager veldig, men du må nok være født for å kunne bruke Scola.";
            strUnrealisticAgeAlert              = "Du har oppgitt at du er %d år gammel. Det er vanskelig å tro. Vennligst oppgi din virkelige fødselsdato.";
            
            // Button titles
            strOK                               = "OK";
            strCancel                           = "Avbryt";
            strHaveAccess                       = "Har tilgang";
            strHaveCode                         = "Har kode";
            strLater                            = "Nei, ikke nå";
            strTryAgain                         = "Prøv igjen";
            strGoBack                           = "Gå tilbake";
            strContinue                         = "Fortsett";
            strSkipThis                         = "Hopp over";
            strNext                             = "Neste";
            strDone                             = "Ferdig";
            strUseConfigured                       = "Lagret navn";
            strUseNew                           = "Nytt navn";
            
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
            
            // Address view
            strProvideAddressUserHelp           = "Hva er adressen din?";
            strVerifyAddressUserHelp            = "Vennligst verifiser adressen din:";
            strAddressLine1Prompt               = "Adresselinje 1";
            strAddressLine2Prompt               = "Adresselinje 2";
            strPostCodeAndCityPrompt            = "Postnummer og poststed";
            
            // Date of birth view
            strDeviceNameUserHelp               = "Scola vil referere til %@ som:";
            strDeviceNamePrompt                 = "Et beskrivende navn på %@";
            strGenderUserHelp                   = "Er du kvinne eller mann?";
            strFemale                           = "Kvinne";
            strMale                             = "Mann";
            strNeutral                          = "Nøytral";
            strDateOfBirthUserHelp              = "Når ble du født?";
            strDateOfBirthPrompt                = "Bruk datohjulene til å angi fødselsdato";
            strDateOfBirthClickHerePrompt       = "Trykk her for å vise datohjulene";
        } else {
            // Grammer snippets
            strPhoneDeterminate                 = "the phone";
            str_iPodDeterminate                 = "the iPod";
            str_iPadDeterminate                 = "the iPad";
            strPhonePossessive                  = "the phone's";
            str_iPodPossessive                  = "the iPod's";
            str_iPadPossessive                  = "the iPad's";
            strThisPhone                        = "this phone";
            strThis_iPod                        = "this iPod";
            strThis_iPad                        = "this iPad";
            
            // Alert messages
            strServerUnavailableAlert           = "We are very sorry, but the Scola service is unavailable at the moment. Please try again later.";
            strInternalServerError              = "An error has occurred. Please try again later.";
            strInvalidNameAlert                 = "Please provide your full name, as in your signature";
            strInvalidEmailAlert                = "Please provide a valid email address";
            strInvalidPasswordAlert             = "The password must contain minimum %d characters";
            strInvalidScolaShortnameAlert       = "The scola shortname is too short. Please use the exact spelling as provided in the invitation.";
            strPasswordsDoNotMatchAlert         = "The password does not match the one you entered before, please try again - or go back and start over.";
            strRegistrationCodesDoNotMatchAlert = "The registration code does not match the one you have received by email, please try again - or go back and start over.";
            strScolaInvitationNotFoundAlert     = "There is no invitation to the '%@' scola for the name you have entered. Please enter your own name and the scola shortname exactly as given in the invitation. (You can correct your name later if it is spelt incorrectly.)";
            strUserExistsAlertTitle             = "Already member";
            strUserExistsButNotLoggedInAlert    = "You are already a Scola member, but the password you have provided does not match our records. Please enter your password again.";
            strUserExistsAndLoggedInAlert       = "You are already a Scola member and are now logged in.";
            strNotLoggedInAlert                 = "Wrong email or password. Please try again.";
            strNoAddressAlert                   = "Please provide an address. It is sufficient to fill in one of the fields.";
            strNoDeviceNameAlert                = "You have not provided a descriptive name for %@. Do you want to use %@ configured name ('%@'), or do you want to provide a new name?";
            strNotBornAlert                     = "You have provided a date of birth that's in the future. We are very sorry, but you must have been born in order to use Scola.";
            strUnrealisticAgeAlert              = "According to the date of birth you have provided, you are %d years old. That's hard to believe. Please provide your true date of birth.";
            
            // Button titles
            strOK                               = "OK";
            strCancel                           = "Cancel";
            strHaveAccess                       = "Have access";
            strHaveCode                         = "Have code";
            strLater                            = "No, not now";
            strTryAgain                         = "Try again";
            strGoBack                           = "Go back";
            strContinue                         = "Continue";
            strSkipThis                         = "Skip this";
            strNext                             = "Next";
            strDone                             = "Done";
            strUseConfigured                    = "Configured";
            strUseNew                           = "New name";
           
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
            
            // Address view
            strProvideAddressUserHelp           = "What is your home address?";
            strVerifyAddressUserHelp            = "Please verify your home address:";
            strAddressLine1Prompt               = "Address line 1";
            strAddressLine2Prompt               = "Address line 2";
            strPostCodeAndCityPrompt            = "Postal code and city/town";
            
            // Date of birth view
            strDeviceNameUserHelp               = "Scola will refer to %@ as:";
            strDeviceNamePrompt                 = "A descriptive name for %@";
            strGenderUserHelp                   = "Please provide your gender";
            strFemale                           = "Female";
            strMale                             = "Male";
            strNeutral                          = "N/A";
            strDateOfBirthUserHelp              = "When were you born?";
            strDateOfBirthPrompt                = "Use the date wheels to enter your birth date";
            strDateOfBirthClickHerePrompt       = "Tap here to display the date wheels";
        }
    }
}
