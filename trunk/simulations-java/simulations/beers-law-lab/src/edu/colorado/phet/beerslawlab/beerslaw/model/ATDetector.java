// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Detector for absorbance (A) and transmittance (T).
 * If place in the path of the beam between the light and cuvette, measures 0 absorbance and 100% transmittance.
 * Inside the cuvette, there is no measurement.
 * <p>
 * Absorbance (A) is unitless. A = abC.
 * <p>
 * a = molar absorptivity.  Units = M-1cm-1  (Also called e or molar extinction coefficient)
 * This is a measure of the amount of light absorbed per unit concentration at a given wavelength.
 * It is different constant value for each chemical substance.  The relationship between absorbance
 * and concentration is linear (below the detection limit).
 * <p>
 * b = pathlength. Units = cm.
 * The container of solution used in spectrophotometers is called a cuvette.
 * The dimension or cross section of the cuvette that the light passes through is the pathlength.
 * <p>
 * C = concentration. Units = M = mol/L (moles of solute per Liter of solution)
 * <p>
 * A = 2 - log10 %T, where %T is percent transmittance.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ATDetector {

    public static enum ATDetectorMode {TRANSMITTANCE, ABSORBANCE}

    public final Property<Double> value; //TODO this should be a CompositeProperty, so it can't be set by clients
    public final Movable body;
    public final Movable probe;
    public final double probeDiameter; // diameter of the probe's sensor area, in cm
    public Property<ATDetectorMode> mode = new Property<ATDetectorMode>( ATDetectorMode.TRANSMITTANCE );

    public ATDetector( ImmutableVector2D bodyLocation, PBounds bodyDragBounds,
                       ImmutableVector2D probeLocation, PBounds probeDragBounds ) {
        this.value = new Property<Double>( null );
        this.body = new Movable( bodyLocation, bodyDragBounds );
        this.probe = new Movable( probeLocation, probeDragBounds );
        this.probeDiameter = 0.25;

        mode.addObserver( new VoidFunction1<ATDetectorMode>() {
            public void apply( ATDetectorMode displayType ) {
               //TODO change value
            }
        });
    }

    public void setValue( Double value ) {
        this.value.set( value );
    }

    // Gets the value to be displayed by the meter, null if the meter is not reading a value.
    public Double getValue() {
        return value.get();
    }

    public void addValueObserver( SimpleObserver observer ) {
        value.addObserver( observer );
    }

    public void reset() {
        this.value.reset();
        this.body.reset();
        this.probe.reset();
        this.mode.reset();
    }
}
