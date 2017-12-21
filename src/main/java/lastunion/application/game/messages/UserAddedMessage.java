package lastunion.application.game.messages;


public class UserAddedMessage extends BaseMessage {

    private String userName;

    public UserAddedMessage(String userName) {
        this.userName = userName;
    }

    @Override
    public String getType() {
        return UserAddedMessage.class.getName();
    }
}
