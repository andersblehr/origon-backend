package co.origon.api.exceptions;

import co.origon.api.OrigonApplication;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;


@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException>
{
	private static final Logger LOG = Logger.getLogger(OrigonApplication.class.getName());

	public Response toResponse(WebApplicationException exception)
	{
		LOG.warning(exception.getMessage());
		return exception.getResponse();
	}
}
