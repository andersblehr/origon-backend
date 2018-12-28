package co.origon.api.filters;

import co.origon.api.annotations.JwtAuthenticated;
import co.origon.api.common.Config;
import co.origon.api.common.Config.Category;
import co.origon.api.common.Config.Setting;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;

import javax.annotation.Priority;
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

    @Override
    public void filter(ContainerRequestContext requestContext) {
        final String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null) {
            throw new BadRequestException("Missing AUTHORIZATION header");
        }

        final String[] authElements = authorizationHeader.split(" ");
        if (authElements.length != 2) {
            throw new BadRequestException("Invalid AUTHORIZATION header: " + authorizationHeader);
        }

        if (!authElements[0].equals("Bearer")) {
            throw new BadRequestException("Invalid authentication scheme for Bearer token: " + authElements[0]);
        }

        try {
            final String jwt = authElements[1];
            final String secret = Config.get(Category.JWT).getString(Setting.SECRET);

            JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(jwt);
        } catch (TokenExpiredException e) {
            throw new NotAuthorizedException("JWT has expired");
        } catch (Exception e) {
            throw new BadRequestException("Invalid JWT", e);
        }
    }
}
