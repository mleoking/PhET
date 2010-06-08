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

    //TODO: recording just the state of the man but smoothing the data series afterwards is problematic; there will be discrepancies between the restored values and the values displayed in the chart.
    //One potential solution: record a pointer into the array of data instead of a deep copy of data, so the value will match.

    public ManState getMovingManState() {
        return manState;
    }
}
