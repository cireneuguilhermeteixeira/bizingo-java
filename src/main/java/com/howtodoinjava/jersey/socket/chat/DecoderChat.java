package com.howtodoinjava.jersey.socket.chat;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.howtodoinjava.jersey.model.Message;

import javax.websocket.Decoder.Text;
import javax.websocket.EndpointConfig;
import java.io.IOException;
import java.util.logging.Logger;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.SerializationFeature.*;

public class DecoderChat implements Text<Message> {

    private final static Logger logger = Logger.getLogger(EncoderChat.class.getName());

    private ObjectMapper objectMapper = createObjectMapper();


    @Override
    public Message decode(String s) {
        logger.info("Transformando string em um objeto.");
        try {
            return objectMapper.reader(Message.class).readValue(s);
        } catch (IOException e) {
            logger.info("Erro ao tentar transformar string em um objeto."+e);
            return null;
        }
    }

    @Override
    public boolean willDecode(String s) {
        return true ;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

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