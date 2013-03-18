package com.origoapp.api.strings;


public class OStrings
{
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_NORWEGIAN_BOKMAL = "nb";
    public static final String LANG_DANISH = "da";
    public static final String LANG_SWEDISH = "sv";
    
    
    public OStrings()
    {
        this(LANG_ENGLISH);
    }
    
    
    public OStrings(String language)
    {
        if (language.equals(LANG_DANISH) || language.equals(LANG_SWEDISH)) {
            language = LANG_NORWEGIAN_BOKMAL;
        }
        
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
    
    public String strButtonOK;
    public String strButtonEdit;
    public String strButtonNext;
    public String strButtonDone;
    public String strButtonContinue;
    public String strButtonCancel;
    public String strButtonSignOut;
    
    public String strAlertTextNoInternet;
    public String strAlertTextServerError;
    
    public String strTermAddress;
    
    private void setCrossViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strButtonOK             = "OK";
            strButtonEdit           = "Endre";
            strButtonNext           = "Neste";
            strButtonDone           = "Ferdig";
            strButtonContinue       = "Fortsett";
            strButtonCancel         = "Avbryt";
            strButtonSignOut        = "Logg ut";
            
            strAlertTextNoInternet  = "Ingen internettforbindelse.";
            strAlertTextServerError = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            
            strTermAddress          = "Adresse";
        } else {
            strButtonOK             = "OK";
            strButtonEdit           = "Edit";
            strButtonNext           = "Next";
            strButtonDone           = "Done";
            strButtonContinue       = "Continue";
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
    public String strFooterActivateEmail;
    
    public String strPlaceholderAuthEmail;
    public String strPlaceholderPassword;
    public String strPlaceholderActivationCode;
    public String strPlaceholderRepeatPassword;
    public String strPlaceholderPleaseWait;
    
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
    
    private void setAuthViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strLabelSignIn                      = "Logg på eller registrer deg";
            strLabelActivate                    = "Oppgi aktiveringskode";
            
            strFooterSignInOrRegister           = "Når du registrerer deg, vil du motta en epost med en aktiveringskode som du må oppgi i neste steg.";
            strFooterActivate                   = "Aktiveringskoden er sendt til %@. Du kan komme tilbake senere om du ikke har tilgang til eposten din her og nå.";
            strFooterActivateEmail              = "Aktiveringskoden er sendt til %@.";
            
            strPlaceholderAuthEmail             = "Epostadressen din";
            strPlaceholderPassword              = "Passordet ditt";
            strPlaceholderActivationCode        = "Aktiveringskode fra epost";
            strPlaceholderRepeatPassword        = "Gjenta passordet ditt";
            strPlaceholderPleaseWait            = "Vent litt...";
            
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
        } else {
            strLabelSignIn                      = "Sign in or register";
            strLabelActivate                  = "Enter activation code";
            
            strFooterSignInOrRegister           = "When you register, you will receive an email with an activation code that you must enter in the next step.";
            strFooterActivate                   = "The activation code has been sent to %@. You can come back later if you don't have access to your email at this time.";
            strFooterActivateEmail              = "The activation code has been sent to %@.";
            
            strPlaceholderAuthEmail             = "Your email address";
            strPlaceholderPassword              = "Your password";
            strPlaceholderActivationCode        = "Activation code from email";
            strPlaceholderRepeatPassword        = "Repeat your password";
            strPlaceholderPleaseWait            = "Please wait...";
            
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
    
    public String strTermMe;
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
            
            strTermMe                    = "Meg";
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
            
            strTermMe                    = "Me";
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
    
    public String strFooterResidence;
    public String strFooterSchoolClass;
    public String strFooterPreschoolClass;
    public String strFooterSportsTeam;
    public String strFooterOtherOrigo;
    
    public String strButtonNewHousemate;
    public String strButtonDeleteMember;
    
    private void setMemberListViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleMembers       = "Medlemmer";
            strViewTitleHousehold     = "I husstanden";
            
            strHeaderContacts         = "Kontaktpersoner";
            strHeaderHouseholdMembers = "Medlemmer i husstanden";
            strHeaderOrigoMembers     = "Medlemmer";
            
            strFooterResidence        = "Trykk [+] for å legge til medlemmer i husstanden.";
            strFooterSchoolClass      = "Trykk [+] for å legge til klassekamerater";
            strFooterPreschoolClass   = "Trykk [+] for å legge til barn i avdelingen";
            strFooterSportsTeam       = "Trykk [+] for å legge til spillere på laget";
            strFooterOtherOrigo       = "Trykk [+] for å legge til origomedlemmer";
            
            strButtonNewHousemate     = "Ny bofelle";   
            strButtonDeleteMember     = "Meld ut";
        } else {
            strViewTitleMembers       = "Members";
            strViewTitleHousehold     = "In the household";
            
            strHeaderContacts         = "Contacts";
            strHeaderHouseholdMembers = "Household members";
            strHeaderOrigoMembers     = "Members";
            
            strFooterResidence        = "Tap [+] to add members to the household.";
            strFooterSchoolClass      = "Tap [+] to add classmates";
            strFooterPreschoolClass   = "Tap [+] to add children";
            strFooterSportsTeam       = "Tap [+] to add team members";
            strFooterOtherOrigo       = "Tap [+] to add origo members";
            
            strButtonNewHousemate     = "New housemate";
            strButtonDeleteMember     = "Remove";
        }
    }
    
    
    /* ==== OOrigoView strings ==== */
    
    public String strDefaultResidenceName;
    
    public String strViewTitleNewOrigo;
    
    public String strLabelAddress;
    public String strLabelCountry;
    public String strLabelTelephone;
    
    public String strHeaderAddresses;
    
    public String strPlaceholderAddress;
    public String strPlaceholderCountry;
    public String strPlaceholderTelephone;
    
    private void setOrigoViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strDefaultResidenceName  = "Min husstand";
            
            strViewTitleNewOrigo     = "Nytt origo";
            
            strLabelAddress          = strTermAddress;
            strLabelCountry          = "Land";
            strLabelTelephone        = "Telefon";
            
            strHeaderAddresses       = "Adresser";
            
            strPlaceholderAddress    = "Gateadresse\nPostnummer og -sted";
            strPlaceholderCountry    = strLabelCountry;
            strPlaceholderTelephone  = "Telefonnummer";
        } else {
            strDefaultResidenceName  = "My place";
            
            strViewTitleNewOrigo     = "New origo";
            
            strLabelAddress          = strTermAddress;
            strLabelCountry          = "Country";
            strLabelTelephone        = "Telephone";
            
            strHeaderAddresses       = "Addresses";
            
            strPlaceholderAddress    = "Street address\nPostal code and city/town";
            strPlaceholderCountry    = strLabelCountry;
            strPlaceholderTelephone  = "Telephone number";
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
    
    public String strPlaceholderPhoto;
    public String strPlaceholderName;
    public String strPlaceholderEmail;
    public String strPlaceholderDateOfBirth;
    public String strPlaceholderMobilePhone;
    
    public String strFooterMember;
    
    public String strButtonNewAddress;
    public String strButtonInviteToHousehold;
    public String strButtonMergeHouseholds;
    
    public String strAlertTitleMemberExists;
    public String strAlertTextMemberExists;
    public String strAlertTitleUserEmailChange;
    public String strAlertTextUserEmailChange;
    public String strAlertTitleFailedEmailChange;
    public String strAlertTextFailedEmailChange;

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
            
            strPlaceholderPhoto            = "Bilde";
            strPlaceholderName             = "Fullt navn";
            strPlaceholderEmail            = "En gyldig epostadresse";
            strPlaceholderDateOfBirth      = "Fødselsdato";
            strPlaceholderMobilePhone      = "Mobilnummer";
            
            strFooterMember                = "Trykk [+] for å legge til en adresse.";
            
            strButtonNewAddress            = "Ny adresse";
            strButtonInviteToHousehold     = "Inviter til husstanden";
            strButtonMergeHouseholds       = "Slå sammen husstandene";
            
            strAlertTitleMemberExists      = "Allerede registrert";
            strAlertTextMemberExists       = "%@ (%@) er allerede registrert i \"%@\". Vennligst oppgi en annen epost-adresse, eller avbryt registreringen.";
            strAlertTitleUserEmailChange   = "Ny epost-adresse";
            strAlertTextUserEmailChange    = "Du er i ferd med å endre epost-adressen din fra %@ til %@. Du må ha tilgang til den nye adressen for å aktivere endringen. Ønsker du å fortsette?";
            strAlertTitleFailedEmailChange = "Aktivering mislyktes";
            strAlertTextFailedEmailChange  = "Aktivering av epost-adressen %@ mislyktes. Prøv igjen, eller trykk Avbryt for avbryte endringen.";
            
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
            strLabelAbbreviatedEmail       = "e";
            strLabelAbbreviatedMobilePhone = "m";
            strLabelAbbreviatedDateOfBirth = "b";
            strLabelAbbreviatedTelephone   = "t";
            
            strPlaceholderPhoto            = "Photo";
            strPlaceholderName             = "Full name";
            strPlaceholderEmail            = "A valid email address";
            strPlaceholderDateOfBirth      = "Date of birth";
            strPlaceholderMobilePhone      = "Mobile phone number";
            
            strFooterMember                = "Tap [+] to add an address.";
            
            strButtonNewAddress            = "New address";
            strButtonInviteToHousehold     = "Invite to household";
            strButtonMergeHouseholds       = "Merge households";
            
            strAlertTitleMemberExists      = "Already registered";
            strAlertTextMemberExists       = "%@ (%@) is already registered in '%@'. Please enter a different email address, or cancel the registration.";
            strAlertTitleUserEmailChange   = "New email address";
            strAlertTextUserEmailChange    = "You are about to change your email address from %@ to %@. You need access to the new address to activate this change. Do you want to continue?";
            strAlertTitleFailedEmailChange = "Activation failed";
            strAlertTextFailedEmailChange  = "The email address %@ could not be activated. Please try again, or tap Cancel to cancel the change.";
            
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
    
    public String strDefaultMessageBoardName;
    
    private void setMessageBoardViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strTabBarTitleMessages      = "Meldinger";
            
            strDefaultMessageBoardName  = "Oppslagstavle";
        } else {
            strTabBarTitleMessages      = "Messages";
            
            strDefaultMessageBoardName  = "Message board";
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
