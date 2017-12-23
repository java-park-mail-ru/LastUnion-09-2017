package lastunion.application.game.models;

import lastunion.application.game.models.world_objects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldObjectController {
    private final Integer typesAmount = 5;
    public enum  Types {
        UP, MID, PIT, MIDGEM, UPGEM
    };
    private List<BaseWorldObject> objectList;

    public WorldObjectController() {
        objectList = new ArrayList<>();
    }

    public void resetObjects() {
        objectList.clear();
    }

    public Integer getObjectsAmount() {
        return objectList.size();
    }

    public boolean CheckAllCollisions(PositionModel positionModel, ScoreModel scoreModel) {
        boolean found = false;
        for (BaseWorldObject worldObject : objectList) {
            boolean res = worldObject.Collission(positionModel, scoreModel);
            if (!found && res) {
                return true;
            }
        }
        return false;
    }


    public void moveAllObjects(Integer horSpeed) {
        for (BaseWorldObject worldObject : objectList) {
            worldObject.setX(worldObject.getX() - horSpeed);
        }

        if (objectList.get(0).getX() < -100) {//this.objectsArray[0].GetWidth()) {
            objectList.remove(0);
        }
    }

    public BaseWorldObject CreateObjectByType(Types type, Integer x) {
        switch (type) {
            case UP : return new UpperObstacle(x);
            case MID : return new MidObstacle(x);
            case PIT : return new PitObstacle(x);
            case MIDGEM : return new MiddleGem(x);
            case UPGEM : return new UpperGem(x);
        }
        return null;
    }

    public void addSeriesOfObjects(Integer screenWidth, Integer minRange, Integer delta) {
        final Integer baseX = (int)Math.floor(screenWidth * 1.5);
        Integer curX = baseX;
        Random rnd = new Random();
        Integer obstaclesInSeries = 9 + rnd.nextInt(10);//rnd.MathGeom.GetRandomNInRange(9, 18);
        while (obstaclesInSeries >= 0) {
            Types curType =  Types.values()[rnd.nextInt(Types.values().length)];//randomEnum<Types>(rnd, Types.class);//rnd.nextInt(5);//MathGeom.GetRandomNLessThen(typesAmount);
            objectList.add(this.CreateObjectByType(curType, curX));
            curX = curX + minRange + rnd.nextInt(delta);//MathGeom.GetRandomNLessThen(delta);
            obstaclesInSeries--;
        }
    }
}
