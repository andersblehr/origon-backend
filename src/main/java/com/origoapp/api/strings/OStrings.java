package com.origoapp.api.strings;


public class OStrings
{
    public static final String LANG_DANISH = "da";
    public static final String LANG_ENGLISH = "en";
    public static final String LANG_NORWEGIAN_BOKMAL = "nb";
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
        
        setDefaultStrings(language);
        setCrossViewStrings(language);
        
        setAuthViewControllerStrings(language);
        setOrigoListViewControllerStrings(language);
        setOrigoViewControllerStrings(language);
        setMemberViewControllerStrings(language);
        
        setCalendarViewControllerStrings(language);
        setTaskViewControllerStrings(language);
        setMessageBoardViewControllerStrings(language);
        setValueListViewControllerStrings(language);
        setValuePickerViewControllerStrings(language);
        
        setOrigoTypeStrings(language);
        setMetaStrings(language);
        setLanguageStrings(language);
    }
    
    
    /* ==== Default strings ==== */
    
    public String strDefaultResidenceName;
    
    private void setDefaultStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strDefaultResidenceName           = "Min husstand";
        } else {
            strDefaultResidenceName           = "My place";
        }
    }
    
    
    /* ==== Cross-view terms & strings ==== */
    
    public String strFooterTapToEdit;
    public String strFooterOrigoSignature;
    
    public String strButtonOK;
    public String strButtonEdit;
    public String strButtonNext;
    public String strButtonDone;
    public String strButtonContinue;
    public String strButtonCancel;
    public String strButtonSignOut;
    
    public String strAlertTextNoInternet;
    public String strAlertTextServerError;
    public String strAlertTextLocating;
    
    public String strTermYes;
    public String strTermNo;
    public String strTermMan;
    public String strTermBoy;
    public String strTermWoman;
    public String strTermGirl;
    public String strTermParentContact;
    
    public String strFormatAge;
    
    public String strSeparatorAnd;
    
    private void setCrossViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strFooterTapToEdit      = "Berør teksten for å gjøre endringer.";
            strFooterOrigoSignature = "\n\nSendt fra Origo - http://origoapp.com";
            
            strButtonOK             = "OK";
            strButtonEdit           = "Rediger";
            strButtonNext           = "Neste";
            strButtonDone           = "Ferdig";
            strButtonContinue       = "Fortsett";
            strButtonCancel         = "Avbryt";
            strButtonSignOut        = "Logg ut";
            
            strAlertTextNoInternet  = "Ingen internettforbindelse.";
            strAlertTextServerError = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            strAlertTextLocating    = "Lokaliserer...";
            
            strTermYes              = "Ja";
            strTermNo               = "Nei";
            strTermMan              = "Mann";
            strTermBoy              = "Gutt";
            strTermWoman            = "Kvinne";
            strTermGirl             = "Jente";
            strTermParentContact    = "Foreldrekontakt";
            
            strFormatAge            = "%d år";
            
            strSeparatorAnd         = " og ";
        } else {
            strFooterTapToEdit      = "Tap text to edit.";
            strFooterOrigoSignature = "\n\nSent from Origo - http://origoapp.com";
            
            strButtonOK             = "OK";
            strButtonEdit           = "Edit";
            strButtonNext           = "Next";
            strButtonDone           = "Done";
            strButtonContinue       = "Continue";
            strButtonCancel         = "Cancel";
            strButtonSignOut        = "Log out";
            
            strAlertTextNoInternet  = "No internet connection.";
            strAlertTextServerError = "An error has occurred. Please try again later. [%d: \"%@\"]";
            strAlertTextLocating    = "Locating...";
            
            strTermYes              = "Yes";
            strTermNo               = "No";
            strTermMan              = "Man";
            strTermBoy              = "Boy";
            strTermWoman            = "Woman";
            strTermGirl             = "Girl";
            strTermParentContact    = "Parent contact";
            
            strFormatAge            = "%d years";
            
            strSeparatorAnd         = " and ";
        }
    }
    
    
    /* ==== OAuthViewController strings ==== */
    
    public String strLabelSignIn;
    public String strLabelActivate;
    
    public String strFooterSignInOrRegister;
    public String strFooterActivateUser;
    public String strFooterActivateEmail;
    
    public String strPlaceholderAuthEmail;
    public String strPlaceholderPassword;
    public String strPlaceholderActivationCode;
    public String strPlaceholderRepeatPassword;
    
    public String strButtonHaveCode;
    public String strButtonStartOver;
    
    public String strAlertTitleActivationFailed;
    public String strAlertTextActivationFailed;
    public String strAlertTitleWelcomeBack;
    public String strAlertTextWelcomeBack;
    
    private void setAuthViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strLabelSignIn                = "Logg på eller registrer deg";
            strLabelActivate              = "Oppgi aktiveringskode";
            
            strFooterSignInOrRegister     = "Når du registrerer deg, vil du motta en epost med en aktiveringskode som du må oppgi i neste steg.";
            strFooterActivateUser         = "Aktiveringskoden er sendt til %@. Du kan komme tilbake senere om du ikke har tilgang til eposten din her og nå.";
            strFooterActivateEmail        = "Aktiveringskoden er sendt til %@.";
            
            strPlaceholderAuthEmail       = "Epostadressen din";
            strPlaceholderPassword        = "Passordet ditt";
            strPlaceholderActivationCode  = "Aktiveringskode fra epost";
            strPlaceholderRepeatPassword  = "Gjenta passordet ditt";
            
            strButtonHaveCode             = "Har kode";
            strButtonStartOver            = "Gå tilbake";
            
            strAlertTitleActivationFailed = "Aktivering mislyktes";
            strAlertTextActivationFailed  = "Det ser ut til at du enten har mistet aktiveringskoden som vi sendte deg på epost, eller at du har glemt passordet ditt. La oss starte på nytt.";
            strAlertTitleWelcomeBack      = "Velkommen tilbake!";
            strAlertTextWelcomeBack       = "Om du har aktiveringskoden som ble sendt til %@, så kan du aktivere medlemskapet ditt nå. Om ikke, kan du gå tilbake og starte på nytt.";
        } else {
            strLabelSignIn                = "Sign in or sign up";
            strLabelActivate              = "Enter activation code";
            
            strFooterSignInOrRegister     = "When you register, you will receive an email with an activation code that you must enter in the next step.";
            strFooterActivateUser         = "The activation code has been sent to %@. You can come back later if you don't have access to your email at this time.";
            strFooterActivateEmail        = "The activation code has been sent to %@.";
            
            strPlaceholderAuthEmail       = "Your email address";
            strPlaceholderPassword        = "Your password";
            strPlaceholderActivationCode  = "Activation code from email";
            strPlaceholderRepeatPassword  = "Repeat your password";
            
            strButtonHaveCode             = "Have code";
            strButtonStartOver            = "Start over";
            
            strAlertTitleActivationFailed = "Activation failed";
            strAlertTextActivationFailed  = "It looks like you may have lost the activation code that we emailed to you, or forgotten your password. Let's start over.";
            strAlertTitleWelcomeBack      = "Welcome back!";
            strAlertTextWelcomeBack       = "If you have handy the activation code sent to %@, you can now activate your membership. If not, you may go back and start over.";
        }
    }
    
    
    /* ==== OOrigoListViewController strings ==== */
    
    public String strViewTitleOrigo;
    
    public String strHeaderWardsOrigos;
    public String strHeaderMyOrigos;
    
    public String strFooterOrigoCreationFirst;
    public String strFooterOrigoCreation;
    public String strFooterOrigoCreationWards;
    
    public String strAlertTitleListedUserRegistration;
    public String strAlertTextListedUserRegistration;
    public String strAlertTitleIncompleteRegistration;
    public String strAlertTextIncompleteRegistration;
    
    public String strSheetPromptOrigoType;
    
    public String strTextNoOrigos;
    
    public String strTermYourChild;
    public String strTermHimOrHer;
    public String strTermForName;
    
    private void setOrigoListViewControllerStrings(String language)
    {
        strViewTitleOrigo                       = "Origo";
        
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strHeaderWardsOrigos                = "Barnas origo";
            strHeaderMyOrigos                   = "Mine origo";
            
            strFooterOrigoCreationFirst         = "Trykk [+] for å opprette et origo";
            strFooterOrigoCreation              = "Trykk [+] for å opprette et nytt origo";
            strFooterOrigoCreationWards         = "for deg selv, eller velg %@ for å opprette et origo for %@";
            
            strAlertTitleListedUserRegistration = "Velkommen til Origo";
            strAlertTextListedUserRegistration  = "Kontroller at opplysningene dine er riktige, samt legg inn opplysninger som %@ ikke hadde adgang til å oppgi da %@ inviterte deg.";  
            strAlertTitleIncompleteRegistration = "Ufullstendig registrering";
            strAlertTextIncompleteRegistration  = "Du må fullføre registreringen før du kan begynne å bruke Origo.";
            
            strSheetPromptOrigoType             = "Hva slags origo ønsker du å opprette";
            
            strTextNoOrigos                     = "(Ingen origo)";
            
            strTermYourChild                    = "et av barna";
            strTermHimOrHer                     = "ham eller henne";
            strTermForName                      = "for %@";
        } else {
            strHeaderWardsOrigos                = "The kids' origos";
            strHeaderMyOrigos                   = "My origos";
            
            strFooterOrigoCreationFirst         = "Tap [+] to create an origo";
            strFooterOrigoCreation              = "Tap [+] to create a new origo";
            strFooterOrigoCreationWards         = "for yourself. Select %@ to create an origo for %@";
            
            strAlertTitleListedUserRegistration = "Welcome to Origo";
            strAlertTextListedUserRegistration  = "Please verify your details and provide the information that %@ was not authorised to enter when %@ invited you.";  
            strAlertTitleIncompleteRegistration = "Incomplete registration";
            strAlertTextIncompleteRegistration  = "You must complete your registration before you can start using Origo.";
            
            strSheetPromptOrigoType             = "What sort of origo du you want to create";
            
            strTextNoOrigos                     = "(No origos)";
            
            strTermYourChild                    = "your child";
            strTermHimOrHer                     = "him or her";
            strTermForName                      = "for %@";
        }
    }
    
    
    /* ==== OOrigoViewController strings ==== */
    
    public String strLabelAddress;
    public String strLabelPurpose;
    public String strLabelDescriptionText;
    public String strLabelTelephone;
    
    public String strPlaceholderResidenceName;
    public String strPlaceholderAddress;
    public String strPlaceholderPurpose;
    public String strPlaceholderDescriptionText;
    public String strPlaceholderTelephone;
    
    public String strButtonEditRoles;
    public String strButtonAddFromOrigo;
    public String strButtonAddParentContact;
    public String strButtonShowInMap;
    public String strButtonAbout;
    public String strButtonNewHousemate;
    public String strButtonOtherGuardian;
    public String strButtonDeleteMember;
    
    private void setOrigoViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strLabelAddress               = "Adresse";
            strLabelPurpose               = "Formål";
            strLabelDescriptionText       = "Beskrivelse";
            strLabelTelephone             = "Telefon";
            
            strPlaceholderResidenceName   = "Kallenavn for denne adressen";
            strPlaceholderAddress         = "Gateadresse\nPostnummer og -sted";
            strPlaceholderPurpose         = "Formålet med dette origoet";
            strPlaceholderDescriptionText = "En valgfri beskrivelse";
            strPlaceholderTelephone       = "Telefonnummer";
            
            strButtonEditRoles            = "Rediger roller";
            strButtonAddFromOrigo         = "Legg til fra andre origo";
            strButtonAddParentContact     = "Legg til foreldrekontakt";
            strButtonShowInMap            = "Vis på kart";
            strButtonAbout                = "Om %@";
            strButtonNewHousemate         = "Ny bofelle";   
            strButtonOtherGuardian        = "Annen foresatt";   
            strButtonDeleteMember         = "Meld ut";
        } else {
            strLabelAddress               = "Address";
            strLabelPurpose               = "Purpose";
            strLabelDescriptionText       = "Description";
            strLabelTelephone             = "Telephone";
            
            strPlaceholderResidenceName   = "Nickname for this address";
            strPlaceholderAddress         = "Street address\nPostal code and city/town";
            strPlaceholderPurpose         = "The purpose of this origo";
            strPlaceholderDescriptionText = "An optional description";
            strPlaceholderTelephone       = "Telephone number";
            
            strButtonEditRoles            = "Edit roles";
            strButtonAddFromOrigo         = "Add from other origos";
            strButtonAddParentContact     = "Add parent contact";
            strButtonShowInMap            = "Show in map";
            strButtonAbout                = "About %@";
            strButtonNewHousemate         = "New housemate";
            strButtonOtherGuardian        = "Other guardian";   
            strButtonDeleteMember         = "Remove";
        }
    }
    
    
    /* ==== OMemberViewController strings ==== */
    
    public String strViewTitleAboutMe;
    
    public String strLabelDateOfBirth;
    public String strLabelMobilePhone;
    public String strLabelEmail;
    
    public String strAlternateLabelDateOfBirth;
    
    public String strPlaceholderName;
    public String strPlaceholderPhoto;
    public String strPlaceholderDateOfBirth;
    public String strPlaceholderMobilePhone;
    public String strPlaceholderEmail;
    
    public String strFooterOrigoInviteAlert;
    public String strFooterJuvenileOrigoGuardian;
    
    public String strButtonParentToSome;
    public String strButtonAddAddress;
    public String strButtonChangePassword;
    public String strButtonEditRelations;
    public String strButtonCorrectGender;
    public String strButtonNewAddress;
    public String strButtonAllContacts;
    public String strButtonAllGuardians;
    public String strButtonRetrieveFromContacts;
    public String strButtonRetrieveFromOrigo;
    public String strButtonDifferentNumber;
    public String strButtonDifferentEmail;
    public String strButtonInviteToHousehold;
    public String strButtonMergeHouseholds;
    
    public String strAlertTitleMemberExists;
    public String strAlertTextMemberExists;
    public String strAlertTitleUserEmailChange;
    public String strAlertTextUserEmailChange;
    public String strAlertTitleFailedEmailChange;
    public String strAlertTextFailedEmailChange;

    public String strSheetPromptEmailRecipient;
    public String strSheetPromptTextRecipient;
    public String strSheetPromptCallRecipient;
    public String strSheetPromptMultiValuePhone;
    public String strSheetPromptMultiValueEmail;
    public String strSheetPromptExistingResidence;
    
    public String strQuestionArgumentGender;
    public String strQuestionArgumentGenderMinor;
    
    public String strTermHisFather;
    public String strTermHerFather;
    public String strTermHisMother;
    public String strTermHerMother;
    
    private void setMemberViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleAboutMe             = "Om meg";
            
            strLabelDateOfBirth             = "Alder";
            strLabelMobilePhone             = "Mobil";
            strLabelEmail                   = "Epost";
            
            strAlternateLabelDateOfBirth    = "Født";
            
            strPlaceholderName              = "Navn";
            strPlaceholderPhoto             = "Bilde";
            strPlaceholderDateOfBirth       = "Fødselsdato";
            strPlaceholderMobilePhone       = "Mobilnummer";
            strPlaceholderEmail             = "En gyldig epostadresse";
            
            strFooterOrigoInviteAlert       = "En invitasjon vil bli sendt om du oppgir en epost-adresse.";
            strFooterJuvenileOrigoGuardian  = "Før du kan registrere en mindreårig, må du registrere hans eller hennes foresatte.";
            
            strButtonParentToSome           = "Til noen av dem";
            strButtonAddAddress             = "Legg til en adresse";
            strButtonChangePassword         = "Endre passord";
            strButtonEditRelations          = "Rediger relasjoner";
            strButtonCorrectGender          = "Korriger kjønn";
            strButtonNewAddress             = "Ny adresse";
            strButtonAllGuardians           = "Alle foresatte";
            strButtonAllContacts            = "Alle kontaktpersoner";
            strButtonRetrieveFromContacts   = "Hent fra Kontakter";
            strButtonRetrieveFromOrigo      = "Hent fra annet origo";
            strButtonDifferentNumber        = "Et annet nummer";
            strButtonDifferentEmail         = "En annen adresse";
            strButtonInviteToHousehold      = "Inviter til husstanden";
            strButtonMergeHouseholds        = "Slå sammen husstandene";
            
            strAlertTitleMemberExists       = "Allerede registrert";
            strAlertTextMemberExists        = "%@ (%@) er allerede registrert i \"%@\". Vennligst oppgi en annen epost-adresse, eller avbryt registreringen.";
            strAlertTitleUserEmailChange    = "Ny epost-adresse";
            strAlertTextUserEmailChange     = "Du er i ferd med å endre epost-adressen din fra %@ til %@. Du må ha tilgang til den nye adressen for å aktivere endringen. Ønsker du å fortsette?";
            strAlertTitleFailedEmailChange  = "Aktivering mislyktes";
            strAlertTextFailedEmailChange   = "Aktivering av epost-adressen %@ mislyktes. Prøv igjen, eller trykk Avbryt for avbryte endringen.";
            
            strSheetPromptEmailRecipient    = "Hvem vil du sende epost til?";
            strSheetPromptTextRecipient     = "Hvem vil du sende tekstmelding til?";
            strSheetPromptCallRecipient     = "Hvem vil du ringe?";
            strSheetPromptExistingResidence = "%@ er allerede medlem av en husstand. Vil du invitere %@ til også å bli med i din husstand, eller ønsker du å slå husstandene deres sammen til én?";
            strSheetPromptMultiValuePhone   = "%@ er registrert med mer enn ett mobilnummer. Hvilket nummer vil du oppgi?";
            strSheetPromptMultiValueEmail   = "%@ er registrert med mer enn én epostadresse. Hvilken adresse vil du oppgi?";
            
            strQuestionArgumentGender       = "kvinne eller mann";
            strQuestionArgumentGenderMinor  = "jente eller gutt";
            
            strTermHisFather                = "faren hans";
            strTermHerFather                = "faren hennes";
            strTermHisMother                = "moren hans";
            strTermHerMother                = "moren hennes";
        } else {
            strViewTitleAboutMe             = "About me";
            
            strLabelDateOfBirth             = "Age";
            strLabelMobilePhone             = "Mobile";
            strLabelEmail                   = "Email";
            
            strAlternateLabelDateOfBirth    = "Born";
            
            strPlaceholderName              = "Name";
            strPlaceholderPhoto             = "Photo";
            strPlaceholderDateOfBirth       = "Date of birth";
            strPlaceholderMobilePhone       = "Mobile phone number";
            strPlaceholderEmail             = "A valid email address";
            
            strFooterOrigoInviteAlert       = "An invitation will be sent if you provide an email address.";
            strFooterJuvenileOrigoGuardian  = "Before you can register a minor, you must register his or her parents/guardians.";
            
            strButtonParentToSome           = "To some of them";
            strButtonAddAddress             = "Add an address";
            strButtonChangePassword         = "Change password";
            strButtonEditRelations          = "Edit relations";
            strButtonCorrectGender          = "Correct gender";
            strButtonNewAddress             = "New address";
            strButtonAllContacts            = "All contacts";
            strButtonAllGuardians           = "All guardians";
            strButtonRetrieveFromContacts   = "Retrieve from Contacts";
            strButtonRetrieveFromOrigo      = "Retrieve from other origo";
            strButtonDifferentNumber        = "A different number";
            strButtonDifferentEmail         = "A different address";
            strButtonInviteToHousehold      = "Invite to household";
            strButtonMergeHouseholds        = "Merge households";
            
            strAlertTitleMemberExists       = "Already registered";
            strAlertTextMemberExists        = "%@ (%@) is already registered in '%@'. Please enter a different email address, or cancel the registration.";
            strAlertTitleUserEmailChange    = "New email address";
            strAlertTextUserEmailChange     = "You are about to change your email address from %@ to %@. You need access to the new address to activate this change. Do you want to continue?";
            strAlertTitleFailedEmailChange  = "Activation failed";
            strAlertTextFailedEmailChange   = "The email address %@ could not be activated. Please try again, or tap Cancel to cancel the change.";
            
            strSheetPromptEmailRecipient    = "Who do you want to email?";
            strSheetPromptTextRecipient     = "Who do you want to text?";
            strSheetPromptCallRecipient     = "Who do you want to call?";
            strSheetPromptExistingResidence = "%@ is already member of a household. Would you like to invite %@ to join your household as well, or do you want to merge your households into one?";
            strSheetPromptMultiValuePhone   = "%@ is registered with more than one mobile phone number. Which number do you want to provide?";
            strSheetPromptMultiValueEmail   = "%@ is registered with more than one email address. Which address do you want to provide?";
            
            strQuestionArgumentGender       = "a woman or a man";
            strQuestionArgumentGenderMinor  = "a girl or a boy";
            
            strTermHisFather                = "his father";
            strTermHerFather                = "her father";
            strTermHisMother                = "his mother";
            strTermHerMother                = "her mother";
        }
    }
    
    
    /* ==== OCalendarViewController strings ==== */
    
    public String strViewTitleCalendar;
    
    private void setCalendarViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleCalendar = "Kalender";
        } else {
            strViewTitleCalendar = "Calendar";
        }
    }
    
    
    /* ==== OTaskViewController strings ==== */
    
    public String strViewTitleTasks;
    
    private void setTaskViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleTasks = "Oppgaver";
        } else {
            strViewTitleTasks = "Tasks";
        }
    }
    
    
    /* ==== OMessageBoardViewController strings ==== */
    
    public String strViewTitleMessages;
    
    public String strDefaultMessageBoardName;
    
    private void setMessageBoardViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleMessages       = "Meldinger";
            
            strDefaultMessageBoardName = "Oppslagstavle";
        } else {
            strViewTitleMessages       = "Messages";
            
            strDefaultMessageBoardName = "Message board";
        }
    }
    
    
    /* ==== OValueListViewController strings ==== */
    
    public String strViewTitleSettings;
    
    private void setValueListViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleSettings   = "Innstillinger";
        } else {
            strViewTitleSettings   = "Settings";
        }
    }
    
    
    /* ==== OValuePickerViewController strings ==== */
    
    public String strSegmentedTitleAdultsMinors;
    
    private void setValuePickerViewControllerStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strSegmentedTitleAdultsMinors = "Voksne|Barn";
        } else {
            strSegmentedTitleAdultsMinors = "Adults|Minors";
        }
    }
    
    
    /* ==== Origo type strings ==== */
    
    public String strOrigoTitleResidence;
    public String strOrigoTitleFriends;
    public String strOrigoTitleTeam;
    public String strOrigoTitleOrganisation;
    public String strOrigoTitlePreschoolClass;
    public String strOrigoTitleSchoolClass;
    public String strOrigoTitlePlaymates;
    public String strOrigoTitleMinorTeam;
    public String strOrigoTitleOther;
    
    public String strNewOrigoTitleResidence;
    public String strNewOrigoTitleFriends;
    public String strNewOrigoTitleTeam;
    public String strNewOrigoTitleOrganisation;
    public String strNewOrigoTitlePreschoolClass;
    public String strNewOrigoTitleSchoolClass;
    public String strNewOrigoTitlePlaymates;
    public String strNewOrigoTitleMinorTeam;
    public String strNewOrigoTitleOther;
    
    public String strFooterResidence;
    public String strFooterFriends;
    public String strFooterTeam;
    public String strFooterOrganisation;
    public String strFooterPreschoolClass;
    public String strFooterSchoolClass;
    public String strFooterPlaymates;
    public String strFooterTeamMinor;
    public String strFooterOther;
    
    public String strButtonAddMemberResidence;
    public String strButtonAddMemberFriends;
    public String strButtonAddMemberTeam;
    public String strButtonAddMemberOrganisation;
    public String strButtonAddMemberPreschoolClass;
    public String strButtonAddMemberSchoolClass;
    public String strButtonAddMemberPlaymates;
    public String strButtonAddMemberTeamMinor;
    public String strButtonAddMemberOther;
    
    public String strButtonAddContactPreschoolClass;
    public String strButtonAddContactSchoolClass;
    public String strButtonAddContactTeamMinor;
    
    public String strContactTitlePreschoolClass;
    public String strContactTitleSchoolClass;
    public String strContactTitleTeamMinor;
    
    public String strMemberListTitleResidence;
    public String strMemberListTitleFriends;
    public String strMemberListTitleTeam;
    public String strMemberListTitleOrganisation;
    public String strMemberListTitlePreschoolClass;
    public String strMemberListTitleSchoolClass;
    public String strMemberListTitlePlaymates;
    public String strMemberListTitleMinorTeam;
    public String strMemberListTitleOther;
    
    public String strNewMemberTitleResidence;
    public String strNewMemberTitleFriends;
    public String strNewMemberTitleTeam;
    public String strNewMemberTitleOrganisation;
    public String strNewMemberTitlePreschoolClass;
    public String strNewMemberTitleSchoolClass;
    public String strNewMemberTitlePlaymates;
    public String strNewMemberTitleMinorTeam;
    public String strNewMemberTitleOther;
    
    public String strAllMembersTitleResidence;
    public String strAllMembersTitleFriends;
    public String strAllMembersTitleTeam;
    public String strAllMembersTitleOrganisation;
    public String strAllMembersTitleSchoolClass;
    public String strAllMembersTitlePlaymates;
    public String strAllMembersTitleMinorTeam;
    public String strAllMembersTitleOther;
    
    private void setOrigoTypeStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strOrigoTitleResidence            = "Husstand";
            strOrigoTitleFriends              = "Vennegruppe";
            strOrigoTitleTeam                 = "Lag, idrettsgruppe";
            strOrigoTitleOrganisation         = "Organisasjon, forening";
            strOrigoTitlePreschoolClass       = "Barnehageavdeling";
            strOrigoTitleSchoolClass          = "Skoleklasse";
            strOrigoTitlePlaymates            = "Vennegjeng";
            strOrigoTitleMinorTeam            = "Lag/idrettsgruppe";
            strOrigoTitleOther                = "Annet formål";
            
            strNewOrigoTitleResidence         = "Ny adresse";
            strNewOrigoTitleFriends           = "Ny vennegruppe";
            strNewOrigoTitleTeam              = "Ny idrettsgruppe";
            strNewOrigoTitleOrganisation      = "Ny organisasjon";
            strNewOrigoTitlePreschoolClass    = "Ny barnehageavdeling";
            strNewOrigoTitleSchoolClass       = "Ny skoleklasse";
            strNewOrigoTitlePlaymates         = "Ny vennegjeng";
            strNewOrigoTitleMinorTeam         = "Ny idrettsgruppe";
            strNewOrigoTitleOther             = "Nytt origo";
            
            strFooterResidence                = "Trykk [+] for å legge til medlemmer i husstanden.";
            strFooterFriends                  = "Trykk [+] for å legge til venner.";
            strFooterTeam                     = "Trykk [+] for å legge til deltakere.";
            strFooterOrganisation             = "Trykk [+] for å legge til medlemmer.";
            strFooterPreschoolClass           = "Trykk [+] for å legge til barn i avdelingen.";
            strFooterSchoolClass              = "Trykk [+] for å legge til elever i klassen.";
            strFooterPlaymates                = "Trykk [+] for å legge til venner i gjengen.";
            strFooterTeamMinor                = "Trykk [+] for å legge til deltakere.";
            strFooterOther                    = "Trykk [+] for å legge til medlemmer.";

            strButtonAddMemberResidence       = "Legg til medlem";
            strButtonAddMemberFriends         = "Legg til venn/venninne";
            strButtonAddMemberTeam            = "Legg til deltaker";
            strButtonAddMemberOrganisation    = "Legg til medlem";
            strButtonAddMemberPreschoolClass  = "Legg til barn";
            strButtonAddMemberSchoolClass     = "Legg til elev";
            strButtonAddMemberPlaymates       = "Legg til venn/venninne";
            strButtonAddMemberTeamMinor       = "Legg til deltaker";
            strButtonAddMemberOther           = "Legg til medlem";
            
            strButtonAddContactPreschoolClass = "Legg til lærer/assistent";
            strButtonAddContactSchoolClass    = "Legg til lærer";
            strButtonAddContactTeamMinor      = "Legg til trener";
            
            strContactTitlePreschoolClass     = "Lærer/assistent";
            strContactTitleSchoolClass        = "Lærer";
            strContactTitleTeamMinor          = "Trener";
            
            strMemberListTitleResidence       = "I husstanden";
            strMemberListTitleFriends         = "I gruppa";
            strMemberListTitleTeam            = "Deltakere";
            strMemberListTitleOrganisation    = "Medlemmer";
            strMemberListTitlePreschoolClass  = "I avdelingen";
            strMemberListTitleSchoolClass     = "I klassen";
            strMemberListTitlePlaymates       = "I gjengen";
            strMemberListTitleMinorTeam       = "Deltakere";
            strMemberListTitleOther           = "Medlemmer";
            
            strNewMemberTitleResidence        = "I husstanden";
            strNewMemberTitleFriends          = "I gruppa";
            strNewMemberTitleTeam             = "Ny deltaker";
            strNewMemberTitleOrganisation     = "Nytt medlem";
            strNewMemberTitlePreschoolClass   = "I avdelingen";
            strNewMemberTitleSchoolClass      = "I klassen";
            strNewMemberTitlePlaymates        = "I gjengen";
            strNewMemberTitleMinorTeam        = "Ny deltaker";
            strNewMemberTitleOther            = "Nytt medlem";
            
            strAllMembersTitleResidence       = "Alle i husstanden";
            strAllMembersTitleFriends         = "Alle i gruppa";
            strAllMembersTitleTeam            = "Alle deltakere";
            strAllMembersTitleOrganisation    = "Alle medlemmer";
            strAllMembersTitleSchoolClass     = "Alle i klassen";
            strAllMembersTitlePlaymates       = "Alle i flokken";
            strAllMembersTitleMinorTeam       = "Alle deltakere";
            strAllMembersTitleOther           = "Alle medlemmer";
        } else {
            strOrigoTitleResidence            = "Household";
            strOrigoTitleFriends              = "Party of friends";
            strOrigoTitleTeam                 = "Team/sports group";
            strOrigoTitleOrganisation         = "Organisation";
            strOrigoTitlePreschoolClass       = "Preschool class";
            strOrigoTitleSchoolClass          = "School class";
            strOrigoTitlePlaymates            = "Flock of friends";
            strOrigoTitleMinorTeam            = "Team/sports group";
            strOrigoTitleOther                = "General purpose";
            
            strNewOrigoTitleResidence         = "New address";
            strNewOrigoTitleFriends           = "New party of friends";
            strNewOrigoTitleTeam              = "New sports group";
            strNewOrigoTitleOrganisation      = "New organisation";
            strNewOrigoTitlePreschoolClass    = "New preschool class";
            strNewOrigoTitleSchoolClass       = "New school class";
            strNewOrigoTitlePlaymates         = "New flock";
            strNewOrigoTitleMinorTeam         = "New sports group";
            strNewOrigoTitleOther             = "New origo";
            
            strFooterResidence                = "Tap [+] to add members to the household.";
            strFooterFriends                  = "Tap [+] to add friends.";
            strFooterTeam                     = "Tap [+] to add players.";
            strFooterOrganisation             = "Tap [+] to add members.";
            strFooterPreschoolClass           = "Tap [+] to add pupils to the class.";
            strFooterSchoolClass              = "Tap [+] to add pupils to the class.";
            strFooterPlaymates                = "Tap [+] to add friends to the flock.";
            strFooterTeamMinor                = "Tap [+] to add players.";
            strFooterOther                    = "Tap [+] to add members.";
            
            strButtonAddMemberResidence       = "Add household member";
            strButtonAddMemberFriends         = "Add friend";
            strButtonAddMemberTeam            = "Add player";
            strButtonAddMemberOrganisation    = "Add member";
            strButtonAddMemberPreschoolClass  = "Add child";
            strButtonAddMemberSchoolClass     = "Add pupil";
            strButtonAddMemberPlaymates       = "Add friend";
            strButtonAddMemberTeamMinor       = "Add player";
            strButtonAddMemberOther           = "Add member";
            
            strButtonAddContactPreschoolClass = "Add teacher";
            strButtonAddContactSchoolClass    = "Add teacher";
            strButtonAddContactTeamMinor      = "Add coach";
            
            strContactTitlePreschoolClass     = "Teacher";
            strContactTitleSchoolClass        = "Teacher";
            strContactTitleTeamMinor          = "Coach";
            
            strMemberListTitleResidence       = "In the household";
            strMemberListTitleFriends         = "In the party";
            strMemberListTitleTeam            = "Participants";
            strMemberListTitleOrganisation    = "Members";
            strMemberListTitlePreschoolClass  = "In the class";
            strMemberListTitleSchoolClass     = "In the class";
            strMemberListTitlePlaymates       = "In the flock";
            strMemberListTitleMinorTeam       = "Participants";
            strMemberListTitleOther           = "Members";
            
            strNewMemberTitleResidence        = "In the household";
            strNewMemberTitleFriends          = "In the party";
            strNewMemberTitleTeam             = "New participant";
            strNewMemberTitleOrganisation     = "New member";
            strNewMemberTitlePreschoolClass   = "New classmate";
            strNewMemberTitleSchoolClass      = "New classmate";
            strNewMemberTitlePlaymates        = "In the flock";
            strNewMemberTitleMinorTeam        = "New participant";
            strNewMemberTitleOther            = "New member";
            
            strAllMembersTitleResidence       = "The whole household";
            strAllMembersTitleFriends         = "The whole group";
            strAllMembersTitleTeam            = "The whole group";
            strAllMembersTitleOrganisation    = "All members";
            strAllMembersTitleSchoolClass     = "The whole class";
            strAllMembersTitlePlaymates       = "The whole flock";
            strAllMembersTitleMinorTeam       = "All participants";
            strAllMembersTitleOther           = "All members";
        }
    }
    
    
    /* ==== Meta strings ==== */
    
    public String metaSupportedLanguages = "nb";
    public String metaMultiLingualCountryCodes = "CA";
    public String metaCountryCodesByCountryCallingCode = "1:US;33:FR;45:DK;46:SE;47:NO";
    public String metaInternationalTemplate = "+{1|20|21#|22#|23#|24#|25#|26#|27|29#|30|31|32|33|34|35#|36|37#|8#|39|40|41|42#|43|44|45|46|47|48|49|50#|51|52|53|54|55|56|57|58|59#|60|61|62|63|64|65|66|67#|68#|69#|7|80#|81|82|84|85#|86|878|88#|90|91|92|93|94|95|96#|97#|98|99#} #@";
    public String metaPhoneNumberTemplatesByRegion =
            "US|AS|AI|AG|BS|BB|BM|VG|KY|DM|DO|GD|GU|JM|MS|MP|PR|KN|LC|VC|SX|TT|TC|VI:[[[+]1 ]^(N##) ]^N##-####;"+
            "en_CA:[[[+]1-]^N##-]^N##-####;"+
            "fr_CA:[[[+]1 ]^N## ]^N##-####;"+
            "FR:{+33 |^0}# ## ## ## ##;"+
            "DK:[+45 ]^N# ## ## ##;"+
            "NO:[+47 ]^{{4|8|9}## ## ###|N# ## ## ##}";
    
    public String metaContactRolesSchoolClass = "classTeacher;topicTeacher;specialEducationTeacher;assistantTeacher;headTeacher;parentRepresentative";
    public String strContactRoleClassTeacher;
    public String strContactRoleTopicTeacher;
    public String strContactRoleSpecialEducationTeacher;
    public String strContactRoleAssistantTeacher;
    public String strContactRoleHeadTeacher;
    public String strContactRoleParentRepresentative;
    
    public String metaContactRolesPreschoolClass = "preschoolClassTeacher;preschoolTeacher;preschoolAssistantTeacher";
    public String strContactRolePreschoolClassTeacher;
    public String strContactRolePreschoolTeacher;
    public String strContactRolePreschoolAssistantTeacher;
    
    public String metaContactRolesOrganisation = "chair;deputyChair;treasurer";
    public String strContactRoleChair;
    public String strContactRoleDeputyChair;
    public String strContactRoleTreasurer;
    
    public String metaContactRolesSportsTeam = "coach;assistantCoach";
    public String strContactRoleCoach;
    public String strContactRolessistantCoach;
    
    private void setMetaStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strContactRoleClassTeacher              = "Kontaktlærer";
            strContactRoleTopicTeacher              = "Faglærer";
            strContactRoleSpecialEducationTeacher   = "Spesiallærer";
            strContactRoleAssistantTeacher          = "Assistentlærer";
            strContactRoleHeadTeacher               = "Rektor";
            strContactRoleParentRepresentative      = "Klassekontakt";
            
            strContactRolePreschoolClassTeacher     = "Avdelingsleder";
            strContactRolePreschoolTeacher          = "Førskolelærer";
            strContactRolePreschoolAssistantTeacher = "Assistent";
            
            strContactRoleChair                     = "Formann/-kvinne";
            strContactRoleDeputyChair               = "Varamann/-kvinne";
            strContactRoleTreasurer                 = "Kasserer";
            
            strContactRoleCoach                     = "Trener";
            strContactRolessistantCoach             = "Assistenttrener";
        } else {
            strContactRoleClassTeacher              = "Teacher";
            strContactRoleTopicTeacher              = "Topic teacher";
            strContactRoleSpecialEducationTeacher   = "Special education teacher";
            strContactRoleAssistantTeacher          = "Assistant teacher";
            strContactRoleHeadTeacher               = "Head teacher";
            strContactRoleParentRepresentative      = "Parent representative";
            
            strContactRolePreschoolClassTeacher     = "Department head";
            strContactRolePreschoolTeacher          = "Teacher";
            strContactRolePreschoolAssistantTeacher = "Assistant";
            
            strContactRoleChair                     = "Chair";
            strContactRoleDeputyChair               = "Deputy chair";
            strContactRoleTreasurer                 = "Treasurer";
            
            strContactRoleCoach                     = "Coach";
            strContactRolessistantCoach             = "Assistant coach";
        }
    }
    
    
    /* ==== Language strings ==== */
    
    public String strQuestionTemplate;
    
    public String verbs    = "be";
    public String nouns    = "origo;father;mother;parent;guardian;contact;address";
    public String pronouns = "I;you;he;she";
    
    public String verbBe;
    
    public String nounOrigo;
    public String nounFather;
    public String nounMother;
    public String nounParent;
    public String nounGuardian;
    public String nounContact;
    public String nounAddress;
    
    public String pronounI;
    public String pronounYou;
    public String pronounHe;
    public String pronounShe;
    
    private void setLanguageStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strQuestionTemplate = "{verb} {subject} {argument}?";
            
            verbBe              = "er;er;er;er;er;er";
            
            nounOrigo           = "-;-;-;-;mine origo;%@ sine origo";
            nounFather          = "far;faren;-;-;faren din;faren til %@";
            nounMother          = "mor;moren;-;-;moren din;moren til %@";
            nounParent          = "-;-;foreldre;foreldrene;foreldrene dine;foreldrene til %@";
            nounGuardian        = "foresatt;-;foresatte;-;-;-";
            nounContact         = "kontaktperson;-;kontaktpersoner;-;-;-";
            nounAddress         = "adresse;-;adresser;-;-;-";
            
            pronounI            = "jeg;meg;meg";
            pronounYou          = "du;deg;deg";
            pronounHe           = "han;ham;ham";
            pronounShe          = "hun;henne;henne";
        } else {
            strQuestionTemplate = "{verb} {subject} {argument}?";
            
            verbBe              = "am;are;is;are;are;are";
            
            nounOrigo           = "-;-;-;-;my origos;%@'s origos";
            nounFather          = "father;the father;-;-;your father;%@'s father";
            nounMother          = "mother;the mother;-;-;your mother;%@'s mother";
            nounParent          = "-;-;parents;the parents;your parents;%@'s parents";
            nounGuardian        = "guardian;-;guardians;-;-;-";
            nounContact         = "contact;-;contacts;-;-;-";
            nounAddress         = "address;-;addresses;-;-;-";
            
            pronounI            = "I;me;me";
            pronounYou          = "you;you;you";
            pronounHe           = "he;him;him";
            pronounShe          = "she;her;her";
        }
    }
}
