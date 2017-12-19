package lastunion.application.game.controllers;

import lastunion.application.game.messages.BaseMessage;
import lastunion.application.game.models.GameUserModel;
import lastunion.application.game.views.UserGameView;
import lastunion.application.managers.UserManager;
import lastunion.application.models.UserModel;
import lastunion.application.views.UserView;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class GameUserController {
    public enum ErrorCodes {
        OK,
        ERROR,
    }

    private GameUserModel gameUserModel;

    public ErrorCodes gameUserControllerInit(WebSocketSession webSocketSession, UserManager userManager) {
        final String userId = (String) webSocketSession.getAttributes().get("userLogin");
        gameUserModel = new GameUserModel(webSocketSession, userManager);
        if (userId == null) {
            return ErrorCodes.ERROR;
        }
        final GameUserModel.ErrorCodes result = gameUserModel.GameUserModelInit(userId);
        switch (result) {
            case OK: {
                break;
            }
            default: {
                return ErrorCodes.ERROR;
            }
        }
        return ErrorCodes.OK;
    }

    @Nullable
    public String getUserId() {
        if (gameUserModel == null) {
            return null;
        }
        return gameUserModel.getUserId();
    }

    public ErrorCodes sendMessageToUser(String msg) {
        if (gameUserModel == null) {
            return ErrorCodes.ERROR;
        }
        final WebSocketSession session = gameUserModel.getSession();
        if (session == null) {
            return ErrorCodes.ERROR;
        }
        try {
            session.sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            return ErrorCodes.ERROR;
        }
        return ErrorCodes.OK;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ErrorCodes sendMessageToUser(BaseMessage message) {
        final String result = message.to_json();
        if (result == null) {
            return ErrorCodes.ERROR;
        }
        return sendMessageToUser(result);
    }

    @Nullable
    public UserView getUserDataView() {
        if (gameUserModel == null) {
            return null;
        }
        final UserView info = gameUserModel.getUserModel();
        return info;
    }

    @Nullable
    public UserGameView getGameView() {
        if (gameUserModel == null) {
            return null;
        }

        return gameUserModel.getGameView();
    }

    public void close() {
        if (gameUserModel != null) {
            final WebSocketSession webSocketSession = gameUserModel.getSession();
            try {
                webSocketSession.close();
            } catch (IOException ignored) {

            }
        }
    }
}
