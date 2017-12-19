package lastunion.application.game.messages;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GameReadyMessage extends BaseMessage{
    public GameReadyMessage(ObjectMapper objectMapper){
        super(objectMapper);
    }
    @Override
    public String getType(){
        return GameReadyMessage.class.getName();
    }
}
