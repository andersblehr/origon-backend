package co.origon.api.exceptions;

import co.origon.api.OrigonApplication;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;


@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException>
{
	private static final Logger LOG = Logger.getLogger(OrigonApplication.class.getName());

	public Response toResponse(IllegalArgumentException e)
	{
		LOG.warning(e.getMessage());
		return Response.status(Response.Status.BAD_REQUEST).build();
	}
}
