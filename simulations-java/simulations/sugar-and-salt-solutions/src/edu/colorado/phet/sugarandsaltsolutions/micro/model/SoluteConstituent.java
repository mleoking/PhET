// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;

/**
 * Composes data relevant to any kind of dissolved solute constituent, such as sodium, nitrate, sucrose, etc.
 *
 * @author Sam Reid
 */
public class SoluteConstituent {
    public final CompositeDoubleProperty concentration;
    public final ObservableProperty<Color> color;
    public final DrainData drainData;

    public SoluteConstituent( MicroModel model, CompositeProperty<Color> color, Class<? extends Particle> type ) {
        concentration = new IonConcentration( model, type );
        this.color = color;
        drainData = new DrainData( type );
    }
}