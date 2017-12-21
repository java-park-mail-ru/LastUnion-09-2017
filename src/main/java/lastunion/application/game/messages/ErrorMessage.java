package lastunion.application.game.messages;


public class ErrorMessage extends BaseMessage {

    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }

    @Override
    public String getType() {
        return ErrorMessage.class.getName();
    }
}
