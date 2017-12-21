package lastunion.application.game.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;


public class BaseMessage {

    protected BaseMessage() { }

    @SuppressWarnings("unused")
    public String getType() {
        return BaseMessage.class.getName();
    }

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

}