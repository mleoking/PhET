/**
 * Class: StripChartModel
 * Package: edu.colorado.phet.coreadditions.chart
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.microwaves.coreadditions.chart;

public class StripChartModel {

    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private double y0;

    public StripChartModel() {
    }

    public StripChartModel( double xMin, double xMax, double yMin, double yMax, double y0 ) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.y0 = y0;
    }

    public double getxMin() {
        return xMin;
    }

    public void setxMin( double xMin ) {
        this.xMin = xMin;
    }

    public double getxMax() {
        return xMax;
    }

    public void setxMax( double xMax ) {
        this.xMax = xMax;
    }

    public double getyMin() {
        return yMin;
    }

    public void setyMin( double yMin ) {
        this.yMin = yMin;
    }

    public double getyMax() {
        return yMax;
    }

    public void setyMax( double yMax ) {
        this.yMax = yMax;
    }

    public double getY0() {
        return y0;
    }

    public void setY0( double y0 ) {
        this.y0 = y0;
    }
}
