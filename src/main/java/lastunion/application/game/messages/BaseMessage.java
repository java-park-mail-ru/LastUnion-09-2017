package lastunion.application.game.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;


public class BaseMessage {
    @JsonIgnore
    private final ObjectMapper objectMapper;

    protected BaseMessage(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return BaseMessage.class.getName();
    }

    @Nullable
    @JsonIgnore
    public String to_json() {
        String result = null;
        try {
            result = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException ignored) {

        }
        return result;
    }

}