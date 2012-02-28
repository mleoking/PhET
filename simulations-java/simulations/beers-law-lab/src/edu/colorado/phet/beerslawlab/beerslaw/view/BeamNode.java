// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

import edu.colorado.phet.beerslawlab.beerslaw.model.Beam;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
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

    public BeamNode( final Beam beam, ModelViewTransform mvt ) {

        setPickable( false );
        setChildrenPickable( false );

        addChild( new LeftSegmentNode( beam, mvt ) );
        addChild( new CenterSegmentNode( beam, mvt ) );
        addChild( new RightSegmentNode( beam, mvt ) );

        // Make this node visible when beam is visible.
        beam.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );
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

        public LeftSegmentNode( Beam beam, final ModelViewTransform mvt ) {

            // Shape
            beam.leftShape.addObserver( new VoidFunction1<ImmutableRectangle2D>() {
                public void apply( ImmutableRectangle2D r ) {
                    setPathTo( mvt.modelToView( r ).toRectangle2D() );
                }
            } );

            // Paint
            beam.leftPaint.addObserver( new VoidFunction1<Paint>() {
                public void apply( Paint paint ) {
                    setBeamPaint( paint );
                }
            } );
        }
    }

    // The center segment, the portion that passes through the solution in the cuvette.
    private static class CenterSegmentNode extends SegmentNode {

        public CenterSegmentNode( Beam beam, final ModelViewTransform mvt ) {

            // Shape
            beam.centerShape.addObserver( new VoidFunction1<ImmutableRectangle2D>() {
                public void apply( ImmutableRectangle2D r ) {
                    setPathTo( mvt.modelToView( r ).toRectangle2D() );
                }
            } );

            // Paint
            beam.centerPaint.addObserver( new VoidFunction1<Paint>() {
                public void apply( Paint paint ) {
                    setBeamPaint( paint );
                }
            } );
        }
    }

    // The right segment, portion that has passed through the cuvette
    private static class RightSegmentNode extends SegmentNode {

        public RightSegmentNode( Beam beam, final ModelViewTransform mvt ) {

            // Shape
            beam.rightShape.addObserver( new VoidFunction1<ImmutableRectangle2D>() {
                public void apply( ImmutableRectangle2D r ) {
                    setPathTo( mvt.modelToView( r ).toRectangle2D() );
                }
            } );

            // Paint
            beam.rightPaint.addObserver( new VoidFunction1<Paint>() {
                public void apply( Paint paint ) {
                    setBeamPaint( paint );
                }
            } );
        }
    }
}
