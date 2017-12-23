package lastunion.application.game.models.world_objects;

import lastunion.application.game.models.Point;
import lastunion.application.game.models.PositionModel;
import lastunion.application.game.models.ScoreModel;

public class MiddleGem extends BaseWorldObject {
    private final Integer Y = 420;
    private final Integer WIDTH = 25;
    private final Integer HEIGHT = 25;
    private final Integer SCORE = 20;


    public MiddleGem(Integer x) {
        this.x = x;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    // check non-Fatal collision
    @Override
    public boolean Collission(PositionModel positionModel, ScoreModel scoreModel)
    {
//        if (playerMidBottom.x-WIDTH <= this.x && this.x <= playerMidBottom.x + WIDTH &&
//                playerUpperLeft.y <= Y && Y <= playerBottomRight.y ) {
//            if (playerMidBottom.x-WIDTH <= this.x && this.x <= playerMidBottom.x + WIDTH &&
//                    playerUpperLeft.y <= Y && Y <= playerBottomRight.y )
//        }
        Point midBottom = positionModel.getMidBottom();
//        if (playerMidBottom.x-WIDTH <= this.x && this.x <= playerMidBottom.x + WIDTH &&
//                playerUpperLeft.y <= Y && Y <= playerBottomRight.y )
        if (midBottom.getX()-WIDTH <= this.x && this.x <= midBottom.getX() + WIDTH &&
                positionModel.getUpRight().getY() <= Y && Y <= positionModel.getBottomLeft().getY() ) {
            scoreModel.changeScore(SCORE);
            // gem disappears in left side of screen
            this.x = -WIDTH;//25;
            return true;
        }
        return false;
    }
}
