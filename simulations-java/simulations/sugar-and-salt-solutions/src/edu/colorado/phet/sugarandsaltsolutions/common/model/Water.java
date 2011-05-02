// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property2.Property;

/**
 * @author Sam Reid
 */
public class Water {
    public final Property<Double> volume = new Property<Double>( 0.1 );//volume in SI (m^3)
    private final Beaker beaker;

    public Water( Beaker beaker ) {
        this.beaker = beaker;
    }

    public Shape getShape() {
        return beaker.getFluidShape( volume.getValue() );
    }
}
