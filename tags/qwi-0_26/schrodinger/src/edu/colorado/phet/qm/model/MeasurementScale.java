/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jul 10, 2005
 * Time: 10:16:04 PM
 * Copyright (c) Jul 10, 2005 by Sam Reid
 */

public class MeasurementScale {
    private double waveAreaLengthModel;
    private double waveAreaLengthMeters;

    public MeasurementScale( int waveAreaLengthModel, double waveAreaLengthMeters ) {
        this.waveAreaLengthModel = waveAreaLengthModel;
        this.waveAreaLengthMeters = waveAreaLengthMeters;
    }

    public double getWaveAreaLengthMeters() {
        return waveAreaLengthMeters;
    }

    public void setWaveAreaLengthMeters( double waveAreaLengthMeters ) {
        this.waveAreaLengthMeters = waveAreaLengthMeters;
    }

    public double modelLengthToMeters( double modelLength ) {
        return modelLength / waveAreaLengthModel * waveAreaLengthMeters;
    }
}
