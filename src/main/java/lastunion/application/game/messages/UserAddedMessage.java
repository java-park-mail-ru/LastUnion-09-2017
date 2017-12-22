package lastunion.application.game.messages;


public class UserAddedMessage extends BaseMessage {

    private String userName;

    public UserAddedMessage(String userName) {
        this.userName = userName;
    }

    public UserAddedMessage() { }

    @Override
    public String getType() {
        return UserAddedMessage.class.getName();
    }
}
