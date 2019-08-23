package co.origon.api.common;

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
      assertEquals(Language.ENGLISH, Language.fromCode("en"));
      assertEquals(Language.GERMAN, Language.fromCode("de"));
      assertEquals(Language.NORWEGIAN, Language.fromCode("nb"));
    }

    @Test
    @DisplayName("Given unsupported language, then throw IllegalArgumentException")
    void givenUnsupportedLanguage_thenThrowIllegalArgumentException() {
      Throwable e = assertThrows(IllegalArgumentException.class, () -> Language.fromCode("da"));
      assertTrue(e.getMessage().startsWith("Unknown or unsupported language"));
    }
  }
}
