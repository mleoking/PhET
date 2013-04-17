// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * Model of the light as a solid beam.
 * Changes in wavelength affect the entire beam instantaneously.
 * Consists of 3 segments: left (between light and cuvette), center (inside cuvette), and right (to right of cuvette).
 * Beam may be intercepted at any point by the Absorbance-Transmittance detector.
 * The beam is in the probe if the entire beam is in contact with the probe lens.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beam {

    private static final double MAX_LIGHT_WIDTH = 50; // cm, wide enough to be way off the right edge of the play area
    private static final int MAX_LIGHT_ALPHA = 200; // transparency of light when transmittance is 1.0 (tuned by eye)
    private static final int MIN_LIGHT_ALPHA = 20; // min transparency of light when transmittance is non-zero (tuned by eye)
    private static final Function TRANSMITTANCE_TO_ALPHA = new LinearFunction( 0, 1, MIN_LIGHT_ALPHA, MAX_LIGHT_ALPHA ); // maps transmittance to transparency

    private final Light light;
    private final Cuvette cuvette;
    private final ATDetector detector;
    private final Absorbance absorbance;
    private final ModelViewTransform mvt;

    public final Property<ImmutableRectangle2D> shape;
    public final Property<Paint> paint;
    public final CompositeProperty<Boolean> visible;

    public Beam( final Light light, Cuvette cuvette, ATDetector detector, Absorbance absorbance, ModelViewTransform mvt ) {

        this.light = light;
        this.cuvette = cuvette;
        this.detector = detector;
        this.absorbance = absorbance;
        this.mvt = mvt;

        // Proper values will be set when observers are registered
        this.shape = new Property<ImmutableRectangle2D>( new ImmutableRectangle2D( 0, 0 ) );
        this.paint = new Property<Paint>( Color.WHITE );

        // Make the beam visible when the light is on.
        visible = new CompositeProperty<Boolean>( new Function0<Boolean>() {
            public Boolean apply() {
                return light.on.get();
            }
        }, light.on );

        // update segment shapes
        RichSimpleObserver shapeObserver = new RichSimpleObserver() {
            @Override public void update() {
                if ( visible.get() ) {
                    updateShape();
                }
            }
        };
        shapeObserver.observe( cuvette.width, detector.probe.location );

        // update segment colors
        final RichSimpleObserver colorObserver = new RichSimpleObserver() {
            public void update() {
                if ( visible.get() ) {
                    updateColor();
                }
            }
        };
        colorObserver.observe( light.wavelength, cuvette.width, absorbance.value );

        // Update when beam becomes visible
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                if ( visible ) {
                    updateColor();
                    updateShape();
                }
            }
        } );
    }

    // Updates colors of the beam segments
    private void updateColor() {
        final double wavelength = light.wavelength.get();
        final Double transmittance = absorbance.getTransmittance();
        Color leftColor = ColorUtils.createColor( new VisibleColor( wavelength ), MAX_LIGHT_ALPHA );
        Color rightColor = ColorUtils.createColor( new VisibleColor( wavelength ), (int) TRANSMITTANCE_TO_ALPHA.evaluate( transmittance ) );
        double x = mvt.modelToViewDeltaX( cuvette.location.getX() );
        double w = mvt.modelToViewDeltaX( cuvette.width.get() );
        paint.set( new GradientPaint( (float) x, 0, leftColor, (float) ( x + w ), 0, rightColor ) );
    }

    // Updates the shape of the beam
    private void updateShape() {
        final double x = light.location.getX();
        final double y = light.getMinY();
        final double width = detector.probeInBeam() ? detector.probe.getX() - x : MAX_LIGHT_WIDTH;
        final double height = light.lensDiameter;
        shape.set( new ImmutableRectangle2D( x, y, width, height ) );
    }
}
