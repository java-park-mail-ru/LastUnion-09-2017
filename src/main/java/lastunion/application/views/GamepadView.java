package lastunion.application.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GamepadView implements AbstractView {
    private final String message;

    @SuppressWarnings("unused")
    @JsonCreator
    GamepadView(@JsonProperty("message") String message) {
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }

    @Override
    public final boolean isFilled() {
        return message != null;
    }

    @Override
    public final boolean isValid() {
        if (!isFilled()) {
            return false;
        }
        return true;
    }
}
