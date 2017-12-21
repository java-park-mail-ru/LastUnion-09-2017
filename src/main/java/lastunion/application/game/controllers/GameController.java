package lastunion.application.game.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.messages.BaseMessage;
import lastunion.application.game.messages.GameReadyMessage;
import lastunion.application.game.messages.UserAddedMessage;
import lastunion.application.game.messages.UserExitedMessage;
import lastunion.application.game.views.GameView;
import lastunion.application.game.views.UserGameView;
import lastunion.application.models.UserModel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class GameController {
    public enum ErrorCodes {
        ERROR,
        READY_START,
        OK,
    }

    private final Map<String, GameUserController> users;
    private final ObjectMapper mapper;

    public GameController(ObjectMapper mapper) {
        this.mapper = mapper;
        this.users = new ConcurrentHashMap<>();
    }

    public synchronized ErrorCodes addUser(@NotNull GameUserController user) {
        final String userId = user.getUserId();
        if (userId == null) {
            return ErrorCodes.ERROR;
        }
        if (users.containsKey(userId)) {
            return ErrorCodes.ERROR;
        }
        users.put(userId, user);
        if (users.size() == 2) {
            return ErrorCodes.READY_START;
        }
        sendMessageAll(new UserAddedMessage(user.getUserDataView().getUserName()));
        return ErrorCodes.OK;
    }

    public ErrorCodes removeUser(@NotNull String userId) {
        if (!users.containsKey(userId)) {
            return ErrorCodes.ERROR;
        }
        final GameUserController user = users.get(userId);
        users.remove(userId);
        final UserModel userModel = user.getUserDataView();
        user.close();
        return sendMessageAll(new UserExitedMessage(userModel.getUserName()));
    }

    @SuppressWarnings({"UnusedReturnValue", "SameReturnValue"})
    public ErrorCodes closeConnections() {
        for (Map.Entry<String, GameUserController> entry : users.entrySet()) {
            final GameUserController user = entry.getValue();
            user.close();
        }
        return ErrorCodes.OK;
    }

    private ErrorCodes sendMessageAll(String msg) {
        for (Map.Entry<String, GameUserController> entry : users.entrySet()) {
            final GameUserController user = entry.getValue();
            final GameUserController.ErrorCodes err = user.sendMessageToUser(msg);
            switch (err) {
                case OK:
                    break;

                default:
                    return ErrorCodes.ERROR;

            }
        }
        return ErrorCodes.OK;
    }

    public ErrorCodes sendMessageAll(BaseMessage baseMessage) {
        final String result = baseMessage.to_json(mapper);
        if (result == null) {
            return ErrorCodes.ERROR;
        }
        return sendMessageAll(result);
    }

    public ErrorCodes sendWithOut(String msg, String userId) {
        for (Map.Entry<String, GameUserController> entry : users.entrySet()) {
            final GameUserController user = entry.getValue();
            if (user.getUserId().equals(userId)) {
                final GameUserController.ErrorCodes err = user.sendMessageToUser(msg);
                switch (err) {
                    case OK:
                        break;

                    default:
                        return ErrorCodes.ERROR;

                }
            }
        }
        return ErrorCodes.OK;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ErrorCodes gameStart() {
        return sendMessageAll(new GameReadyMessage());
    }

    public GameView getGameView() {
        final Vector<UserGameView> userList = new Vector<>();
        for (GameUserController tab : users.values()) {
            final UserGameView view = tab.getGameView();
            userList.add(view);
        }
        return new GameView(mapper, userList);
    }
}
