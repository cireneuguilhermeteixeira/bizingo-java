package com.howtodoinjava.jersey.socket.chat;


import com.howtodoinjava.jersey.model.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.logging.Logger;
import java.util.*;
import static java.util.Collections.synchronizedMap;
import static java.util.stream.Stream.of;


@ServerEndpoint(value = "/ws/chat/{userName}/{playerType}", encoders = EncoderChat.class, decoders = DecoderChat.class)
public class ChatEndPoint {


    private final static Logger logger = Logger.getLogger(ChatEndPoint.class.getName());
    private static Map<Session,String> sessionsById = synchronizedMap(new HashMap<>());
    private static Map<Session, Session> playersAssociation = synchronizedMap(new HashMap<>());
    private Message message = new Message();

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") final String userName,
                       @PathParam("playerType") final String playerType)
            throws IOException, EncodeException {

        logger.info("Iniciando socket com usuário de id "+userName+"  na sessão ["+Integer.parseInt(session.getId(),16)+"].");
        if (this.checkIfUserHasSessionActive(sessionsById,userName)) {
            logger.warning("Opa, você já está conectado em outra sessão.");
            throw new StreamCorruptedException();
        }else {
               sessionsById.put(session,userName);
        }


        if(playerType.equals("1")){
            //Aguardando um player 2;
            playersAssociation.put(session, session);
            message.setTypeMessage("systemMessageWaiting");
            message.setUserName("Sistema");
            message.setMessage("Aguarde alguém para jogar com você. ;)");
            session.getBasicRemote().sendObject(message);

        }else{
            if(!this.checkIfThereIsPlayer1Waiting(playersAssociation,session)){
                logger.warning("Nenhum player buscando partida.");
                message.setTypeMessage("systemMessageNoPlayer1Waiting");
                message.setMessage("Não existe nenhuma sala esperando por outro player.");
                session.getBasicRemote().sendObject(message);

            }else{
                //comunica ao oponente
                message.setTypeMessage("systemMessageStart");
                message.setUserName(userName);
                message.setMessage("Cheguei, vamos começar...");
                this.onMessage(session,message);

                //comunica a si mesmo na parte do cliente
                message.setUserName("Sistema");
                message.setMessage("Bem vindo, converse com seu oponente...");
                session.getBasicRemote().sendObject(message);

            }
        }

    }

    @OnMessage
    public void onMessage(Session session, Message message) throws IOException, EncodeException {
        logger.info("Na Sessão ["+Integer.parseInt(session.getId(),16)+"] - Enviando mensagem: ");

        Map.Entry<Session, Session> currentAssociation = this.getCurrentAssociation(playersAssociation,session);
        if(currentAssociation != null) {
            if (currentAssociation.getKey().equals(currentAssociation.getValue())) {
                logger.warning("Aguarde  o player 2");
                message = new Message();
                message.setUserName("Sistema");
                message.setMessage("Aguarde  o player 2.");
                session.getBasicRemote().sendObject(message);

            } else if (session.equals(currentAssociation.getKey())) {
                logger.info("Player 1 envia pro Player 2");
                currentAssociation.getValue().getBasicRemote().sendObject(message);
            } else if (session.equals(currentAssociation.getValue())) {

                logger.info("Player 2 envia pro Player 1");
                currentAssociation.getKey().getBasicRemote().sendObject(message);
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

