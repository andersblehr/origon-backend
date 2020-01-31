package co.origon.api.filter;

import co.origon.api.common.Mailer;
import co.origon.api.common.UrlParams;
import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
@SupportedLanguage
@Priority(5)
public class SupportedLanguageFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext requestContext) {
    final String languageCode =
        requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.LANGUAGE);
    if (languageCode == null || languageCode.length() == 0)
      throw new BadRequestException("Missing parameter: " + UrlParams.LANGUAGE);
    if (languageCode.length() != 2)
      throw new BadRequestException("Invalid language code: " + languageCode);

    try {
      Mailer.Language.fromCode(languageCode);
    } catch (IllegalArgumentException e) {
      throw new BadRequestException("Invalid or unsupported language: " + languageCode, e);
    }
  }
}
