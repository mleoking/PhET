/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.model.ModelElement;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:38:31 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class DataSeries extends ModelElement {
    ArrayList pts = new ArrayList();
//    private double dt;

    public DataSeries() {
    }

    public void addPoint(double x) {
        this.pts.add(new Double(x));
        updateObservers();
    }

    public double getLastPoint() {
        return lastPointAt(0);
    }

    public int size() {
        return pts.size();
    }

    public void stepInTime(double dt) {
//        this.dt = dt;
    }

    public void reset() {
        this.pts = new ArrayList();
        updateObservers();
    }

    public double lastPointAt(int i) {
        return pointAt(pts.size() - 1 - i);
    }

    public double pointAt(int i) {
        return ((Double) pts.get(i)).doubleValue();
    }

    public boolean indexInBounds(int index) {

        return index >= 0 && index < pts.size();
    }

    public void addAll(DataSeries dataSeries) {
        for (int i = 0; i < dataSeries.size(); i++) {
            addPoint(dataSeries.pointAt((i)));
        }
    }

//    public void updateWithDerivative(DataSeries integral) {
//        /*Simplest is divide the last two points by dt.*/
//        if (integral.size() >= 4) {
//            double x0 = integral.pointAt(integral.size() - 1);
//            double x1 = integral.pointAt(integral.size() - 2);
//            double diff = x1 - x0;
//            double derivative = diff / dt;
//
////            x0=integral.pointAt(integral.size()-1);
////            x1=integral.pointAt(integral.size()-3);
////            diff=(x1-x0)/2;
////            derivative+=diff;
//
////            derivative/=2.0;
////            x0=integral.pointAt(integral.size()-3);
////            x1=integral.pointAt(integral.size()-1);
////            ds.add(derivative);
////            addPoint(ds.average());
//            addPoint(derivative);
//        }
//    }
}
