package co.origon.api.exceptions;

import co.origon.api.OrigonApplication;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;


@Provider
public class NullPointerExceptionMapper implements ExceptionMapper<NullPointerException>
{
	private static final Logger LOG = Logger.getLogger(OrigonApplication.class.getName());

	public Response toResponse(NullPointerException e)
	{
		LOG.warning(e.getMessage());
		throw new BadRequestException(e);
	}
}
