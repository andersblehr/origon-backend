package co.origon.api.common;

import co.origon.mailer.api.Mailer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

  @Nested
  @DisplayName("fromCode()")
  class WhenFromCode {

    @Test
    @DisplayName("Given supported language, then create instance")
    void givenSupportedLanguage_thenCreateInstance() {
      assertEquals(Mailer.Language.ENGLISH, Mailer.Language.fromCode("en"));
      assertEquals(Mailer.Language.GERMAN, Mailer.Language.fromCode("de"));
      assertEquals(Mailer.Language.NORWEGIAN, Mailer.Language.fromCode("nb"));
    }

    @Test
    @DisplayName("Given unsupported language, then throw IllegalArgumentException")
    void givenUnsupportedLanguage_thenThrowIllegalArgumentException() {
      Throwable e = assertThrows(IllegalArgumentException.class, () -> Mailer.Language.fromCode("da"));
      assertTrue(e.getMessage().startsWith("Unknown or unsupported language"));
    }
  }
}
