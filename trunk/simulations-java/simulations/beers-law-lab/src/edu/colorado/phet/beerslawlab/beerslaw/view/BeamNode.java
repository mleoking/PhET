// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.beerslaw.model.Cuvette;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * The beam view of the light.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeamNode extends PhetPNode {

    private static final int MAX_LIGHT_ALPHA = 200; // transparency of light when transmittance is 100%
    private static final int MIN_LIGHT_ALPHA = 20; // min transparency of light when transmittance is non-zero
    private static final Function TRANSMITTANCE_TO_ALPHA = new LinearFunction( 0, 100, MIN_LIGHT_ALPHA, MAX_LIGHT_ALPHA ); // maps %transmittance to transparency
    private static final Color INVISIBLE_COLOR = new Color( 0, 0, 0, 0 );

    public BeamNode( final Light light, Cuvette cuvette, ModelViewTransform mvt, ObservableProperty<Double> percentTransmittance ) {

        setPickable( false );
        setChildrenPickable( false );

        addChild( new LeftSegmentNode( light, cuvette, mvt ) );
        addChild( new CenterSegmentNode( light, cuvette, mvt, percentTransmittance ) );
        addChild( new RightSegmentNode( light, cuvette, mvt, percentTransmittance ) );

        // Make this node visible when the light is on and type is "beam".
        final RichSimpleObserver observer = new RichSimpleObserver() {
            public void update() {
                setVisible( light.on.get() & light.representation.get() == LightRepresentation.BEAM );
            }
        };
        observer.observe( light.on, light.representation );
    }

    // Base class for all segments of the beam.
    private static abstract class SegmentNode extends PPath {

        public SegmentNode() {
            setStroke( new BasicStroke( 0.25f ) );
        }

        public void setBeamPaint( Paint paint ) {
            setPaint( paint );
            if ( paint instanceof Color ) {
                setStrokePaint( ColorUtils.darkerColor( (Color)paint, 0.5 ) ); // use a darker color for Color strokes, preserve alpha
            }
            else {
                setStrokePaint( null );
            }
        }
    }

    // The left segment, between the light and the left edge of the cuvette.
    private static class LeftSegmentNode extends SegmentNode {

        public LeftSegmentNode( final Light light, Cuvette cuvette, ModelViewTransform mvt ) {

            // Distance from light to cuvette is fixed
            double x = mvt.modelToViewDeltaX( light.location.getX() );
            double y = mvt.modelToViewDeltaY( light.location.getY() - ( light.lensDiameter / 2 ) );
            double w = mvt.modelToViewDeltaX( cuvette.location.getX() - light.location.getX() );
            double h = mvt.modelToViewDeltaY( light.lensDiameter );
            setPathTo( new Rectangle2D.Double( x, y, w, h ) );

            // Set the color to match the light's wavelength
            light.wavelength.addObserver( new VoidFunction1<Double>() {
                public void apply( Double wavelength ) {
                    setBeamPaint( ColorUtils.createColor( new VisibleColor( wavelength ), MAX_LIGHT_ALPHA ) );
                }
            } );
        }
    }

    // The center segment, the portion that passes through the solution in the cuvette.
    private static class CenterSegmentNode extends SegmentNode {

        public CenterSegmentNode( final Light light, final Cuvette cuvette, final ModelViewTransform mvt, final ObservableProperty<Double> percentTransmittance ) {

            // resize the beam when the path length (cuvette width) changes
            cuvette.width.addObserver( new VoidFunction1<Double>() {
                public void apply( Double width ) {
                    if ( percentTransmittance.get() == 0 ) {
                        setPathTo( new Rectangle2D.Double() );
                    }
                    else {
                        double x = mvt.modelToViewDeltaX( cuvette.location.getX() );
                        double y = mvt.modelToViewDeltaY( light.location.getY() - ( light.lensDiameter / 2 ) );
                        double w = mvt.modelToViewDeltaX( cuvette.width.get() );
                        double h = mvt.modelToViewDeltaY( light.lensDiameter );
                        setPathTo( new Rectangle2D.Double( x, y, w, h ) );
                    }
                }
            } );

            // Set the color to a gradient that corresponds to the wavelength and %transmittance, gradually fading from left-to-right
            RichSimpleObserver colorUpdater = new RichSimpleObserver() {
                @Override public void update() {
                    if ( percentTransmittance.get() == 0 ) {
                        setBeamPaint( INVISIBLE_COLOR );
                    }
                    else {
                        final double wavelength = light.wavelength.get();
                        Color leftColor = ColorUtils.createColor( new VisibleColor( wavelength ), MAX_LIGHT_ALPHA );
                        Color rightColor = ColorUtils.createColor( new VisibleColor( wavelength ), (int) TRANSMITTANCE_TO_ALPHA.evaluate( percentTransmittance.get() ) );
                        double x = mvt.modelToViewDeltaX( cuvette.location.getX() );
                        double w = mvt.modelToViewDeltaX( cuvette.width.get() );
                        setBeamPaint( new GradientPaint( (float) x, 0, leftColor, (float) ( x + w ), 0, rightColor ) );
                    }
                }
            };
            colorUpdater.observe( light.wavelength, percentTransmittance, cuvette.width );
        }
    }

    // The right segment, portion that has passed through the cuvette
    private static class RightSegmentNode extends SegmentNode {

        public RightSegmentNode( final Light light, final Cuvette cuvette, final ModelViewTransform mvt, final ObservableProperty<Double> percentTransmittance ) {

            // resize the beam when the path length (cuvette width) changes
            cuvette.width.addObserver( new VoidFunction1<Double>() {
                public void apply( Double width ) {
                    if ( percentTransmittance.get() == 0 ) {
                        setPathTo( new Rectangle2D.Double() );
                    }
                    else {
                        double x = mvt.modelToViewDeltaX( cuvette.location.getX() + cuvette.width.get() );
                        double y = mvt.modelToViewDeltaY( light.location.getY() - ( light.lensDiameter / 2 ) );
                        double w = mvt.modelToViewDeltaX( 100 ); // very long, so that the end is way off to the right of the play area
                        double h = mvt.modelToViewDeltaY( light.lensDiameter );
                        setPathTo( new Rectangle2D.Double( x, y, w, h ) );
                    }
                }
            } );

            // Set the color to match the wavelength and transmittance.
            RichSimpleObserver updateColor = new RichSimpleObserver() {
                @Override public void update() {
                    if ( percentTransmittance.get() == 0 ) {
                        setBeamPaint( INVISIBLE_COLOR );
                    }
                    else {
                        Color color = new VisibleColor( light.wavelength.get() );
                        setBeamPaint( ColorUtils.createColor( color, (int) TRANSMITTANCE_TO_ALPHA.evaluate( percentTransmittance.get() ) ) );
                    }
                }
            };
            updateColor.observe( light.wavelength, percentTransmittance );
        }
    }
}
