package lastunion.application.game.views;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Vector;

public class GameView{
    private final Vector<UserGameView> list;
    public GameView(ObjectMapper mapper, Vector<UserGameView> list) {
        this.list = list;
    }

    public Vector<UserGameView> getList() {
        return list;
    }

}
