package lastunion.application.game.messages;


public class EndGame extends BaseMessage {

    private String status;

    public EndGame(String status) {
        this.status = status;
    }

    @Override
    public String getType() {
        return BaseMessage.class.getName();
    }
}
