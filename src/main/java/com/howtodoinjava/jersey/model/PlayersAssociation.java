package com.howtodoinjava.jersey.model;

import javax.websocket.Session;

public class PlayersAssociation {
    private int idAssociation;
    private boolean hasPlayer1;
    private boolean hasPlayer2;
    private Session sessionPlayer1;
    private Session sessionPlayer2;

    public int getIdAssociation() {
        return idAssociation;
    }

    public void setIdAssociation(int idAssociation) {
        this.idAssociation = idAssociation;
    }

    public Session getSessionPlayer1() {
        return sessionPlayer1;
    }

    public void setSessionPlayer1(Session sessionPlayer1) {
        this.sessionPlayer1 = sessionPlayer1;
    }

    public Session getSessionPlayer2() {
        return sessionPlayer2;
    }

    public void setSessionPlayer2(Session sessionPlayer2) {
        this.sessionPlayer2 = sessionPlayer2;
    }

    public boolean isHasPlayer1() {
        return hasPlayer1;
    }

    public void setHasPlayer1(boolean hasPlayer1) {
        this.hasPlayer1 = hasPlayer1;
    }

    public boolean isHasPlayer2() {
        return hasPlayer2;
    }

    public void setHasPlayer2(boolean hasPlayer2) {
        this.hasPlayer2 = hasPlayer2;
    }
}
