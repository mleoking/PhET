/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.view;

import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 9:17:33 PM
 * Copyright (c) Apr 11, 2005 by Sam Reid
 */

public class RightEdgeWalkwayObjectGraphic extends WalkwayObjectGraphic {
    public RightEdgeWalkwayObjectGraphic( WalkWayGraphic walkWayGraphic, double coordinate, BufferedImage barrierImage ) {
        super( walkWayGraphic, coordinate, barrierImage );
    }

    public void update() {
        int x = getWalkWayGraphic().getViewCoordinate( super.getModelLocation() ) - getWidth();
        int y = getWalkWayGraphic().getFloorY() - getHeight();
        setLocation( x, y );
    }
}
