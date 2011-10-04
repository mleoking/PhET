// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Particle;

/**
 * Composes data relevant to any kind of dissolved solute constituent, such as sodium, nitrate, sucrose, etc.
 *
 * @author Sam Reid
 */
public class SoluteConstituent {

    //True particle concentrations for the dissolved components
    public final CompositeDoubleProperty concentration;

    //The concentration to show on the bar chart should be the the display concentration instead of the true concentration because the value must be held constant when draining out the drain
    public final Property<Double> concentrationToDisplay;

    //Color to display in the bar chart, depends on whether "show charges" has been selected by the user
    public final ObservableProperty<Color> color;

    public SoluteConstituent( MicroModel model, ObservableProperty<Color> color, Class<? extends Particle> type,

                              //Flag indicating whether the value should be held constant, in this case if the user is draining fluid out the drain
                              final ObservableProperty<Boolean> hold ) {
        concentration = new IonConcentration( model, type );
        concentrationToDisplay = new Property<Double>( concentration.get() );

        //When the true concentration changes, move the display concentration in accordance with the delta, but also toward the true value
        //Unless the user is draining fluid (in which case it should remain constant)
        concentration.addObserver( new ChangeObserver<Double>() {
            public void update( Double newValue, Double oldValue ) {
                if ( !hold.get() ) {
                    double delta = newValue - oldValue;
                    double proposedDisplayValue = concentrationToDisplay.get() + delta;
                    double trueValue = concentration.get();

                    //Mix the proposed and true values so it will step in the right direction but also toward the true value
                    double fractionTrueValue = 0.5;
                    double newValueToDisplay = fractionTrueValue * trueValue + ( 1 - fractionTrueValue ) * proposedDisplayValue;
                    concentrationToDisplay.set( newValueToDisplay );
                }

                //Even if the value is being held constant, jump directly to zero if the true concentration becomes zero,
                //So that if all particles flow out the drain, the displayed concentration of that solute will read 0
                else if ( concentration.get() == 0.0 ) {
                    concentrationToDisplay.set( 0.0 );
                }
            }
        } );
        this.color = color;
    }
}