package co.origon.api.filters;

import co.origon.api.helpers.Config;
import co.origon.api.helpers.Config.Category;
import co.origon.api.helpers.Config.Setting;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@JwtAuthenticated
public class JwtTokenFilter implements ContainerRequestFilter {

    private final static String TOKEN_TYPE_BEARER = "Bearer";

    @Override
    public void filter(ContainerRequestContext context) {
        final String authorizationHeader = context.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null) {
            throw new WebApplicationException("Missing AUTHORIZATION header", Response.Status.FORBIDDEN);
        }

        final String[] authElements = authorizationHeader.split(" ");

        if (authElements.length != 2) {
            throw new WebApplicationException("Invalid AUTHORIZATION header: " + authorizationHeader, Response.Status.FORBIDDEN);
        }

        if (!authElements[0].equals(TOKEN_TYPE_BEARER)) {
            throw new WebApplicationException("Invalid bearer token type: " + authElements[0], Response.Status.FORBIDDEN);
        }

        try {
            final String jwt = authElements[1];
            final String secret = Config.get(Category.JWT).getString(Setting.SECRET);

            JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(jwt);
        } catch (TokenExpiredException e) {
            throw new WebApplicationException("JWT has expired", Response.Status.UNAUTHORIZED);
        } catch (Exception e) {
            throw new WebApplicationException("Invalid JWT", Response.Status.FORBIDDEN);
        }
    }
}
