package com.howtodoinjava.jersey.socket.game;


import com.howtodoinjava.jersey.model.Move;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static java.util.Collections.synchronizedMap;


@ServerEndpoint(value = "/ws/game/{userName}/{playerType}", encoders = EncoderGame.class, decoders = DecoderGame.class)
public class GameEndPoint {


    private final static Logger logger = Logger.getLogger(GameEndPoint.class.getName());
    private static Map<Session,String> sessionsById = synchronizedMap(new HashMap<>());
    private static Map<Session, Session> playersAssociation = synchronizedMap(new HashMap<>());

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") final String userName,
                       @PathParam("playerType") final String playerType)
            throws IOException, EncodeException {

        logger.info("Iniciando socket com usuário de nome "+userName+"  na sessão ["+Integer.parseInt(session.getId(),16)+"].");
        if (this.checkIfUserHasSessionActive(sessionsById,userName)) {
            logger.warning("Opa, você já está conectado em outra sessão.");
            throw new StreamCorruptedException();
        }else {
               sessionsById.put(session,userName);
        }


        if(playerType.equals("1")){
            //Aguardando um player 2;
            playersAssociation.put(session, session);

        }else{
            if(!this.checkIfThereIsPlayer1Waiting(playersAssociation,session)){
                logger.warning("Nenhum player buscando partida.");
                throw new StreamCorruptedException();
            }
        }

    }

    @OnMessage
    public void onMessage(Session session, Move move) throws IOException, EncodeException {
        logger.info("Na Sessão ["+Integer.parseInt(session.getId(),16)+"] - Enviando mensagem: ");

        Map.Entry<Session, Session> currentAssociation = this.getCurrentAssociation(playersAssociation,session);
        if(currentAssociation != null) {
            if (currentAssociation.getKey().equals(currentAssociation.getValue())) {
                logger.warning("Aguardando  o player 2");
                move = new Move();
                //move.setMessage("Aguardando  o player 2");
                session.getBasicRemote().sendObject(move);

            } else if (session.equals(currentAssociation.getKey())) {
                logger.info("Player 1 envia jogada pro Player 2");
                currentAssociation.getValue().getBasicRemote().sendObject(move);
            } else if (session.equals(currentAssociation.getValue())) {

                logger.info("Player 2 envia jogada pro Player 1");
                currentAssociation.getKey().getBasicRemote().sendObject(move);
            }
        }else {
            logger.warning("Conexão foi perdida.");
            throw new StreamCorruptedException();
        }
        // Handle new messages
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Finalizando socket da sessão "+Integer.parseInt(session.getId(),16));
        sessionsById.remove(session);
        playersAssociation.remove(session);
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.warning("Finalizando socket da sessão "+Integer.parseInt(session.getId(),16)+" devido a um erro."+throwable);
        sessionsById.remove(session);
        playersAssociation.remove(session);

    }



    private Boolean checkIfUserHasSessionActive(Map<Session,String> sessionsById,String userName){
        ArrayList<Map.Entry<Session, String>> lista = new ArrayList<>(sessionsById.entrySet());
        for (Map.Entry<Session, String> sessionStringEntry : lista) {
            if (userName.equals(sessionStringEntry.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfThereIsPlayer1Waiting(Map<Session,Session> playersAssociation,Session session) {
        ArrayList<Map.Entry<Session, Session>> lista = new ArrayList<>(playersAssociation.entrySet());

        for (Map.Entry<Session, Session> association : lista) {
            if (association.getKey().equals(association.getValue())) {
                association.setValue(session);
                return true;
            }
        }
        return false;
    }

    private Map.Entry<Session, Session> getCurrentAssociation(Map<Session,Session> playersAssociation, Session session){
        ArrayList<Map.Entry<Session, Session>> lista = new ArrayList<>(playersAssociation.entrySet());
        for (Map.Entry<Session, Session> association : lista) {
            if (session.equals(association.getKey()) || session.equals(association.getValue())) {
                return association;
            }
        }
        return null;
    }



}

