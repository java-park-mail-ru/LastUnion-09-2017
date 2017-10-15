package lastunion.application.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

public final class ResponseCode<T> {
    private final boolean result;
    private final String responseMessage;
    private final T data;

    @JsonCreator
    public ResponseCode(@JsonProperty("result") boolean result,
                        @JsonProperty("responseMessage") String responseMessage,
                        @JsonProperty("data") T data) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.data = data;
    }

    @JsonCreator
    public ResponseCode(@JsonProperty("result") boolean result,
                        @JsonProperty("responseMessage") String responseMessage) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.data = null;
    }

    @SuppressWarnings("unused")
    public boolean getResult() {
        return result;
    }

    @SuppressWarnings("unused")
    public String getResponseMessage() {
        return responseMessage;
    }

    @Nullable
    @SuppressWarnings({"unused", "MissortedModifiers"})
    public T getData() {
        return data;
    }
}
