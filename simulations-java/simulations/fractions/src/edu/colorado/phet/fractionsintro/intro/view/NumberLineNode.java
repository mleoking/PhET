// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Shows a number line and a dot on the number line to represent a fraction.
 * The dot is draggable to change the fraction.  The number line is truncated at 6 (the max number for fraction values) so it won't look weird at odd aspect ratios.
 *
 * @author Sam Reid
 */
public class NumberLineNode extends PNode {

    private ArrayList<Pair<Double, Integer>> tickLocations;

    //When tick marks change, clear everything except the green circle--it has to be persisted across recreations of the number line because the user interacts with it
    private final PhetPPath greenCircle;

    public NumberLineNode( final IntegerProperty numerator, final IntegerProperty denominator, ValueEquals<Representation> showing ) {
        final double scale = 5;
        scale( scale );
        showing.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                setVisible( aBoolean );
            }
        } );

        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();

                //always go the same distance to whole numbers
                final double distanceBetweenTicks = 32;
                int divisionsBetweenTicks = denominator.get();

                double dx = distanceBetweenTicks / divisionsBetweenTicks;

                //The number line itself
                addChild( new PhetPPath( new Line2D.Double( 0, 0, dx * 6 * divisionsBetweenTicks, 0 ) ) );

                //For snapping
                tickLocations = new ArrayList<Pair<Double, Integer>>();

                for ( int i = 0; i <= divisionsBetweenTicks * 6; i++ ) {

                    final int finalI = i;

                    //Major ticks at each integer
                    if ( i % divisionsBetweenTicks == 0 ) {
                        int div = i / divisionsBetweenTicks;
                        final int mod = div % 2;
                        double height = mod == 0 ? 8 : 8;
                        final BasicStroke stroke = mod == 0 ? new BasicStroke( 1 ) : new BasicStroke( 0.5f );
                        final PhetPPath path = new PhetPPath( new Line2D.Double( i * dx, -height, i * dx, height ), stroke, Color.black );
                        final PhetPPath highlightPath = new PhetPPath( new Line2D.Double( i * dx, -height, i * dx, height ), new BasicStroke( 4 ), Color.yellow );

                        new RichSimpleObserver() {
                            @Override public void update() {
                                final boolean visible = numerator.get().equals( finalI );
                                highlightPath.setVisible( visible );
                                highlightPath.setPickable( visible );
                            }
                        }.observe( numerator, denominator );
                        addChild( highlightPath );
                        addChild( path );
                        addChild( new PhetPText( div + "", new PhetFont( 8 ) ) {{
                            setOffset( path.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, path.getFullBounds().getMaxY() );
                        }} );

                        //make it so the green handle can snap to this site
                        tickLocations.add( new Pair<Double, Integer>( i * dx, i ) );
                    }

                    //Minor ticks
                    else {

                        final PhetPPath highlightPath = new PhetPPath( new Line2D.Double( i * dx, -4, i * dx, 4 ), new BasicStroke( 4 ), Color.yellow );

                        new RichSimpleObserver() {
                            @Override public void update() {
                                highlightPath.setVisible( numerator.get().equals( finalI ) );
                            }
                        }.observe( numerator, denominator );
                        addChild( highlightPath );

                        //minor ticks between the integers
                        addChild( new PhetPPath( new Line2D.Double( i * dx, -4, i * dx, 4 ), new BasicStroke( 0.25f ), Color.black ) );

                        //make it so the green handle can snap to this site
                        tickLocations.add( new Pair<Double, Integer>( i * dx, i ) );
                    }
                }


                //Allow the user to click anywhere in the area to set the numerator value
                //Have to subtract out the offset of this node (set in RepresentationControlPanel) or everything is off by a little bit.  I don't know why!
                final PBounds fullBounds = getFullBounds();
                fullBounds.setOrigin( fullBounds.getX() - getOffset().getX(), fullBounds.getY() - getOffset().getY() );
                addChild( new PhetPPath( AffineTransform.getScaleInstance( 1.0 / scale, 1.0 / scale ).createTransformedShape( fullBounds ), new Color( 0, 0, 0, 0 ), new BasicStroke( 1 ), new Color( 0, 0, 0, 0 ) ) {{
                    addInputEventListener( new CursorHandler() );
                    addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mousePressed( PInputEvent event ) {
                            handleMousePress( event, numerator );
                        }
                    } );
                }} );

                if ( greenCircle != null ) {
                    greenCircle.setOffset( (double) numerator.get() / denominator.get() * distanceBetweenTicks, 0 );
                    addChild( greenCircle );
                }
            }
        }.observe( numerator, denominator );

        //Green circle in the middle of it all
        final double w = 5;
        final double w2 = 0;
        greenCircle = new PhetPPath( new Area( new Ellipse2D.Double( -w / 2, -w / 2, w, w ) ) {{
            subtract( new Area( new Ellipse2D.Double( -w2 / 2, -w2 / 2, w2, w2 ) ) );
        }}, FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 0.6f ), Color.black ) {{

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseDragged( PInputEvent event ) {
                    handleMousePress( event, numerator );
                }
            } );
        }};
        addChild( greenCircle );
    }

    private void handleMousePress( PInputEvent event, IntegerProperty numerator ) {
        final Point2D newPressPoint = event.getPositionRelativeTo( event.getPickedNode().getParent() );

        //whichever tick mark is closer, go to that one
        ArrayList<Pair<Double, Integer>> sorted = new ArrayList<Pair<Double, Integer>>( tickLocations );
        Collections.sort( sorted, new Comparator<Pair<Double, Integer>>() {
            public int compare( Pair<Double, Integer> o1, Pair<Double, Integer> o2 ) {
                return Double.compare( Math.abs( o1._1 - newPressPoint.getX() ), Math.abs( o2._1 - newPressPoint.getX() ) );
            }
        } );
        numerator.set( sorted.get( 0 )._2 );
    }
}