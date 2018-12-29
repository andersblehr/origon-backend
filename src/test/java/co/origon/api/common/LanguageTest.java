package co.origon.api.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {

    @Nested
    class FromCode {

        @Test
        @DisplayName("Create instance when language is supported")
        void createInstance_whenLanguageIsSupported() {
            assertEquals(Language.ENGLISH, Language.fromCode("en"));
            assertEquals(Language.GERMAN, Language.fromCode("de"));
            assertEquals(Language.NORWEGIAN, Language.fromCode("nb"));
        }

        @Test
        void throwIllegalArgumentException_whenUnknownLanguuage() {
            Throwable e = assertThrows(IllegalArgumentException.class, () ->
                    Language.fromCode("da")
            );
            assertTrue(e.getMessage().startsWith("Unknown or unsupported language"));
        }
    }
}
