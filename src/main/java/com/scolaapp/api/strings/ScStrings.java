package com.scolaapp.api.strings;


public class ScStrings
{
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_NORWEGIAN_BOKMAL = "nb";
    
    
    public ScStrings()
    {
        this(LANG_ENGLISH);
    }
    
    
    public ScStrings(String language)
    {
        setEULA(language);
        
        setGenericStrings(language);
        setPrompts(language);
        setLabels(language);
        setHeaderAndFooterStrings(language);
        setButtonTitles(language);
        setAlertsAndErrorMessages(language);
        
        setMembershipViewStrings(language);
        setMemberViewStrings(language);
    }
    
    
    /* ==== EULA ==== */
    
    public String strEULA;
    public String strAccept;
    public String strDecline;
    
    private void setEULA(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strEULA    = "– MELLOM OSS –\n" +
                         "\n" +
                         "Du er nå nesten klar til å begynne å bruke Scola. Men for at Scola skal være til nytte for deg og dine, må vi be deg gi noen opplysninger om husstanden din. Vi lover derfor at:\n" +
                         "\n" +
                         "- Opplysningene forblir dine og bare dine.\n" +
                         "- Vi forbeholder oss absolutt ingen rett til dem.\n" +
                         "- Vi selger dem ikke videre.\n" + 
                         "- Vi analyserer dem ikke for å finne ut hva du og dine nærmeste liker og mener.\n" +
                         "- Vi har dem bare til låns, for uten dem har Scola ingen nytteverdi.\n" +
                         "\n" +
                         "Om du godtar dette, vil du motta en epost fra oss som dokumenterer ovenstående.";
            strAccept  = "Godta";
            strDecline = "Avslå";
        } else {
            strEULA    = "– BETWEEN US –\n" +
                         "\n" +
                         "You are now almost ready to start using Scola. But for Scola to be of use to you and your family, we need you to provide some information about your household. We promise that:\n" +
                         "\n" +
                         "- The information remains yours and yours only.\n" +
                         "- We claim no rights to it whatsoever.\n" +
                         "- We do not sell it to third parties.\n" + 
                         "- We do not analyse it in order to figure out what you and your family may like.\n" +
                         "- We are only borrowing it, for without it, Scola has no use.\n" +
                         "\n" +
                         "If you accept this, you will receive an email from us that documents the above.";
            strAccept    = "Accept";
            strDecline   = "Decline";
        }
    }
    
    
    /* ==== GENERIC STRINGS ==== */
    
    public String strPleaseWait;
    public String strAbout;
    public String strToBe2ndPSg;
    public String strToBe3rdPSg;
    public String strYouNom;
    public String strYouAcc;
    public String strHousehold;
    public String strFemale;
    public String strFemaleMinor;
    public String strMale;
    public String strMaleMinor;
    public String strMyPlace;
    public String strOurPlace;
    public String strMyMessageBoard;
    public String strOurMessageBoard;
    
    private void setGenericStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strPleaseWait      = "Vent litt...";
            strAbout           = "Om";
            strToBe2ndPSg      = "Er";
            strToBe3rdPSg      = "Er";
            strYouNom          = "Du";
            strYouAcc          = "Deg";
            strFemale          = "Kvinne";
            strFemaleMinor     = "Jente";
            strMale            = "Mann";
            strMaleMinor       = "Gutt";
            strHousehold       = "Husstand";
            strMyPlace         = "Min husstand";
            strOurPlace        = "Vår husstand";
            strMyMessageBoard  = "Min oppslagstavle";
            strOurMessageBoard = "Vår oppslagstavle";
        } else {
            strPleaseWait      = "Please wait...";
            strAbout           = "About";
            strToBe2ndPSg      = "Are";
            strToBe3rdPSg      = "Is";
            strYouNom          = "You";
            strYouAcc          = "You";
            strFemale          = "Woman";
            strFemaleMinor     = "Girl";
            strMale            = "Man";
            strMaleMinor       = "Boy";
            strHousehold       = "Household";
            strMyPlace         = "My place";
            strOurPlace        = "Our place";
            strMyMessageBoard  = "My message board";
            strOurMessageBoard = "Our message board";
        }
    }
    
    
    /* ==== PROMTPS ==== */
    
    public String strAuthEmailPrompt;
    public String strPasswordPrompt;
    public String strRegistrationCodePrompt;
    public String strRepeatPasswordPrompt;
    public String strPhotoPrompt;
    public String strNamePrompt;
    public String strEmailPrompt;
    public String strDateOfBirthPrompt;
    public String strMobilePhonePrompt;
    public String strUserWebsitePrompt;
    public String strAddressLine1Prompt;
    public String strAddressLine2Prompt;
    public String strHouseholdLandlinePrompt;
    public String strScolaLandlinePrompt;
    public String strScolaWebsitePrompt;
    
    private void setPrompts(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strAuthEmailPrompt         = "Epostadressen din";
            strPasswordPrompt          = "Passordet ditt";
            strRegistrationCodePrompt  = "Registreringskode fra epost";
            strRepeatPasswordPrompt    = "Gjenta passordet ditt";
            strPhotoPrompt             = "Bilde";
            strNamePrompt              = "Fullt navn";
            strEmailPrompt             = "En gyldig epostadresse";
            strDateOfBirthPrompt       = "Fødselsdato";
            strMobilePhonePrompt       = "Mobilnummer";
            strUserWebsitePrompt       = "Nettside, blogg el.tilsv.";
            strAddressLine1Prompt      = "Gateadresse";
            strAddressLine2Prompt      = "Postnummer og -sted";
            strHouseholdLandlinePrompt = "Hjemmetelefonnummer";
            strScolaLandlinePrompt     = "Telefonnummer til kontaktperson";
            strScolaWebsitePrompt      = "Hjemmeside";
        } else {
            strAuthEmailPrompt         = "Your email address";
            strPasswordPrompt          = "Your password";
            strRegistrationCodePrompt  = "Registration code from email";
            strRepeatPasswordPrompt    = "Repeat your password";
            strPhotoPrompt             = "Photo";
            strNamePrompt              = "Full name";
            strEmailPrompt             = "A valid email address";
            strDateOfBirthPrompt       = "Date of birth";
            strMobilePhonePrompt       = "Mobile phone number";
            strUserWebsitePrompt       = "Web page, blog or similar";
            strAddressLine1Prompt      = "Street address";
            strAddressLine2Prompt      = "Postal code and city/town";
            strHouseholdLandlinePrompt = "Home telephone number";
            strScolaLandlinePrompt     = "Contact phone number";
            strScolaWebsitePrompt      = "Home page";
        }
    }
    
    
    /* ==== LABELS ==== */
    
    public String strSignInOrRegisterLabel;
    public String strConfirmRegistrationLabel;
    public String strSingleLetterEmailLabel;
    public String strSingleLetterMobilePhoneLabel;
    public String strSingleLetterDateOfBirthLabel;
    public String strSingleLetterAddressLabel;
    public String strSingleLetterLandlineLabel;
    public String strSingleLetterWebsiteLabel;
    public String strAddressLabel;
    public String strLandlineLabel;
    
    private void setLabels(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strSignInOrRegisterLabel        = "Logg på eller registrer ny bruker";
            strConfirmRegistrationLabel     = "Oppgi registreringskoden din";
            strSingleLetterEmailLabel       = "e:";
            strSingleLetterMobilePhoneLabel = "m:";
            strSingleLetterDateOfBirthLabel = "f:";
            strSingleLetterAddressLabel     = "a:";
            strSingleLetterLandlineLabel    = "t:";
            strSingleLetterWebsiteLabel     = "w:";
            strAddressLabel                 = "Adresse";
            strLandlineLabel                = "Telefon";
        } else {
            strSignInOrRegisterLabel        = "Sign in or register new user";
            strConfirmRegistrationLabel     = "Provide your registration code";
            strSingleLetterEmailLabel       = "e:";
            strSingleLetterMobilePhoneLabel = "m:";
            strSingleLetterDateOfBirthLabel = "b:";
            strSingleLetterAddressLabel     = "a:";
            strSingleLetterLandlineLabel    = "t:";
            strSingleLetterWebsiteLabel     = "w:";
            strAddressLabel                 = "Address";
            strLandlineLabel                = "Telephone";
        }
    }
    
    
    /* ==== HEADER & FOOTER STRINGS ==== */
    
    public String strSignInOrRegisterFooter;
    public String strConfirmRegistrationFooter;
    public String strHouseholdMemberListFooter;
    
    private void setHeaderAndFooterStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strSignInOrRegisterFooter    = "Når du registrerer ny bruker, vil du motta en epost med en registreringskode som du må oppgi for å bekrefte identiteten din.";
            strConfirmRegistrationFooter = "Oppgi registreringskoden som du har mottatt på epost, eller kom tilbake senere om ikke du har tilgang til eposten din her og nå.";
            strHouseholdMemberListFooter = "Her kan du legge til eller ta bort medlemmer i husstanden din. Nye medlemmer vil motta en Scola-invitasjon fra deg.";
        } else {
            strSignInOrRegisterFooter    = "When you register, you will receive an email with a registration code that you must provide in order to confirm your identity.";
            strConfirmRegistrationFooter = "Please provide the registration code that was emailed to you, or come back later if you don't have access to your email at this time.";
            strHouseholdMemberListFooter = "Here you may add or remove members of your household. New members will receive a Scola invitation from you.";
        }
    }
    
    
    /* ==== BUTTON TITLES ==== */
    
    public String strOK;
    public String strCancel;
    public String strRetry;
    public String strStartOver;
    public String strHaveCode;
    
    private void setButtonTitles(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strOK        = "OK";
            strCancel    = "Avbryt";
            strRetry     = "Prøv igjen";
            strStartOver = "Gå tilbake";
            strHaveCode  = "Har kode";
        } else {
            strOK        = "OK";
            strCancel    = "Cancel";
            strRetry     = "Retry";
            strStartOver = "Start over";
            strHaveCode  = "Have code";
        }
    }
    
    
    /* ==== ALERTS & ERROR MESSAGES ==== */
    
    public String strNoInternetError;
    public String strServerErrorAlert;
    public String strUserConfirmationFailedTitle;
    public String strUserConfirmationFailedAlert;
    public String strWelcomeBackTitle;
    public String strWelcomeBackAlert;
    public String strIncompleteRegistrationTitle;
    public String strIncompleteRegistrationAlert;

    
    private void setAlertsAndErrorMessages(String language) 
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strNoInternetError              = "Ingen internettforbindelse.";
            strServerErrorAlert             = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            strUserConfirmationFailedTitle  = "Bekreftelse mislyktes";
            strUserConfirmationFailedAlert  = "Det ser ut til at du enten har mistet registreringskoden som ble sendt på epost, eller at du har glemt passordet du oppga. La oss starte på nytt.";
            strWelcomeBackTitle             = "Velkommen tilbake!";
            strWelcomeBackAlert             = "Om du har registreringskoden som ble sendt til %@, kan du nå fullføre registreringen. Om ikke, kan du gå tilbake og starte på nytt.";
            strIncompleteRegistrationTitle  = "Ufullstendig registrering";
            strIncompleteRegistrationAlert  = "Du må fullføre registreringen før du kan begynne å bruke Scola.";
        } else {
            strNoInternetError              = "No internet connection.";
            strServerErrorAlert             = "An error has occurred. Please try again later. [%d: \"%@\"]";
            strUserConfirmationFailedTitle  = "Confirmation failed";
            strUserConfirmationFailedAlert  = "It looks like you may have lost the registration code that was sent to you by email, or forgotten the password you provided. Let's start over.";
            strWelcomeBackTitle             = "Welcome back!";
            strWelcomeBackAlert             = "If you have handy the registration code sent to %@, you can now complete your registration. If not, you may go back and start over.";
            strIncompleteRegistrationTitle  = "Incomplete registration";
            strIncompleteRegistrationAlert  = "You must complete your registration before you can start using Scola.";
        }
    }
    
    
    /* ==== ScMembershipView STRINGS ==== */
    
    public String strMembershipViewTitleDefault;
    public String strMembershipViewTitleMyPlace;
    public String strMembershipViewTitleOurPlace;
    public String strHouseholdMembers;
    public String strDeleteConfirmation;
    
    private void setMembershipViewStrings(String language)
    {
        strMembershipViewTitleMyPlace  = strMyPlace;
        strMembershipViewTitleOurPlace = strOurPlace;
        
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strMembershipViewTitleDefault = "Medlemmer";
            strHouseholdMembers           = "Medlemmer i husstanden";
            strDeleteConfirmation         = "Ta bort";
        } else {
            strMembershipViewTitleDefault = "Members";
            strHouseholdMembers           = "Household members";
            strDeleteConfirmation         = "Remove";
        }
    }
    
    
    /* ==== ScMemberView STRINGS ==== */
    
    public String strMemberViewTitleAboutYou;
    public String strMemberViewTitleNewMember;
    public String strMemberViewTitleNewHouseholdMember;
    public String strGenderActionSheetTitle;
    
    private void setMemberViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strMemberViewTitleAboutYou           = "Om deg";
            strMemberViewTitleNewMember          = "Nytt medlem";
            strMemberViewTitleNewHouseholdMember = "I husstanden";
            strGenderActionSheetTitle            = "%@ %@ %@ eller %@?";           
        } else {
            strMemberViewTitleAboutYou           = "About you";
            strMemberViewTitleNewMember          = "New member";
            strMemberViewTitleNewHouseholdMember = "In the household";
            strGenderActionSheetTitle            = "%@ %@ a %@ or a %@?";
        }
    }
}
