package edu.colorado.phet.movingman.model;

/**
 * This is the persistent state for the Moving Man simulation; i.e. the state that gets recorded and played back.
 * It is immutable because it should be impossible to change a previously recorded point.
 *
 * @author Sam Reid
 */
public class MovingManState {
    private final double time;//The time of the sampled data point, note this time is different than the time in the recordandplaybackmodel, it will lag since we don't record a point until we have good estimates of the derivatives
    private final ManState manState;//The state of the man character
    private final boolean walls;//true if the walls are enabled at this point in time

    public MovingManState(double time, ManState manState, boolean walls) {
        this.time = time;
        this.manState = manState;
        this.walls = walls;
    }

    public double getTime() {
        return time;
    }

    public ManState getMovingManState() {
        return manState;
    }

    public boolean getWalls() {
        return walls;
    }
}
