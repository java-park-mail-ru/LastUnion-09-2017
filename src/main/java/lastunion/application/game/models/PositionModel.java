package lastunion.application.game.models;

public class PositionModel {
    String userId;
    Point bottomLeft;
    Point upRight;
    Boolean stand;

    public PositionModel() {}

    public PositionModel(String userId, Boolean stand) {
        this.userId = userId;
        this.stand = stand;
    }

    public PositionModel(String userId, Point bottomLeft, Point center, Point upRight, Integer x, Integer y, Boolean stand) {
        this.userId = userId;
        this.bottomLeft = bottomLeft;
        this.upRight = upRight;
        this.stand = stand;
    }

    public Point getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(Point leftBottom) {
        this.bottomLeft = leftBottom;
    }

    public Point getCenter() {
        return new Point((bottomLeft.getX() + upRight.getX()) / 2 , (bottomLeft.getY() + upRight.getY()) / 2);
    }

    public Point getMidBottom() {
        return new Point((bottomLeft.getX() + upRight.getX()) / 2, bottomLeft.getY());
    }

    public void changeX(Integer delta) {
        bottomLeft.setX(bottomLeft.getX() + delta);
        upRight.setX(upRight.getX() + delta);
    }

    public Point getUpRight() {
        return upRight;
    }

    public void bend() {
        bottomLeft.setY((upRight.getY() - bottomLeft.getY()) / 2 + bottomLeft.getY());
    }

    public void standUp() {
        upRight.setY((upRight.getY() - bottomLeft.getY()) * 2 + bottomLeft.getY());
    }

    public void setUpRight(Point upRight) {
        this.upRight = upRight;
    }
}
