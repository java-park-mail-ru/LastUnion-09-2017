package lastunion.application.game.models.world_objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lastunion.application.game.models.GameSettings;
import lastunion.application.game.models.Point;
import org.jetbrains.annotations.Nullable;
import lastunion.application.game.models.PositionModel;
import lastunion.application.game.models.ScoreModel;





public class MidObstacle extends BaseWorldObject {
	protected Integer x;
	private final Integer Y = 405;
	private final Integer WIDTH = 50;
	private final Integer HEIGHT = 100;
    public MidObstacle(Integer x) { this.x = x; }

    // check if collision commited (returns true) and performs
    public boolean Collission(PositionModel positionModel, ScoreModel scoreModel, GameSettings gameSettings) {
    	Point playerMidBottom = new Point((positionModel.getBottomLeft().getX() + positionModel.getUpRight().getX())/2,
											positionModel.getBottomLeft().getY()
										 );

		Point spikeCenterBottom = new Point(this.getX()+WIDTH/2, Y + WIDTH/2);
		
		double dist = GetDistance(playerMidBottom, spikeCenterBottom);


		//check fatal collision with spikes
		if ((dist < WIDTH/2)) {
			scoreModel.setAlive(false);
			return true;
		}

		// check non-Fatal collision with wall
		if ((this.x <= positionModel.getUpRight().getX()) && (positionModel.getUpRight().getX() <= this.x+WIDTH) &&
				(positionModel.getBottomLeft().getY() > Y+WIDTH/2)) {
			positionModel.changeX(-gameSettings.horSpeed); //setter nujen
			
			return true;
		}

    	return false;
    }

//    private double GetDistance(Point a, Point b) {
//    	ddx = a.getX() - b.getX();
//		float dy = a.getY() - b.getY();
//
//		return Math.sqrt(dx*dx + dy*dy);
//	}
}