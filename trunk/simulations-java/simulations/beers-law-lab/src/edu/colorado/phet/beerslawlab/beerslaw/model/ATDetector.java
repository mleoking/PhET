// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Detector for absorbance (A) and percent transmittance (%T).
 * If place in the path of the beam between the light and cuvette, measures 0 absorbance and 100% transmittance.
 * Inside the cuvette, there is no measurement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ATDetector {

    public static enum ATDetectorMode {PERCENT_TRANSMITTANCE, ABSORBANCE}

    public final CompositeProperty<Double> value; // null if no value is detected
    public final Movable body;
    public final Movable probe;
    public final double probeDiameter; // diameter of the probe's sensor area, in cm //TODO should be an attribute of the probe
    public Property<ATDetectorMode> mode = new Property<ATDetectorMode>( ATDetectorMode.PERCENT_TRANSMITTANCE );

    public ATDetector( ImmutableVector2D bodyLocation, PBounds bodyDragBounds,
                       ImmutableVector2D probeLocation, PBounds probeDragBounds,
                       final ObservableProperty<Double> absorbance, final ObservableProperty<Double> percentTransmittance,
                       final Light light ) {

        this.body = new Movable( bodyLocation, bodyDragBounds );
        this.probe = new Movable( probeLocation, probeDragBounds );
        this.probeDiameter = 0.57; // cm, specific to the probe image file

        //TODO this should also be a function of the detector's probe location
        // update the value that is displayed by the detector
        this.value = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                if ( light.on.get() ) {
                    if ( mode.get() == ATDetectorMode.PERCENT_TRANSMITTANCE ) {
                        return percentTransmittance.get();
                    }
                    else {
                        return absorbance.get();
                    }
                }
                else {
                    return null;
                }
            }
        }, absorbance, percentTransmittance, light.on, mode );
    }

    public void reset() {
        this.body.reset();
        this.probe.reset();
        this.mode.reset();
    }

    public double getProbeMinY() {
        return probe.location.get().getY() - ( probeDiameter / 2 );
    }

    public double getProbeMaxY() {
        return probe.location.get().getY() + ( probeDiameter / 2 );
    }
}
