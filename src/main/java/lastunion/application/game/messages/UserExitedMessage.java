package lastunion.application.game.messages;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserExitedMessage extends BaseMessage{
    String userName;
    public UserExitedMessage(String userName, ObjectMapper mapper){
        super(mapper);
        this.userName = userName;
    }

    @Override
    public String getType() {
        return UserAddedMessage.class.getName();
    }
}
