package co.origon.api.common;

import co.origon.api.model.MemberProxy;
import co.origon.api.model.api.entity.Member;
import co.origon.api.model.api.entity.Membership;
import co.origon.api.model.api.entity.Origo;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.inject.Singleton;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Singleton
@NoArgsConstructor
public class Mailer {
  private static final String JWT_CFG = "origon.jwt";
  private static final String JWT_CFG_ISSUER = "issuer";
  private static final String JWT_CFG_SECRET = "secret";
  private static final String JWT_CFG_EXPIRES_IN_SECONDS = "expiresInSeconds";

  private static final String MAILER_CFG = "origon.mailer";
  private static final String MAILER_CFG_BASE_URL = "baseUrl";

  private static final String MAILER_RESOURCE_PATH = "/mailer";
  private static final String MAILER_TO = "to";
  private static final String MAILER_SUBJECT = "subject";
  private static final String MAILER_BODY = "body";

  private Language language;

  public Mailer using(String languageCode) {
    return using(Language.fromCode(languageCode));
  }

  public Mailer using(Language language) {
    return new Mailer(language);
  }

  public void sendInvitation(String invitationEmail, MemberProxy userProxy) {
    String invitationSubject = invitationSubject(language, null, null);
    String invitationText = invitationText(language, invitationEmail, userProxy, null, null);

    if (language != Language.ENGLISH) {
      invitationText =
          invitationText
              + "\n\n"
              + invitationText(Language.ENGLISH, invitationEmail, userProxy, null, null);
    }

    sendEmail(invitationEmail, invitationSubject, invitationText);
  }

  public void sendInvitation(MemberProxy userProxy, Membership membership, Origo origo) {
    String invitationEmail = membership.member().email();
    String invitationSubject = invitationSubject(language, membership, origo);
    String invitationText = invitationText(language, invitationEmail, userProxy, membership, origo);

    if (language != Language.ENGLISH) {
      invitationText =
          invitationText
              + "\n\n"
              + invitationText(Language.ENGLISH, invitationEmail, userProxy, membership, origo);
    }

    sendEmail(invitationEmail, invitationSubject, invitationText);
  }

  public void sendEmailChangeNotification(Member invitee, String oldEmail, MemberProxy userProxy) {
    String emailChangeNotificationSubject;

    if (language == Language.NORWEGIAN) {
      emailChangeNotificationSubject = "Epostadressen din på Origon har blitt endret";
    } else if (language == Language.GERMAN) {
      emailChangeNotificationSubject = "Deine E-Mail-Adresse bei Origon ist geändert worden";
    } else {
      emailChangeNotificationSubject = "Your email address on Origon has been changed";
    }

    String emailChangeNotificationText =
        emailChangeNotificationText(language, userProxy, invitee, oldEmail);

    if (language != Language.ENGLISH) {
      emailChangeNotificationText =
          emailChangeNotificationText
              + "\n\n"
              + emailChangeNotificationText(Language.ENGLISH, userProxy, invitee, oldEmail);
    }

    sendEmail(invitee.email(), emailChangeNotificationSubject, emailChangeNotificationText);
    sendEmail(oldEmail, emailChangeNotificationSubject, emailChangeNotificationText);
  }

