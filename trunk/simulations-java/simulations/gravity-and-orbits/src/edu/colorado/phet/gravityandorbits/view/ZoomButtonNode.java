// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

//REVIEW yikes, big chunk of copied code here. Generalize and share with other sims that have this control. This deserves an Unfuddle ticket (but I didn't create one.)
//REVIEW unclear from the doc why "custom handling of bounds" was needed. Please elaborate.

/**
 * Button used in the zoom control.
 * Copied from ButtonNode to add custom handling of bounds.  Maybe this could be integrated back into ButtonNode?
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ZoomButtonNode extends PhetPNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final double COLOR_SCALING_FACTOR = 0.5;
    private static final double BUTTON_CORNER_ROUNDEDNESS = 10;
    private static final int SHADOW_OFFSET = 3;

    // button enabled properties
    private static final Color ENABLED_STROKE_COLOR = Color.BLACK;
    private static final Color ENABLED_SHADOW_COLOR = new Color( 0f, 0f, 0f, 0.2f ); // transparent so that it's invisible

    // button disabled properties
    private static final Color DISABLED_TEXT_COLOR = new Color( 180, 180, 180 );
    private static final Color DISABLED_BACKGROUND_COLOR = new Color( 210, 210, 210 );
    private static final Color DISABLED_STROKE_COLOR = new Color( 190, 190, 190 );
    private static final Color DISABLED_SHADOW_COLOR = new Color( 0, 0, 0, 0 );

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // immutable
    private final EventListenerList _listeners;
    private final Color _backgroundColor;
    private final PText _htmlNode;
    private final PPath _zoomButtonNode;
    private final PPath _shadowNode;

    // mutable
    private Color _textColor;
    private boolean _enabled;

    public ZoomButtonNode( PText htmlLabelNode, Color textColor, Color backgroundColor, double buttonWidth, double buttonHeight ) {
        this._htmlNode = htmlLabelNode;
        _htmlNode.setOffset( buttonWidth / 2 - _htmlNode.getFullBounds().getWidth() / 2, buttonHeight / 2 - _htmlNode.getFullBounds().getHeight() / 2 );

        this._textColor = textColor;
        this._backgroundColor = backgroundColor;
        this._enabled = true;

        // Initialize local data.
        _listeners = new EventListenerList();

        final Paint mouseNotOverGradient = createMouseNotOverGradient();
        final Paint mouseOverGradient = createMouseOverGradient();

        // button
        RoundRectangle2D buttonShape = new RoundRectangle2D.Double( 0, 0,
                                                                    buttonWidth,
                                                                    buttonHeight,
                                                                    BUTTON_CORNER_ROUNDEDNESS, BUTTON_CORNER_ROUNDEDNESS );
        _zoomButtonNode = new PPath( buttonShape );
        _zoomButtonNode.setPaint( mouseNotOverGradient );
        _zoomButtonNode.setStrokePaint( ENABLED_STROKE_COLOR );
        _zoomButtonNode.addInputEventListener( new CursorHandler() ); // Does the hand cursor thing.

        // drop shadow
        _shadowNode = new PPath( buttonShape );
        _shadowNode.setPaint( ENABLED_SHADOW_COLOR );
        _shadowNode.setPickable( false );
        _shadowNode.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
        _shadowNode.setStroke( null );

        // Add the children to the node in the appropriate order so that they appear as desired.
        addChild( _shadowNode );
        addChild( _zoomButtonNode );
        _zoomButtonNode.addChild( _htmlNode ); // HTML is a child of the button so we don't have to move it separately

        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        _zoomButtonNode.addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventHandler.ButtonEventListener() {

            private boolean focus = false; // true if the button has focus

            public void setFocus( boolean focus ) {
                this.focus = focus;
                if ( _enabled ) {
                    _zoomButtonNode.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                }
            }

            public void setArmed( boolean armed ) {
                if ( armed ) {
                    _zoomButtonNode.setPaint( createArmedGradient() );
                    _zoomButtonNode.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
                }
                else {
                    _zoomButtonNode.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                    _zoomButtonNode.setOffset( 0, 0 );
                }
            }

            public void fire() {
                ActionEvent event = new ActionEvent( this, 0, "BUTTON_FIRED" );
                for ( ActionListener listener : _listeners.getListeners( ActionListener.class ) ) {
                    listener.actionPerformed( event );
                }
            }
        } );
    }

    private double getHtmlWidth() {
        return _htmlNode.getFullBoundsReference().getWidth();
    }

    private double getHtmlHeight() {
        return _htmlNode.getFullBoundsReference().getHeight();
    }

    protected Paint createMouseNotOverGradient() {
        return createGradient( createBrighterColor( _backgroundColor ), _backgroundColor );
    }

    protected Paint createMouseOverGradient() {
        return createGradient( createBrighterColor( createBrighterColor( _backgroundColor ) ), createBrighterColor( _backgroundColor ) );
    }

    protected Paint createArmedGradient() {
        return createGradient( _backgroundColor, createBrighterColor( _backgroundColor ) );
    }

    private Paint createDisabledGradient() {
        return createGradient( createBrighterColor( DISABLED_BACKGROUND_COLOR ), DISABLED_BACKGROUND_COLOR );
    }

    private Paint createGradient( Color topColor, Color bottomColor ) {
        return new GradientPaint( (float) getHtmlWidth() / 2, 0f, topColor, (float) getHtmlWidth() * 0.5f, (float) getHtmlHeight(), bottomColor );
    }

    public void addActionListener( ActionListener listener ) {
        _listeners.add( ActionListener.class, listener );
    }

    public void removeActionListener( ActionListener listener ) {
        _listeners.remove( ActionListener.class, listener );
    }

    private static Color createBrighterColor( Color origColor ) {
        int red = origColor.getRed() + (int) Math.round( ( 255 - origColor.getRed() ) * COLOR_SCALING_FACTOR );
        int green = origColor.getGreen() + (int) Math.round( ( 255 - origColor.getGreen() ) * COLOR_SCALING_FACTOR );
        int blue = origColor.getBlue() + (int) Math.round( ( 255 - origColor.getBlue() ) * COLOR_SCALING_FACTOR );
        int alpha = origColor.getAlpha(); // preserve transparency of original color, see #2123
        return new Color( red, green, blue, alpha );
    }

    public void setEnabled( boolean enabled ) {
        if ( this._enabled != enabled ) {
            this._enabled = enabled;
            if ( enabled ) {
                // Restore original colors.
                _zoomButtonNode.setPaint( createMouseNotOverGradient() );
                _zoomButtonNode.setStrokePaint( ENABLED_STROKE_COLOR );
                _htmlNode.setTextPaint( _textColor );
                _shadowNode.setPaint( ENABLED_SHADOW_COLOR );
            }
            else {
                // Set the colors to make the button appear disabled.
                _zoomButtonNode.setPaint( createDisabledGradient() );
                _zoomButtonNode.setStrokePaint( DISABLED_STROKE_COLOR );
                _htmlNode.setTextPaint( DISABLED_TEXT_COLOR );
                _shadowNode.setPaint( DISABLED_SHADOW_COLOR );
            }
            setPickable( enabled );
            setChildrenPickable( enabled );
        }
    }
}