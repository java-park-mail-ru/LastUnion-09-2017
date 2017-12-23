package lastunion.application.game.models.world_objects;

import lastunion.application.game.models.Point;
import lastunion.application.game.models.PositionModel;
import lastunion.application.game.models.ScoreModel;

public class UpperGem extends BaseWorldObject {
    private final Integer SCORE = 40;
    private final Integer Y = 320;
    private final Integer WIDTH = 25;

    public UpperGem(Integer x) {
        this.x = x;
    }

    @Override
    public boolean Collission(PositionModel positionModel, ScoreModel scoreModel) {
        Point midBottom = positionModel.getMidBottom();

//        (playerMidBottom.x-WIDTH <= this.x && this.x <= playerMidBottom.x + WIDTH &&
//                playerUpperLeft.y <= Y && Y <= playerBottomRight.y )
        if (midBottom.getX() - WIDTH <= this.x && this.x <= midBottom.getX() + WIDTH &&
                positionModel.getUpRight().getY() <= Y && Y <= positionModel.getBottomLeft().getY()) {
            scoreModel.changeScore(SCORE);
            this.x = -WIDTH;//25;
            return true;
        }
        return false;
    }
}
