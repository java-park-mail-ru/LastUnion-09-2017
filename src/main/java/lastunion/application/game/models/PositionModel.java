package lastunion.application.game.models;

public class PositionModel {
    String userId;
    Point leftBottom;
    Point center;
    Point upRigh;
    Integer x;
    Integer y;
    Boolean stand;

    public PositionModel() {}

    public PositionModel(String userId, Integer x, Integer y, Boolean stand) {
        this.userId = userId;
        this.x = x;
        this.y = y;
        this.stand = stand;
    }
}
