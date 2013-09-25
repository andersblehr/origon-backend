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
    
    public String strFormatAge;
    
    public String strSeparatorAnd;
    
    private void setCrossViewTermsAndStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strFooterTapToEdit      = "Berør teksten for å gjøre endringer.";
            strFooterOrigoSignature = "\n\nSendt fra Origo - http://origoapp.com";
            
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
            
            strTermYes              = "Ja";
            strTermNo               = "Nei";
            strTermMan              = "Mann";
            strTermBoy              = "Gutt";
            strTermWoman            = "Kvinne";
            strTermGirl             = "Jente";
            
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
            strButtonSignOut        = "Sign out";
            
            strAlertTextNoInternet  = "No internet connection.";
            strAlertTextServerError = "An error has occurred. Please try again later. [%d: \"%@\"]";
            strAlertTextLocating    = "Locating...";
            
            strTermYes              = "Yes";
            strTermNo               = "No";
            strTermMan              = "Man";
            strTermBoy              = "Boy";
            strTermWoman            = "Woman";
            strTermGirl             = "Girl";
            
            strFormatAge            = "%d years";
            
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
        
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strHeaderWardsOrigos                = "Barnas origo";
            strHeaderMyOrigos                   = "Mine origo";
            
            strFooterOrigoCreationFirst         = "Trykk [+] for å opprette et origo";
            strFooterOrigoCreation              = "Trykk [+] for å opprette et nytt origo";
            strFooterOrigoCreationWards         = "for deg selv, eller velg %@ for å opprette et origo for %@";
            
            strButtonCountryLocate              = "Landet jeg er i nå";
            strButtonCountryOther               = "Et annet land";
            
            strAlertTitleListedUserRegistration = "Velkommen til Origo";
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
            
            strAlertTitleListedUserRegistration = "Welcome to Origo";
            strAlertTextListedUserRegistration  = "Please verify your details and provide the information that %@ did not have access to when %@ invited you.";  
            strAlertTitleIncompleteRegistration = "Incomplete registration";
            strAlertTextIncompleteRegistration  = "You must complete your registration before you can start using Origo.";
            strAlertTitleCountryOther           = "Other countries";
            strAlertTextCountryOther            = "To choose another country, you must first specify it as region format in you system settings.\n(Settings > General > International)";
            strAlertTextCountrySupported        = "New origos will be adapted for %@.";
            strAlertTextCountryUnsupported      = "Local adaptations are not yet available for %@. New origos will be created without local adaptations.";
            
            strSheetTitleCountry                = "The new origo will if possible be adapted for the country where you live. What is your country of residence?";
            strSheetTitleOrigoType              = "What sort of origo du you want to create";
            
            strTermYourChild                    = "your child";
            strTermHimOrHer                     = "him or her";
            strTermForName                      = "for %@";
        }
    }
    
    
    /* ==== OMemberListView strings ==== */
    
    public String strFooterResidence;
    public String strFooterFriends;
    public String strFooterTeam;
    public String strFooterOrganisation;
    public String strFooterPreschoolClass;
    public String strFooterSchoolClass;
    public String strFooterPlaymates;
    public String strFooterTeamMinor;
    public String strFooterOther;
    
    public String strButtonNewHousemate;
    public String strButtonOtherGuardian;
    public String strButtonDeleteMember;
    
    private void setMemberListViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strFooterResidence      = "Trykk [+] for å legge til medlemmer i husstanden.";
            strFooterFriends        = "Trykk [+] for å legge til venner.";
            strFooterTeam           = "Trykk [+] for å legge til spillere på laget.";
            strFooterOrganisation   = "Trykk [+] for å legge til medlemmer.";
            strFooterPreschoolClass = "Trykk [+] for å legge til barn i avdelingen.";
            strFooterSchoolClass    = "Trykk [+] for å legge til elever i klassen.";
            strFooterPlaymates      = "Trykk [+] for å legge til venner i gjengen.";
            strFooterTeamMinor      = "Trykk [+] for å legge til spillere på laget.";
            strFooterOther          = "Trykk [+] for å legge til medlemmer.";
            
            strButtonNewHousemate   = "Ny bofelle";   
            strButtonOtherGuardian  = "Annen foresatt";   
            strButtonDeleteMember   = "Meld ut";
        } else {
            strFooterResidence      = "Tap [+] to add members to the household.";
            strFooterFriends        = "Tap [+] to add friends.";
            strFooterTeam           = "Tap [+] to add players.";
            strFooterOrganisation   = "Tap [+] to add members.";
            strFooterPreschoolClass = "Tap [+] to add pupils to the class.";
            strFooterSchoolClass    = "Tap [+] to add pupils to the class.";
            strFooterPlaymates      = "Tap [+] to add friends to the flock.";
            strFooterTeamMinor      = "Tap [+] to add players.";
            strFooterOther          = "Tap [+] to add members.";
            
            strButtonNewHousemate   = "New housemate";
            strButtonOtherGuardian  = "Other guardian";   
            strButtonDeleteMember   = "Remove";
        }
    }
    
    
    /* ==== OOrigoView strings ==== */
    
    public String strDefaultResidenceName;
    
    public String strViewTitleNewOrigo;
    
    public String strLabelAddress;
    public String strLabelPurpose;
    public String strLabelDescriptionText;
    public String strLabelTelephone;
    
    public String strPlaceholderAddress;
    public String strPlaceholderPurpose;
    public String strPlaceholderDescriptionText;
    public String strPlaceholderTelephone;
    
    private void setOrigoViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strDefaultResidenceName       = "Min husstand";
            
            strViewTitleNewOrigo          = "Nytt origo";
            
            strLabelAddress               = "Adresse";
            strLabelPurpose               = "Formål";
            strLabelDescriptionText       = "Beskrivelse";
            strLabelTelephone             = "Telefon";
            
            strPlaceholderAddress         = "Gateadresse\nPostnummer og -sted";
            strPlaceholderPurpose         = "Hva er formålet med dette origoet?";
            strPlaceholderDescriptionText = "En valgfri beskrivelse";
            strPlaceholderTelephone       = "Telefonnummer";
        } else {
            strDefaultResidenceName       = "My place";
            
            strViewTitleNewOrigo          = "New origo";
            
            strLabelAddress               = "Address";
            strLabelPurpose               = "Purpose";
            strLabelDescriptionText       = "Description";
            strLabelTelephone             = "Telephone";
            
            strPlaceholderAddress         = "Street address\nPostal code and city/town";
            strPlaceholderPurpose         = "What is the purpose of this origo?";
            strPlaceholderDescriptionText = "An optional description";
            strPlaceholderTelephone       = "Telephone number";
        }
    }
    
    
    /* ==== OMemberView strings ==== */
    
    public String strViewTitleAboutMe;
    
    public String strLabelAge;
    public String strLabelDateOfBirth;
    public String strLabelMobilePhone;
    public String strLabelEmail;
    
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
    public String strButtonInviteToHousehold;
    public String strButtonMergeHouseholds;
    
    public String strAlertTitleMemberExists;
    public String strAlertTextMemberExists;
    public String strAlertTitleUserEmailChange;
    public String strAlertTextUserEmailChange;
    public String strAlertTitleFailedEmailChange;
    public String strAlertTextFailedEmailChange;

    public String strSheetTitleEmailRecipient;
    public String strSheetTitleTextRecipient;
    public String strSheetTitlePhoneCallRecipient;
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
            strViewTitleAboutMe             = "Om meg";
            
            strLabelAge                     = "Alder";
            strLabelDateOfBirth             = "Født";
            strLabelMobilePhone             = "Mobil";
            strLabelEmail                   = "Epost";
            
            strPlaceholderName              = "Navn";
            strPlaceholderPhoto             = "Bilde";
            strPlaceholderDateOfBirth       = "Fødselsdato";
            strPlaceholderMobilePhone       = "Mobilnummer";
            strPlaceholderEmail             = "En gyldig epostadresse";
            
            strFooterOrigoInviteAlert       = "En invitasjon vil bli sendt om du oppgir en epost-adresse.";
            strFooterJuvenileOrigoGuardian  = "Før du kan registrere et mindreårig origo-medlem, må du registrere hans eller hennes foresatte.";
            
            strButtonParentToSome           = "Til noen av dem";
            strButtonAddAddress             = "Legg til en adresse";
            strButtonChangePassword         = "Endre passord";
            strButtonEditRelations          = "Rediger relasjoner";
            strButtonCorrectGender          = "Korriger kjønn";
            strButtonNewAddress             = "Ny adresse";
            strButtonAllContacts            = "Alle kontaktpersoner";
            strButtonInviteToHousehold      = "Inviter til husstanden";
            strButtonMergeHouseholds        = "Slå sammen husstandene";
            
            strAlertTitleMemberExists       = "Allerede registrert";
            strAlertTextMemberExists        = "%@ (%@) er allerede registrert i \"%@\". Vennligst oppgi en annen epost-adresse, eller avbryt registreringen.";
            strAlertTitleUserEmailChange    = "Ny epost-adresse";
            strAlertTextUserEmailChange     = "Du er i ferd med å endre epost-adressen din fra %@ til %@. Du må ha tilgang til den nye adressen for å aktivere endringen. Ønsker du å fortsette?";
            strAlertTitleFailedEmailChange  = "Aktivering mislyktes";
            strAlertTextFailedEmailChange   = "Aktivering av epost-adressen %@ mislyktes. Prøv igjen, eller trykk Avbryt for avbryte endringen.";
            
            strSheetTitleEmailRecipient     = "Hvem vil du sende epost til?";
            strSheetTitleTextRecipient      = "Hvem vil du sende tekstmelding til?";
            strSheetTitlePhoneCallRecipient = "Hvem vil du ringe?";
            strSheetTitleExistingResidence  = "%@ er allerede medlem av en husstand. Vil du invitere %@ til også å bli med i din husstand, eller ønsker du å slå husstandene deres sammen til én?";
            
            strQuestionArgumentGender       = "kvinne eller mann";
            strQuestionArgumentGenderMinor  = "jente eller gutt";
            
            strTermHisFather                = "faren hans";
            strTermHerFather                = "faren hennes";
            strTermHisMother                = "moren hans";
            strTermHerMother                = "moren hennes";
        } else {
            strViewTitleAboutMe             = "About me";
            
            strLabelAge                     = "Age";
            strLabelDateOfBirth             = "Born";
            strLabelMobilePhone             = "Mobile";
            strLabelEmail                   = "Email";
            
            strPlaceholderName              = "Name";
            strPlaceholderPhoto             = "Photo";
            strPlaceholderDateOfBirth       = "Date of birth";
            strPlaceholderMobilePhone       = "Mobile phone number";
            strPlaceholderEmail             = "A valid email address";
            
            strFooterOrigoInviteAlert       = "An invitation will be sent if you provide an email address.";
            strFooterJuvenileOrigoGuardian  = "Before you can register a minor origo member, you must register his or her parents/guardians.";
            
            strButtonParentToSome           = "To some of them";
            strButtonAddAddress             = "Add an address";
            strButtonChangePassword         = "Change password";
            strButtonEditRelations          = "Edit relations";
            strButtonCorrectGender          = "Correct gender";
            strButtonNewAddress             = "New address";
            strButtonAllContacts            = "All contacts";
            strButtonInviteToHousehold      = "Invite to household";
            strButtonMergeHouseholds        = "Merge households";
            
            strAlertTitleMemberExists       = "Already registered";
            strAlertTextMemberExists        = "%@ (%@) is already registered in '%@'. Please enter a different email address, or cancel the registration.";
            strAlertTitleUserEmailChange    = "New email address";
            strAlertTextUserEmailChange     = "You are about to change your email address from %@ to %@. You need access to the new address to activate this change. Do you want to continue?";
            strAlertTitleFailedEmailChange  = "Activation failed";
            strAlertTextFailedEmailChange   = "The email address %@ could not be activated. Please try again, or tap Cancel to cancel the change.";
            
            strSheetTitleEmailRecipient     = "Who do you want to email?";
            strSheetTitleTextRecipient      = "Who do you want to text?";
            strSheetTitlePhoneCallRecipient = "Who do you want to call?";
            strSheetTitleExistingResidence  = "%@ is already member of a household. Would you like to invite %@ to join your household as well, or do you want to merge your households into one?";
            
            strQuestionArgumentGender       = "a woman or a man";
            strQuestionArgumentGenderMinor  = "a girl or a boy";
            
            strTermHisFather                = "his father";
            strTermHerFather                = "her father";
            strTermHisMother                = "his mother";
            strTermHerMother                = "her mother";
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
            strSettingTextCountry  = "Adapt origos for";
        }
    }
    
    
    /* ==== OSettingView strings ==== */
    
    public String strLabelCountrySettings;
    public String strLabelCountryLocation;
    
    public String strFooterCountryInfo;
    public String strFooterCountryInfoNote;
    public String strFooterCountryInfoLocate;
    
    private void setSettingViewStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strLabelCountrySettings    = "Lokal innstilling";
            strLabelCountryLocation    = "Dette landet";
            
            strFooterCountryInfo       = "Lokale tilpasninger er foreløpig kun tilgjengelig for %@.";
            strFooterCountryInfoNote   = "Generelle tilpasninger vil bli benyttet om du velger et annet land.";
            strFooterCountryInfoLocate = "Om du ønsker å angi landet du befinner deg i, må du tillate Origo å bruke stedstjenestene.\n(Innstillinger > Personvern > Sted)";
        } else {
            strLabelCountrySettings    = "Local setting";
            strLabelCountryLocation    = "This country";
            
            strFooterCountryInfo       = "Local adaptations are currently only availbale for %@.";
            strFooterCountryInfoNote   = "General adaptations will be used if you select a different country.";
            strFooterCountryInfoLocate = "If you wish to specify the country you're in, you must permit Origo to use location services.\n(Settings > Privacy > Location)";
        }
    }
    
    
    /* ==== Origo type strings ==== */
    
    public String strOrigoLabelContactList;
    public String strOrigoLabelResidence;
    public String strOrigoLabelFriends;
    public String strOrigoLabelTeam;
    public String strOrigoLabelOrganisation;
    public String strOrigoLabelPreschoolClass;
    public String strOrigoLabelSchoolClass;
    public String strOrigoLabelPlaymates;
    public String strOrigoLabelMinorTeam;
    public String strOrigoLabelOther;
    
    public String strNewOrigoLabelContactList;
    public String strNewOrigoLabelResidence;
    public String strNewOrigoLabelFriends;
    public String strNewOrigoLabelTeam;
    public String strNewOrigoLabelOrganisation;
    public String strNewOrigoLabelPreschoolClass;
    public String strNewOrigoLabelSchoolClass;
    public String strNewOrigoLabelPlaymates;
    public String strNewOrigoLabelMinorTeam;
    public String strNewOrigoLabelOther;
    
    public String strMemberListLabelContactList;
    public String strMemberListLabelResidence;
    public String strMemberListLabelFriends;
    public String strMemberListLabelTeam;
    public String strMemberListLabelOrganisation;
    public String strMemberListLabelPreschoolClass;
    public String strMemberListLabelSchoolClass;
    public String strMemberListLabelPlaymates;
    public String strMemberListLabelMinorTeam;
    public String strMemberListLabelOther;
    
    public String strNewMemberLabelContactList;
    public String strNewMemberLabelResidence;
    public String strNewMemberLabelFriends;
    public String strNewMemberLabelTeam;
    public String strNewMemberLabelOrganisation;
    public String strNewMemberLabelPreschoolClass;
    public String strNewMemberLabelSchoolClass;
    public String strNewMemberLabelPlaymates;
    public String strNewMemberLabelMinorTeam;
    public String strNewMemberLabelOther;
    
    private void setOrigoTypeStrings(String language)
    {
        if (language.equals(LANG_NORWEGIAN_BOKMAL)) {
            strOrigoLabelResidence           = "Adresse";
            strOrigoLabelContactList         = "Personlig kontaktliste";
            strOrigoLabelFriends             = "Vennegruppe";
            strOrigoLabelTeam                = "Sportslag";
            strOrigoLabelOrganisation        = "Organisasjon";
            strOrigoLabelPreschoolClass      = "Barnehageavdeling";
            strOrigoLabelSchoolClass         = "Skoleklasse";
            strOrigoLabelPlaymates           = "Vennegjeng";
            strOrigoLabelMinorTeam           = "Sportslag";
            strOrigoLabelOther               = "Annet formål";
            
            strNewOrigoLabelResidence        = "Ny adresse";
            strNewOrigoLabelContactList      = "Ny kontaktliste";
            strNewOrigoLabelFriends          = "Ny vennegruppe";
            strNewOrigoLabelTeam             = "Nytt lag";
            strNewOrigoLabelOrganisation     = "Ny organisasjon";
            strNewOrigoLabelPreschoolClass   = "Ny barnehageavdeling";
            strNewOrigoLabelSchoolClass      = "Ny skoleklasse";
            strNewOrigoLabelPlaymates        = "Ny vennegjeng";
            strNewOrigoLabelMinorTeam        = "Nytt lag";
            strNewOrigoLabelOther            = "Nytt origo";
            
            strMemberListLabelResidence      = "I husstanden";
            strMemberListLabelContactList    = "Kontakter";
            strMemberListLabelFriends        = "I gruppa";
            strMemberListLabelTeam           = "På laget";
            strMemberListLabelOrganisation   = "Medlemmer";
            strMemberListLabelPreschoolClass = "I avdelingen";
            strMemberListLabelSchoolClass    = "I klassen";
            strMemberListLabelPlaymates      = "I gjengen";
            strMemberListLabelMinorTeam      = "På laget";
            strMemberListLabelOther          = "Medlemmer";
            
            strNewMemberLabelResidence       = "I husstanden";
            strNewMemberLabelContactList     = "Ny kontakt";
            strNewMemberLabelFriends         = "I gruppa";
            strNewMemberLabelTeam            = "På laget";
            strNewMemberLabelOrganisation    = "Nytt medlem";
            strNewMemberLabelPreschoolClass  = "Ny i avdelingen";
            strNewMemberLabelSchoolClass     = "Ny klassekompis";
            strNewMemberLabelPlaymates       = "I gjengen";
            strNewMemberLabelMinorTeam       = "På laget";
            strNewMemberLabelOther           = "Nytt medlem";
        } else {
            strOrigoLabelResidence           = "Address";
            strOrigoLabelContactList         = "Personal contact list";
            strOrigoLabelFriends             = "Party of friends";
            strOrigoLabelTeam                = "Sports team";
            strOrigoLabelOrganisation        = "Organisation";
            strOrigoLabelPreschoolClass      = "Preschool class";
            strOrigoLabelSchoolClass         = "School class";
            strOrigoLabelPlaymates           = "Flock of friends";
            strOrigoLabelMinorTeam           = "Sports team";
            strOrigoLabelOther               = "General purpose";
            
            strNewOrigoLabelResidence        = "New address";
            strNewOrigoLabelContactList      = "New contact list";
            strNewOrigoLabelFriends          = "New party of friends";
            strNewOrigoLabelTeam             = "New team";
            strNewOrigoLabelOrganisation     = "New organisation";
            strNewOrigoLabelPreschoolClass   = "New preschool class";
            strNewOrigoLabelSchoolClass      = "New school class";
            strNewOrigoLabelPlaymates        = "New flock";
            strNewOrigoLabelMinorTeam        = "New team";
            strNewOrigoLabelOther            = "New origo";
            
            strMemberListLabelResidence      = "In the household";
            strMemberListLabelContactList    = "Contacts";
            strMemberListLabelFriends        = "In the party";
            strMemberListLabelTeam           = "On the team";
            strMemberListLabelOrganisation   = "Members";
            strMemberListLabelPreschoolClass = "In the class";
            strMemberListLabelSchoolClass    = "In the class";
            strMemberListLabelPlaymates      = "In the flock";
            strMemberListLabelMinorTeam      = "On the team";
            strMemberListLabelOther          = "Members";
            
            strNewMemberLabelResidence       = "In the household";
            strNewMemberLabelContactList     = "New contact";
            strNewMemberLabelFriends         = "In the party";
            strNewMemberLabelTeam            = "On the team";
            strNewMemberLabelOrganisation    = "New member";
            strNewMemberLabelPreschoolClass  = "New classmate";
            strNewMemberLabelSchoolClass     = "New classmate";
            strNewMemberLabelPlaymates       = "In the flock";
            strNewMemberLabelMinorTeam       = "On the team";
            strNewMemberLabelOther           = "New member";
        }
    }
    
    
    /* ==== Meta strings ==== */
    
    public String metaSupportedCountryCodes = "no";
    
    public String metaContactRolesSchoolClass = "classTeacher|topicTeacher|specialEducationTeacher|assistantTeacher|headTeacher|parentRepresentative";
    public String strContactRoleClassTeacher;
    public String strContactRoleTopicTeacher;
    public String strContactRoleSpecialEducationTeacher;
    public String strContactRoleAssistantTeacher;
    public String strContactRoleHeadTeacher;
    public String strContactRoleParentRepresentative;
    
    public String metaContactRolesPreschoolClass = "preschoolClassTeacher|preschoolTeacher|preschoolAssistantTeacher";
    public String strContactRolePreschoolClassTeacher;
    public String strContactRolePreschoolTeacher;
    public String strContactRolePreschoolAssistantTeacher;
    
    public String metaContactRolesOrganisation = "chair|deputyChair|treasurer";
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
            strContactRoleParentRepresentative      = "Klassekontakt";
            
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
    public String nouns    = "origo|father|mother|parent|guardian|contact|address";
    public String pronouns = "I|you|he|she";
    
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
            
            verbBe              = "er|er|er|er|er|er";
            
            nounOrigo           = "-|-|-|-|mine origo|%@ sine origo";
            nounFather          = "far|faren|-|-|faren din|faren til %@";
            nounMother          = "mor|moren|-|-|moren din|moren til %@";
            nounParent          = "-|-|foreldre|foreldrene|foreldrene dine|foreldrene til %@";
            nounGuardian        = "foresatt|-|foresatte|-|-|-";
            nounContact         = "kontaktperson|-|kontaktpersoner|-|-|-";
            nounAddress         = "adresse|-|adresser|-|-|-";
            
            pronounI            = "jeg|meg|meg|meg";
            pronounYou          = "du|deg|deg|deg";
            pronounHe           = "han|ham|ham|ham";
            pronounShe          = "hun|henne|henne|henne";
        } else {
            strQuestionTemplate = "{verb} {subject} {argument}?";
            
            verbBe              = "am|are|is|are|are|are";
            
            nounOrigo           = "-|-|-|-|my origos|%@'s origos";
            nounFather          = "father|the father|-|-|your father|%@'s father";
            nounMother          = "mother|the mother|-|-|your mother|%@'s mother";
            nounParent          = "-|-|parents|the parents|your parents|%@'s parents";
            nounGuardian        = "guardian|-|guardians|-|-|-";
            nounContact         = "contact|-|contacts|-|-|-";
            nounAddress         = "address|-|addresses|-|-|-";
            
            pronounI            = "I|me|me|me";
            pronounYou          = "you|you|you|you";
            pronounHe           = "he|him|him|him";
            pronounShe          = "she|her|her|her";
        }
    }
}
