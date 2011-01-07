// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import java.io.Serializable;

/**
 * User: Sam Reid
 * Date: Oct 23, 2005
 * Time: 4:51:38 PM
 */

public class HistoryPoint implements Serializable {
    private Body body;
    private double x;
    private double y;
    private double time;
    private double ke;
    private double pe;
    private double totalEnergy;
    private double thermalEnergy;

    private boolean readoutVisible = false;
    private double dy;
    private double speed;

    public HistoryPoint( double time, Body body ) {
        this.body = body;
        this.x = body.getX();
        this.y = body.getY();
        this.time = time;
        this.ke = body.getKineticEnergy();
        this.pe = body.getPotentialEnergy();
        this.thermalEnergy = body.getThermalEnergy();
        this.totalEnergy = body.getTotalEnergy();
        this.dy = body.getHeightAboveZero();
        this.speed = body.getSpeed();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTime() {
        return time;
    }

    public double getKE() {
        return ke;
    }

    public double getTotalEnergy() {
        return totalEnergy;
    }

    public double getPe() {
        return pe;
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }

    public void setReadoutVisible( boolean readoutVisible ) {
        this.readoutVisible = readoutVisible;
    }

    public boolean isReadoutVisible() {
        return readoutVisible;
    }

    public double getHeightAboveZero() {
        return dy;
    }

    public double getSpeed() {
        return speed;
    }
}
