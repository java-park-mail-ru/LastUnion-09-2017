package lastunion.application.views;

import org.jetbrains.annotations.Nullable;

public final class ResponseCode<T> {
    private final boolean result;
    private final String responseMessage;
    private final T data;

    public ResponseCode(boolean result, String responseMessage, @Nullable T data) {
        this.result = result;
        this.responseMessage = responseMessage;
        this.data = data;
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
