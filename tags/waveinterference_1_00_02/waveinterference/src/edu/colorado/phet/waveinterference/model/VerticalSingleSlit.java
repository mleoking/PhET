/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 3:46:37 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class VerticalSingleSlit extends VerticalBarrier {
    private Rectangle slit;

    private Rectangle topBar;
    private Rectangle bottomBar;

    public VerticalSingleSlit( int gridWidth, int gridHeight, int x, int thickness, int slitSize, int slitSeparation, double potential ) {
        super( gridWidth, gridHeight, x, thickness, slitSize, slitSeparation, potential );
    }

    protected void update() {
        int gridHeight = getGridHeight();
        int slitSize = getSlitSize();
        int thickness = getThickness();
        int x = getX();
        double potential = getPotential();

        int slitCenter = round( gridHeight / 2.0 );
        topBar = new Rectangle( x, 0, thickness, round( slitCenter - slitSize / 2.0 ) );
        int y = round( slitCenter + slitSize / 2.0 );
        bottomBar = new Rectangle( x, y, thickness, getGridHeight() - y );
        this.slit = new Rectangle( x, topBar.x + topBar.width, thickness, slitSize );

        CompositePotential compositePotential = new CompositePotential();
        if( super.getInverse() ) {
            compositePotential.addPotential( new BarrierPotential( slit, potential ) );
        }
        else {
            compositePotential.addPotential( new BarrierPotential( topBar, potential ) );
            compositePotential.addPotential( new BarrierPotential( bottomBar, potential ) );
        }
        setPotentialDelegate( new PrecomputedPotential( compositePotential, getGridWidth(), getGridHeight() ) );
        notifyListeners();
    }

    public Rectangle[] getSlitAreas() {
        return new Rectangle[]{new Rectangle( slit )};
    }

    public Rectangle[]getRectangleBarriers() {
        ArrayList r = new ArrayList();
        if( getInverse() ) {
            r.add( slit );
        }
        else {
            r.add( topBar );
            r.add( bottomBar );
        }
        return (Rectangle[])r.toArray( new Rectangle[0] );
    }
}
