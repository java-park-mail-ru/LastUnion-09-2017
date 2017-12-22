package lastunion.application.game.messages;

import lastunion.application.game.models.PositionModel;
import org.springframework.security.access.method.P;

public class PositionMessage extends BaseMessage {
    PositionModel positionFirst;
    PositionModel positionSecond;

    public PositionMessage() {}
    public PositionMessage(PositionModel first, PositionModel second) {
        positionFirst = first;
        positionSecond = second;
    }

}
