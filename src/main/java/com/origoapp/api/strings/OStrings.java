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
        setCrossViewStrings(language);
        
        setAuthViewStrings(language);
        setOrigoListViewStrings(language);
        setMemberListViewStrings(language);
        setOrigoViewStrings(language);
        setMemberViewStrings(language);
        
        setCalendarViewStrings(language);
        setTaskViewStrings(language);
        setMessageBoardViewStrings(language);
        setSettingsViewStrings(language);
        
        setMeta(language);
    }
    
    
    /* ==== Cross-view strings ==== */
    
    public String strNameMyHousehold;
    public String strNameOurHousehold;
    public String strNameMyMessageBoard;
    public String strNameOurMessageBoard;
    
    public String strButtonOK;
    public String strButtonEdit;
    public String strButtonDone;
    public String strButtonCancel;
    public String strButtonSignOut;
    
    public String strAlertTextNoInternet;
    public String strAlertTextServerError;
    
    public String strTermAddress;
    
    private void setCrossViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strNameMyHousehold      = "Min husstand";
            strNameOurHousehold     = "Min husstand";
            strNameMyMessageBoard   = "Min oppslagstavle";
            strNameOurMessageBoard  = "Vår oppslagstavle";
            
            strButtonOK             = "OK";
            strButtonEdit           = "Endre";
            strButtonDone           = "Ferdig";
            strButtonCancel         = "Avbryt";
            strButtonSignOut        = "Logg ut";
            
            strAlertTextNoInternet  = "Ingen internettforbindelse.";
            strAlertTextServerError = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            
            strTermAddress          = "Adresse";
        } else {
            strNameMyHousehold      = "My place";
            strNameOurHousehold     = "Our place";
            strNameMyMessageBoard   = "My message board";
            strNameOurMessageBoard  = "Our message board";
            
            strButtonOK             = "OK";
            strButtonEdit           = "Edit";
            strButtonDone           = "Done";
            strButtonCancel         = "Cancel";
            strButtonSignOut        = "Sign out";
            
            strAlertTextNoInternet  = "No internet connection.";
            strAlertTextServerError = "An error has occurred. Please try again later. [%d: \"%@\"]";
            
            strTermAddress          = "Address";
        }
    }
    
    
    /* ==== OAuthView strings ==== */
    
    public String strLabelSignIn;
    public String strLabelActivate;
    
    public String strFooterSignInOrRegister;
    public String strFooterActivate;
    
    public String strPromptAuthEmail;
    public String strPromptPassword;
    public String strPromptActivationCode;
    public String strPromptRepeatPassword;
    public String strPromptPleaseWait;
    
    public String strButtonHaveCode;
    public String strButtonStartOver;
    public String strButtonAccept;
    public String strButtonDecline;
    
    public String strAlertTitleActivationFailed;
    public String strAlertTextActivationFailed;
    public String strAlertTitleWelcomeBack;
    public String strAlertTextWelcomeBack;
    public String strAlertTitleIncompleteRegistration;
    public String strAlertTextIncompleteRegistration;
    
    public String strSheetTitleEULA;
    
    private void setAuthViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strLabelSignIn                      = "Logg på eller registrer ny bruker";
            strLabelActivate                    = "Oppgi aktiveringskoden din";
            
            strFooterSignInOrRegister           = "Om du registrerer ny bruker, vil du motta en aktiveringskode som du oppgir i neste steg.";
            strFooterActivate                   = "Oppgi aktiveringskoden som du har mottatt på epost, eller kom tilbake senere om ikke du har tilgang til eposten din her og nå.";
            
            strPromptAuthEmail                  = "Epostadressen din";
            strPromptPassword                   = "Passordet ditt";
            strPromptActivationCode             = "Aktiveringskode fra epost";
            strPromptRepeatPassword             = "Gjenta passordet ditt";
            strPromptPleaseWait                 = "Vent litt...";
            
            strButtonHaveCode                   = "Har kode";
            strButtonStartOver                  = "Gå tilbake";
            strButtonAccept                     = "Godta";
            strButtonDecline                    = "Avslå";
            
            strAlertTitleActivationFailed       = "Aktivering mislyktes";
            strAlertTextActivationFailed        = "Det ser ut til at du enten har mistet aktiveringskoden som vi sendte deg på epost, eller at du har glemt passordet du oppga. La oss starte på nytt.";
            strAlertTitleWelcomeBack            = "Velkommen tilbake!";
            strAlertTextWelcomeBack             = "Om du har aktiveringskoden som ble sendt til %@, så kan du aktivere medlemskapet ditt nå. Om ikke, kan du gå tilbake og starte på nytt.";
            strAlertTitleIncompleteRegistration = "Ufullstendig registrering";
            strAlertTextIncompleteRegistration  = "Du må fullføre registreringen før du kan begynne å bruke Origo.";
            
            strSheetTitleEULA                   = "– MELLOM OSS –\n" +
                                                  "\n" +
                                                  "Du er nå nesten klar til å begynne å bruke Origo. Men for at Origo skal være til nytte for deg og dine, må vi be om noen opplysninger om husstanden din. Vi lover at:\n" +
                                                  "\n" +
                                                  "- Opplysningene forblir dine og bare dine.\n" +
                                                  "- Vi forbeholder oss absolutt ingen rett til dem.\n" +
                                                  "- Vi selger dem ikke videre.\n" + 
                                                  "- Vi analyserer dem ikke for å finne ut hva du og dine nærmeste liker og mener.\n" +
                                                  "- Vi har dem bare til låns, for uten dem har Origo ingen nytteverdi.\n" +
                                                  "\n" +
                                                  "Om du godtar dette, vil du motta en epost fra oss som dokumenterer ovenstående.";
        } else {
            strLabelSignIn                      = "Sign in or register new user";
            strLabelActivate                    = "Enter your activation code";
            
            strFooterSignInOrRegister           = "If you are registering a new user, you will receive an activation code that you must enter in the next step.";
            strFooterActivate                   = "Please provide the activation code that was emailed to you, or come back later if you don't have access to your email at this time.";
            
            strPromptAuthEmail                  = "Your email address";
            strPromptPassword                   = "Your password";
            strPromptActivationCode             = "Activation code from email";
            strPromptRepeatPassword             = "Repeat your password";
            strPromptPleaseWait                 = "Please wait...";
            
            strButtonHaveCode                   = "Have code";
            strButtonStartOver                  = "Start over";
            strButtonAccept                     = "Accept";
            strButtonDecline                    = "Decline";
            
            strAlertTitleActivationFailed       = "Activation failed";
            strAlertTextActivationFailed        = "It looks like you may have lost the activation code that we sent to you by email, or forgotten the password you provided. Let's start over.";
            strAlertTitleWelcomeBack            = "Welcome back!";
            strAlertTextWelcomeBack             = "If you have handy the activation code sent to %@, you can now activate your membership. If not, you may go back and start over.";
            strAlertTitleIncompleteRegistration = "Incomplete registration";
            strAlertTextIncompleteRegistration  = "You must complete your registration before you can start using Origo.";
            
            strSheetTitleEULA                   = "– BETWEEN US –\n" +
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
        }
    }
    
    
    /* ==== OOrigoListView strings ==== */
    
    public String strTabBarTitleOrigo;
    public String strViewTitleWardOrigoList;
    
    public String strHeaderWardsOrigos;
    public String strHeaderMyOrigos;
    
    public String strFooterOrigoCreationFirst;
    public String strFooterOrigoCreation;
    public String strFooterOrigoCreationWards;
    
    public String strSheetTitleOrigoType;
    
    public String strTermYourChild;
    public String strTermHim;
    public String strTermHer;
    public String strTermHimOrHer;
    public String strTermForName;
    
    private void setOrigoListViewStrings(String language)
    {
        strTabBarTitleOrigo              = "Origo";
        strViewTitleWardOrigoList        = "Origo • %@";
        
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strHeaderWardsOrigos         = "Barnas origo";
            strHeaderMyOrigos            = "Mine origo";
            
            strFooterOrigoCreationFirst  = "Trykk [+] for å opprette et origo";
            strFooterOrigoCreation       = "Trykk [+] for å opprette et nytt origo";
            strFooterOrigoCreationWards  = "for deg selv. Velg %@ for å opprette et origo for %@.";
            
            strSheetTitleOrigoType       = "Hva slags origo ønsker du å opprette";
            
            strTermYourChild             = "et av barna";
            strTermHim                   = "ham";
            strTermHer                   = "henne";
            strTermHimOrHer              = "ham eller henne";
            strTermForName               = "for %@";
        } else {
            strHeaderWardsOrigos         = "The kids' origos";
            strHeaderMyOrigos            = "My origos";
            
            strFooterOrigoCreationFirst  = "Tap [+] to create an origo";
            strFooterOrigoCreation       = "Tap [+] to create a new origo";
            strFooterOrigoCreationWards  = "for yourself. Select %@ to create an origo for %@.";
            
            strSheetTitleOrigoType       = "What sort of origo du you want to create";
            
            strTermYourChild             = "your child";
            strTermHim                   = "him";
            strTermHer                   = "her";
            strTermHimOrHer              = "him or her";
            strTermForName               = "for %@";
        }
    }
    
    
    /* ==== OMemberListView strings ==== */
    
    public String strViewTitleMembers;
    public String strViewTitleHousehold;
    
    public String strHeaderContacts;
    public String strHeaderHouseholdMembers;
    public String strHeaderOrigoMembers;
    
    public String strFooterHousehold;
    
    public String strButtonDeleteMember;
    
    private void setMemberListViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleMembers       = "Medlemmer";
            strViewTitleHousehold     = "I husstanden";
            
            strHeaderContacts         = "Kontaktpersoner";
            strHeaderHouseholdMembers = "Medlemmer i husstanden";
            strHeaderOrigoMembers     = "Medlemmer";
            
            strFooterHousehold        = "Trykk [+] for å legge til medlemmer i husstanden.";
            
            strButtonDeleteMember     = "Meld ut";
        } else {
            strViewTitleMembers       = "Members";
            strViewTitleHousehold     = "In the household";
            
            strHeaderContacts         = "Contacts";
            strHeaderHouseholdMembers = "Household members";
            strHeaderOrigoMembers     = "Members";
            
            strFooterHousehold        = "Tap [+] to add members to the household.";
            
            strButtonDeleteMember     = "Remove";
        }
    }
    
    
    /* ==== OOrigoView strings ==== */
    
    public String strViewTitleNewOrigo;
    
    public String strLabelTelephone;
    
    public String strHeaderAddresses;
    
    public String strPromptAddressLine1;
    public String strPromptAddressLine2;
    public String strPromptTelephone;
    
    private void setOrigoViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleNewOrigo   = "Nytt origo";
            
            strLabelTelephone      = "Telefon";
            
            strHeaderAddresses     = "Adresser";
            
            strPromptAddressLine1  = "Gateadresse";
            strPromptAddressLine2  = "Postnummer og -sted";
            strPromptTelephone     = "Telefonnummer";
        } else {
            strViewTitleNewOrigo   = "New origo";
            
            strLabelTelephone      = "Telephone";
            
            strHeaderAddresses     = "Addresses";
            
            strPromptAddressLine1  = "Street address";
            strPromptAddressLine2  = "Postal code and city/town";
            strPromptTelephone     = "Telephone number";
        }
    }
    
    
    /* ==== OMemberView strings ==== */
    
    public String strViewTitleAboutMe;
    public String strViewTitleNewMember;
    public String strViewTitleNewHouseholdMember;
    
    public String strLabelEmail;
    public String strLabelMobilePhone;
    public String strLabelDateOfBirth;
    public String strLabelAbbreviatedEmail;
    public String strLabelAbbreviatedMobilePhone;
    public String strLabelAbbreviatedDateOfBirth;
    public String strLabelAbbreviatedTelephone;
    
    public String strPromptPhoto;
    public String strPromptName;
    public String strPromptEmail;
    public String strPromptDateOfBirth;
    public String strPromptMobilePhone;
    
    public String strButtonInviteToHousehold;
    public String strButtonMergeHouseholds;
    
    public String strAlertTitleMemberExists;
    public String strAlertTextMemberExists;

    public String strSheetTitleGenderSelf;
    public String strSheetTitleGenderSelfMinor;
    public String strSheetTitleGenderMember;
    public String strSheetTitleGenderMinor;
    public String strSheetTitleExistingResidence;
    
    public String strTermFemale;
    public String strTermFemaleMinor;
    public String strTermMale;
    public String strTermMaleMinor;
    
    private void setMemberViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleAboutMe            = "Om meg";
            strViewTitleNewMember          = "Nytt medlem";
            strViewTitleNewHouseholdMember = "I husstanden";
            
            strLabelEmail                  = "Epost";
            strLabelMobilePhone            = "Mobil";
            strLabelDateOfBirth            = "Født";
            strLabelAbbreviatedEmail       = "e";
            strLabelAbbreviatedMobilePhone = "m";
            strLabelAbbreviatedDateOfBirth = "f";
            strLabelAbbreviatedTelephone   = "t";
            
            strPromptPhoto                 = "Bilde";
            strPromptName                  = "Fullt navn";
            strPromptEmail                 = "En gyldig epostadresse";
            strPromptDateOfBirth           = "Fødselsdato";
            strPromptMobilePhone           = "Mobilnummer";
            
            strButtonInviteToHousehold     = "Inviter til husstanden";
            strButtonMergeHouseholds       = "Slå sammen husstandene";
            
            strAlertTitleMemberExists      = "Medlem eksisterer";
            strAlertTextMemberExists       = "Epost-adressen '%@' er allerede registrert i '@%'. Vennligst oppgi en annen adresse (eller avbryt registreringen).";
            
            strSheetTitleGenderSelf        = "Er du kvinne eller mann?";
            strSheetTitleGenderSelfMinor   = "Er du jente eller gutt?";
            strSheetTitleGenderMember      = "Er %@ kvinne eller mann?";
            strSheetTitleGenderMinor       = "Er %@ jente eller gutt?";
            strSheetTitleExistingResidence = "%@ er allerede medlem av en husstand. Vil du invitere %@ til også å bli med i din husstand, eller ønsker du å slå husstandene deres sammen til én?";
            
            strTermFemale                  = "Kvinne";
            strTermFemaleMinor             = "Jente";
            strTermMale                    = "Mann";
            strTermMaleMinor               = "Gutt";
        } else {
            strViewTitleAboutMe            = "About me";
            strViewTitleNewMember          = "New member";
            strViewTitleNewHouseholdMember = "In the household";
            
            strLabelEmail                  = "Email";
            strLabelMobilePhone            = "Mobile";
            strLabelDateOfBirth            = "Born";
            strLabelAbbreviatedEmail       = "e:";
            strLabelAbbreviatedMobilePhone = "m:";
            strLabelAbbreviatedDateOfBirth = "b:";
            strLabelAbbreviatedTelephone   = "t:";
            
            strPromptPhoto                 = "Photo";
            strPromptName                  = "Full name";
            strPromptEmail                 = "A valid email address";
            strPromptDateOfBirth           = "Date of birth";
            strPromptMobilePhone           = "Mobile phone number";
            
            strButtonInviteToHousehold     = "Invite to household";
            strButtonMergeHouseholds       = "Merge households";
            
            strAlertTitleMemberExists      = "Member exists";
            strAlertTextMemberExists       = "The email address '%@' is already registered in '%@'. Please enter a different address (or cancel the registration).";
            
            strSheetTitleGenderSelf        = "Are you a woman or a man?";
            strSheetTitleGenderSelfMinor   = "Are you a girl or a boy?";
            strSheetTitleGenderMember      = "Is %@ a woman or a man?";
            strSheetTitleGenderMinor       = "Is %@ a girl or a boy?";
            strSheetTitleExistingResidence = "%@ is already member of a household. Would you like to invite %@ to join your household as well, or do you want to merge your households into one?";
            
            strTermFemale                  = "Woman";
            strTermFemaleMinor             = "Girl";
            strTermMale                    = "Man";
            strTermMaleMinor               = "Boy";
        }
    }
    
    
    /* ==== OCalendarView strings ==== */
    
    public String strTabBarTitleCalendar;
    
    private void setCalendarViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strTabBarTitleCalendar = "Kalender";
        } else {
            strTabBarTitleCalendar = "Calendar";
        }
    }
    
    
    /* ==== OTaskView strings ==== */
    
    public String strTabBarTitleTasks;
    
    private void setTaskViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strTabBarTitleTasks = "Oppgaver";
        } else {
            strTabBarTitleTasks = "Tasks";
        }
    }
    
    
    /* ==== OMessageBoardView strings ==== */
    
    public String strTabBarTitleMessages;
    
    private void setMessageBoardViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strTabBarTitleMessages = "Meldinger";
        } else {
            strTabBarTitleMessages = "Messages";
        }
    }
    
    
    /* ==== OSettingsView strings ==== */
    
    public String strTabBarTitleSettings;
    
    private void setSettingsViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strTabBarTitleSettings = "Innstillinger";
        } else {
            strTabBarTitleSettings = "Settings";
        }
    }
    
    
    /* ==== Meta strings ==== */
    
    public String origoTypeMemberRoot;
    public String origoTypeResidence;
    public String origoTypeSchoolClass;
    public String origoTypePreschoolClass;
    public String origoTypeSportsTeam;
    public String origoTypeDefault;
    
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
            origoTypeMemberRoot = "Medlemsrot";
            origoTypeResidence = "Bolig";
            origoTypeSchoolClass = "Skoleklasse";
            origoTypePreschoolClass = "Barnehage/avdeling";
            origoTypeSportsTeam = "Sportslag";
            origoTypeDefault = "Annet";
            
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
            origoTypeMemberRoot = "Member root";
            origoTypeResidence = "Residence";
            origoTypeSchoolClass = "School class";
            origoTypePreschoolClass = "Preschool/daycare";
            origoTypeSportsTeam = "Sports team";
            origoTypeDefault = "Other";
            
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
}
