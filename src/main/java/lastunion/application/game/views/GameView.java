package lastunion.application.game.views;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

public class GameView {
    private final ArrayList<UserGameView> list;

    public GameView(ObjectMapper mapper, ArrayList<UserGameView> list) {
        this.list = list;
    }

    public ArrayList<UserGameView> getList() {
        return list;
    }
}
