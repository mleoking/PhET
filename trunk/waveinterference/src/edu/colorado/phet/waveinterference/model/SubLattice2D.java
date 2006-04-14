/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 11:19:48 PM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class SubLattice2D extends Lattice2D {
    private Lattice2D parent;
    private Rectangle region;

    public SubLattice2D( Lattice2D parent, Rectangle region ) {
        super( region.width, region.height );
        this.parent = parent;
        this.region = region;
    }

    public double getAverageValue( int x, int y, int windowWidth ) {
        return super.getAverageValue( x, y, windowWidth );
    }

    public int getWidth() {
        if( region != null ) {
            return region.width;
        }
        else {
            return super.getWidth();
        }
    }

    public int getHeight() {
        if( region != null ) {
            return region.height;
        }
        else {
            return super.getHeight();
        }
    }

    public float getValue( int i, int j ) {
        return parent.getValue( i + region.x, j + region.y );
    }

}
