package edu.colorado.phet.movingmanii;

/**
 * @author Sam Reid
 */
public class MovingManState {
    private double time;
    private ManState manState;

    public MovingManState(double time, ManState manState) {
        this.time = time;
        this.manState = manState;
    }

    public double getTime() {
        return time;
    }

    public ManState getMovingManState() {
        return manState;
    }
}
