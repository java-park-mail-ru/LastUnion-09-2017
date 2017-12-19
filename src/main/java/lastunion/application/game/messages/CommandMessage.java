package lastunion.application.game.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandMessage extends BaseMessage{
    private String command;
    private String arguments;

    public CommandMessage(String command, String arguments, ObjectMapper objectMapper)
    {
        super(objectMapper);
        this.command = command;
        this.arguments = arguments;
    }

    @Override
    public String getType() {
        return ErrorMessage.class.getName();
    }
    public String getCommand(){
        return command;
    }
    public String getArguments(){
        return arguments;
    }
}
