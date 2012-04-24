// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.fractions.util.FJUtils.ord;
import static fj.data.List.iterableList;
import static java.lang.Math.abs;

/**
 * Shows a number line and a dot on the number line to represent a fraction.
 * The dot is draggable to change the fraction.  The number line is truncated at 6 (the max number for fraction values) so it won't look weird at odd aspect ratios.
 *
 * @author Sam Reid
 */
public class NumberLineNode extends PNode {

    //Location -> tick index
    private ArrayList<Pair<Double, Integer>> tickLocations;

    //When tick marks change, clear everything except the green circle--it has to be persisted across recreations of the number line because the user interacts with it
    private final PhetPPath circle;
    private final SettableProperty<Integer> settableNumerator;
    private final ObservableProperty<Integer> denominator;
    private final IntegerProperty max;
    private final Orientation orientation;
    private final PhetPPath arrowPath;

    public static abstract class Orientation {
        public abstract Vector2D get( final double x, final double y );

        public Line2D.Double line( double x0, double y0, double x1, double y1 ) { return get( x0, y0 ).lineTo( get( x1, y1 ) ); }

        public abstract Vector2D fromUserSpace( final double x, final double y );

        public abstract Point2D getPositionForPText( PNode path, final PNode node );
    }

    public static class Horizontal extends Orientation {
        @Override public Vector2D get( final double x, final double y ) { return new Vector2D( x, y ); }

        @Override public Vector2D fromUserSpace( final double x, final double y ) { return new Vector2D( x, y ); }

        //Put the text below the tick mark
        @Override public Point2D getPositionForPText( PNode path, final PNode node ) {
            return new Point2D.Double( path.getFullBounds().getCenterX() - node.getFullBounds().getWidth() / 2, path.getFullBounds().getMaxY() );
        }
    }

    public static class Vertical extends Orientation {
        @Override public Vector2D get( final double x, final double y ) { return new Vector2D( y, -x ); }

        @Override public Vector2D fromUserSpace( final double x, final double y ) { return new Vector2D( -y, x ); }

        //Put the text to the right of the tick mark
        @Override public Point2D getPositionForPText( final PNode path, final PNode node ) {
            return new Point2D.Double( path.getFullBounds().getMaxX(), path.getFullBounds().getCenterY() - node.getFullBounds().getHeight() / 2 );
        }
    }

