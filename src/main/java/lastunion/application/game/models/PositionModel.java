package lastunion.application.game.models;

public class PositionModel {
    String userId;
    Point bottomLeft;
    Point upRight;
    Boolean stand;
    Double jumpTime;
    Double jumpLambda;
    Integer acceleration;
    Integer jumpPower;
    Integer height;

    public PositionModel() {
        this.init();
    }

    public PositionModel(String userId, Boolean stand) {
        this.userId = userId;
        this.stand = stand;
        this.init();
    }

    public PositionModel(String userId, Point bottomLeft, Point upRight, Integer x, Integer y, Boolean stand) {
        this.userId = userId;
        this.bottomLeft = bottomLeft;
        this.upRight = upRight;
        this.stand = stand;
        this.height = Math.abs(upRight.getY() - bottomLeft.getY());
        this.init();
    }

    public void init() {
        this.jumpTime = 0.0;
        this.jumpLambda = 0.0;
        this.acceleration = 10;
        this.jumpPower = 33;
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

    public void jump() {
        if(jumpTime == 0) {
            jumpLambda = 0.0;
        }

        jumpLambda = jumpPower * jumpTime - (acceleration * Math.pow(jumpTime, 2) / 2) - jumpLambda;
        jumpTime += 0.8;

        if(getMidBottom().getY() + jumpLambda < 0) {
            bottomLeft.setY(0);
            upRight.setY(height);
            jumpTime = 0.0;
            return;
        }
        bottomLeft.setY(bottomLeft.getY() + jumpLambda.intValue());
        upRight.setY(upRight.getY() + jumpLambda.intValue());
    }
}
