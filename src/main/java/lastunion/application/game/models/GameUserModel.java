package lastunion.application.game.models;

import lastunion.application.models.UserModel;
import lastunion.application.views.UserView;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.socket.WebSocketSession;
import lastunion.application.managers.UserManager;
import lastunion.application.game.views.UserGameView;


@SuppressWarnings("DefaultFileTemplate")
public class GameUserModel {
    public enum ErrorCodes {
        @SuppressWarnings("EnumeratedConstantNamingConvention")OK,
        INCORRECT_LOGIN,
        DATABASE_ERROR,
        SERVER_ERROR
    }

    private final WebSocketSession session;
    private UserView userView;
    private final UserManager userManager;

    public GameUserModel(WebSocketSession session, UserManager userManager) {
        this.userManager = userManager;
        this.session = session;
        this.userView = null;
    }

    public ErrorCodes GameUserModelInit(String userId) {
        final UserModel userModel = new UserModel();
        final UserManager.ResponseCode resp = userManager.getUserByName(userId, userModel);
        //noinspection EnumSwitchStatementWhichMissesCases
        switch (resp) {
            case OK: {
                break;
            }
            case INCORRECT_LOGIN: {
                return ErrorCodes.INCORRECT_LOGIN;
            }
            case DATABASE_ERROR: {
                return ErrorCodes.DATABASE_ERROR;
            }
            default: {
                return ErrorCodes.SERVER_ERROR;
            }
        }
        this.userView = userView;
        return ErrorCodes.OK;
    }

    public String getUserId() {
        return userView.getUserLogin();
    }

    public WebSocketSession getSession() {
        return session;
    }

    public UserView getUserModel() {
        return userView;
    }

    @Nullable
    public UserGameView getGameView() {
        if (userView == null) {
            return null;
        }
        final String name = userView.getUserLogin();
        return new UserGameView(name);
    }
}