    public NumberLineNode( final ObservableProperty<Integer> numerator, final SettableProperty<Integer> settableNumerator, final ObservableProperty<Integer> denominator, ValueEquals<Representation> showing, final IntegerProperty max, final Orientation orientation, final double distanceBetweenTicks, Color color, final boolean arrowPathVisible ) {
        this.settableNumerator = settableNumerator;
        this.denominator = denominator;
        this.max = max;
        this.orientation = orientation;
        final double scale = 5;
        scale( scale );
        showing.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                setVisible( aBoolean );
            }
        } );

        final RichSimpleObserver mainObserver = new RichSimpleObserver() {
            @Override public void update() {

                removeAllChildren();

                //Only render if visible, visibility flags set in RepresentationNode
                if ( !getVisible() ) {
                    return;
                }

                final ArrayList<PNode> highlights = new ArrayList<PNode>();


                //always go the same distance to whole numbers
                int divisionsBetweenTicks = denominator.get();

                double dx = distanceBetweenTicks / divisionsBetweenTicks;

                //The number line itself
                addChild( new PhetPPath( orientation.line( 0, 0, dx * max.get() * divisionsBetweenTicks, 0 ) ) );

                //For snapping
                tickLocations = new ArrayList<Pair<Double, Integer>>();

                for ( int i = 0; i <= divisionsBetweenTicks * max.get(); i++ ) {

                    //Major ticks at each integer
                    final BasicStroke highlightStroke = new BasicStroke( 4 );
                    if ( i % divisionsBetweenTicks == 0 ) {
                        int div = i / divisionsBetweenTicks;
                        final int mod = div % 2;
                        double height = mod == 0 ? 8 : 8;
                        final BasicStroke stroke = mod == 0 ? new BasicStroke( 1 ) : new BasicStroke( 0.5f );
                        final PhetPPath path = new PhetPPath( orientation.line( i * dx, -height, i * dx, height ), stroke, Color.black );

                        final boolean highlightVisible = numerator.get().equals( i );
                        if ( highlightVisible ) {
                            final PhetPPath highlightPath = new PhetPPath( orientation.line( i * dx, -height, i * dx, height ), highlightStroke, Color.yellow );
                            highlightPath.setVisible( highlightVisible );
                            highlightPath.setPickable( highlightVisible );
                            addChild( highlightPath );
                            highlights.add( highlightPath );
                        }
                        addChild( path );
                        addChild( new PhetPText( div + "", new PhetFont( 8 ) ) {{
                            setOffset( orientation.getPositionForPText( path, this ) );
                        }} );

                        //make it so the green handle can snap to this site
                        tickLocations.add( new Pair<Double, Integer>( i * dx, i ) );
                    }

                    //Minor ticks
                    else {

                        final boolean highlightVisible = numerator.get().equals( i );
                        if ( highlightVisible ) {
                            final PhetPPath highlightPath = new PhetPPath( orientation.line( i * dx, -4, i * dx, 4 ), highlightStroke, Color.yellow );
                            highlights.add( highlightPath );
                            addChild( highlightPath );
                        }

                        //minor ticks between the integers
                        addChild( new PhetPPath( orientation.line( i * dx, -4, i * dx, 4 ), new BasicStroke( 0.25f ), Color.black ) );

                        //make it so the green handle can snap to this site
                        tickLocations.add( new Pair<Double, Integer>( i * dx, i ) );
                    }
                }
                for ( PNode highlight : highlights ) {
                    highlight.moveToBack();
                }

                //Allow the user to click anywhere in the area to set the numerator value
                //Have to subtract out the offset of this node (set in RepresentationControlPanel) or everything is off by a little bit.  I don't know why!
                final PBounds fullBounds = getFullBounds();
                fullBounds.setOrigin( fullBounds.getX() - getOffset().getX(), fullBounds.getY() - getOffset().getY() );
                addChild( new PhetPPath( AffineTransform.getScaleInstance( 1.0 / scale, 1.0 / scale ).createTransformedShape( fullBounds ), new Color( 0, 0, 0, 0 ), new BasicStroke( 1 ), new Color( 0, 0, 0, 0 ) ) {{
                    addInputEventListener( new CursorHandler() );
                    addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mousePressed( PInputEvent event ) {
                            handleMousePress( event );
                        }
                    } );
                }} );

                if ( circle != null ) {
                    circle.setOffset( orientation.get( (double) numerator.get() / denominator.get() * distanceBetweenTicks, 0 ).toPoint2D() );
                    addChild( circle );
                }

                if ( arrowPath != null && highlights.size() > 0 ) {
                    arrowPath.setPathTo( createArrowShape( scale, highlights ) );
                    arrowPath.setVisible( arrowPathVisible );
                    addChild( arrowPath );
                }
            }
        };
        mainObserver.observe( numerator, denominator, max );

        //Green circle in the middle of it all
        final double w = 5;
        final double w2 = 0;
        final BasicStroke borderStroke = new BasicStroke( 0.6f );
        circle = new PhetPPath( new Area( new Ellipse2D.Double( -w / 2, -w / 2, w, w ) ) {{
            subtract( new Area( new Ellipse2D.Double( -w2 / 2, -w2 / 2, w2, w2 ) ) );
        }}, color, borderStroke, Color.black ) {{

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new SimSharingDragHandler( Components.numberLineKnob, UserComponentTypes.sprite, true ) {
                @Override protected void startDrag( final PInputEvent event ) {
                    super.startDrag( event );
                    handleMousePress( event );
                }

                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    handleMousePress( event );
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );
                    handleMousePress( event );
                }
            } );
        }};
        addChild( circle );

        //Arrow pointing to the location (for when tick marks get dense)
        arrowPath = new PhetPPath( color, new BasicStroke( borderStroke.getLineWidth() / 2f ), Color.black ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new SimSharingDragHandler( Components.numberLineArrow, UserComponentTypes.sprite, true ) {
                @Override protected void startDrag( final PInputEvent event ) {
                    super.startDrag( event );
                    handleMousePress( event );
                }

                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    handleMousePress( event );
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );
                    handleMousePress( event );
                }
            } );
        }};
        addChild( arrowPath );

        mainObserver.update();
    }

    private static GeneralPath createArrowShape( final double scale, final ArrayList<PNode> highlights ) {
        final PBounds highlightBounds = highlights.get( 0 ).getFullBounds();
        Vector2D tickA = new Vector2D( highlightBounds.getX() + 1, highlightBounds.getCenterY() );
        double headWidth = 10.0 / scale;
        return new Arrow( tickA.plus( -5, 0 ).toPoint2D(), tickA.toPoint2D(), headWidth, headWidth, headWidth / 2.0 ).getShape();
    }

    protected void handleMousePress( PInputEvent event ) {
        final Point2D d = event.getPositionRelativeTo( event.getPickedNode().getParent() );
        final Vector2D pt = orientation.fromUserSpace( d.getX(), d.getY() );

        final Ord<Pair<Double, Integer>> closest = ord( new F<Pair<Double, Integer>, Double>() {
            @Override public Double f( final Pair<Double, Integer> p ) {
                return abs( p._1 - pt.getX() );
            }
        } );
        final List<Pair<Double, Integer>> list = iterableList( tickLocations ).filter( new F<Pair<Double, Integer>, Boolean>() {
            @Override public Boolean f( Pair<Double, Integer> p ) {
                return p._2 <= max.get() * denominator.get();
            }
        } ).sort( closest );

        if ( settableNumerator != null ) {
            settableNumerator.set( list.head()._2 );
        }
    }
}