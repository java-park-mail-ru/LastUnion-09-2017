package lastunion.application.game.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ErrorMessage extends BaseMessage{
    String error;

    public ErrorMessage(String error, ObjectMapper objectMapper)
    {
        super(objectMapper);
        this.error = error;
    }

    @Override
    public String getType() {
        return ErrorMessage.class.getName();
    }
}
