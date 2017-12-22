package lastunion.application.game.models;

public class Point {
    private Integer x;
    private Integer y;

    public Point() {
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Point(Integer x, Integer y) {

        this.x = x;
        this.y = y;
    }
}
