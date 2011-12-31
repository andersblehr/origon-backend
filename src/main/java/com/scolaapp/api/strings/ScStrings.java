package com.scolaapp.api.strings;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ScStrings")
public class ScStrings
{
    // Alert messages
    public String strInvalidNameAlert;
    public String strInvalidEmailAlert;
    public String strInvalidPasswordAlert;
    public String strInvalidInvitationCodeAlert;
    
    // Generic strings
    public String strOK;
    public String strCancel;
    public String strContinue;
    public String strLater;
    public String strPleaseWait;
    public String strPleaseProvide;
    
    // Root view
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
    public String strInvitationCodePrompt;
    public String strNewPasswordPrompt;
    public String strPasswordPrompt;
    public String strEmailSentPopUp;
    public String strRegistrationCodePrompt;
    public String strRepeatPasswordPrompt;
    public String strScolaDescription;
    
    // Confirm new user
    public String strUserWelcome;
    public String strEnterRegistrationCode;
    public String strRegistrationCode;
    public String strFemale;
    public String strFemaleMinor;
    public String strMale;
    public String strMaleMinor;

    
    public ScStrings()
    {
    	this("en");
    }
    
    
    public ScStrings(String language)
    {
        if ("nb".equals(language)) {
            // Alert messages
            strInvalidNameAlert           = "Vennligst oppgi fullt navn";
            strInvalidEmailAlert          = "Vennligst oppgi en gyldig epost-adresse";
            strInvalidPasswordAlert       = "Passordet må være på minimum %d tegn";
            strInvalidInvitationCodeAlert = "Invitasjonskoden er for kort, vennligst sjekk om du har skrevet den riktig";
            
            // Generic strings
            strOK                         = "OK";
            strCancel                     = "Avbryt";
            strContinue                   = "Fortsett";     
            strLater                      = "Senere";
            strPleaseWait                 = "Vennligst vent...";
            strPleaseProvide              = "Vennligst oppgi:";
            
            // Root view UI strings
            strMembershipPrompt           = "Er du ny her? Invitert? Allerede medlem?";
            strIsNew                      = "Ny her";
            strIsInvited                  = "Invitert";
            strIsMember                   = "Medlem";
            strUserHelpNew                = "Om du vil bli Scola-medlem, vennligst oppgi:";
            strUserHelpInvited            = "Om du har mottatt en invitasjon, vennligst oppgi:";
            strUserHelpMember             = "Logg på om du allerede er Scola-medlem:";
            strNamePrompt                 = "Fullt navn som i signaturen din";
            strNameAsReceivedPrompt       = "Navnet ditt som skrevet i invitasjonen";
            strEmailPrompt                = "Epost-adressen din";
            strInvitationCodePrompt       = "Invitasjonskode din";
            strNewPasswordPrompt          = "Et fritt valgt passord";
            strPasswordPrompt             = "Passordet ditt";
            strEmailSentPopUp             = "En epost med din personlige registreringskode er sendt til %@. Trykk \"Senere\" om du ikke har tilgang til eposten din akkurat nå.";
            strRegistrationCodePrompt     = "Din personlige registreringskode";
            strRepeatPasswordPrompt       = "Samme passord som i sted";
            strScolaDescription           = "[subst.] en gruppe mennesker som omgås, samarbeider og/eller er avhengige av hverandre i det daglige.";
            
            // Confirm new user
            strUserWelcome                = "Velkommen, %@!";
            strEnterRegistrationCode      = "Vi har sendt en registreringskode til epost-adressen din (%@). Vennligst oppgi den i feltet under.";
            strRegistrationCode           = "Registreringskode";
            strFemale                     = "Kvinne";
            strFemaleMinor                = "Jente";
            strMale                       = "Mann";
            strMaleMinor                  = "Gutt";
        } else {
            // Alert messages
            strInvalidNameAlert           = "Please provide your full name";
            strInvalidEmailAlert          = "Please provide a valid email address";
            strInvalidPasswordAlert       = "The password must contain minimum %d characters";
            strInvalidInvitationCodeAlert = "The invitation code is too short, please verify that you have written it correctly";
            
            // Generic strings
            strOK                         = "OK";
            strCancel                     = "Cancel";
            strContinue                   = "Continue";
            strLater                      = "Later";
            strPleaseWait                 = "Please wait...";
            strPleaseProvide              = "Please provide:";
            
            // Root view
            strMembershipPrompt           = "Are you new here? Invited? Already a member?";
            strIsNew                      = "New here";
            strIsInvited                  = "Invited";
            strIsMember                   = "Member";
            strUserHelpNew                = "To become a Scola member, please provide:";
            strUserHelpInvited            = "If you have received an invitation, please provide:";
            strUserHelpMember             = "Log in if you're already a Scola member:";
            strNamePrompt                 = "Your name as you sign it";
            strNameAsReceivedPrompt       = "Your name as written in the invitation";
            strEmailPrompt                = "Your email address";
            strInvitationCodePrompt       = "Your invitation code";
            strNewPasswordPrompt          = "A password of your choice";
            strPasswordPrompt             = "Your password";
            strEmailSentPopUp             = "An email has been sent to %@. Please provide:";
            strEmailSentPopUp             = "An email with your personal registration code has been sent to %@. Tap \"Later\" if you don't have access to your email right now.";
            strRegistrationCodePrompt     = "Your personal registration code";
            strRepeatPasswordPrompt       = "The same password as before";
            strScolaDescription           = "[noun] a group of people who interact, team up, and/or depend on each other in day-to-day activities.";
            
            // Confirm new user
            strUserWelcome                = "Welcome, %s!";
            strEnterRegistrationCode      = "We have sent a registration code to your email address (%s). Please enter it into the field below.";
            strRegistrationCode           = "Registration code";
            strFemale                     = "Female";
            strFemaleMinor                = "Girl";
            strMale                       = "Male";
            strMaleMinor                  = "Boy";
        }
    }
}
