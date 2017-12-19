package lastunion.application.game.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jetbrains.annotations.NotNull;


@JsonIgnoreProperties(ignoreUnknown=true)
public class MessageView {
    @JsonProperty("messageType")
    private String messageType;
    @JsonProperty("content")
    private String content;
    public MessageView(@NotNull String messageType, @NotNull String content){
        this.messageType = messageType;
        this.content = content;
    }
    public MessageView(){}
    public String getContent() {
        return content;
    }

    public String getMessageType() {
        return messageType;
    }

    @Override
    public String toString(){
        return "{\""+ messageType +"\":\""+ content +"\"}";
    }
}
