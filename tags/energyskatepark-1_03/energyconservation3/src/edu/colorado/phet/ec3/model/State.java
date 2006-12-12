package edu.colorado.phet.ec3.model;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 11, 2006
 * Time: 4:52:36 AM
 * Copyright (c) Oct 11, 2006 by Sam Reid
 */
public class State {
    private Body body;

    public State( Body body ) {
        this.body = body.copyState();
    }

    public Body getBody() {
        return body;
    }

    public Point2D.Double getPosition() {
        return body.getPosition();
    }

    public double getMechanicalEnergy() {
        return body.getMechanicalEnergy();
    }

    public double getTotalEnergy() {
        return body.getTotalEnergy();
    }

    public double getHeat() {
        return body.getThermalEnergy();
    }
}
