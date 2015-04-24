package co.origon.api;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class OWebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException>
{
	public Response toResponse(WebApplicationException exception)
	{
		return exception.getResponse();
	}
}
