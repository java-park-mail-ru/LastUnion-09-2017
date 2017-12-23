package lastunion.application.game.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.models.PositionModel;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.access.method.P;

public class PositionMessage {
    PositionModel positionFirst;
    PositionModel positionSecond;

    @Nullable
    @JsonIgnore
    public String to_json(ObjectMapper objectMapper) {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException ignored) {
            return null;
        }
        return result;
    }

    public PositionMessage() {}
    public PositionMessage(PositionModel first, PositionModel second) {
        positionFirst = first;
        positionSecond = second;
    }

}
