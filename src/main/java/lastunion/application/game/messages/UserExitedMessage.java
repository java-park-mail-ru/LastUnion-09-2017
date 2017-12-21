package lastunion.application.game.messages;


public class UserExitedMessage extends BaseMessage {

    private String userName;

    public UserExitedMessage() { }

    public UserExitedMessage(String userName) {
        this.userName = userName;
    }

    @Override
    public String getType() {
        return UserAddedMessage.class.getName();
    }
}
