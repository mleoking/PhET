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

    private final Light light;
    private final Cuvette cuvette;
    private final ObservableProperty<Double> absorbance;
    private final ObservableProperty<Double> percentTransmittance;

    public final CompositeProperty<Double> value; // null if no value is detected
    public final Movable body;
    public final Movable probe;
    public final double probeDiameter; // diameter of the probe's sensor area, in cm //TODO should be an attribute of the probe
    public Property<ATDetectorMode> mode = new Property<ATDetectorMode>( ATDetectorMode.PERCENT_TRANSMITTANCE );

    public ATDetector( ImmutableVector2D bodyLocation, PBounds bodyDragBounds,
                       ImmutableVector2D probeLocation, PBounds probeDragBounds,
                       final Light light, final Cuvette cuvette,
                       final ObservableProperty<Double> absorbance, final ObservableProperty<Double> percentTransmittance ) {

        this.light = light;
        this.cuvette = cuvette;
        this.absorbance = absorbance;
        this.percentTransmittance = percentTransmittance;

        this.body = new Movable( bodyLocation, bodyDragBounds );
        this.probe = new Movable( probeLocation, probeDragBounds );
        this.probeDiameter = 0.57; // cm, specific to the probe image file

        // update the value that is displayed by the detector
        this.value = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return computeValue();
            }
        }, probe.location, cuvette.width, light.on, mode, absorbance, percentTransmittance );
    }

    public void reset() {
        this.body.reset();
        this.probe.reset();
        this.mode.reset();
    }

    /*
     * Display:
     * - A=0 and %T=100 to the left of the cuvette.
     * - nothing inside the cuvette.
     * - A and %T to the right of the cuvette.
     */
    private Double computeValue() {
        Double value = null;
        if ( light.on.get() ) {
            if ( probeInLeftSegment() ) {
                value = ( mode.get() == ATDetectorMode.PERCENT_TRANSMITTANCE ) ? 100d : 0d;
            }
            else if ( probeInCenterSegment() ) {
                // display nothing inside the cuvette
                value = null;
            }
            else if ( probeInRightSegment() ) {
                value = ( mode.get() == ATDetectorMode.PERCENT_TRANSMITTANCE ) ? percentTransmittance.get() : absorbance.get();
            }
        }
        return value;
    }

    private double getProbeMinY() {
        return probe.location.get().getY() - ( probeDiameter / 2 );
    }

    private double getProbeMaxY() {
        return probe.location.get().getY() + ( probeDiameter / 2 );
    }

    // Is the probe in the left segment?
    public boolean probeInLeftSegment() {
        return probeInBeam() &&
               ( probe.location.get().getX() > light.location.getX() ) &&
               ( probe.location.get().getX() < cuvette.location.getX() );
    }

    // Is the probe in the center segment?
    public boolean probeInCenterSegment() {
        return probeInBeam() &&
               ( probe.location.get().getX() >= cuvette.location.getX() ) &&
               ( probe.location.get().getX() <= cuvette.location.getX() + cuvette.width.get() );

    }

    // Is the probe in the right segment?
    public boolean probeInRightSegment() {
        return probeInBeam() && ( probe.location.get().getX() > cuvette.location.getX() + cuvette.width.get() );
    }

    // Is the probe in some segment of the beam?
    private boolean probeInBeam() {
        return ( getProbeMinY() < light.getMinY() ) &&
               ( getProbeMaxY() > light.getMaxY() ) &&
               ( probe.location.get().getX() > light.location.getX() );
    }
}
