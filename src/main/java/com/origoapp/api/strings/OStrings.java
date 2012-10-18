package com.origoapp.api.strings;


public class OStrings
{
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_NORWEGIAN_BOKMAL = "nb";
    
    
    public OStrings()
    {
        this(LANG_ENGLISH);
    }
    
    
    public OStrings(String language)
    {
        setMeta(language);
        
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
    
    
    /* ==== META ==== */
    
    public String strOrigoTypeSchoolClass;
    public String strOrigoTypePreschoolClass;
    public String strOrigoTypeSportsTeam;
    public String strOrigoTypeOther;
    
    public String xstrContactRolesSchoolClass = "schoolClassTeacher|schoolTopicTeacher|schoolSpecialEducationTeacher|schoolAssistantTeacher|schoolHeadTeacher";
    public String schoolClassTeacher;
    public String schoolTopicTeacher;
    public String schoolSpecialEducationTeacher;
    public String schoolAssistantTeacher;
    public String schoolHeadTeacher;
    
    public String xstrContactRolesPreschoolClass = "preschoolDepartmentHead|preschoolTeacher|preschoolAssistant";
    public String preschoolDepartmentHead;
    public String preschoolTeacher;
    public String preschoolAssistant;
    
    public String xstrContactRolesSportsTeam = "teamCoach|teamAssistantCoach";
    public String teamCoach;
    public String teamAssistantCoach;
    
    private void setMeta(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strOrigoTypeSchoolClass = "Skoleklasse";
            strOrigoTypePreschoolClass = "Barnehage/avdeling";
            strOrigoTypeSportsTeam = "Lag";
            strOrigoTypeOther = "Annet";
            
            schoolClassTeacher = "Kontaktlærer";
            schoolTopicTeacher = "Faglærer";
            schoolSpecialEducationTeacher = "Spesiallærer";
            schoolAssistantTeacher = "Assistentlærer";
            schoolHeadTeacher = "Rektor";
            
            preschoolDepartmentHead = "Avdelingsleder";
            preschoolTeacher = "Førskolelærer";
            preschoolAssistant = "Assistent";
            
            teamCoach = "Trener";
            teamAssistantCoach = "Assistenttrener";
        } else {
            strOrigoTypeSchoolClass = "School class";
            strOrigoTypePreschoolClass = "Preschool/daycare";
            strOrigoTypeSportsTeam = "Sports team";
            strOrigoTypeOther = "Other";
            
            schoolClassTeacher = "Teacher";
            schoolTopicTeacher = "Topic teacher";
            schoolSpecialEducationTeacher = "Special education teacher";
            schoolAssistantTeacher = "Assistant teacher";
            schoolHeadTeacher = "Head teacher";
            
            preschoolDepartmentHead = "Department head";
            preschoolTeacher = "Teacher";
            preschoolAssistant = "Assistant";
            
            teamCoach = "Coach";
            teamAssistantCoach = "Assistant coach";
        }
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
                         "Du er nå nesten klar til å begynne å bruke Origo. Men for at Origo skal være til nytte for deg og dine, må vi be deg gi noen opplysninger om husstanden din. Vi lover derfor at:\n" +
                         "\n" +
                         "- Opplysningene forblir dine og bare dine.\n" +
                         "- Vi forbeholder oss absolutt ingen rett til dem.\n" +
                         "- Vi selger dem ikke videre.\n" + 
                         "- Vi analyserer dem ikke for å finne ut hva du og dine nærmeste liker og mener.\n" +
                         "- Vi har dem bare til låns, for uten dem har Origo ingen nytteverdi.\n" +
                         "\n" +
                         "Om du godtar dette, vil du motta en epost fra oss som dokumenterer ovenstående.";
            strAccept  = "Godta";
            strDecline = "Avslå";
        } else {
            strEULA    = "– BETWEEN US –\n" +
                         "\n" +
                         "You are now almost ready to start using Origo. But for Origo to be of use to you and your family, we need you to provide some information about your household. We promise that:\n" +
                         "\n" +
                         "- The information remains yours and yours only.\n" +
                         "- We claim no rights to it whatsoever.\n" +
                         "- We do not sell it to third parties.\n" + 
                         "- We do not analyse it in order to figure out what you and your family may like.\n" +
                         "- We are only borrowing it, for without it, Origo has no use.\n" +
                         "\n" +
                         "If you accept this, you will receive an email from us that documents the above.";
            strAccept    = "Accept";
            strDecline   = "Decline";
        }
    }
    
    
    /* ==== GENERIC STRINGS ==== */
    
    public String strPleaseWait;
    public String strAboutYou;
    public String strAboutMember;
    public String strFemale;
    public String strFemaleMinor;
    public String strMale;
    public String strMaleMinor;
    public String strMyHousehold;
    public String strMyMessageBoard;
    public String strOurMessageBoard;
    
    private void setGenericStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strPleaseWait      = "Vent litt...";
            strAboutYou        = "Om deg";
            strAboutMember     = "Om %@";
            strFemale          = "Kvinne";
            strFemaleMinor     = "Jente";
            strMale            = "Mann";
            strMaleMinor       = "Gutt";
            strMyHousehold     = "Min husstand";
            strMyMessageBoard  = "Min oppslagstavle";
            strOurMessageBoard = "Vår oppslagstavle";
        } else {
            strPleaseWait      = "Please wait...";
            strAboutYou        = "About you";
            strAboutMember     = "About %@";
            strFemale          = "Woman";
            strFemaleMinor     = "Girl";
            strMale            = "Man";
            strMaleMinor       = "Boy";
            strMyHousehold     = "My place";
            strMyMessageBoard  = "My message board";
            strOurMessageBoard = "Our message board";
        }
    }
    
    
    /* ==== PROMTPS ==== */
    
    public String strAuthEmailPrompt;
    public String strPasswordPrompt;
    public String strActivationCodePrompt;
    public String strRepeatPasswordPrompt;
    public String strPhotoPrompt;
    public String strNamePrompt;
    public String strEmailPrompt;
    public String strDateOfBirthPrompt;
    public String strMobilePhonePrompt;
    public String strUserWebsitePrompt;
    public String strAddressLine1Prompt;
    public String strAddressLine2Prompt;
    public String strTelephonePrompt;
    
    private void setPrompts(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strAuthEmailPrompt         = "Epostadressen din";
            strPasswordPrompt          = "Passordet ditt";
            strActivationCodePrompt    = "Aktiveringskode fra epost";
            strRepeatPasswordPrompt    = "Gjenta passordet ditt";
            strPhotoPrompt             = "Bilde";
            strNamePrompt              = "Fullt navn";
            strEmailPrompt             = "En gyldig epostadresse";
            strDateOfBirthPrompt       = "Fødselsdato";
            strMobilePhonePrompt       = "Mobilnummer";
            strUserWebsitePrompt       = "Nettside, blogg el.tilsv.";
            strAddressLine1Prompt      = "Gateadresse";
            strAddressLine2Prompt      = "Postnummer og -sted";
            strTelephonePrompt         = "Telefonnummer";
        } else {
            strAuthEmailPrompt         = "Your email address";
            strPasswordPrompt          = "Your password";
            strActivationCodePrompt    = "Activation code from email";
            strRepeatPasswordPrompt    = "Repeat your password";
            strPhotoPrompt             = "Photo";
            strNamePrompt              = "Full name";
            strEmailPrompt             = "A valid email address";
            strDateOfBirthPrompt       = "Date of birth";
            strMobilePhonePrompt       = "Mobile phone number";
            strUserWebsitePrompt       = "Web page, blog or similar";
            strAddressLine1Prompt      = "Street address";
            strAddressLine2Prompt      = "Postal code and city/town";
            strTelephonePrompt         = "Telephone number";
        }
    }
    
    
    /* ==== LABELS ==== */
    
    public String strSignInOrRegisterLabel;
    public String strActivateLabel;
    public String strSingleLetterEmailLabel;
    public String strSingleLetterMobilePhoneLabel;
    public String strSingleLetterDateOfBirthLabel;
    public String strSingleLetterTelephoneLabel;
    public String strAddressLabel;
    public String strAddressesLabel;
    public String strTelephoneLabel;
    
    private void setLabels(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strSignInOrRegisterLabel        = "Logg på eller registrer ny bruker";
            strActivateLabel                = "Oppgi aktiveringskoden din";
            strSingleLetterEmailLabel       = "e";
            strSingleLetterMobilePhoneLabel = "m";
            strSingleLetterDateOfBirthLabel = "f";
            strSingleLetterTelephoneLabel   = "t";
            strAddressLabel                 = "Adresse";
            strAddressesLabel               = "Adresser";
            strTelephoneLabel               = "Telefon";
        } else {
            strSignInOrRegisterLabel        = "Sign in or register new user";
            strActivateLabel                = "Provide your activation code";
            strSingleLetterEmailLabel       = "e:";
            strSingleLetterMobilePhoneLabel = "m:";
            strSingleLetterDateOfBirthLabel = "b:";
            strSingleLetterTelephoneLabel   = "t:";
            strAddressLabel                 = "Address";
            strAddressesLabel               = "Addresses";
            strTelephoneLabel               = "Telephone";
        }
    }
    
    
    /* ==== HEADER & FOOTER STRINGS ==== */
    
    public String strSignInOrRegisterFooter;
    public String strActivateFooter;
    public String strHouseholdMemberListFooter;
    
    private void setHeaderAndFooterStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strSignInOrRegisterFooter    = "Når du registrerer ny bruker, vil du motta en epost med en aktiveringskode som du må oppgi for å aktivere medlemskapet ditt.";
            strActivateFooter            = "Oppgi aktiveringskoden som du har mottatt på epost, eller kom tilbake senere om ikke du har tilgang til eposten din her og nå.";
            strHouseholdMemberListFooter = "Her kan du legge til eller ta bort medlemmer i husstanden din. Nye medlemmer vil motta en Origo-invitasjon fra deg.";
        } else {
            strSignInOrRegisterFooter    = "When you register, you will receive an email with an activation code that you must provide in order to activate your membership.";
            strActivateFooter            = "Please provide the activation code that was emailed to you, or come back later if you don't have access to your email at this time.";
            strHouseholdMemberListFooter = "Here you may add or remove members of your household. New members will receive a Origo invitation from you.";
        }
    }
    
    
    /* ==== BUTTON TITLES ==== */
    
    public String strOK;
    public String strCancel;
    public String strRetry;
    public String strStartOver;
    public String strHaveCode;
    public String strInviteToHousehold;
    public String strMergeHouseholds;
    
    private void setButtonTitles(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strOK                = "OK";
            strCancel            = "Avbryt";
            strRetry             = "Prøv igjen";
            strStartOver         = "Gå tilbake";
            strHaveCode          = "Har kode";
            strInviteToHousehold = "Inviter til husstand";
            strMergeHouseholds   = "Slå sammen husstandene";
        } else {
            strOK                = "OK";
            strCancel            = "Cancel";
            strRetry             = "Retry";
            strStartOver         = "Start over";
            strHaveCode          = "Have code";
            strInviteToHousehold = "Invite to household";
            strMergeHouseholds   = "Merge households";
        }
    }
    
    
    /* ==== ALERTS & ERROR MESSAGES ==== */
    
    public String strNoInternetError;
    public String strServerErrorAlert;
    public String strActivationFailedTitle;
    public String strActivationFailedAlert;
    public String strWelcomeBackTitle;
    public String strWelcomeBackAlert;
    public String strIncompleteRegistrationTitle;
    public String strIncompleteRegistrationAlert;
    public String strMemberExistsTitle;
    public String strMemberExistsAlert;
    public String strExistingResidenceAlert;

    
    private void setAlertsAndErrorMessages(String language) 
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strNoInternetError             = "Ingen internettforbindelse.";
            strServerErrorAlert            = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            strActivationFailedTitle       = "Aktivering mislyktes";
            strActivationFailedAlert       = "Det ser ut til at du enten har mistet aktiveringskoden som vi sendte deg på epost, eller at du har glemt passordet du oppga. La oss starte på nytt.";
            strWelcomeBackTitle            = "Velkommen tilbake!";
            strWelcomeBackAlert            = "Om du har aktiveringskoden som ble sendt til %@, så kan du aktivere medlemskapet ditt nå. Om ikke, kan du gå tilbake og starte på nytt.";
            strIncompleteRegistrationTitle = "Ufullstendig registrering";
            strIncompleteRegistrationAlert = "Du må fullføre registreringen før du kan begynne å bruke Origo.";
            strMemberExistsTitle           = "Medlem eksisterer";
            strMemberExistsAlert           = "Epost-adressen '%@' er allerede registrert i '@%'. Vennligst oppgi en annen adresse (eller avbryt registreringen).";
            strExistingResidenceAlert      = "%@ er allerede medlem av en husstand. Vil du invitere %@ til også å bli med i din husstand, eller ønsker du å slå husstandene deres sammen til én?";
        } else {
            strNoInternetError             = "No internet connection.";
            strServerErrorAlert            = "An error has occurred. Please try again later. [%d: \"%@\"]";
            strActivationFailedTitle       = "Activation failed";
            strActivationFailedAlert       = "It looks like you may have lost the activation code that we sent to you by email, or forgotten the password you provided. Let's start over.";
            strWelcomeBackTitle            = "Welcome back!";
            strWelcomeBackAlert            = "If you have handy the activation code sent to %@, you can now activate your membership. If not, you may go back and start over.";
            strIncompleteRegistrationTitle = "Incomplete registration";
            strIncompleteRegistrationAlert = "You must complete your registration before you can start using Origo.";
            strMemberExistsTitle           = "Member exists";
            strMemberExistsAlert           = "The email address '%@' is already registered in '%@'. Please enter a different address (or cancel the registration).";
            strExistingResidenceAlert      = "%@ is already member of a household. Would you like to invite %@ to join your household as well, or do you want to merge your households into one?";
        }
    }
    
    
    /* ==== OMemberListView strings ==== */
    
    public String strMemberListViewTitleDefault;
    public String strMemberListViewTitleHousehold;
    public String strHouseholdMembers;
    public String strDeleteConfirmation;
    
    private void setMembershipViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strMemberListViewTitleDefault   = "Medlemmer";
            strMemberListViewTitleHousehold = "I husstanden";
            strHouseholdMembers             = "Medlemmer i husstanden";
            strDeleteConfirmation           = "Ta bort";
        } else {
            strMemberListViewTitleDefault   = "Members";
            strMemberListViewTitleHousehold = "In the household";
            strHouseholdMembers             = "Household members";
            strDeleteConfirmation           = "Remove";
        }
    }
    
    
    /* ==== OMemberView strings ==== */
    
    public String strMemberViewTitleAboutYou;
    public String strMemberViewTitleNewMember;
    public String strMemberViewTitleNewHouseholdMember;
    public String strGenderActionSheetTitleSelf;
    public String strGenderActionSheetTitleSelfMinor;
    public String strGenderActionSheetTitleMember;
    public String strGenderActionSheetTitleMemberMinor;
    
    private void setMemberViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strMemberViewTitleAboutYou           = "Om deg";
            strMemberViewTitleNewMember          = "Nytt medlem";
            strMemberViewTitleNewHouseholdMember = "I husstanden";
            strGenderActionSheetTitleSelf        = "Er du kvinne eller mann?";
            strGenderActionSheetTitleSelfMinor   = "Er du jente eller gutt?";
            strGenderActionSheetTitleMember      = "Er %@ kvinne eller mann?";
            strGenderActionSheetTitleMemberMinor = "Er %@ jente eller gutt?";
        } else {
            strMemberViewTitleAboutYou           = "About you";
            strMemberViewTitleNewMember          = "New member";
            strMemberViewTitleNewHouseholdMember = "In the household";
            strGenderActionSheetTitleSelf        = "Are you a woman or a man?";
            strGenderActionSheetTitleSelfMinor   = "Are you a girl or a boy?";
            strGenderActionSheetTitleMember      = "Is %@ a woman or a man?";
            strGenderActionSheetTitleMemberMinor = "Is %@ a girl or a boy?";
        }
    }
}
