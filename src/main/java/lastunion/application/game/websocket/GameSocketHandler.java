package lastunion.application.game.websocket;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


import lastunion.application.game.services.GameService;
import lastunion.application.game.views.MessageView;
import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("DefaultFileTemplate")
public class GameSocketHandler extends TextWebSocketHandler {
    @SuppressWarnings({"SpringJavaAutowiredMembersInspection", "SpringAutowiredFieldsWarningInspection"})
    @Autowired
    private
    GameService gameService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static Map<String, String> gamepads = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws AuthenticationException {
        sessions.put(webSocketSession.getId(), webSocketSession);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        if (sessions.get(webSocketSession.getId())!=null){
            sessions.remove(webSocketSession.getId());
        }
        if (gamepads.get(webSocketSession.getId())!=null){
            gamepads.remove(webSocketSession.getId());
        }
        webSocketSession.close();
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage text) {
        if (!webSocketSession.isOpen()) {
            return;
        }
        MessageView message;
        try {
            String tmp = text.getPayload();
            message = objectMapper.readValue(tmp, MessageView.class);
        } catch (IOException ex){
            return;
        }
        // Добавляем контроллер
        if (message.getMessageType().compareTo("set_field")==0){

            if (sessions.get(webSocketSession.getId()).equals(null)){
                sessions.put(webSocketSession.getId(), webSocketSession);
            }
            if (sessions.get(message.getContent()).equals(null)){
                return;
            }
            gamepads.put(webSocketSession.getId(), message.getContent());
            try {
                MessageView answer = new MessageView("gamepad_add", webSocketSession.getId());
                TextMessage textMessage = new TextMessage(answer.toString());
                sessions.get(message.getContent()).sendMessage(textMessage);
                webSocketSession.sendMessage(textMessage);
            } catch (IOException ex){
                return;
            }

        }// Выполняем команду
        else if (message.getMessageType().compareTo("do_it")==0){
            String fieldId = gamepads.get(webSocketSession.getId());
            WebSocketSession fieldSession = sessions.get(fieldId);
            try {
                fieldSession.sendMessage(text);
            } catch (IOException ex){
                return;
            }
        }// Создаем поле
        else if (message.getMessageType().compareTo("create_field")==0){
            if (sessions.get(webSocketSession.getId()).equals(null)){
                sessions.put(webSocketSession.getId(), webSocketSession);
            }
            try {
                MessageView answer = new MessageView("create_field", webSocketSession.getId());
                TextMessage textMessage = new TextMessage(answer.toString());
                webSocketSession.sendMessage(textMessage);
            } catch (IOException ex){
                return;
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
