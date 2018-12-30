package co.origon.api.filter;

import co.origon.api.annotation.LanguageSupported;
import co.origon.api.common.Language;
import co.origon.api.common.UrlParams;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import static com.google.common.base.Preconditions.checkArgument;

@Provider
@LanguageSupported
@Priority(5)
public class LanguageSupportedFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String languageCode = requestContext.getUriInfo().getQueryParameters().getFirst(UrlParams.LANGUAGE);
        try {
            checkArgument(languageCode != null && languageCode.length() > 0, "Missing parameter: " + UrlParams.LANGUAGE);
            checkArgument(languageCode.length() == 2, "Invalid language code: " + languageCode);
            Language.fromCode(languageCode);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Missing or invalid language: " + languageCode, e);
        }
    }
}
