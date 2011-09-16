// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.colorado.phet.common.phetcommon.view.graphics.TriColorRoundGradientPaint;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.nodes.ArrowButtonNode.Orientation.*;
import static java.awt.Color.WHITE;
import static java.awt.event.ActionEvent.ACTION_PERFORMED;
import static java.awt.geom.AffineTransform.getTranslateInstance;

/**
 * Displays a button with a pointed arrow
 * TODO: add an external shadow for use in non-black-background situations
 */
public class ArrowButtonNode extends PNode {
    public static enum Orientation {
        LEFT, RIGHT, UP, DOWN
    }

    /**
     * Color scheme for the ArrowButtonNode.  Tri-color gradients are used for each state.
     */
    public static class ColorScheme {
        public final Color upInner;
        public final Color upMiddle;
        public final Color upOuter;

        public final Color overInner;
        public final Color overMiddle;
        public final Color overOuter;

        public final Color pressedInner;
        public final Color pressedMiddle;
        public final Color pressedOuter;

        /**
         * Convenience constructor that creates a ColorScheme based on a single central color
         *
         * @param upMiddle
         */
        public ColorScheme( Color upMiddle ) {
            this( add( upMiddle, -20, -20, -20 ), upMiddle, add( upMiddle, 35, 35, 35 ),
                  add( upMiddle, -20, 20, 20 ), add( upMiddle, 0, 20, 20 ), add( upMiddle, 35, 35, 35 ),
                  add( upMiddle, 35, 35, 35 ), add( upMiddle, 0, -20, -20 ), add( upMiddle, -20, -20, -20 ) );
        }

        /**
         * Utility method to create a new color based on an old color and delta values for red, green and blue.
         *
         * @param color
         * @param red
         * @param green
         * @param blue
         * @return
         */
        private static Color add( Color color, int red, int green, int blue ) {
            return new Color( MathUtil.clamp( 0, color.getRed() + red, 255 ),
                              MathUtil.clamp( 0, color.getGreen() + green, 255 ),
                              MathUtil.clamp( 0, color.getBlue() + blue, 255 ) );
        }

        /**
         * The original gray color scheme, same as calling the convenience constructor with the arg: new Color( 220, 220, 220 )
         */
        public ColorScheme() {
            this( new Color( 200, 200, 200 ), new Color( 220, 220, 220 ), WHITE,
                  new Color( 200, 240, 240 ), new Color( 220, 240, 240 ), WHITE,
                  WHITE, new Color( 220, 220, 220 ), new Color( 200, 200, 200 ) );
        }

        /**
         * Fully explicit constructor for the ColorScheme
         *
         * @param upInner
         * @param upMiddle
         * @param upOuter
         * @param overInner
         * @param overMiddle
         * @param overOuter
         * @param pressedInner
         * @param pressedMiddle
         * @param pressedOuter
         */
        public ColorScheme( Color upInner, Color upMiddle, Color upOuter, Color overInner, Color overMiddle, Color overOuter, Color pressedInner, Color pressedMiddle, Color pressedOuter ) {
            this.upInner = upInner;
            this.upMiddle = upMiddle;
            this.upOuter = upOuter;
            this.overInner = overInner;
            this.overMiddle = overMiddle;
            this.overOuter = overOuter;
            this.pressedInner = pressedInner;
            this.pressedMiddle = pressedMiddle;
            this.pressedOuter = pressedOuter;
        }
    }

    private static final double DEFAULT_SIZE = 20;
    private static final double ARROW_PRESS_OFFSET = 1;
    private final ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

    public ArrowButtonNode( Orientation orientation ) {
        this( orientation, DEFAULT_SIZE );
    }

    public ArrowButtonNode( Orientation orientation, ColorScheme colorScheme ) {
        this( orientation, DEFAULT_SIZE, DEFAULT_SIZE / 10, colorScheme );
    }

