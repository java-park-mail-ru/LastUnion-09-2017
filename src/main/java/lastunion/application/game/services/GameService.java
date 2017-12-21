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
    private GameTransportService gameTransportService;
    private final Random random = new Random();

    static final int LENGTH = 10;
    static final int TYPES = 5;
    static final int DELTA = 256;

    private final Map<String, Integer> userGameControllerId = new ConcurrentHashMap<>();
    private final Map<Integer, GameTransportService> idGameController = new ConcurrentHashMap<>();

    public GameService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        createGame();
    }

    private void createGame() {
        gameTransportService = new GameTransportService(objectMapper);
    }

    private void startGame() {
        gameTransportService.gameStart();
        int id = gameTransportService.hashCode();
        while (idGameController.get(id) != null) {
            id++;
        }
        idGameController.put(id, gameTransportService);
        final ArrayList<UserGameView> userList = gameTransportService.getGameView().getList();
        for (UserGameView user: userList) {
            String userId = user.getUserId();
            userGameControllerId.put(userId, id);
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
        if (userGameControllerId.get(userController.getUserId()) != null) {
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
        Integer gameControllerId = userGameControllerId.get(userId);
        if (gameControllerId != null) {
            userGameControllerId.remove(userId);
        }
        GameTransportService game = idGameController.get(gameControllerId);
        if (game != null) {
            EndGame endGame = new EndGame("user exit");
            game.sendMessageAll(endGame);
            game.closeConnections();
            idGameController.remove(gameControllerId);
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
        Integer gameControllerId = userGameControllerId.get(userId);
        GameTransportService game = idGameController.get(gameControllerId);
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
            }
        } else if (commandMessage.getCommand().equals("SetPosition")) {
            game.sendWithOut(commandMessage, userId);
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
