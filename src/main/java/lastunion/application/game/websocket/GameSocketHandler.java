package lastunion.application.game.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



import lastunion.application.game.services.GameService;

import javax.naming.AuthenticationException;
import java.io.IOException;

/**
 * Created by ksg on 11.04.17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class GameSocketHandler extends TextWebSocketHandler {
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringAutowiredFieldsWarningInspection"})
    @Autowired
    private
    GameService gameService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws AuthenticationException {
        LOGGER.debug("connection establisher handler");
        gameService.addUser(webSocketSession);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

        final String userId = (String) webSocketSession.getAttributes().get("userLogin");
        final Boolean rejected = (Boolean) webSocketSession.getAttributes().get("rejected");
        if ((userId != null) && (rejected == null)) {
            gameService.removeUser(userId);
        }
        webSocketSession.close();
        //TODO: sercer closed connection status?
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        final String userId = (String) session.getAttributes().get("userLogin");
        if (userId == null) {
            try {
                session.sendMessage(new TextMessage(text_msg));
            } catch (IOException ignored) {
                LOGGER.error("IOException", ignored);
            }
        } else {
            handleMessage(userId, session, message);
        }
    }

    private void handleMessage(String userId, WebSocketSession session, TextMessage text) {


    }

    private void endSession(String userId, WebSocketSession session) {
        final GameService.ErrorCodes codes = gameService.removeUser(userId);

        switch (codes) {

        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
