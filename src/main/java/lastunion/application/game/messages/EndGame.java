package lastunion.application.game.messages;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EndGame extends BaseMessage{

    String status;

    public EndGame(String status, ObjectMapper objectMapper)
    {
        super(objectMapper);
        this.status = status;
    }

    @Override
    public String getType() {
        return BaseMessage.class.getName();
    }
}
