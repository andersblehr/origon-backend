package co.origon.api.filter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import co.origon.api.common.UrlParams;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupportedLanguageFilterTest {

  private static final String SUPPORTED_LANGUAGE = "en";
  private static final String UNSUPPORTED_LANGUAGE = "hr";
  private static final String INVALID_LANGUAGE_CODE = "harr";

  private SupportedLanguageFilter supportedLanguageFilter = new SupportedLanguageFilter();

  @Mock private ContainerRequestContext requestContext;
  @Mock private UriInfo uriInfo;
  @Mock private MultivaluedMap<String, String> queryParameters;

  @Nested
  @DisplayName("filter()")
  class WhenFilter {

    @Test
    @DisplayName("Given supported language, then run to completion")
    void givenSupportedLanguage_thenRunToCompletion() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.LANGUAGE)).thenReturn(SUPPORTED_LANGUAGE);

      // when
      supportedLanguageFilter.filter(requestContext);

      // then
      assertTrue(true);
    }

    @Test
    @DisplayName("Given no language, then throw BadRequestException")
    void givenNoLanguage_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.LANGUAGE)).thenReturn(null, "");

      assertAll(
          "Missing language",
          // then
          () -> {
            final Throwable eNull =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        supportedLanguageFilter.filter(requestContext));
            assertEquals("Missing parameter: " + UrlParams.LANGUAGE, eNull.getMessage());
          },

          // then
          () -> {
            final Throwable eEmpty =
                assertThrows(
                    BadRequestException.class,
                    () ->
                        // when
                        supportedLanguageFilter.filter(requestContext));
            assertEquals("Missing parameter: " + UrlParams.LANGUAGE, eEmpty.getMessage());
          });
    }

    @Test
    @DisplayName("Given invalid language code, then throw BadRequestException")
    void givenInvalidLanguageCode_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.LANGUAGE)).thenReturn(INVALID_LANGUAGE_CODE);

      // then
      Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  supportedLanguageFilter.filter(requestContext));
      assertEquals("Invalid language code: " + INVALID_LANGUAGE_CODE, e.getMessage());
    }

    @Test
    @DisplayName("Given invalid language, then throw BadRequestException")
    void givenInvalidOrUnsupportedLanguage_thenThrowBadRequestException() {
      // given
      when(requestContext.getUriInfo()).thenReturn(uriInfo);
      when(uriInfo.getQueryParameters()).thenReturn(queryParameters);
      when(queryParameters.getFirst(UrlParams.LANGUAGE)).thenReturn(UNSUPPORTED_LANGUAGE);

      // then
      Throwable e =
          assertThrows(
              BadRequestException.class,
              () ->
                  // when
                  supportedLanguageFilter.filter(requestContext));
      assertEquals("Invalid or unsupported language: " + UNSUPPORTED_LANGUAGE, e.getMessage());
    }
  }
}
