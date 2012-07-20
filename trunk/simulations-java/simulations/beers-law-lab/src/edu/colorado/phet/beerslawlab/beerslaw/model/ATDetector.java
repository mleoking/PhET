// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Detector for absorbance (A) and percent transmittance (%T)
 * of light passing through a solution in a cuvette.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ATDetector {

    public static enum ATDetectorMode {TRANSMITTANCE, ABSORBANCE}

    private final Light light;
    private final Cuvette cuvette;
    private final Absorbance absorbance;

    public final CompositeProperty<Double> value; // value displayed by the detector, null if no value is detected
    public final Movable body;
    public final Probe probe;
    public Property<ATDetectorMode> mode = new Property<ATDetectorMode>( ATDetectorMode.TRANSMITTANCE );

    public static class Probe extends Movable {

        public final double sensorDiameter; // diameter of the probe's sensor area, in cm

        public Probe( Vector2D probeLocation, PBounds probeDragBounds, double sensorDiameter ) {
            super( probeLocation, probeDragBounds );
            this.sensorDiameter = sensorDiameter;
        }
    }

    public ATDetector( Vector2D bodyLocation, PBounds bodyDragBounds,
                       Vector2D probeLocation, PBounds probeDragBounds,
                       Light light, Cuvette cuvette, Absorbance absorbance ) {

        this.light = light;
        this.cuvette = cuvette;
        this.absorbance = absorbance;

        this.body = new Movable( bodyLocation, bodyDragBounds );
        this.probe = new Probe( probeLocation, probeDragBounds, 0.57 );

        // update the value that is displayed by the detector
        this.value = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return computeValue();
            }
        }, probe.location, light.on, mode, absorbance.value );
    }

    public void reset() {
        this.body.reset();
        this.probe.reset();
        this.mode.reset();
    }

    // Computes the displayed value, null if the light is off or the probe is outside the beam.
    private Double computeValue() {
        Double value = null;
        if ( probeInBeam() ) {
            // path length is between 0 and cuvette width
            final double pathLength = Math.min( Math.max( 0, probe.location.get().getX() - cuvette.location.getX() ), cuvette.width.get() );
            if ( mode.get() == ATDetectorMode.ABSORBANCE ) {
                value = absorbance.getAbsorbanceAt( pathLength );
            }
            else {
                value = 100 * absorbance.getTransmittanceAt( pathLength );
            }
        }
        return value;
    }

    private double getProbeMinY() {
        return probe.location.get().getY() - ( probe.sensorDiameter / 2 );
    }

    private double getProbeMaxY() {
        return probe.location.get().getY() + ( probe.sensorDiameter / 2 );
    }

    // Is the probe in some segment of the beam?
    public boolean probeInBeam() {
        return light.on.get() &&
               ( getProbeMinY() < light.getMinY() ) &&
               ( getProbeMaxY() > light.getMaxY() ) &&
               ( probe.location.get().getX() > light.location.getX() );
    }
}
