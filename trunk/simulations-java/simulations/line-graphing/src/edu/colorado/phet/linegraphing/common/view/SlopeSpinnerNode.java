// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Spinners for changing components of slope.
 * These spinners have custom up/down functions that avoid creating an undefined line with slope=0/0.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SlopeSpinnerNode extends SpinnerNode {

    /**
     * Constructor.
     *
     * @param userComponent
     * @param variableComponent the part of the slope that this spinner is manipulating
     * @param fixedComponent the part of the slope that this spinner is not manipulating
     * @param variableRange the variableRange of variableComponent
     * @param colors
     * @param font
     * @param format
     */
    public SlopeSpinnerNode( IUserComponent userComponent,
                             final Property<Double> variableComponent, final Property<Double> fixedComponent, Property<DoubleRange> variableRange,
                             SpinnerStateIndicator<Color> colors, PhetFont font, NumberFormat format ) {
        super( userComponent, variableComponent, variableRange, colors, font, format,
               // "up" function, skips over undefined line condition (slope=0/0)
               new Function0<Double>() {
                   public Double apply() {
                       return ( variableComponent.get() == -1 && fixedComponent.get() == 0 ) ? 1d : variableComponent.get() + 1;
                   }
               },
               // "down" function, skips over undefined line condition (slope=0/0)
               new Function0<Double>() {
                   public Double apply() {
                       return ( variableComponent.get() == 1 && fixedComponent.get() == 0 ) ? -1d : variableComponent.get() - 1;
                   }
               }
        );
    }

    // Spinner for rise component of slope.
    public static class RiseSpinnerNode extends SlopeSpinnerNode {
        public RiseSpinnerNode( IUserComponent userComponent,
                                final Property<Double> rise, final Property<Double> run, Property<DoubleRange> riseRange,
                                SpinnerStateIndicator<Color> colors, PhetFont font, NumberFormat format ) {
            // note order of args to super: rise is variable, run is fixed
            super( userComponent, rise, run, riseRange, colors, font, format );
        }
    }

    // Spinner for run component of slope.
    public static class RunSpinnerNode extends SlopeSpinnerNode {
        public RunSpinnerNode( IUserComponent userComponent,
                               final Property<Double> rise, final Property<Double> run, Property<DoubleRange> runRange,
                               SpinnerStateIndicator<Color> colors, PhetFont font, NumberFormat format ) {
            // note order of args to super: run is variable, rise is fixed
            super( userComponent, run, rise, runRange, colors, font, format );
        }
    }
}
