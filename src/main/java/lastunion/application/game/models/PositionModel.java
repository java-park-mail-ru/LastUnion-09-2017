package lastunion.application.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PositionModel {
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String userId;
    Point bottomLeft;
    Point upRight;
    @JsonIgnore
    Boolean stand;
    @JsonIgnore
    Double jumpTime;
    @JsonIgnore
    Double jumpLambda;
    @JsonIgnore
    Integer acceleration;
    @JsonIgnore
    Integer jumpPower;
    @JsonIgnore
    Integer height;
    @JsonIgnore
    Boolean needJump;

    public PositionModel() {
        this.init();
    }

    public PositionModel(String userId, Boolean stand) {
        this.userId = userId;
        this.stand = stand;
        this.init();
    }

    public PositionModel(String userId, Point bottomLeft, Point upRight) {
        this.userId = userId;
        this.bottomLeft = bottomLeft;
        this.upRight = upRight;
        this.height = Math.abs(upRight.getY() - bottomLeft.getY());
        this.init();
    }

    public void init() {
        this.jumpTime = 0.0;
        this.jumpLambda = 0.0;
        this.acceleration = 10;
        this.jumpPower = 33;
        this.needJump = false;
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

    public void initJump() {
        needJump = true;
    }

    public void finishJump() {
        needJump = false;
    }

    public void jump() {
        if(needJump) {
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
}
