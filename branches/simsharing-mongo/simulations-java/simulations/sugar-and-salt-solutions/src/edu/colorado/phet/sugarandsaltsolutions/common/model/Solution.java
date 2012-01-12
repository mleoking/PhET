// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * The fluid combination of water and dissolved solutes, sitting on top of any precipitated solid.
 *
 * @author Sam Reid
 */
public class Solution {
    //Volume of the solution (water plus dissolved solutes)
    public final DoubleProperty volume;

    //Get the shape this water takes in its containing beaker
    public final ObservableProperty<Shape> shape;

    public Solution( final DoubleProperty waterVolume,
                     final Beaker beaker ) {

        //To simplify the model and make it less confusing for students (since adding salt could change sugar concentration),
        // we have switched back to using just the water volume for concentration computations
        //To add to the volume based on dissolved solute volume, use something like this line:
        // this.volume = waterVolume.plus( dissolvedSaltVolume, dissolvedSugarVolume );
        this.volume = waterVolume;
        shape = new CompositeProperty<Shape>( new Function0<Shape>() {
            public Shape apply() {

                //Assumes the beaker is rectangular
                return beaker.getWaterShape( 0, waterVolume.get() );
            }
        }, volume );
    }
}