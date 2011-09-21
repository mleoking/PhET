// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.faucet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Or;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.TriColorRoundGradientPaint;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Displays a button with a pointed arrow
 * TODO: add an external shadow for use in non-black-background situations
 */
public class KnobNode extends PNode {
    // properties for button state
    private final Property<Boolean> enabled = new Property<Boolean>( true );
    private final BooleanProperty entered = new BooleanProperty( false );
    private final BooleanProperty pressed = new BooleanProperty( false );
    private final Or focused = entered.or( pressed );

    //Store the cursor handler and moused-over component so the cursor can be changed from a hand to an arrow when the ArrowButtonNode becomes disabled
    private final CursorHandler cursorHandler = new CursorHandler();
    private PComponent component;

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

        public final Color disabledInner;
        public final Color disabledMiddle;
        public final Color disabledOuter;

        /**
         * Convenience constructor that creates a ColorScheme based on a single central color
         *
         * @param upMiddle
         */
        public ColorScheme( Color upMiddle ) {
            this( add( upMiddle, -20, -20, -20 ), upMiddle, add( upMiddle, 35, 35, 35 ),
                  add( upMiddle, -20, 20, 20 ), add( upMiddle, 0, 20, 20 ), add( upMiddle, 35, 35, 35 ),
                  add( upMiddle, 35, 35, 35 ), add( upMiddle, 0, -20, -20 ), add( upMiddle, -20, -20, -20 ),
                  Color.white, Color.lightGray, Color.white );
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
         * The original gray color scheme.
         */
        public ColorScheme() {
            this( new Color( 220, 220, 220 ) );
        }

        /**
         * Fully explicit constructor for the ColorScheme
         */
        public ColorScheme( Color upInner, Color upMiddle, Color upOuter, Color overInner, Color overMiddle, Color overOuter, Color pressedInner, Color pressedMiddle, Color pressedOuter, Color disabledInner, Color disabledMiddle, Color disabledOuter ) {
            this.upInner = upInner;
            this.upMiddle = upMiddle;
            this.upOuter = upOuter;
            this.overInner = overInner;
            this.overMiddle = overMiddle;
            this.overOuter = overOuter;
            this.disabledInner = disabledInner;
            this.disabledMiddle = disabledMiddle;
            this.disabledOuter = disabledOuter;
        }
    }

    private static final double DEFAULT_SIZE = 20;
    private static final double ARROW_PRESS_OFFSET = 1;

    public KnobNode() {
        this( DEFAULT_SIZE );
    }

    public KnobNode( ColorScheme colorScheme ) {
        this( DEFAULT_SIZE, colorScheme );
    }

    public KnobNode( final double size ) {
        this( size, new ColorScheme() );
    }

    /**
     * Creates an arrow button node with the arrow pointed in a particular direction
     *
     * @param size Width and Height of the button
     */
    public KnobNode( final double size, ColorScheme colorScheme ) {

        // shape of the outer circle of the button
        Ellipse2D.Double circle = new Ellipse2D.Double( 0, 0, size, size );

        /*---------------------------------------------------------------------------*
        * paints
        *----------------------------------------------------------------------------*/

        // gradient paints for different states
        Function3<Color, Color, Color, TriColorRoundGradientPaint> createGradient = new Function3<Color, Color, Color, TriColorRoundGradientPaint>() {
            public TriColorRoundGradientPaint apply( Color colors0, Color color1, Color color2 ) {
                return new TriColorRoundGradientPaint( colors0, color1, color2, size / 2, size * 3 / 4, size / 2.5, size / 3 );
            }
        };
        final TriColorRoundGradientPaint normalGradient = createGradient.apply( colorScheme.upInner, colorScheme.upMiddle, colorScheme.upOuter );
        final TriColorRoundGradientPaint focusGradient = createGradient.apply( colorScheme.overInner, colorScheme.overMiddle, colorScheme.overOuter );
        final TriColorRoundGradientPaint disabledGradient = createGradient.apply( colorScheme.disabledInner, colorScheme.disabledMiddle, colorScheme.disabledOuter );

        /*---------------------------------------------------------------------------*
        * components
        *----------------------------------------------------------------------------*/

        // add a spacer in the background so our full bounds don't change
        addChild( new Spacer( 0, 0, size + ARROW_PRESS_OFFSET, size + ARROW_PRESS_OFFSET ) );

        // make the background (circular) part of the button
        final PhetPPath background = new PhetPPath( circle ) {{
            setPaint( normalGradient );
            setStroke( new BasicStroke( 0.3f ) );
            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean enabled ) {
                    setStrokePaint( enabled ? Color.gray : Color.lightGray );
                }
            } );
        }};
        addChild( background );

        /*---------------------------------------------------------------------------*
        * event handling
        *----------------------------------------------------------------------------*/

        // handle JME cursors properly. (if pulling to non-JME code, use the CursorHandler)
        addInputEventListener( cursorHandler );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseEntered( PInputEvent event ) {
                entered.set( true );
            }

            @Override public void mousePressed( PInputEvent event ) {
                pressed.set( true );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                pressed.set( false );
            }

            @Override public void mouseExited( PInputEvent event ) {
                entered.set( false );
            }
        } );

        // when the button state changes, cause the repaint

        new RichSimpleObserver() {
            @Override public void update() {
                System.out.println( "enabled.get = " + enabled.get() + "< focused = " + focused.get() );
                background.setPaint( !enabled.get() ? disabledGradient : ( focused.get() ? focusGradient : normalGradient ) );
            }
        }.observe( focused, enabled );

        //Store the component so the mouse can be changed from hand to arrow when disabled
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseMoved( PInputEvent event ) {
                component = event.getComponent();
            }
        } );
    }

    /**
     * Sets whether the ArrowButtonNode is enabled.  Disabling grays it out and makes it ignore mouse interaction.
     *
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        this.enabled.set( enabled );
        setPickable( enabled );
        setChildrenPickable( enabled );
        if ( component != null && !enabled ) {
            cursorHandler.mouseExited( (JComponent) component );
        }
    }
}