    public ArrowButtonNode( Orientation orientation, final double size ) {
        this( orientation, size, size / 12, new ColorScheme() );
    }

    /**
     * Creates an arrow button node with the arrow pointed in a particular direction
     *
     * @param orientation  Direction of the arrow
     * @param size         Width and Height of the button
     * @param strokeRadius Half-thickness of the arrow stroke
     */
    public ArrowButtonNode( Orientation orientation, final double size, final double strokeRadius, ColorScheme colorScheme ) {
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
        Ellipse2D.Double circle = new Ellipse2D.Double( 0, 0, size, size );

        // shape of a left arrow centered inside the button. using area to combine shapes that can be used with CSG
        final Area leftArrow = new Area() {{
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
        final Shape arrow = new Function1<Orientation, Shape>() {
            public Shape apply( Orientation orientation ) {
                if ( orientation == LEFT ) { return leftArrow; }// already have the left arrow
                else if ( orientation == RIGHT ) { return new AffineTransform( -1, 0, 0, 1, size, 0 ).createTransformedShape( leftArrow ); }
                else if ( orientation == UP ) { return new AffineTransform( 0, 1, 1, 0, 0, 0 ).createTransformedShape( leftArrow ); }
                else if ( orientation == DOWN ) { return new AffineTransform( 0, -1, 1, 0, 0, size ).createTransformedShape( leftArrow ); }
                else { throw new RuntimeException( "Bad Orientation: " + orientation ); }
            }
        }.apply( orientation );

        /*---------------------------------------------------------------------------*
        * paints
        *----------------------------------------------------------------------------*/

        // gradient paints for different states
        Function3<Color, Color, Color, TriColorRoundGradientPaint> createGradient = new Function3<Color, Color, Color, TriColorRoundGradientPaint>() {
            public TriColorRoundGradientPaint apply( Color colors0, Color color1, Color color2 ) {
                return new TriColorRoundGradientPaint( colors0, color1, color2, size / 2, size * 3 / 4, size / 2.5, size / 3 );
            }
        };
        final TriColorRoundGradientPaint upGradient = createGradient.apply( colorScheme.upInner, colorScheme.upMiddle, colorScheme.upOuter );
        final TriColorRoundGradientPaint overGradient = createGradient.apply( colorScheme.overInner, colorScheme.overMiddle, colorScheme.overOuter );
        final TriColorRoundGradientPaint pressedGradient = createGradient.apply( colorScheme.pressedInner, colorScheme.pressedMiddle, colorScheme.pressedOuter );

        /*---------------------------------------------------------------------------*
        * components
        *----------------------------------------------------------------------------*/

        // add a spacer in the background so our full bounds don't change
        addChild( new Spacer( 0, 0, size + ARROW_PRESS_OFFSET, size + ARROW_PRESS_OFFSET ) );

        // make the background (circular) part of the button
        final PhetPPath background = new PhetPPath( circle ) {{
            setPaint( upGradient );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.gray );
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
        addInputEventListener( new CursorHandler() );

        // properties for button state
        final Property<Boolean> focusedProperty = new Property<Boolean>( false );
        final Property<Boolean> armedProperty = new Property<Boolean>( false );

        // when the button state changes, cause the repaint
        SimpleObserver updateObserver = new SimpleObserver() {
            public void update() {
                background.setPaint( armedProperty.get() ? pressedGradient : ( focusedProperty.get() ? overGradient : upGradient ) );
                background.setOffset( armedProperty.get() ? new Point2D.Double( ARROW_PRESS_OFFSET, ARROW_PRESS_OFFSET ) : new Point2D.Double( 0, 0 ) );
                repaint();
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
        ActionEvent event = new ActionEvent( this, ACTION_PERFORMED, "" ); // use Swing convention from AbstractButton.fireActionPerformed
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
        return getTranslateInstance( translation.getX(), translation.getY() ).createTransformedShape( shape );
    }
}