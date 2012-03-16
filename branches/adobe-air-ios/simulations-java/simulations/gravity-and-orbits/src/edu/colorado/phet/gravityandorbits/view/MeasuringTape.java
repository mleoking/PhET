// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
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
 * Rewrote instead of using pre-existing piccolo measuring tape for improved compatibility with Property<T> paradigm and reported problems using the other scale at multiple scale (not sure why)
 *
 * @author Sam Reid
 */
public class MeasuringTape extends PNode {
    private static final double CROSS_HAIR_RADIUS = 4;
    public static double METERS_PER_MILE = 0.000621371192;

    public MeasuringTape( final ObservableProperty<Boolean> visible,
                          final Property<ImmutableVector2D> modelStart,
                          final Property<ImmutableVector2D> modelEnd,
                          final Property<ModelViewTransform> transform ) {
        //The line that represents the "tape" part of the measuring tape
        addChild( new PhetPPath( new BasicStroke( 3 ), Color.gray ) {{
            setPickable( false );
            new RichSimpleObserver() {
                public void update() {
                    setPathTo( new Line2D.Double( transform.get().modelToView( modelStart.get() ).toPoint2D(),
                                                  transform.get().modelToView( modelEnd.get() ).toPoint2D() ) );
                }
            }.observe( modelStart, modelEnd, transform );
        }} );

        //The body of the measuring tape
        try {
            addChild( createMeasuringTapeBody( modelStart, modelEnd, transform ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        addChild( new CrossHairGraphic( modelStart, transform ) );
        addChild( new CrossHairGraphic( modelEnd, transform ) );

        //The textual (numeric) readout
        addChild( createTextReadout( modelStart, modelEnd, transform ) );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.get() );
            }
        } );
    }

    private PText createTextReadout( final Property<ImmutableVector2D> modelStart, final Property<ImmutableVector2D> modelEnd, final Property<ModelViewTransform> transform ) {
        return new PText( "Hello" ) {{  //Dummy string to get the layout right
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.white );
            setPickable( false );
            final SimpleObserver updateOffset = new SimpleObserver() {
                public void update() {
                    final Point2D offset = transform.get().modelToView( modelStart.get().toPoint2D() );
                    setOffset( offset.getX() - getFullBounds().getWidth() / 2, offset.getY() + CROSS_HAIR_RADIUS );
                }
            };
            transform.addObserver( updateOffset );
            modelStart.addObserver( updateOffset );
            final SimpleObserver updateReadout = new SimpleObserver() {
                public void update() {
                    double modelDistance = modelStart.get().getDistance( modelEnd.get() );
                    //use a scale that makes sense in all modes
                    double miles = metersToMiles( modelDistance );
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
        }};
    }

    private PImage createMeasuringTapeBody( final Property<ImmutableVector2D> modelStart, final Property<ImmutableVector2D> modelEnd, final Property<ModelViewTransform> transform ) throws IOException {
        return new PImage( ImageLoader.loadBufferedImage( "piccolo-phet/images/measuringTape.png" ) ) {{
            final SimpleObserver updateBody = new SimpleObserver() {
                public void update() {
                    setTransform( new AffineTransform() );
                    Point2D.Double offset = transform.get().modelToView( modelStart.get() ).toPoint2D();
                    translate( offset.getX() - getFullBounds().getWidth(), offset.getY() - getFullBounds().getHeight() + CROSS_HAIR_RADIUS );
                    final ImmutableVector2D delta = new ImmutableVector2D( modelStart.get().toPoint2D(), modelEnd.get().toPoint2D() );
                    double angle = new ImmutableVector2D( delta.getX(), -delta.getY() ).getAngle();//invert coordinate frame
                    rotateAboutPoint( angle, getFullBounds().getWidth(), getFullBounds().getHeight() - CROSS_HAIR_RADIUS );
                }
            };
            modelStart.addObserver( updateBody );
            modelEnd.addObserver( updateBody );
            transform.addObserver( updateBody );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new DragHandler( this, transform, modelStart, modelEnd ) );
        }};
    }

    public static double metersToMiles( double modelDistance ) {
        return modelDistance * METERS_PER_MILE;
    }

    public static double milesToMeters( double modelDistance ) {
        return modelDistance / METERS_PER_MILE;
    }

    private static class CrossHairGraphic extends PNode {
        public CrossHairGraphic( final Property<ImmutableVector2D> point, final Property<ModelViewTransform> transform ) {
            addChild( new PhetPPath( new Ellipse2D.Double( -CROSS_HAIR_RADIUS, -CROSS_HAIR_RADIUS, CROSS_HAIR_RADIUS * 2, CROSS_HAIR_RADIUS * 2 ), new Color( 0, 0, 0, 0 ) ) );
            addChild( new PhetPPath( new Line2D.Double( -CROSS_HAIR_RADIUS, 0, CROSS_HAIR_RADIUS, 0 ), new BasicStroke( 2 ), PhetColorScheme.RED_COLORBLIND ) );
            addChild( new PhetPPath( new Line2D.Double( 0, -CROSS_HAIR_RADIUS, 0, CROSS_HAIR_RADIUS ), new BasicStroke( 2 ), PhetColorScheme.RED_COLORBLIND ) );
            final SimpleObserver updateOffset = new SimpleObserver() {
                public void update() {
                    setOffset( transform.get().modelToView( point.get() ).toPoint2D() );
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
            Dimension2D delta = transform.get().viewToModelDelta( event.getDeltaRelativeTo( node.getParent() ) );
            for ( Property<ImmutableVector2D> point : points ) {
                point.set( new ImmutableVector2D( point.get().getX() + delta.getWidth(), point.get().getY() + delta.getHeight() ) );
            }
        }
    }
}
