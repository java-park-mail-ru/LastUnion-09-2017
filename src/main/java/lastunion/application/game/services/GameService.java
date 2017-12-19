package lastunion.application.game.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.controllers.GameUserController;
import lastunion.application.game.messages.EndGame;
import lastunion.application.game.messages.ErrorMessage;
import lastunion.application.game.views.UserGameView;
import lastunion.application.managers.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);
    @Autowired
    private UserManager userManager;
    private ObjectMapper objectMapper;
    private GameController gameController;

    private static final Map <String, Integer> userGameControllerId = new ConcurrentHashMap<>();
    private static final Map <Integer, GameController> idGameController = new ConcurrentHashMap<>();
    public GameService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        createGame();
    }

    private void createGame(){
        gameController = new GameController(objectMapper);
    }

    private void startGame(){
        gameController.gameStart();
        int id = gameController.hashCode();
        while (idGameController.get(id)!=null) id++;
        idGameController.put(id, gameController);
        final Vector<UserGameView> userList = gameController.getGameView().getList();
        for (UserGameView user: userList){
            String userId = user.getUserId();
            userGameControllerId.put(userId, id);
        }
        resetGame();
    }

    private void resetGame(){
        gameController = new GameController(objectMapper);
    }

    public synchronized ResponseCode addUser(WebSocketSession userSession) {
        GameUserController userController = new GameUserController();
        GameUserController.ErrorCodes errorUser = userController.gameUserControllerInit(userSession, userManager);
        switch (errorUser){
            case OK: {
                break;
            }
            default:{
                return ResponseCode.ERROR;
            }
        }
        if (userGameControllerId.get(userController.getUserId())!=null){
            userController.sendMessageToUser(new ErrorMessage("Error", objectMapper));
            userController.close();
            return ResponseCode.ERROR;
        }
        GameController.ErrorCodes errorController = gameController.addUser(userController);
        switch (errorController){
            case OK: {
                break;
            }
            case READY_START: {
                startGame();
                break;
            }
            case ERROR: {
                return ResponseCode.ERROR;
            }
        }
        return ResponseCode.OK;
    }
    public synchronized ResponseCode removeUser(String userId) {
        Integer gameControllerId = userGameControllerId.get(userId);
        if (gameControllerId != null){
            userGameControllerId.remove(userId);
        }
        GameController game = idGameController.get(gameControllerId);
        if (game != null){
            Vector<UserGameView> usersList = game.getGameView().getList();
            EndGame endGame = new EndGame("user exit", objectMapper);
            game.sendMessageAll(endGame);
            game.closeConnections();
            idGameController.remove(gameControllerId);
        }
        return ResponseCode.OK;
    }

    public void addMessage(WebSocketSession webSocketSession, TextMessage message){
        String userId = (String) webSocketSession.getAttributes().get("userName");
        if (userId == null){
            ErrorMessage errorMessage = new ErrorMessage("User error", objectMapper);
            try {
                webSocketSession.sendMessage(new TextMessage(errorMessage.to_json()));
            } catch (IOException ex){
            }
            return;
        }
        Integer gameControllerId = userGameControllerId.get(userId);
        GameController game = idGameController.get(gameControllerId);
        if(game != null){
            game.sendWithOut(message.getPayload(), userId);
        }

    }

}
