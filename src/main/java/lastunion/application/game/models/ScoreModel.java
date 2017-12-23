package lastunion.application.game.models;

public class ScoreModel {
    String userId;
    Integer score;
    boolean isAlive;

    public ScoreModel() {
        this.score = 0;
        this.isAlive = true;
    }

    public ScoreModel(String userId) {
        this.userId = userId;
        this.score = 0;
        this.isAlive = true;
    }
    
    public ScoreModel(String userId, Integer score) {
        this.userId = userId;
        this.score = score;
        this.isAlive = true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void changeScore(Integer delta) {
        this.score += delta;
    }

    public Integer getScore() {
        return this.score;
    }
}
