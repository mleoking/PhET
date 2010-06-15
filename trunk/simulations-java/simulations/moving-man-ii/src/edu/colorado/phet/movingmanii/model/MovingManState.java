package edu.colorado.phet.movingmanii.model;

/**
 * @author Sam Reid
 */
public class MovingManState {
    private double time;//Note this time is different than the time in the recordandplaybackmodel, it will lag since we don't record a point until we have good estimates of the derivatives
    private ManState manState;
    private boolean walls;

    public MovingManState(double time, ManState manState, boolean walls) {
        this.time = time;
        this.manState = manState;
        this.walls = walls;
    }

    public double getTime() {
        return time;
    }

    //TODO: recording just the state of the man but smoothing the data series afterwards is problematic; there will be discrepancies between the restored values and the values displayed in the chart.
    //One potential solution: record a pointer into the array of data instead of a deep copy of data, so the value will match.

    public ManState getMovingManState() {
        return manState;
    }

    public boolean getWalls() {
        return walls;
    }
}
