package edu.colorado.phet.ec3.model;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 11, 2006
 * Time: 4:52:36 AM
 * Copyright (c) Oct 11, 2006 by Sam Reid
 */
public class State {
    private EnergyConservationModel model;
    private Body body;

    public State( EnergyConservationModel model, Body body ) {
        this.model = model.copyState();
        this.body = body.copyState( model );
    }

    public EnergyConservationModel getModel() {
        return model;
    }

    public Body getBody() {
        return body;
    }

    public Point2D.Double getPosition() {
        return body.getPosition();
    }

    public double getMechanicalEnergy() {
        return model.getMechanicalEnergy( body );
    }

    public double getTotalEnergy() {
        return model.getMechanicalEnergy( body ) + model.getThermalEnergy();
    }

    public double getHeat() {
        return model.getThermalEnergy();
    }
}
