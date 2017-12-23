package lastunion.application.game.models.world_objects;

import lastunion.application.game.models.PositionModel;
import lastunion.application.game.models.ScoreModel;
import lastunion.application.game.models.ScoreModel;

public class BaseWorldObject {
	protected Integer x;
    protected BaseWorldObject() {}
    protected BaseWorldObject(Integer x) { this.x = x; }

    // check if collision commited (returns true) and performs
    public boolean Collission(PositionModel positionModel, ScoreModel scoreModel) {return false;}
    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }
}