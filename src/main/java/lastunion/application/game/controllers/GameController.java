package lastunion.application.game.controllers;

import lastunion.application.game.models.GameUserModel;
import lastunion.application.managers.UserManager;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class GameController {
    public enum ErrorCodes{
        ERROR,
        OK,
    }
    private GameUserModel gameUserModel;

    public ErrorCodes GameControllerInit(WebSocketSession session, UserManager userManager){
        final String userId = (String) session.getAttributes().get("userLogin");
        gameUserModel = new GameUserModel(session, userManager);
        if (userId == null) {
            return ErrorCodes.ERROR;
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

}
