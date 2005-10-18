/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 30, 2005
 * Time: 10:20:22 PM
 * Copyright (c) May 30, 2005 by Sam Reid
 */

public class RightBarrierGraphic extends BarrierGraphic {
    public RightBarrierGraphic( Component component, RampPanel rampPanel, SurfaceGraphic surfaceGraphic ) {
        super( component, rampPanel, surfaceGraphic );
    }

    protected int getOffsetX() {
        return imageGraphic.getImage().getWidth( getRampPanel() ) / 2;
    }

    protected double getBarrierPosition() {
        return getRampGraphic().getSurface().getLength();
    }
}
