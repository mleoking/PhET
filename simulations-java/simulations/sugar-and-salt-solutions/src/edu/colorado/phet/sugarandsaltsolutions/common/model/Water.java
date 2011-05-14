// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;

/**
 * Model for the water, including its volume and a way of accessing its shape (by querying the containing beaker.)
 *
 * @author Sam Reid
 */
public class Water {
    //volume in SI (m^3).  Start at 1 L (halfway up the 2L beaker).  Note that 0.001 cubic meters = 1L
    public final DoubleProperty volume = new DoubleProperty( 0.001 );
    private final Beaker beaker;

    public Water( Beaker beaker ) {
        this.beaker = beaker;
    }

    //Get the shape this water takes in its containing beaker
    public Shape getShape() {
        return beaker.getFluidShape( volume.get() );
    }

    public void reset() {
        volume.reset();
    }
}
