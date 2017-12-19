package lastunion.application.game.messages;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserAddedMessage extends BaseMessage{
    String userName;
    public UserAddedMessage(String userName, ObjectMapper mapper){
        super(mapper);
        this.userName = userName;
    }

    @Override
    public String getType() {
        return UserAddedMessage.class.getName();
    }
}
