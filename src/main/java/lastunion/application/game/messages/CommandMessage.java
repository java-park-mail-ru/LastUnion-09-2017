package lastunion.application.game.messages;


public class CommandMessage extends BaseMessage {

    private String command;
    private String arguments;

    public CommandMessage(String command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public CommandMessage() { }

    @Override
    public String getType() {
        return ErrorMessage.class.getName();
    }

    public String getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }
}
