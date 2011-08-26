// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;

/**
 * Composes data relevant to any kind of dissolved solute constituent, such as sodium, nitrate, sucrose, etc.
 *
 * @author Sam Reid
 */
public class SoluteConstituent {
    //Particle concentrations for the dissolved components
    public final CompositeDoubleProperty concentration;

    //Color to display in the bar chart, depends on whether "show charges" has been selected by the user
    public final ObservableProperty<Color> color;

    public SoluteConstituent( MicroModel model, ObservableProperty<Color> color, Class<? extends Particle> type ) {
        concentration = new IonConcentration( model, type );
        this.color = color;
    }
}