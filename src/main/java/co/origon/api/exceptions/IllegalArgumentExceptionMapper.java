package co.origon.api.exceptions;

import co.origon.api.OOrigonApplication;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;


@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException>
{
	private static final Logger LOG = Logger.getLogger(OOrigonApplication.class.getName());

	public Response toResponse(IllegalArgumentException e)
	{
		LOG.warning(e.getMessage());
		throw new BadRequestException(e);
	}
}
