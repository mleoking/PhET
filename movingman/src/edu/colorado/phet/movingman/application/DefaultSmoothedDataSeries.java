/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.application;

import edu.colorado.phet.common.math.Average;
import edu.colorado.phet.movingman.elements.DataSeries;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 3:42:23 PM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class DefaultSmoothedDataSeries {
    ArrayList listeners = new ArrayList();
    DataSeries data = new DataSeries();
    DataSeries smoothed = new DataSeries();
    DefaultSmoothedDataSeries derivative;
    int numSmoothingPoints;

    public DefaultSmoothedDataSeries(int numSmoothingPoints) {
        this.numSmoothingPoints = numSmoothingPoints;
    }

    public void setDerivative(DefaultSmoothedDataSeries derivative) {
        this.derivative = derivative;
    }

    public DataSeries getData() {
        return data;
    }

    public void updateSmoothedSeries() {
        Average avg = new Average();
        for (int i = 0; i < numSmoothingPoints && i < data.size(); i++) {
            avg.update(data.lastPointAt(i));
        }
        double value = avg.value();
        if (Double.isNaN(value))
            value = 0;
        smoothed.addPoint(value);
        for (int i = 0; i < listeners.size(); i++) {
            DataSeriesListener dataSeriesListener = (DataSeriesListener) listeners.get(i);
            dataSeriesListener.dataPointChanged(value, this);
        }
    }

    public void addDataSeriesListener(DataSeriesListener listener) {
        this.listeners.add(listener);
    }

    public DataSeries getSmoothedDataSeries() {
        return smoothed;
    }

    public void updateDerivative(double dt) {
        if (smoothed.size() >= 2) {
            double x1 = smoothed.lastPointAt(0);
            double x0 = smoothed.lastPointAt(1);
            double dx = x1 - x0;
            double vel = dx / dt;
            derivative.addPoint(vel);
        }
    }

    public void addPoint(double pt) {
        data.addPoint(pt);
    }

    public int numSmoothedPoints() {
        return smoothed.size();
    }

    public void reset() {
        data.reset();
        smoothed.reset();
    }

    public double smoothedPointAt(int index) {
        return smoothed.pointAt(index);
    }

    public void setNumSmoothingPoints(int numSmoothingPoints) {
        this.numSmoothingPoints = numSmoothingPoints;
    }
}