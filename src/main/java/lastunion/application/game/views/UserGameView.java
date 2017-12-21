package lastunion.application.game.views;

@SuppressWarnings("DefaultFileTemplate")
public class UserGameView {
    private final String userName;

    public UserGameView(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return this.userName;
    }

    @SuppressWarnings("unused")
    public String getUserName() {
        return this.userName;
    }
}
