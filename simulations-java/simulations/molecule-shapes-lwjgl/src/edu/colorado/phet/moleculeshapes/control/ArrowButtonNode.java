// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.TriColorRoundGradientPaint;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.umd.cs.piccolo.PNode;

/**
 * Displays a button with a pointed arrow
 * TODO: add an external shadow for use in non-black-background situations
 */
public class ArrowButtonNode extends PNode {
    public static enum Orientation {
        LEFT, RIGHT, UP, DOWN
    }

    private static final double DEFAULT_SIZE = 20;
    private static final double ARROW_PRESS_OFFSET = 1;
    private final ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

    public ArrowButtonNode( Orientation orientation ) {
        this( orientation, DEFAULT_SIZE );
    }

    public ArrowButtonNode( Orientation orientation, final double size ) {
        this( orientation, size, size / 12 );
    }

    /**
     * Creates an arrow button node with the arrow pointed in a particular direction
     *
     * @param orientation  Direction of the arrow
     * @param size         Width and Height of the button
     * @param strokeRadius Half-thickness of the arrow stroke
     */
    public ArrowButtonNode( Orientation orientation, final double size, final double strokeRadius ) {
        final double rootTwoLength = size * 0.2;

        /*---------------------------------------------------------------------------*
        * shapes
        *----------------------------------------------------------------------------*/

        // construct the center points of the arrow stroke
        final Point2D point = new Point2D.Double( size * 0.35, size * 0.5 ); // main point of the arrow
        final Point2D endTop = new Point2D.Double( point.getX() + rootTwoLength, point.getY() - rootTwoLength );
        final Point2D endBottom = new Point2D.Double( point.getX() + rootTwoLength, point.getY() + rootTwoLength );
        final double segmentLength = Math.sqrt( 2 * rootTwoLength * rootTwoLength );

        // shape of the outer circle of the button
        Ellipse2D.Double circle = new Ellipse2D.Double( -0.5, -0.5, size + 1, size + 1 ); // allow a bit extra room for the border

        // shape of a left arrow centered inside the button. using area to combine shapes that can be used with CSG
        Area leftArrow = new Area() {{
            // make the rounded parts for each point in the arrow
            Ellipse2D.Double pointCircle = new Ellipse2D.Double( -strokeRadius, -strokeRadius, strokeRadius * 2, strokeRadius * 2 );
            add( new Area( getTranslatedShape( pointCircle, point ) ) );
            add( new Area( getTranslatedShape( pointCircle, endTop ) ) );
            add( new Area( getTranslatedShape( pointCircle, endBottom ) ) );

            // add the segments connecting the rounded points in
            Double segment = new Double( 0, -strokeRadius, segmentLength, strokeRadius * 2 );
            add( new Area( new AffineTransform() {{
                translate( point.getX(), point.getY() );
                rotate( Math.PI / 4 );
            }}.createTransformedShape( segment ) ) );
            add( new Area( new AffineTransform() {{
                translate( point.getX(), point.getY() );
                rotate( -Math.PI / 4 );
            }}.createTransformedShape( segment ) ) );
        }};

        // change the orientation of the arrow if necessary
        final Shape arrow;
        switch( orientation ) {
            case LEFT:
                arrow = leftArrow; // already have the left arrow
                break;
            case RIGHT:
                arrow = new AffineTransform( -1, 0, 0, 1, size, 0 ).createTransformedShape( leftArrow );
                break;
            case UP:
                arrow = new AffineTransform( 0, 1, 1, 0, 0, 0 ).createTransformedShape( leftArrow );
                break;
            case DOWN:
                arrow = new AffineTransform( 0, -1, 1, 0, 0, size ).createTransformedShape( leftArrow );
                break;
            default:
                throw new RuntimeException( "Bad orientation: " + orientation );
        }

        /*---------------------------------------------------------------------------*
        * paints
        *----------------------------------------------------------------------------*/

        // gradient paints for different states
        Function1<Color[], TriColorRoundGradientPaint> createGradient = new Function1<Color[], TriColorRoundGradientPaint>() {
            public TriColorRoundGradientPaint apply( Color[] colors ) {
                return new TriColorRoundGradientPaint(
                        colors[0], colors[1], colors[2],
                        size / 2, size * 3 / 4,
                        size / 2.5, size / 3 );
            }
        };
        final TriColorRoundGradientPaint upGradient = createGradient.apply(
                new Color[]{new Color( 200, 200, 200 ), new Color( 220, 220, 220 ), Color.WHITE} );
        final TriColorRoundGradientPaint overGradient = createGradient.apply(
                new Color[]{new Color( 200, 240, 240 ), new Color( 220, 240, 240 ), Color.WHITE} );
        final TriColorRoundGradientPaint pressedGradient = createGradient.apply(
                new Color[]{Color.WHITE, new Color( 220, 220, 220 ), new Color( 200, 200, 200 )} );

        /*---------------------------------------------------------------------------*
        * components
        *----------------------------------------------------------------------------*/

        // add a spacer in the background so our full bounds don't change
        addChild( new Spacer( 0, 0, size + ARROW_PRESS_OFFSET, size + ARROW_PRESS_OFFSET ) );

        // make the background (circular) part of the button
        final PhetPPath background = new PhetPPath( circle ) {{
            setPaint( upGradient );
            setStrokePaint( new Color( 0, 0, 0, 0.5f ) );
        }};
        addChild( background );

        // add the main arrow color
        background.addChild( new PhetPPath( arrow ) {{
            setPaint( new Color( 60, 60, 60 ) );
            setStroke( null );
        }} );

        // add a few internal shadowing components, so it looks like the arrow is cut out of the button
        background.addChild( new PhetPPath( getInternalShadow( arrow, new Point2D.Double( 0, strokeRadius ) ) ) {{
            setPaint( new Color( 30, 30, 30 ) );
            setStroke( null );
        }} );
        background.addChild( new PhetPPath( getInternalShadow( arrow, new Point2D.Double( 0, strokeRadius * 3 / 4 ) ) ) {{
            setPaint( new Color( 0, 0, 0 ) );
            setStroke( null );
        }} );

        /*---------------------------------------------------------------------------*
        * event handling
        *----------------------------------------------------------------------------*/

        // handle JME cursors properly. (if pulling to non-JME code, use the CursorHandler)
        addInputEventListener( new LWJGLCursorHandler() );

        // properties for button state
        final Property<Boolean> focusedProperty = new Property<Boolean>( false );
        final Property<Boolean> armedProperty = new Property<Boolean>( false );

        // when the button state changes, cause the repaint
        SimpleObserver updateObserver = new SimpleObserver() {
            public void update() {
                background.setPaint( armedProperty.get() ? pressedGradient : ( focusedProperty.get() ? overGradient : upGradient ) );
                background.setOffset( armedProperty.get() ? new Point2D.Double( ARROW_PRESS_OFFSET, ARROW_PRESS_OFFSET ) : new Point2D.Double( 0, 0 ) );
            }
        };
        focusedProperty.addObserver( updateObserver, false );
        armedProperty.addObserver( updateObserver ); // call it once

        // hook our properties up to the button handler
        addInputEventListener( new ButtonEventHandler() {{
            addButtonEventListener( new ButtonEventListener() {
                public void setFocus( boolean focus ) {
                    focusedProperty.set( focus );
                }

                public void setArmed( boolean armed ) {
                    armedProperty.set( armed );
                }

                public void fire() {
                    notifyActionPerformed();
                }
            } );
        }} );
    }

    /*---------------------------------------------------------------------------*
    * Swing button helpers, copied from ButtonNode
    *----------------------------------------------------------------------------*/

    public void addActionListener( ActionListener listener ) {
        actionListeners.add( listener );
    }

    public void removeActionListener( ActionListener listener ) {
        actionListeners.remove( listener );
    }

    private void notifyActionPerformed() {
        ActionEvent event = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, "" ); // use Swing convention from AbstractButton.fireActionPerformed
        for ( ActionListener actionListener : new ArrayList<ActionListener>( actionListeners ) ) {
            actionListener.actionPerformed( event );
        }
    }

    /*---------------------------------------------------------------------------*
    * shape utilities
    *----------------------------------------------------------------------------*/

    private static Shape getInternalShadow( final Shape shape, final Point2D translation ) {
        return new Area() {{
            add( new Area( shape ) );
            subtract( new Area( getTranslatedShape( shape, translation ) ) );
        }};
    }

    private static Shape getTranslatedShape( Shape shape, Point2D translation ) {
        return AffineTransform.getTranslateInstance( translation.getX(), translation.getY() ).createTransformedShape( shape );
    }
}
