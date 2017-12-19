package lastunion.application.game.websocket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.ws.handler.HandlerException;
import lastunion.application.game.messages.CommandMessage;
import lastunion.application.game.messages.EndGame;
import lastunion.application.game.messages.ErrorMessage;
import lastunion.application.game.messages.Message;
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

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws AuthenticationException {
        gameService.addUser(webSocketSession);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        final String userId = (String) webSocketSession.getAttributes().get("userId");
        if (userId != null){
            gameService.removeUser(userId);
        }
        webSocketSession.close();
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage text) {
        gameService.addMessage(webSocketSession, text);
    }

    private void handleMessage(String userId, WebSocketSession webSocketSession, TextMessage text){
        /*final CommandMessage commandMessage;
        try {
            commandMessage = objectMapper.readValue(text.getPayload(), CommandMessage.class);
        } catch (IOException e){
            LOGGER.error("uncorrect message");
            return;
        }
        switch (commandMessage.getCommand()){
            case "EndSession": {
                endSession(userId, webSocketSession);
                break;
            }
            default: {
                LOGGER.error("wrong command");
                final ErrorMessage message = new ErrorMessage("Invalid command", objectMapper);
                try {
                    webSocketSession.sendMessage(new TextMessage(message.to_json()));
                } catch (IOException ignored) {
                    LOGGER.error("IOException", ignored);
                }
            }
        }*/

    }
//
//    private void endSession(String userId, WebSocketSession session) {
//        final GameService.ResponseCode codes = gameService.removeUser(userId);
//        switch (codes) {
//            case OK: {
//                final EndGame message = new EndGame("Ok", objectMapper);
//                try {
//                    session.sendMessage(new TextMessage(message.to_json()));
//                } catch (IOException ignored) {
//                    LOGGER.error("IOException", ignored);
//                }
//                break;
//            }
//        }
//    }

//        if (!webSocketSession.isOpen()) {
//            return;
//        }
//        MessageView message;
//        try {
//            String tmp = text.getPayload();
//            message = objectMapper.readValue(tmp, MessageView.class);
//        } catch (IOException ex){
//            return;
//        }
//        // Добавляем контроллер
//        if (message.getMessageType().compareTo("set_field")==0){
//
//            if (sessions.get(webSocketSession.getId()).equals(null)){
//                sessions.put(webSocketSession.getId(), webSocketSession);
//            }
//            if (sessions.get(message.getContent()).equals(null)){
//                return;
//            }
//            gamepads.put(webSocketSession.getId(), message.getContent());
//            try {
//                MessageView answer = new MessageView("gamepad_add", webSocketSession.getId());
//                TextMessage textMessage = new TextMessage(answer.toString());
//                sessions.get(message.getContent()).sendMessage(textMessage);
//                webSocketSession.sendMessage(textMessage);
//            } catch (IOException ex){
//                return;
//            }
//
//        }// Выполняем команду
//        else if (message.getMessageType().compareTo("do_it")==0){
//            String fieldId = gamepads.get(webSocketSession.getId());
//            WebSocketSession fieldSession = sessions.get(fieldId);
//            try {
//                fieldSession.sendMessage(text);
//            } catch (IOException ex){
//                return;
//            }
//        }// Создаем поле
//        else if (message.getMessageType().compareTo("create_field")==0){
//            if (sessions.get(webSocketSession.getId()).equals(null)){
//                sessions.put(webSocketSession.getId(), webSocketSession);
//            }
//            try {
//                MessageView answer = new MessageView("create_field", webSocketSession.getId());
//                TextMessage textMessage = new TextMessage(answer.toString());
//                webSocketSession.sendMessage(textMessage);
//            } catch (IOException ex){
//                return;
//            }
//        }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
