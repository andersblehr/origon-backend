package co.origon.api.filter;

import co.origon.api.annotation.JwtAuthenticated;
import co.origon.api.model.api.DaoFactory;
import co.origon.api.model.api.entity.Config;
import co.origon.api.model.api.entity.Config.Category;
import co.origon.api.model.api.entity.Config.Setting;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

@Provider
@JwtAuthenticated
@Priority(2)
public class JwtAuthenticatedFilter implements ContainerRequestFilter {

    static String WWW_AUTHENTICATE_CHALLENGE_BEARER_TOKEN = "renew";

    private DaoFactory daoFactory;

    @Inject
    JwtAuthenticatedFilter(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.length() == 0)
            throw new BadRequestException("Missing Authorization header");

        final String[] authElements = authorizationHeader.split(" ");
        if (authElements.length != 2)
            throw new BadRequestException("Invalid Authorization header: " + authorizationHeader);
        if (!authElements[0].equals("Bearer"))
            throw new BadRequestException("Invalid authentication scheme for Bearer token: " + authElements[0]);

        try {
            final String jwt = authElements[1];
            final Config jwtConfig = daoFactory.daoFor(Config.class).get(Category.JWT);
            JWT.require(Algorithm.HMAC256(jwtConfig.getString(Setting.SECRET)))
                    .build()
                    .verify(jwt);
        } catch (TokenExpiredException e) {
            throw new NotAuthorizedException("JWT token has expired", WWW_AUTHENTICATE_CHALLENGE_BEARER_TOKEN);
        } catch (Exception e) {
            throw new BadRequestException("Invalid JWT token", e);
        }
    }
}
