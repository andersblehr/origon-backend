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
        
        setCrossViewTermsAndStrings(language);
        
        setAuthViewStrings(language);
        setOrigoListViewStrings(language);
        setMemberListViewStrings(language);
        setOrigoViewStrings(language);
        setMemberViewStrings(language);
        
        setCalendarViewStrings(language);
        setTaskViewStrings(language);
        setMessageBoardViewStrings(language);
        setSettingListViewStrings(language);
        setSettingViewStrings(language);
        
        setOrigoTypeStrings(language);
        setMeta(language);
        setLanguageStrings(language);
    }
    
    
    /* ==== Cross-view terms & strings ==== */
    
    public String strFooterTapToEdit;
    
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
    
    public String strArgumentFormatAofB;
    
    public String strTermYes;
    public String strTermNo;
    public String strTermMan;
    public String strTermBoy;
    public String strTermWoman;
    public String strTermGirl;
    
    public String strSeparatorAnd;
    
    private void setCrossViewTermsAndStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strFooterTapToEdit      = "Berør teksten for å gjøre endringer.";
            
            strButtonOK             = "OK";
            strButtonEdit           = "Endre";
            strButtonNext           = "Neste";
            strButtonDone           = "Ferdig";
            strButtonContinue       = "Fortsett";
            strButtonCancel         = "Avbryt";
            strButtonSignOut        = "Logg ut";
            
            strAlertTextNoInternet  = "Ingen internettforbindelse.";
            strAlertTextServerError = "Det har oppstått en feil, vennligst prøv igjen senere. [%d: \"%@\"]";
            strAlertTextLocating    = "Lokaliserer...";
            
            strArgumentFormatAofB   = "%@ til %@";
            
            strTermYes              = "Ja";
            strTermNo               = "Nei";
            strTermMan              = "Mann";
            strTermBoy              = "Gutt";
            strTermWoman            = "Kvinne";
            strTermGirl             = "Jente";
            
            strSeparatorAnd         = " og ";
        } else {
            strFooterTapToEdit      = "Tap text to edit.";
            
            strButtonOK             = "OK";
            strButtonEdit           = "Edit";
            strButtonNext           = "Next";
            strButtonDone           = "Done";
            strButtonContinue       = "Continue";
            strButtonCancel         = "Cancel";
            strButtonSignOut        = "Sign out";
            
            strAlertTextNoInternet  = "No internet connection.";
            strAlertTextServerError = "An error has occurred. Please try again later. [%d: \"%@\"]";
            strAlertTextLocating    = "Locating...";
            
            strArgumentFormatAofB   = "%@ of %@";
            
            strTermYes              = "Yes";
            strTermNo               = "No";
            strTermMan              = "Man";
            strTermBoy              = "Boy";
            strTermWoman            = "Woman";
            strTermGirl             = "Girl";
            
            strSeparatorAnd         = " and ";
        }
    }
    
    
    /* ==== OAuthView strings ==== */
    
    public String strLabelSignIn;
    public String strLabelActivate;
    
    public String strFooterSignInOrRegister;
    public String strFooterActivateUser;
    public String strFooterActivateEmail;
    
    public String strPlaceholderAuthEmail;
    public String strPlaceholderPassword;
    public String strPlaceholderActivationCode;
    public String strPlaceholderRepeatPassword;
    public String strPlaceholderPleaseWait;
    
    public String strButtonHaveCode;
    public String strButtonStartOver;
    
    public String strAlertTitleActivationFailed;
    public String strAlertTextActivationFailed;
    public String strAlertTitleWelcomeBack;
    public String strAlertTextWelcomeBack;
    
    private void setAuthViewStrings(String language)
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
            strPlaceholderPleaseWait      = "Vent litt...";
            
            strButtonHaveCode             = "Har kode";
            strButtonStartOver            = "Gå tilbake";
            
            strAlertTitleActivationFailed = "Aktivering mislyktes";
            strAlertTextActivationFailed  = "Det ser ut til at du enten har mistet aktiveringskoden som vi sendte deg på epost, eller at du har glemt passordet du oppga. La oss starte på nytt.";
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
            strPlaceholderPleaseWait      = "Please wait...";
            
            strButtonHaveCode             = "Have code";
            strButtonStartOver            = "Start over";
            
            strAlertTitleActivationFailed = "Activation failed";
            strAlertTextActivationFailed  = "It looks like you may have lost the activation code that we sent to you by email, or forgotten the password you provided. Let's start over.";
            strAlertTitleWelcomeBack      = "Welcome back!";
            strAlertTextWelcomeBack       = "If you have handy the activation code sent to %@, you can now activate your membership. If not, you may go back and start over.";
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
    
    public String strButtonCountryLocate;
    public String strButtonCountryOther;
    
    public String strAlertTitleListedUserRegistration;
    public String strAlertTextListedUserRegistration;
    public String strAlertTitleIncompleteRegistration;
    public String strAlertTextIncompleteRegistration;
    public String strAlertTitleCountryOther;
    public String strAlertTextCountryOther;
    public String strAlertTextCountrySupported;
    public String strAlertTextCountryUnsupported;
    
    public String strSheetTitleCountry;
    public String strSheetTitleOrigoType;
    
    public String strTermYourChild;
    public String strTermHimOrHer;
    public String strTermForName;
    
    private void setOrigoListViewStrings(String language)
    {
        strTabBarTitleOrigo                     = "Origo";
        strViewTitleWardOrigoList               = "Origo • %@";
        
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strHeaderWardsOrigos                = "Barnas origo";
            strHeaderMyOrigos                   = "Mine origo";
            
            strFooterOrigoCreationFirst         = "Trykk [+] for å opprette et origo";
            strFooterOrigoCreation              = "Trykk [+] for å opprette et nytt origo";
            strFooterOrigoCreationWards         = "for deg selv, eller velg %@ for å opprette et origo for %@";
            
            strButtonCountryLocate              = "Landet jeg er i nå";
            strButtonCountryOther               = "Et annet land";
            
            strAlertTitleListedUserRegistration = "Velkommen til Origo!";
            strAlertTextListedUserRegistration  = "Kontroller at opplysningene dine er riktige, samt legg inn opplysningene som %@ ikke hadde tilgang til da %@ inviterte deg.";  
            strAlertTitleIncompleteRegistration = "Ufullstendig registrering";
            strAlertTextIncompleteRegistration  = "Du må fullføre registreringen før du kan begynne å bruke Origo.";
            strAlertTitleCountryOther           = "Andre land";
            strAlertTextCountryOther            = "For å kunne velge et annet land, må du først angi det som regionformat i systeminnstillingene.\n(Innstillinger > Generelt > Internasjonalt)";
            strAlertTextCountrySupported        = "Nye origo vil bli tilpasset %@.";
            strAlertTextCountryUnsupported      = "Lokale tilpasninger er foreløpig ikke tilgjengelige for %@. Nye origo vil bli opprettet uten lokale tilpasninger.";
            
            strSheetTitleCountry                = "Det nye origoet vil om mulig bli tilpasset landet du bor i. Hva er bostedslandet ditt?";
            strSheetTitleOrigoType              = "Hva slags origo ønsker du å opprette";
            
            strTermYourChild                    = "et av barna";
            strTermHimOrHer                     = "ham eller henne";
            strTermForName                      = "for %@";
        } else {
            strHeaderWardsOrigos                = "The kids' origos";
            strHeaderMyOrigos                   = "My origos";
            
            strFooterOrigoCreationFirst         = "Tap [+] to create an origo";
            strFooterOrigoCreation              = "Tap [+] to create a new origo";
            strFooterOrigoCreationWards         = "for yourself. Select %@ to create an origo for %@";
            
            strButtonCountryLocate              = "The country I'm in";
            strButtonCountryOther               = "Another country";
            
            strAlertTitleListedUserRegistration = "Welcome to Origo!";
            strAlertTextListedUserRegistration  = "Please verify your details and provide the information that %@ did not have access to when %@ invited you.";  
            strAlertTitleIncompleteRegistration = "Incomplete registration";
            strAlertTextIncompleteRegistration  = "You must complete your registration before you can start using Origo.";
            strAlertTitleCountryOther           = "Other countries";
            strAlertTextCountryOther            = "To choose another country, you must first specify it as region format in you system settings.\n(Settings > General > International)";
            strAlertTextCountrySupported        = "New origos will be adapted for %@.";
            strAlertTextCountryUnsupported      = "Local adaptations are not yet available for %@. New origos will be created without local adaptations.";
            
            strSheetTitleCountry                = "The new origo will if possible be adapted for the country where you live. What is your country of residence?";
            strSheetTitleOrigoType              = "What sort of origo du you want to create?";
            
            strTermYourChild                    = "your child";
            strTermHimOrHer                     = "him or her";
            strTermForName                      = "for %@";
        }
    }
    
    
    /* ==== OMemberListView strings ==== */
    
    public String strViewTitleMembers;
    public String strViewTitleResidence;
    
    public String strHeaderContacts;
    public String strHeaderHouseholdMembers;
    public String strHeaderOrigoMembers;
    
    public String strFooterResidence;
    public String strFooterSchoolClass;
    public String strFooterPreschoolClass;
    public String strFooterSportsTeam;
    public String strFooterOtherOrigo;
    
    public String strButtonNewHousemate;
    public String strButtonOtherGuardian;
    public String strButtonDeleteMember;
    
    private void setMemberListViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strViewTitleMembers       = "Medlemmer";
            strViewTitleResidence     = "Hos %@";
            
            strHeaderContacts         = "Kontaktpersoner";
            strHeaderHouseholdMembers = "Medlemmer i husstanden";
            strHeaderOrigoMembers     = "Medlemmer";
            
            strFooterResidence        = "Trykk [+] for å legge til medlemmer i husstanden.";
            strFooterSchoolClass      = "Trykk [+] for å legge til klassekamerater.";
            strFooterPreschoolClass   = "Trykk [+] for å legge til barn i avdelingen.";
            strFooterSportsTeam       = "Trykk [+] for å legge til spillere på laget.";
            strFooterOtherOrigo       = "Trykk [+] for å legge til medlemmer.";
            
            strButtonNewHousemate     = "Ny bofelle";   
            strButtonOtherGuardian    = "Annen foresatt";   
            strButtonDeleteMember     = "Meld ut";
        } else {
            strViewTitleMembers       = "Members";
            strViewTitleResidence     = "%@'s place";
            
            strHeaderContacts         = "Contacts";
            strHeaderHouseholdMembers = "Household members";
            strHeaderOrigoMembers     = "Members";
            
            strFooterResidence        = "Tap [+] to add members to the household.";
            strFooterSchoolClass      = "Tap [+] to add classmates.";
            strFooterPreschoolClass   = "Tap [+] to add children.";
            strFooterSportsTeam       = "Tap [+] to add team members.";
            strFooterOtherOrigo       = "Tap [+] to add members.";
            
            strButtonNewHousemate     = "New housemate";
            strButtonOtherGuardian    = "Other guardian";   
            strButtonDeleteMember     = "Remove";
        }
    }
    
    
    /* ==== OOrigoView strings ==== */
    
    public String strDefaultResidenceName;
    
    public String strViewTitleNewOrigo;
    
    public String strLabelAddress;
    public String strLabelDescriptionText;
    public String strLabelTelephone;
    
    public String strPlaceholderAddress;
    public String strPlaceholderDescriptionText;
    public String strPlaceholderTelephone;
    
    private void setOrigoViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strDefaultResidenceName       = "Min husstand";
            
            strViewTitleNewOrigo          = "Nytt origo";
            
            strLabelAddress               = "Adresse";
            strLabelDescriptionText       = "Beskrivelse";
            strLabelTelephone             = "Telefon";
            
            strPlaceholderAddress         = "Gateadresse\nPostnummer og -sted";
            strPlaceholderDescriptionText = "En kort beskrivelse";
            strPlaceholderTelephone       = "Telefonnummer";
        } else {
            strDefaultResidenceName       = "My place";
            
            strViewTitleNewOrigo          = "New origo";
            
            strLabelAddress               = "Address";
            strLabelDescriptionText       = "Description";
            strLabelTelephone             = "Telephone";
            
            strPlaceholderAddress         = "Street address\nPostal code and city/town";
            strPlaceholderDescriptionText = "A short description";
            strPlaceholderTelephone       = "Telephone number";
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
    
    public String strHeaderAddress;
    public String strHeaderAddresses;
    
    public String strFooterTapToAddAddress;
    
    public String strButtonParentToSome;
    public String strButtonNewAddress;
    public String strButtonInviteToHousehold;
    public String strButtonMergeHouseholds;
    
    public String strAlertTitleMemberExists;
    public String strAlertTextMemberExists;
    public String strAlertTitleUserEmailChange;
    public String strAlertTextUserEmailChange;
    public String strAlertTitleFailedEmailChange;
    public String strAlertTextFailedEmailChange;

    public String strSheetTitleGender;
    public String strSheetTitleParenthood;
    public String strSheetTitleExistingResidence;
    
    public String strQuestionArgumentGender;
    public String strQuestionArgumentGenderMinor;
    
    public String strTermHisFather;
    public String strTermHerFather;
    public String strTermHisMother;
    public String strTermHerMother;
    
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
            strPlaceholderName             = "Navn";
            strPlaceholderEmail            = "En gyldig epostadresse";
            strPlaceholderDateOfBirth      = "Fødselsdato";
            strPlaceholderMobilePhone      = "Mobilnummer";
            
            strHeaderAddress               = "Adresse";
            strHeaderAddresses             = "Adresser";
            
            strFooterTapToAddAddress       = "Trykk [+] for å legge til en adresse.";
            
            strButtonParentToSome          = "Til noen av dem";
            strButtonNewAddress            = "Ny adresse";
            strButtonInviteToHousehold     = "Inviter til husstanden";
            strButtonMergeHouseholds       = "Slå sammen husstandene";
            
            strAlertTitleMemberExists      = "Allerede registrert";
            strAlertTextMemberExists       = "%@ (%@) er allerede registrert i \"%@\". Vennligst oppgi en annen epost-adresse, eller avbryt registreringen.";
            strAlertTitleUserEmailChange   = "Ny epost-adresse";
            strAlertTextUserEmailChange    = "Du er i ferd med å endre epost-adressen din fra %@ til %@. Du må ha tilgang til den nye adressen for å aktivere endringen. Ønsker du å fortsette?";
            strAlertTitleFailedEmailChange = "Aktivering mislyktes";
            strAlertTextFailedEmailChange  = "Aktivering av epost-adressen %@ mislyktes. Prøv igjen, eller trykk Avbryt for avbryte endringen.";
            
            strSheetTitleGender            = "%@ %@ %@ eller %@?";
            strSheetTitleParenthood        = "%@ %@ %@ til %@?";
            strSheetTitleExistingResidence = "%@ er allerede medlem av en husstand. Vil du invitere %@ til også å bli med i din husstand, eller ønsker du å slå husstandene deres sammen til én?";
            
            strQuestionArgumentGender      = "kvinne eller mann";
            strQuestionArgumentGenderMinor = "jente eller gutt";
            
            strTermHisFather               = "faren hans";
            strTermHerFather               = "faren hennes";
            strTermHisMother               = "moren hans";
            strTermHerMother               = "moren hennes";
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
            strPlaceholderName             = "Name";
            strPlaceholderEmail            = "A valid email address";
            strPlaceholderDateOfBirth      = "Date of birth";
            strPlaceholderMobilePhone      = "Mobile phone number";
            
            strFooterTapToAddAddress       = "Tap [+] to add an address.";
            
            strHeaderAddress               = "Address";
            strHeaderAddresses             = "Addresses";
            
            strButtonParentToSome          = "To some of them";
            strButtonNewAddress            = "New address";
            strButtonInviteToHousehold     = "Invite to household";
            strButtonMergeHouseholds       = "Merge households";
            
            strAlertTitleMemberExists      = "Already registered";
            strAlertTextMemberExists       = "%@ (%@) is already registered in '%@'. Please enter a different email address, or cancel the registration.";
            strAlertTitleUserEmailChange   = "New email address";
            strAlertTextUserEmailChange    = "You are about to change your email address from %@ to %@. You need access to the new address to activate this change. Do you want to continue?";
            strAlertTitleFailedEmailChange = "Activation failed";
            strAlertTextFailedEmailChange  = "The email address %@ could not be activated. Please try again, or tap Cancel to cancel the change.";
            
            strSheetTitleGender            = "%@ %@ a %@ or a %@?";
            strSheetTitleParenthood        = "%@ %@ %@ of %@?";
            strSheetTitleExistingResidence = "%@ is already member of a household. Would you like to invite %@ to join your household as well, or do you want to merge your households into one?";
            
            strQuestionArgumentGender      = "a woman or a man";
            strQuestionArgumentGenderMinor = "a girl or a boy";
            
            strTermHisFather               = "his father";
            strTermHerFather               = "her father";
            strTermHisMother               = "his mother";
            strTermHerMother               = "her mother";
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
    
    
    /* ==== OSettingListView strings ==== */
    
    public String strTabBarTitleSettings;
    
    public String strSettingTitleCountry;
    public String strSettingTextCountry;
    
    private void setSettingListViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strTabBarTitleSettings = "Innstillinger";
            
            strSettingTitleCountry = "Land";
            strSettingTextCountry  = "Nye origo tilpasses";
        } else {
            strTabBarTitleSettings = "Settings";
            
            strSettingTitleCountry = "Country";
            strSettingTextCountry  = "New origos are adapted for";
        }
    }
    
    
    /* ==== OSettingView strings ==== */
    
    public String strLabelCountrySettings;
    public String strLabelCountryLocation;
    
    public String strFooterCountryInfoParenthesis;
    public String strFooterCountryInfoLocate;
    
    private void setSettingViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strLabelCountrySettings         = "Angitt i lokale innstillinger";
            strLabelCountryLocation         = "Dette landet";
            
            strFooterCountryInfoParenthesis = "Lokale tilpasninger er foreløpig ikke tilgjengelige for land angitt i parentes.";
            strFooterCountryInfoLocate      = "Om du ønsker å angi landet du befinner deg i, må du tillate Origo å bruke stedstjenestene.\n(Innstillinger > Personvern > Sted)";
        } else {
            strLabelCountrySettings         = "Specified in local settings";
            strLabelCountryLocation         = "This country";
            
            strFooterCountryInfoParenthesis = "Origo is not yet adapted for countries listed in parenthesis.";
            strFooterCountryInfoLocate      = "If you wish to specify the country you're in, you must permit Origo to use location services.\n(Settings > Privacy > Location)";
        }
    }
    
    
    /* ==== Origo type strings ==== */
    
    public String strOrigoTypeResidence;
    public String strOrigoTypeOrganisation;
    public String strOrigoTypeAssociation;
    public String strOrigoTypeSchoolClass;
    public String strOrigoTypePreschoolClass;
    public String strOrigoTypeSportsTeam;
    public String strOrigoTypeOther;
    
    public String strNewOrigoOfTypeResidence;
    public String strNewOrigoOfTypeOrganisation;
    public String strNewOrigoOfTypeAssociation;
    public String strNewOrigoOfTypeSchoolClass;
    public String strNewOrigoOfTypePreschoolClass;
    public String strNewOrigoOfTypeSportsTeam;
    public String strNewOrigoOfTypeOther;
    
    private void setOrigoTypeStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strOrigoTypeResidence           = "Adresse";
            strOrigoTypeOrganisation        = "Organisasjon";
            strOrigoTypeAssociation         = "Forening";
            strOrigoTypeSchoolClass         = "Skoleklasse";
            strOrigoTypePreschoolClass      = "Barnehageavdeling";
            strOrigoTypeSportsTeam          = "Sportslag";
            strOrigoTypeOther               = "Annet";
            
            strNewOrigoOfTypeResidence      = "Ny adresse";
            strNewOrigoOfTypeOrganisation   = "Ny organisasjon";
            strNewOrigoOfTypeAssociation    = "Ny forening";
            strNewOrigoOfTypeSchoolClass    = "Ny skoleklasse";
            strNewOrigoOfTypePreschoolClass = "Ny barnehageavdeling";
            strNewOrigoOfTypeSportsTeam     = "Nytt lag";
            strNewOrigoOfTypeOther          = "Nytt origo";
        } else {
            strOrigoTypeResidence           = "Address";
            strOrigoTypeOrganisation        = "Organisation";
            strOrigoTypeAssociation         = "Association";
            strOrigoTypeSchoolClass         = "School class";
            strOrigoTypePreschoolClass      = "Preschool/daycare";
            strOrigoTypeSportsTeam          = "Sports team";
            strOrigoTypeOther               = "Other";
            
            strNewOrigoOfTypeResidence      = "New address";
            strNewOrigoOfTypeOrganisation   = "New organisation";
            strNewOrigoOfTypeAssociation    = "New association";
            strNewOrigoOfTypeSchoolClass    = "New school class";
            strNewOrigoOfTypePreschoolClass = "New preschool/daycare";
            strNewOrigoOfTypeSportsTeam     = "New team";
            strNewOrigoOfTypeOther          = "New origo";
        }
    }
    
    
    /* ==== Meta strings ==== */
    
    public String metaSupportedCountryCodes = "NO";
    
    public String metaContactRolesSchoolClass = "classTeacher|topicTeacher|specialEducationTeacher|assistantTeacher|headTeacher";
    public String strContactRoleClassTeacher;
    public String strContactRoleTopicTeacher;
    public String strContactRoleSpecialEducationTeacher;
    public String strContactRoleAssistantTeacher;
    public String strContactRoleHeadTeacher;
    
    public String metaContactRolesPreschoolClass = "preschoolClassTeacher|preschoolTeacher|preschoolAssistantTeacher";
    public String strContactRolePreschoolClassTeacher;
    public String strContactRolePreschoolTeacher;
    public String strContactRolePreschoolAssistantTeacher;
    
    public String metaContactRolesAssociation = "chair|deputyChair|treasurer";
    public String strContactRoleChair;
    public String strContactRoleDeputyChair;
    public String strContactRoleTreasurer;
    
    public String metaContactRolesSportsTeam = "coach|assistantCoach";
    public String strContactRoleCoach;
    public String strContactRolessistantCoach;
    
    private void setMeta(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strContactRoleClassTeacher              = "Kontaktlærer";
            strContactRoleTopicTeacher              = "Faglærer";
            strContactRoleSpecialEducationTeacher   = "Spesiallærer";
            strContactRoleAssistantTeacher          = "Assistentlærer";
            strContactRoleHeadTeacher               = "Rektor";
            
            strContactRolePreschoolClassTeacher     = "Avdelingsleder";
            strContactRolePreschoolTeacher          = "Førskolelærer";
            strContactRolePreschoolAssistantTeacher = "Assistent";
            
            strContactRoleChair                     = "Formann";
            strContactRoleDeputyChair               = "Varamann";
            strContactRoleTreasurer                 = "Kasserer";
            
            strContactRoleCoach                     = "Trener";
            strContactRolessistantCoach             = "Assistenttrener";
        } else {
            strContactRoleClassTeacher              = "Teacher";
            strContactRoleTopicTeacher              = "Topic teacher";
            strContactRoleSpecialEducationTeacher   = "Special education teacher";
            strContactRoleAssistantTeacher          = "Assistant teacher";
            strContactRoleHeadTeacher               = "Head teacher";
            
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
    public String nouns    = "father|mother|parents";
    public String pronouns = "I|you|he|she";
    
    public String verbBe;
    
    public String nounFather;
    public String nounMother;
    public String nounParents;
    
    public String pronounI;
    public String pronounYou;
    public String pronounHe;
    public String pronounShe;
    
    private void setLanguageStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strQuestionTemplate    = "{verb} {subject} {argument}?";
            
            verbBe                 = "er|er|er|er|er|er";
            
            nounFather             = "faren|faren din|faren til %@";
            nounMother             = "moren|moren din|moren til %@";
            nounParents            = "foreldrene|foreldrene dine|foreldrene til %@";
            
            pronounI               = "jeg|meg|meg|meg";
            pronounYou             = "du|deg|deg|deg";
            pronounHe              = "han|ham|ham|ham";
            pronounShe             = "hun|henne|henne|henne";
        } else {
            strQuestionTemplate    = "{verb} {subject} {argument}?";
            
            verbBe                 = "am|are|is|are|are|are";
            
            nounFather             = "the father|your father|%@'s father";
            nounMother             = "the mother|your mother|%@'s mother";
            nounParents            = "the parents|your parents|%@'s parents";
            
            pronounI               = "I|me|me|me";
            pronounYou             = "you|you|you|you";
            pronounHe              = "he|him|him|him";
            pronounShe             = "she|her|her|her";
        }
    }
}