  public void sendRegistrationEmail(String email, String activationCode) {
    if (language == Language.NORWEGIAN) {
      sendEmail(
          email,
          "Fullfør registreringen på Origon",
          String.format(
              "Velkommen til Origon!\n"
                  + "\n"
                  + "Alt som gjenstår før du kan begynne å bruke Origon, er å oppgi aktiveringskoden under og fylle ut evt manglende opplysninger.\n"
                  + "\n"
                  + "    Aktiveringskode: %s\n"
                  + "\n"
                  + "Merk: Opplysningene dine tilhører deg og bare deg. Vi kommer aldri til å selge dem videre eller bruke dem til noe annet enn det du kan "
                  + "se med egne øyne i Origon.\n"
                  + "\n"
                  + bestRegards(language),
              activationCode));
    } else if (language == Language.GERMAN) {
      sendEmail(
          email,
          "Bitte deine Registrierung bei Origon vollenden",
          String.format(
              "Willkommen bei Origon!\n"
                  + "\n"
                  + "Bevor du Origon nutzen kannst, musst du den folgenden Aktivierungscode eingeben und alle eventuell fehlenden Informationen nachtragen."
                  + "\n"
                  + "    Aktivierungscode: %s\n"
                  + "\n"
                  + "Bitte beachten: Deine Informationen gehören dir und nur dir. Wir werden sie nie weiterverkaufen oder für andere Zwecke nutzen, als was du dir"
                  + "mit eigenen Augen in Origon anschauen kannst."
                  + "\n"
                  + bestRegards(language),
              activationCode));
    } else {
      sendEmail(
          email,
          "Complete your registration with Origon",
          String.format(
              "Welcome to Origon!\n"
                  + "\n"
                  + "All you need to do to start using Origon, is to enter the activation code below and fill in any missing information.\n"
                  + "\n"
                  + "    Activation code: %s\n"
                  + "\n"
                  + "Please note: Your information belongs to you and you alone. We will never sell it nor use it for anything besides what you can "
                  + "see with your own eyes in Origon.\n"
                  + "\n"
                  + bestRegards(Language.ENGLISH),
              activationCode));
    }
  }

  public void sendEmailActivationCode(String email, String activationCode) {
    if (language == Language.NORWEGIAN) {
      sendEmail(
          email,
          "Aktiver epostadressen din for bruk i Origon",
          String.format(
              "Oppgi følgende kode for å aktivere epostadressen %s for bruk i Origon.\n"
                  + "\n"
                  + "    Aktiveringskode: %s\n"
                  + "\n"
                  + bestRegards(language),
              email,
              activationCode));
    } else if (language == Language.GERMAN) {
      sendEmail(
          email,
          "Bitte deine E-Mail-Adresse für Gebrauch bei Origon aktivieren",
          String.format(
              "Bitte den folgenden Code eingeben, um die E-Mail-Adresse %s für Gebrauch bei Origon zu aktivieren.\n"
                  + "\n"
                  + "    Aktivierungscode: %s\n"
                  + "\n"
                  + bestRegards(language),
              email,
              activationCode));
    } else {
      sendEmail(
          email,
          "Activate your email address for use with Origon",
          String.format(
              "Please enter the following code to activate the email address %s for use with Origon.\n"
                  + "\n"
                  + "    Activation code: %s\n"
                  + "\n"
                  + bestRegards(Language.ENGLISH),
              email,
              activationCode));
    }
  }

  public void sendPasswordResetEmail(String email, String activationCode) {
    if (language == Language.NORWEGIAN) {
      sendEmail(
          email,
          "Passordet ditt har blitt tilbakestilt",
          String.format(
              "Ditt midlertidige passord er: %s.\n"
                  + "\n"
                  + "Så snart du har logget på Origon, bør du gå til Innstillinger og endre passordet til noe bare du vet om.\n"
                  + "\n"
                  + bestRegards(language),
              activationCode));
    } else if (language == Language.GERMAN) {
      sendEmail(
          email,
          "Dein Passwort ist zurückgestellt worden",
          String.format(
              "Dein temporäres Passwort ist: %s.\n"
                  + "\n"
                  + "Wenn du dich bei Origon eingeloggt hast, bitte unter Einstellungen ein Passwort wählen, das nur du kennst."
                  + "\n"
                  + bestRegards(language),
              activationCode));
    } else {
      sendEmail(
          email,
          "Your password has been reset",
          String.format(
              "Your temporary passord is: %s.\n"
                  + "\n"
                  + "Once you have logged in to Origon, you should go to Settings and change the password to something only you know.\n"
                  + "\n"
                  + bestRegards(Language.ENGLISH),
              activationCode));
    }
  }

  private Mailer(Language language) {
    this.language = language;
  }

