package lastunion.application.game.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.controllers.GameUserController;
import lastunion.application.game.messages.CommandMessage;
import lastunion.application.game.messages.EndGame;
import lastunion.application.game.messages.ErrorMessage;
import lastunion.application.game.views.UserGameView;
import lastunion.application.managers.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import lastunion.application.game.controllers.GameTransportService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("DefaultFileTemplate")
@Service
public class GameService {

    public enum ResponseCode {
        ERROR,
        OK,
    }

    @Autowired
    private UserManager userManager;
    private ObjectMapper objectMapper;
    private static GameTransportService gameTransportService;
    private final Random random = new Random();

    static final int LENGTH = 10;
    static final int TYPES = 5;
    static final int DELTA = 256;

    private static final Map<String, Integer> USER_GAME_CONTROLLER_ID = new ConcurrentHashMap<>();
    private static final Map<Integer, GameTransportService> INTEGER_GAME_TRANSPORT_SERVICE_MAP = new ConcurrentHashMap<>();

    public GameService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        createGame();
    }

    private void createGame() {
        gameTransportService = new GameTransportService(objectMapper);
    }

    private void startGame() {
//        gameTransportService.gameStart();

        int id = gameTransportService.hashCode();
        while (INTEGER_GAME_TRANSPORT_SERVICE_MAP.get(id) != null) {
            id++;
        }
        INTEGER_GAME_TRANSPORT_SERVICE_MAP.put(id, gameTransportService);
        final ArrayList<UserGameView> userList = gameTransportService.getGameView().getList();
        for (UserGameView user: userList) {
            String userId = user.getUserId();
            USER_GAME_CONTROLLER_ID.put(userId, id);
        }
        resetGame();
    }

    private void resetGame() {
        gameTransportService = new GameTransportService(objectMapper);
    }

    public synchronized ResponseCode addUser(WebSocketSession userSession) {
        GameUserController userController = new GameUserController();
        GameUserController.ErrorCodes errorUser = userController.gameUserControllerInit(userSession, userManager);
        switch (errorUser) {
            case OK:
                break;
            default:
                return ResponseCode.ERROR;
        }
        if (USER_GAME_CONTROLLER_ID.get(userController.getUserId()) != null) {
            userController.sendMessageToUser(new ErrorMessage("Error"), objectMapper);
            userController.close();
            return ResponseCode.ERROR;
        }
        GameTransportService.ErrorCodes errorController = gameTransportService.addUser(userController);
        switch (errorController) {
            case OK:
                break;

            case READY_START:
                startGame();
                break;

            default:
                return ResponseCode.ERROR;

        }
        return ResponseCode.OK;
    }

    public synchronized ResponseCode removeUser(String userId) {
        Integer gameControllerId = USER_GAME_CONTROLLER_ID.get(userId);
        if (gameControllerId != null) {
            USER_GAME_CONTROLLER_ID.remove(userId);
        } else {
            gameTransportService.removeUser(userId);
            return ResponseCode.OK;
        }
        GameTransportService game = INTEGER_GAME_TRANSPORT_SERVICE_MAP.get(gameControllerId);
        // нужно удалить всех игроков с тем же GameController
        for (String usid: USER_GAME_CONTROLLER_ID.keySet()) {
            if (USER_GAME_CONTROLLER_ID.get(usid).intValue() == gameControllerId.intValue()) {
                USER_GAME_CONTROLLER_ID.remove(usid);
            }
        }
        if (game != null) {
            EndGame endGame = new EndGame("user exit");
            game.sendWithOut(endGame.to_json(objectMapper), userId);
            game.closeConnectionsWithOut(userId);
            INTEGER_GAME_TRANSPORT_SERVICE_MAP.remove(gameControllerId);
        }
        return ResponseCode.OK;
    }

    public void addMessage(WebSocketSession webSocketSession, TextMessage message) {
        CommandMessage commandMessage;
        try {
            commandMessage = objectMapper.readValue(message.getPayload(), CommandMessage.class);
        } catch (IOException exception) {
            ErrorMessage errorMessage = new ErrorMessage("Not valid message");
            try {
                webSocketSession.sendMessage(new TextMessage(errorMessage.to_json(objectMapper)));
            } catch (IOException ex) {
                return;
            }
            return;
        }
        String userId = (String) webSocketSession.getAttributes().get("userName");
        if (userId == null) {
            ErrorMessage errorMessage = new ErrorMessage("User error");
            try {
                webSocketSession.sendMessage(new TextMessage(errorMessage.to_json(objectMapper)));
            } catch (IOException ex) {
                return;
            }
            return;
        }
        Integer gameControllerId = USER_GAME_CONTROLLER_ID.get(userId);

        if (gameControllerId != null) {
            GameTransportService game = INTEGER_GAME_TRANSPORT_SERVICE_MAP.get(gameControllerId);
            if (game == null) {
                return;
            }
            if (commandMessage.getCommand().equals("Sequence")) {
                CommandMessage command = new CommandMessage("NewSequence", genWorldSeq());
                game.sendMessageAll(command);
            } else if (commandMessage.getCommand().equals("Ready")) {
                game.setStatus(userId, true);
                if (game.checkStatus()) {
                    CommandMessage command = new CommandMessage("Start", genWorldSeq());
                    game.sendMessageAll(command);
                    game.gameStart();
                }
            } else if (commandMessage.getCommand().equals("Do")) {
                game.addMessage(commandMessage, userId);
            }
            //else if (commandMessage.getCommand().equals("SetPosition")) {
            //  game.sendWithOut(commandMessage.to_json(objectMapper), userId);
            //}
        }
    }

    private String genWorldSeq() {
        int len = random.nextInt(LENGTH) + LENGTH; // length of sequence from 9 to 18
        String res = "";
        byte nextType = 0;
        byte delta = 0;
        for (int i = len; i >= 0; i--) {
            nextType = (byte) random.nextInt(TYPES); // types 0 - 4
            res = res + nextType;
            delta = (byte) random.nextInt(DELTA); // delta 0-255
            res = res + (char) delta;
        }
        return res;
    }
}
