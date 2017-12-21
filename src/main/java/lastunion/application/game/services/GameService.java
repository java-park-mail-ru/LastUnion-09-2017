package lastunion.application.game.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.controllers.GameUserController;
import lastunion.application.game.messages.EndGame;
import lastunion.application.game.messages.ErrorMessage;
import lastunion.application.game.views.UserGameView;
import lastunion.application.managers.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import lastunion.application.game.controllers.GameController;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
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
    private GameController gameController;

    private static final Map<String, Integer> USER_GAME_CONTROLLER_ID = new ConcurrentHashMap<>();
    private static final Map<Integer, GameController> ID_GAME_CONTROLLER = new ConcurrentHashMap<>();

    public GameService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        createGame();
    }

    private void createGame() {
        gameController = new GameController(objectMapper);
    }

    private void startGame() {
        gameController.gameStart();
        int id = gameController.hashCode();
        while (ID_GAME_CONTROLLER.get(id) != null) {
            id++;
        }
        ID_GAME_CONTROLLER.put(id, gameController);
        final Vector<UserGameView> userList = gameController.getGameView().getList();
        for (UserGameView user: userList) {
            String userId = user.getUserId();
            USER_GAME_CONTROLLER_ID.put(userId, id);
        }
        resetGame();
    }

    private void resetGame() {
        gameController = new GameController(objectMapper);
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
        GameController.ErrorCodes errorController = gameController.addUser(userController);
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
        }
        GameController game = ID_GAME_CONTROLLER.get(gameControllerId);
        if (game != null) {
            EndGame endGame = new EndGame("user exit");
            game.sendMessageAll(endGame);
            game.closeConnections();
            ID_GAME_CONTROLLER.remove(gameControllerId);
        }
        return ResponseCode.OK;
    }

    public void addMessage(WebSocketSession webSocketSession, TextMessage message) {
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
        GameController game = ID_GAME_CONTROLLER.get(gameControllerId);
        if (game != null) {
            game.sendWithOut(message.getPayload(), userId);
        }
    }
}