  private String createJwtBearerToken() {
    try {
      final Config jwtConfig = ConfigFactory.load().getConfig(JWT_CFG);
      final Instant now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).toInstant();
      final Instant jwtExpiry = now.plusSeconds(jwtConfig.getInt(JWT_CFG_EXPIRES_IN_SECONDS));

      return JWT.create()
          .withIssuer(jwtConfig.getString(JWT_CFG_ISSUER))
          .withIssuedAt(Date.from(now))
          .withExpiresAt(Date.from(jwtExpiry))
          .sign(Algorithm.HMAC256(jwtConfig.getString(JWT_CFG_SECRET)));
    } catch (Exception e) {
      throw new RuntimeException("Error during JWT creatiion", e);
    }
  }

  private void sendEmail(String to, String subject, String body) {
    try {
      if (!Matcher.isEmailAddress(to)) {
        throw new IllegalArgumentException("Invalid email address");
      }

      final Map<String, String> requestBody = new HashMap<>();
      requestBody.put(MAILER_TO, to);
      requestBody.put(MAILER_SUBJECT, subject);
      requestBody.put(MAILER_BODY, body);

      final Config mailerConfig = ConfigFactory.load().getConfig(MAILER_CFG);
      final URL mailerUrl =
          new URL(mailerConfig.getString(MAILER_CFG_BASE_URL) + MAILER_RESOURCE_PATH);
      final HttpURLConnection connection = (HttpURLConnection) mailerUrl.openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + createJwtBearerToken());
      connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
      connection.setRequestProperty(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

      final JSONObject requestJson = new JSONObject(requestBody);
      final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
      writer.write(requestJson.toString());
      writer.close();

      if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
        final BufferedReader reader =
            new BufferedReader(new InputStreamReader(connection.getInputStream()));
        final StringBuffer responseString = new StringBuffer();

        String responseLine;
        while ((responseLine = reader.readLine()) != null) {
          responseString.append(responseLine);
        }

        final JSONObject response = new JSONObject(responseString);

        throw new RuntimeException("Error sending email: " + response.getString("message"));
      }
    } catch (Exception e) {
      throw new InternalServerErrorException(e.getMessage(), e);
    }
  }

  private String availabilityInfo(Language language, String registrationEmail) {
    String availabilityInfo;

    if (language == Language.NORWEGIAN) {
      availabilityInfo =
          String.format(
              "Origon er tilgjengelig for iOS 8 og senere (iPhone, iPad og iPod touch). "
                  + "Last ned Origon fra App Store og registrer deg med %s for å komme i gang. "
                  + "(Origon er foreløpig ikke tilgjengelig for Android eller Windows Phone.)",
              registrationEmail);
    } else if (language == Language.GERMAN) {
      availabilityInfo =
          String.format(
              "Origon steht für iOS 8 und höher zur Verfügung (iPhone, iPad und iPod touch). "
                  + "Zum Losfahren, Origon im App Store herunterladen und dich mit %s registrieren. "
                  + "(Origon unterstützt zur Zeit nicht Android oder Windows Phone.)",
              registrationEmail);
    } else {
      availabilityInfo =
          String.format(
              "Origon is available on iOS 8 and later (iPhone, iPad and iPod touch). "
                  + "Download Origon from the App Store and register with %s to getOrCreate going. "
                  + "(Origon is currently not available on Android or Windows Phone.)",
              registrationEmail);
    }

    return availabilityInfo;
  }

  private String bestRegards(Language language) {
    String bestRegards;

    if (language == Language.NORWEGIAN) {
      bestRegards = "Med vennlig hilsen Origon-teamet\n" + "https://origon.co\n";
    } else if (language == Language.GERMAN) {
      bestRegards = "Mit freundlichen Grüßen,\n" + "das Origon-Team\n" + "https://origon.co\n";
    } else {
      bestRegards = "Best regards,\n" + "The Origon team\n" + "https://origon.co\n";
    }

    return bestRegards;
  }

  private String invitationSubject(Language language, Membership membership, Origo origo) {
    String invitationSubject;

    if (language == Language.NORWEGIAN) {
      if (origo == null) {
        invitationSubject = "Epostadressen din er lagt inn på Origon";
      } else if (origo.isPrivate()) {
        invitationSubject = "Du har blitt lagt til i en privat liste på Origon";
      } else if (origo.isResidence()) {
        invitationSubject = "Husstanden din har blitt lagt inn på Origon";
      } else if (membership.isAssociate() && !origo.isForMinors()) {
        invitationSubject =
            String.format(
                "Du har blitt lagt inn som familiemedlem i den delte kontaktlista \"%s\" på Origon",
                origo.name());
      } else {
        invitationSubject =
            String.format(
                "Du har blitt lagt inn i den delte kontaktlista \"%s\" på Origon", origo.name());
      }
    } else if (language == Language.GERMAN) {
      if (origo == null) {
        invitationSubject = "Deine E-Mail-Adresse ist in Origon eingetragen worden";
      } else if (origo.isPrivate()) {
        invitationSubject = "Du bist in eine private Liste bei Origon eingetragen worden";
      } else if (origo.isResidence()) {
        invitationSubject = "Dein Haushalt ist bei Origon eingetragen worden";
      } else if (membership.isAssociate() && !origo.isForMinors()) {
        invitationSubject =
            String.format(
                "Du bist als Familienmitglied in die geteilte Kontaktliste \"%s\" bei Origon eingetragen worden",
                origo.name());
      } else {
        invitationSubject =
            String.format(
                "Du bist in die geteilte Kontaktliste \"%s\" bei Origon eingetragen worden",
                origo.name());
      }
    } else {
      if (origo == null) {
        invitationSubject = "Your email address has been added on Origon";
      } else if (origo.isPrivate()) {
        invitationSubject = "You have been added to a private list on Origon";
      } else if (origo.isResidence()) {
        invitationSubject = "Your household has been added on Origon";
      } else if (membership.isAssociate() && !origo.isForMinors()) {
        invitationSubject =
            String.format(
                "You have been added as a family member in the shared contact list '%s' on Origon",
                origo.name());
      } else {
        invitationSubject =
            String.format(
                "You have been added to the shared contact list '%s' on Origon", origo.name());
      }
    }

    return invitationSubject;
  }

  private String invitationBody(
      Language language, MemberProxy userProxy, Membership membership, Origo origo) {
    String invitationBody;

    if (language == Language.NORWEGIAN) {
      if (origo == null) {
        invitationBody =
            String.format(
                "%s (%s) har lagt inn epostadressen din på Origon",
                userProxy.memberName(), userProxy.id());
      } else if (origo.isPrivate()) {
        invitationBody = "Du har blitt lagt til i en privat liste på Origon";
      } else if (origo.isResidence()) {
        invitationBody =
            String.format(
                "%s (%s) har lagt inn husstanden din på Origon",
                userProxy.memberName(), userProxy.id());
      } else if (membership.isAssociate() && !origo.isForMinors()) {
        invitationBody =
            String.format(
                "%s (%s) har lagt deg inn som familiemedlem i den delte kontaktlista \"%s\" på Origon",
                userProxy.memberName(), userProxy.id(), origo.name());
      } else {
        invitationBody =
            String.format(
                "%s (%s) har lagt deg inn i den delte kontaktlista \"%s\" på Origon",
                userProxy.memberName(), userProxy.id(), origo.name());
      }
    } else if (language == Language.GERMAN) {
      if (origo == null) {
        invitationBody =
            String.format(
                "%s (%s) hat deine E-Mail-Adresse in Origon eingetragen",
                userProxy.memberName(), userProxy.id());
      } else if (origo.isPrivate()) {
        invitationBody = "Du bist in eine private Liste bei Origon eingetragen worden";
      } else if (origo.isResidence()) {
        invitationBody =
            String.format(
                "%s (%s) hat deinen Haushalt bei Origon eingetragen",
                userProxy.memberName(), userProxy.id());
      } else if (membership.isAssociate() && !origo.isForMinors()) {
        invitationBody =
            String.format(
                "%s (%s) hat dich als Familienmitglied in die geteilte Kontaktliste \"%s\" bei Origon eingetragen",
                userProxy.memberName(), userProxy.id(), origo.name());
      } else {
        invitationBody =
            String.format(
                "%s (%s) hat dich in die geteilte Kontaktliste \"%s\" bei Origon eingetragen",
                userProxy.memberName(), userProxy.id(), origo.name());
      }
    } else {
      if (origo == null) {
        invitationBody =
            String.format(
                "%s (%s) has added your email address on Origon",
                userProxy.memberName(), userProxy.id());
      } else if (origo.isPrivate()) {
        invitationBody = "You have been added to a private list on Origon";
      } else if (origo.isResidence()) {
        invitationBody =
            String.format(
                "%s (%s) has added your household on Origon",
                userProxy.memberName(), userProxy.id());
      } else if (membership.isAssociate() && !origo.isForMinors()) {
        invitationBody =
            String.format(
                "%s (%s) has added you as a family member in the shared contact list '%s' on Origon",
                userProxy.memberName(), userProxy.id(), origo.name());
      } else {
        invitationBody =
            String.format(
                "%s (%s) has added you to the shared contact list '%s' on Origon",
                userProxy.memberName(), userProxy.id(), origo.name());
      }
    }

    return invitationBody;
  }

  private String invitationText(
      Language language, String email, MemberProxy userProxy, Membership membership, Origo origo) {
    return (language == Language.NORWEGIAN
            ? "Hei!"
            : (language == Language.GERMAN ? "Hallo!" : "Hi!"))
        + "\n\n"
        + invitationBody(language, userProxy, membership, origo)
        + ".\n"
        + "\n"
        + availabilityInfo(language, email)
        + "\n"
        + "\n"
        + bestRegards(language);
  }

  private String emailChangeNotificationText(
      Language language, MemberProxy userProxy, Member invitee, String oldEmail) {
    String emailChangeNotificationText;
    String inviteeGivenName = invitee.name().split(" ")[0];

    if (language == Language.NORWEGIAN) {
      emailChangeNotificationText =
          String.format(
              "%s,\n"
                  + "\n"
                  + "%s (%s) har endret epostadressen din på Origon fra %s til %s.\n"
                  + "\n"
                  + availabilityInfo(language, invitee.email())
                  + "\n"
                  + "\n"
                  + bestRegards(language),
              inviteeGivenName,
              userProxy.memberName(),
              userProxy.id(),
              oldEmail,
              invitee.email());
    } else if (language == Language.GERMAN) {
      emailChangeNotificationText =
          String.format(
              "%s,\n"
                  + "\n"
                  + "%s (%s) hat deine E-Mail-Adresse bei Origon von %s auf %s geändert.\n"
                  + "\n"
                  + availabilityInfo(language, invitee.email())
                  + "\n"
                  + "\n"
                  + bestRegards(language),
              inviteeGivenName,
              userProxy.memberName(),
              userProxy.id(),
              oldEmail,
              invitee.email());
    } else {
      emailChangeNotificationText =
          String.format(
              "%s,\n"
                  + "\n"
                  + "%s (%s) has changed your email address on Origon from %s to %s.\n"
                  + "\n"
                  + availabilityInfo(Language.ENGLISH, invitee.email())
                  + "\n"
                  + "\n"
                  + bestRegards(Language.ENGLISH),
              inviteeGivenName,
              userProxy.memberName(),
              userProxy.id(),
              oldEmail,
              invitee.email());
    }

    return emailChangeNotificationText;
  }

  public enum Language {
    ENGLISH("en"),
    GERMAN("de"),
    NORWEGIAN("nb");

    private final String languageCoode;

    Language(String languageCoode) {
      this.languageCoode = languageCoode;
    }

    public static Language fromCode(String languageCoode) {
      for (Language language : Language.values()) {
        if (language.languageCoode.equals(languageCoode)) {
          return language;
        }
      }

      throw new IllegalArgumentException("Unknown or unsupported language: " + languageCoode);
    }
  }
}
