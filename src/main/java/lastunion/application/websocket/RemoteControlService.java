package lastunion.application.websocket;

import lastunion.application.managers.UserManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class RemoteControlService {
    @NotNull
    private final UserManager userManager;
    public RemoteControlService(@NotNull UserManager userManager){
        this.userManager = userManager;
    }
}
