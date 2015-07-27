package me.svv.fireside;

import javax.websocket.Session;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by svanvliet on 7/26/2015.
 */
public class SessionHandler {

    private final static Logger LOGGER = Logger.getLogger(FiresideServer.LOGGER_KEY);
    private final static String DSN_KEY = "DSN";

    private Map<String, Session> _sessions = new LinkedHashMap<String, Session>();

    /**
     *
     * @param session
     */
    public void addSession(Session session) throws IllegalArgumentException {

        String queryString = session.getQueryString();
        Map<String, List<String>> keyValues = null;

        if (queryString == null) {
            closeSessionWithoutDSN(session);
            return;
        }

        try {
            keyValues = parseQueryString(queryString);
        } catch (UnsupportedEncodingException e) {
            closeSessionWithoutDSN(session);
        }

        List<String> dsnValues = keyValues.get(DSN_KEY);

        if (dsnValues.size() > 0) {
            String dsn = dsnValues.get(0);
            _sessions.put(session.getId(), session);
        } else{
            closeSessionWithoutDSN(session);
        }
    }

    /**
     *
     * @param session
     */
    private void closeSessionWithoutDSN(Session session) {
        LOGGER.info("Session initiated without DSN specified, terminating session.");
        try {
            session.close();
        } catch (IOException io) {
            LOGGER.info(String.format("Unable to close the session with ID %s: %s", session.getId(), io));
        }
    }

    /**
     *
     * @param session
     */
    public void removeSession(Session session) {
        _sessions.remove(session);
    }

    private static Map<String, List<String>> parseQueryString(String queryString) throws UnsupportedEncodingException {
        final Map<String, List<String>> keyValues = new LinkedHashMap<String, List<String>>();
        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!keyValues.containsKey(key)) {
                keyValues.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            keyValues.get(key).add(value);
        }
        return keyValues;
    }

}
