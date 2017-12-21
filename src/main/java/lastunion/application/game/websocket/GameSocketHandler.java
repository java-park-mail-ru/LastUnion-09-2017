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
        gameService.addUser(webSocketSession);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        final String userId = (String) webSocketSession.getAttributes().get("userName");
        if (userId != null) {
            gameService.removeUser(userId);
        }
        webSocketSession.close();
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage text) {
        gameService.addMessage(webSocketSession, text);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
