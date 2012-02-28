// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * Model of the light beam.
 * Consists of 3 segments: left (between light and cuvette), center (inside cuvette), and right (to right of cuvette).
 * Beam may be intercepted at any point by the Absorbance-Transmittance detector.
 * The beam is in the probe if the entire beam is in contact with the probe lens.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beam {

    private static final double RIGHT_SEGMENT_WIDTH = 10; // cm, wide enough to be way off the right edge of the play area
    private static final int MAX_LIGHT_ALPHA = 200; // transparency of light when transmittance is 100%
    private static final int MIN_LIGHT_ALPHA = 20; // min transparency of light when transmittance is non-zero
    private static final Function TRANSMITTANCE_TO_ALPHA = new LinearFunction( 0, 100, MIN_LIGHT_ALPHA, MAX_LIGHT_ALPHA ); // maps %transmittance to transparency
    private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );

    private final Light light;
    private final Cuvette cuvette;
    private final ATDetector detector;
    private final ObservableProperty<Double> percentTransmittance;
    private final ModelViewTransform mvt;
    //TODO add CompositeProperty<Boolean> visible, derived from light.on

    public final Property<ImmutableRectangle2D> leftShape, centerShape, rightShape;
    public final Property<Paint> leftPaint, centerPaint, rightPaint;
    public final CompositeProperty<Boolean> visible;

    public Beam( final Light light, Cuvette cuvette, ATDetector detector, ObservableProperty<Double> percentTransmittance, ModelViewTransform mvt ) {

        this.light = light;
        this.cuvette = cuvette;
        this.detector = detector;
        this.percentTransmittance = percentTransmittance;
        this.mvt = mvt;

        // Proper values will be set when observers are registered
        this.leftShape = new Property<ImmutableRectangle2D>( new ImmutableRectangle2D( 0, 0 ) );
        this.centerShape = new Property<ImmutableRectangle2D>( new ImmutableRectangle2D( 0, 0 ) );
        this.rightShape = new Property<ImmutableRectangle2D>( new ImmutableRectangle2D( 0, 0 ) );

        // Proper values will be set when observers are registered
        this.leftPaint = new Property<Paint>( Color.WHITE );
        this.centerPaint = new Property<Paint>( Color.WHITE );
        this.rightPaint = new Property<Paint>( Color.WHITE );

        // update segment shapes
        RichSimpleObserver shapeObserver = new RichSimpleObserver() {
            @Override public void update() {
                updateSegments();
            }
        };
        shapeObserver.observe( cuvette.width, detector.probe.location );

        // update segment colors
        final RichSimpleObserver colorObserver = new RichSimpleObserver() {
            public void update() {
                updateColors();
            }
        };
        colorObserver.observe( light.wavelength, cuvette.width, percentTransmittance );

        // Make the beam visible when the light is on and set to "beam" view.
        visible = new CompositeProperty<Boolean>( new Function0<Boolean>() {
            public Boolean apply() {
                return light.on.get() && light.representation.get() == LightRepresentation.BEAM;
            }
        }, light.on, light.representation );
    }

    // Updates colors of the beam segments
    private void updateColors() {

        final double wavelength = light.wavelength.get();

        // left uses color of light
        leftPaint.set( ColorUtils.createColor( new VisibleColor( wavelength ), MAX_LIGHT_ALPHA ) );

        // center is a gradient
        if ( percentTransmittance.get() == 0 ) {
            centerPaint.set( INVISIBLE_COLOR );
        }
        else {
            // This gradient is in view coordinates.
            Color leftColor = ColorUtils.createColor( new VisibleColor( wavelength ), MAX_LIGHT_ALPHA );
            Color rightColor = ColorUtils.createColor( new VisibleColor( wavelength ), (int) TRANSMITTANCE_TO_ALPHA.evaluate( percentTransmittance.get() ) );
            double x = mvt.modelToViewDeltaX( cuvette.location.getX() );
            double w = mvt.modelToViewDeltaX( cuvette.width.get() );
            centerPaint.set( new GradientPaint( (float) x, 0, leftColor, (float) ( x + w ), 0, rightColor ) );
        }

        // right is a solid color
        if ( percentTransmittance.get() == 0 ) {
            rightPaint.set( INVISIBLE_COLOR );
        }
        else {
            Color color = new VisibleColor( light.wavelength.get() );
            rightPaint.set( ColorUtils.createColor( color, (int) TRANSMITTANCE_TO_ALPHA.evaluate( percentTransmittance.get() ) ) );
        }
    }

    // Updates the geometry of the beam segments
    private void updateSegments() {

        // light
        final double lightX = light.location.getX();
        final double lightMinY = light.getMinY();
        final double lightHeight = light.lensDiameter;

        // cuvette
        final double cuvetteMinX = cuvette.location.getX();
        final double cuvetteMaxX = cuvetteMinX + cuvette.width.get();
        final double cuvetteWidth = cuvette.width.get();

        // probe
        final double probeX = detector.probe.location.get().getX();

        // left segment
        if ( probeInLeftSegment() ) {
            leftShape.set( new ImmutableRectangle2D( lightX, lightMinY, probeX - lightX, lightHeight ) );
        }
        else {
            leftShape.set( new ImmutableRectangle2D( lightX, lightMinY, cuvetteMinX - lightX, lightHeight ) );
        }

        // center segment
        if ( probeInLeftSegment() ) {
            // this segment doesn't exist
            centerShape.set( new ImmutableRectangle2D( 0, 0 ) );
        }
        else if ( probeInCenterSegment() ) {
            // probe is interacting with this segment
            centerShape.set( new ImmutableRectangle2D( cuvetteMinX, lightMinY, probeX - cuvetteMinX, lightHeight ) );
        }
        else {
            // probe is not interacting with this segment
            centerShape.set( new ImmutableRectangle2D( cuvetteMinX, lightMinY, cuvetteWidth, lightHeight ) );
        }

        // right segment
        if ( probeInLeftSegment() || probeInCenterSegment() ) {
            // this segment doesn't exist
            rightShape.set( new ImmutableRectangle2D( 0, 0 ) );
        }
        else if ( probeInRightSegment() ) {
            // probe is interacting with this segment
            rightShape.set( new ImmutableRectangle2D( cuvetteMaxX, lightMinY, probeX - cuvetteMaxX, lightHeight ) );
        }
        else {
            // probe is not interacting with this segment
            rightShape.set( new ImmutableRectangle2D( cuvetteMaxX, lightMinY, RIGHT_SEGMENT_WIDTH, lightHeight ) );
        }
    }

    // Is the probe in the left segment?
    private boolean probeInLeftSegment() {
        return probeInBeam() &&
               ( detector.probe.location.get().getX() > light.location.getX() ) &&
               ( detector.probe.location.get().getX() < cuvette.location.getX() );
    }

    // Is the probe in the center segment?
    private boolean probeInCenterSegment() {
        return probeInBeam() &&
               ( detector.probe.location.get().getX() >= cuvette.location.getX() ) &&
               ( detector.probe.location.get().getX() <= cuvette.location.getX() + cuvette.width.get() );

    }

    // Is the probe in the right segment?
    private boolean probeInRightSegment() {
        return probeInBeam() && ( detector.probe.location.get().getX() > cuvette.location.getX() + cuvette.width.get() );
    }

    // Is the probe in some segment of the beam?
    private boolean probeInBeam() {
        return ( detector.getProbeMinY() < light.getMinY() ) &&
               ( detector.getProbeMaxY() > light.getMaxY() ) &&
               ( detector.probe.location.get().getX() > light.location.getX() );
    }
}
