// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows a piccolo measuring tape that works in multiple scales.  Its body and tip can be moved, and it provides a readout in the appropriate units.
 *
 * @author Sam Reid
 */
public class MeasuringTape extends PNode {
    static double crossHairsRadius = 4;

    public MeasuringTape( final Property<Boolean> visible, final Property<ImmutableVector2D> modelStart, final Property<ImmutableVector2D> modelEnd, final Property<ModelViewTransform> transform ) {
        addChild( new PhetPPath( new BasicStroke( 3 ), Color.darkGray ) {{
            setPickable( false );
            final SimpleObserver updateTape = new SimpleObserver() {
                public void update() {
                    setPathTo( new Line2D.Double( transform.getValue().modelToView( modelStart.getValue() ).toPoint2D(),
                                                  transform.getValue().modelToView( modelEnd.getValue() ).toPoint2D() ) );
                }
            };
            modelStart.addObserver( updateTape );
            modelEnd.addObserver( updateTape );
            transform.addObserver( updateTape );
        }} );
        try {
            addChild( new PImage( ImageLoader.loadBufferedImage( edu.colorado.phet.common.piccolophet.nodes.MeasuringTape.MEASURING_TAPE_IMAGE ) ) {{
                final SimpleObserver updateBody = new SimpleObserver() {
                    public void update() {
                        setTransform( new AffineTransform() );
                        Point2D.Double offset = transform.getValue().modelToView( modelStart.getValue() ).toPoint2D();
                        translate( offset.getX() - getFullBounds().getWidth(), offset.getY() - getFullBounds().getHeight() + crossHairsRadius );
                        final ImmutableVector2D delta = new ImmutableVector2D( modelStart.getValue().toPoint2D(), modelEnd.getValue().toPoint2D() );
                        double angle = new ImmutableVector2D( delta.getX(), -delta.getY() ).getAngle();//invert coordinate frame
                        rotateAboutPoint( angle, getFullBounds().getWidth(), getFullBounds().getHeight() - crossHairsRadius );
                    }
                };
                modelStart.addObserver( updateBody );
                modelEnd.addObserver( updateBody );
                transform.addObserver( updateBody );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new DragHandler( this, transform, modelStart, modelEnd ) );
            }} );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        addChild( new CrossHairGraphic( modelStart, transform ) );
        addChild( new CrossHairGraphic( modelEnd, transform ) );

        addChild( new PText( "Hello" ) {{
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.white );
            setPickable( false );
            final SimpleObserver updateOffset = new SimpleObserver() {
                public void update() {
                    final Point2D offset = transform.getValue().modelToView( modelStart.getValue().toPoint2D() );
                    setOffset( offset.getX() - getFullBounds().getWidth() / 2, offset.getY() + crossHairsRadius );
                }
            };
            transform.addObserver( updateOffset );
            modelStart.addObserver( updateOffset );
            final SimpleObserver updateReadout = new SimpleObserver() {
                public void update() {
                    double modelDistance = modelStart.getValue().getDistance( modelEnd.getValue() );
                    //use a scale that makes sense in all modes
                    double miles = modelDistance * 0.000621371192;
                    double thousandMiles = miles / 1E3;
                    String valueString = new Function1<Double, DecimalFormat>() {
                        public DecimalFormat apply( Double value ) {
                            if ( value < 0.01 ) {return new DecimalFormat( "0" );}
                            if ( value < 10 ) { return new DecimalFormat( "0.0" ); }
                            else { return new DecimalFormat( "0" ); }
                        }
                    }.apply( thousandMiles ).format( thousandMiles );
                    setText( MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, valueString, GAOStrings.THOUSAND_MILES ) );
                    updateOffset.update();
                }
            };
            modelStart.addObserver( updateReadout );
            modelEnd.addObserver( updateReadout );
        }} );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
    }

    public static class CrossHairGraphic extends PNode {

        public CrossHairGraphic( final Property<ImmutableVector2D> point, final Property<ModelViewTransform> transform ) {
            addChild( new PhetPPath( new Ellipse2D.Double( -crossHairsRadius, -crossHairsRadius, crossHairsRadius * 2, crossHairsRadius * 2 ), new Color( 0, 0, 0, 0 ) ) );
            addChild( new PhetPPath( new Line2D.Double( -crossHairsRadius, 0, crossHairsRadius, 0 ), new BasicStroke( 2 ), Color.red ) );
            addChild( new PhetPPath( new Line2D.Double( 0, -crossHairsRadius, 0, crossHairsRadius ), new BasicStroke( 2 ), Color.red ) );
            final SimpleObserver updateOffset = new SimpleObserver() {
                public void update() {
                    setOffset( transform.getValue().modelToView( point.getValue() ).toPoint2D() );
                }
            };
            point.addObserver( updateOffset );
            transform.addObserver( updateOffset );

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new DragHandler( this, transform, point ) );
        }
    }

    private static class DragHandler extends PBasicInputEventHandler {
        private final PNode node;
        private final Property<ModelViewTransform> transform;
        private final Property<ImmutableVector2D>[] points;

        private DragHandler( PNode node, Property<ModelViewTransform> transform, Property<ImmutableVector2D>... points ) {
            this.node = node;
            this.transform = transform;
            this.points = points;
        }

        public void mouseDragged( PInputEvent event ) {
            Dimension2D delta = transform.getValue().viewToModelDelta( event.getDeltaRelativeTo( node.getParent() ) );
            for ( Property<ImmutableVector2D> point : points ) {
                point.setValue( new ImmutableVector2D( point.getValue().getX() + delta.getWidth(), point.getValue().getY() + delta.getHeight() ) );
            }
        }
    }
}
