package lastunion.application.game.models.world_objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.models.GameSettings;
import lastunion.application.game.models.Point;
import org.jetbrains.annotations.Nullable;
import lastunion.application.game.models.PositionModel;
import lastunion.application.game.models.ScoreModel;

// Its maybe not work
public class UpperObstacle extends BaseWorldObject {
	private final Integer Y = 295;
	private final Integer WIDTH = 50;
	private final Integer HEIGHT = 100;
	protected Integer x;

    public UpperObstacle(Integer x) { this.x = x; }
    // check if collision commited (returns true) and performs

    public boolean Collission(PositionModel positionModel, ScoreModel scoreModel, GameSettings gameSettings) {
    	Point playerMidTop = new Point((positionModel.getBottomLeft().getX() + positionModel.getUpRight().getX())/2,
											positionModel.getUpRight().getY()
										 );

		Point spikeCenterBottom = new Point(this.x+WIDTH/2, Y + HEIGHT - WIDTH/2);
		
		double dist = GetDistance(playerMidTop, spikeCenterBottom);

		//check fatal collision with spikes
		if ((dist < WIDTH/2)) {
			scoreModel.setAlive(false);
			return true;
		}

		Point playerRightTop = positionModel.getUpRight();
		if (this.x <= playerRightTop.getX() && playerRightTop.getX() <= this.x+WIDTH && playerRightTop.getY() < Y+HEIGHT-WIDTH/2) {
			positionModel.changeX(-gameSettings.horSpeed);
			return true;
		}

    	return false;
    }

//    private double GetDistance(Point a, Point b) {
//    	double dx = a.getX() - b.getX();
//		double dy = a.getY() - b.getY();
//
//		return Math.sqrt(dx*dx + dy*dy);
//	}
}