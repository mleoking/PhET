package edu.colorado.phet.ec3.serialization;

import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.model.Body;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Nov 7, 2006
 * Time: 12:49:52 PM
 * Copyright (c) Nov 7, 2006 by Sam Reid
 */

public class EnergySkateParkModelBean {
    private double x;
    private double y;

    public EnergySkateParkModelBean() {
    }

    public EnergySkateParkModelBean( EnergySkateParkModule module ) {
        Body body = module.getEnergyConservationModel().bodyAt( 0 );
        x = body.getX();
        y = body.getY();
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public void apply( EnergySkateParkModule module ) {
        module.getEnergyConservationModel().bodyAt( 0 ).setAttachmentPointPosition( new Point2D.Double( x, y ) );
    }
}
