package lastunion.application.game.models.world_objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.models.GameSettings;
import lastunion.application.game.models.Point;
import org.jetbrains.annotations.Nullable;
import lastunion.application.game.models.PositionModel;
import lastunion.application.game.models.ScoreModel;


public class PitObstacle extends BaseWorldObject {
	protected Integer x;
	final private Integer Y = 480;
	final private Integer WIDTH = 100;
	final private Integer HEIGHT = 100;

    protected PitObstacle(Integer x) { this.x = x; }

    // check if collision commited (returns true) and performs
    public boolean Collission(PositionModel positionModel, ScoreModel scoreModel, GameSettings gameSettings) {
		// check spikes
		Point playerMidBottom = new Point(
			(positionModel.getBottomLeft().getX() + positionModel.getUpRight().getX())/2,
			positionModel.getBottomLeft().getY()
		);
							
		Point spikeCenterBottom = new Point(
			this.x+WIDTH/2,
			Y + WIDTH/2
		);
							
		if (this.x <= playerMidBottom.getX() && playerMidBottom.getX() <= this.x+WIDTH && playerMidBottom.getY() > Y+5) {
			scoreModel.setAlive(false);
			return true;
		}
		
		return false;
	}

    private double GetDistance(Point a, Point b) {
    	double dx = a.getX() - b.getX();
		double dy = a.getY() - b.getY();
		
		return Math.sqrt(dx*dx + dy*dy);
	}
}