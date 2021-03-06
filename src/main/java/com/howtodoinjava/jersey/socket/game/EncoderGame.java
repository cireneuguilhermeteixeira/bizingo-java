package com.howtodoinjava.jersey.socket.game;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.howtodoinjava.jersey.model.Move;

import javax.websocket.Encoder.Text;
import javax.websocket.EndpointConfig;
import java.util.logging.Logger;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.SerializationFeature.*;


public class EncoderGame implements Text<Move> {

    private final static Logger logger = Logger.getLogger(EncoderGame.class.getName());

    private ObjectMapper objectMapper = createObjectMapper();


    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String encode(Move object) {
        logger.info("Transformando objeto Move de id "+object.getMoveId()+" em string.");
        ObjectWriter ow = objectMapper.writerFor(Move.class);
        try {
            return ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.info("Erro ao tentar transformar objeto Move de id "+object.getMoveId()+"em string. "+e);
            return null;
        }

    }
    private ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .setVisibility(ALL, NONE)
                .setVisibility(FIELD, ANY)
                .setSerializationInclusion(NON_EMPTY)
                .enable(INDENT_OUTPUT)
                .enable(USE_EQUALITY_FOR_OBJECT_ID)
                .enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);
    }

}
