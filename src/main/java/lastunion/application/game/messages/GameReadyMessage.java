package lastunion.application.game.messages;


public class GameReadyMessage extends BaseMessage {

    public GameReadyMessage() { }

    @Override
    public String getType() {
        return GameReadyMessage.class.getName();
    }
}
