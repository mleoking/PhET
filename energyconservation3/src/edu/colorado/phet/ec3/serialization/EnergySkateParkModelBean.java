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
    private Point2D.Double bodyZeroPosition;

    public EnergySkateParkModelBean() {
    }

    public EnergySkateParkModelBean( EnergySkateParkModule module ) {
        Body body = module.getEnergyConservationModel().bodyAt( 0 );
        bodyZeroPosition = body.getPosition();
    }

    public Point2D.Double getBodyZeroPosition() {
        return bodyZeroPosition;
    }

    public void setBodyZeroPosition( Point2D.Double bodyZeroPosition ) {
        this.bodyZeroPosition = bodyZeroPosition;
    }

    public void apply( EnergySkateParkModule module ) {
        module.getEnergyConservationModel().bodyAt( 0 ).setAttachmentPointPosition( bodyZeroPosition );
    }
}
