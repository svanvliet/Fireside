package me.svv.fireside;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Logger;

/**
 * Created by svanvliet on 7/26/2015.
 */
@ApplicationScoped
@ServerEndpoint("/fireside-main")
public class FiresideServer {

    public final static String LOGGER_KEY = "FiresideServer";
    private final static Logger LOGGER = Logger.getLogger(FiresideServer.LOGGER_KEY);

    @Inject
    private SessionHandler sessionHandler;

    @OnOpen
    public void open(Session session) {
        LOGGER.info(String.format("Session opened for %s", session.getId()));
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        LOGGER.info(String.format("Session closed for %s", session.getId()));
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {
        LOGGER.severe(String.format("An error occurred: %s", error));
    }

    @OnMessage
    public void handleMessage(String message, Session session) {

        LOGGER.info(String.format("Message received for %s: %s", session.getId(), message));

        try {
            session.getBasicRemote().sendText(String.format("Received your message as \"%s\"", message));
        } catch (Exception e) {
            LOGGER.severe(e.toString());
        }

    }
}
