package com.howtodoinjava.jersey;
import com.howtodoinjava.jersey.model.Message;

import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/message")
public class JerseyService {

    private final static Logger logger = Logger.getLogger(JerseyService.class.getName());

    @POST
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    public Response getMsg(Message message) {
        logger.info("Get message funcionando");


        return Response.status(Response.Status.OK).entity(message).build();
    }
}
