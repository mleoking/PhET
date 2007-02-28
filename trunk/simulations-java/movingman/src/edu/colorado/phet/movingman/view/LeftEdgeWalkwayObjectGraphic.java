/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.view;

import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 9:01:44 PM
 * Copyright (c) Apr 11, 2005 by Sam Reid
 */

public class LeftEdgeWalkwayObjectGraphic extends WalkwayObjectGraphic {
    public LeftEdgeWalkwayObjectGraphic( WalkWayGraphic walkWayGraphic, double coordinate, BufferedImage imageURL ) {
        super( walkWayGraphic, coordinate, imageURL );
    }

    public void update() {
        int x = getWalkWayGraphic().getViewCoordinate( super.getModelLocation() );
        int y = getWalkWayGraphic().getFloorY() - getHeight();
        setLocation( x, y );
    }

}
