package lastunion.application.game.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@SuppressWarnings("DefaultFileTemplate")
@Service
public class GameService {
    public enum ErrorCodes{
        ERROR,
        OK,
    }
    public synchronized ErrorCodes addUser(WebSocketSession userSession) {
        return ErrorCodes.ERROR;
    }
    public synchronized ErrorCodes removeUser(String UserId) {
        return ErrorCodes.ERROR;
    }
    public GameService() {
    }

}
