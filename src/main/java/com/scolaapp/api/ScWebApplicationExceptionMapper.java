package com.scolaapp.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ScWebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException>
{
	@Override
	public Response toResponse(WebApplicationException exception)
	{
		return exception.getResponse();
	}
}
