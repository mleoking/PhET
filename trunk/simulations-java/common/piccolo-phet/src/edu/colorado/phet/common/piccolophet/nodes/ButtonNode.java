// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventListener;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents a button that is a PNode and thus can be placed
 * within another PNode or on a PCanvas, and that is filled with a color
 * gradient to make it more "fun" looking (and thus suitable for adding to
 * the play area of the sims).
 *
 * @author John Blanco
 */
public class ButtonNode extends PhetPNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // padding (or margin) around the button text
    private static final double VERTICAL_PADDING = 3;
    private static final double HORIZONTAL_PADDING = 10;

    // general button properties
    private static final int FONT_SIZE = 14;
    private static final int FONT_STYLE = Font.BOLD;
    private static final double COLOR_SCALING_FACTOR = 0.5;
    private static final double BUTTON_CORNER_ROUNDEDNESS = 8;
    private static final int SHADOW_OFFSET = 3;

    // button enabled properties
    private static final Color ENABLED_TEXT_COLOR = Color.BLACK;
    private static final Color ENABLED_BACKGROUND_COLOR = Color.GRAY;
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
    private final HTMLNode _htmlNode;
    private final PPath _buttonNode;
    private final PPath _shadowNode;

    // mutable
    private Color _textColor;
    private boolean _enabled;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    public ButtonNode( String text ) {
        this( text, FONT_SIZE, ENABLED_BACKGROUND_COLOR );
    }

    public ButtonNode( String text, Color backgroundColor ) {
        this( text, FONT_SIZE, backgroundColor );
    }

    public ButtonNode( String text, int fontSize, Color backgroundColor ) {
        this( text, fontSize, ENABLED_TEXT_COLOR, backgroundColor );
    }

    public ButtonNode( String text, int fontSize, Color textColor, Color backgroundColor ) {
        this( text, new PhetFont( FONT_STYLE, fontSize ), textColor, backgroundColor );
    }

    /**
     * Construct a button node.
     *
     * @param text            text that will appear on button, supports HTML
     * @param font            font for the label text.
     * @param textColor       color of the HTML that will appear on the button.
     * @param backgroundColor overall color of button from which gradient will be created.
     */
    public ButtonNode( String text, Font font, Color textColor, Color backgroundColor ) {
        this( createHtmlNode( text, font, textColor ), textColor, backgroundColor );
    }

    /*
     * Primary constructor, all other constructors ultimately result in this one being called.
     */
    private ButtonNode( HTMLNode htmlLabelNode, Color textColor, Color backgroundColor ) {

        this._htmlNode = htmlLabelNode;
        _htmlNode.setOffset( HORIZONTAL_PADDING, VERTICAL_PADDING );

        this._textColor = textColor;
        this._backgroundColor = backgroundColor;
        this._enabled = true;

        // Initialize local data.
        _listeners = new EventListenerList();

        final Paint mouseNotOverGradient = createMouseNotOverGradient();
        // Gradient for when the mouse is over the button.
        final Paint mouseOverGradient = createMouseOverGradient();
        // Gradient for when the button is armed.
        final Paint armedGradient = createArmedGradient();

        // button
        RoundRectangle2D buttonShape = new RoundRectangle2D.Double( 0, 0,
                                                                    getHtmlWidth() + HORIZONTAL_PADDING * 2,
                                                                    getHtmlHeight() + VERTICAL_PADDING * 2,
                                                                    BUTTON_CORNER_ROUNDEDNESS, BUTTON_CORNER_ROUNDEDNESS );
        _buttonNode = new PPath( buttonShape );
        _buttonNode.setPaint( mouseNotOverGradient );
        _buttonNode.setStrokePaint( ENABLED_STROKE_COLOR );
        _buttonNode.addInputEventListener( new CursorHandler() ); // Does the hand cursor thing.

        // drop shadow
        _shadowNode = new PPath( buttonShape );
        _shadowNode.setPaint( ENABLED_SHADOW_COLOR );
        _shadowNode.setPickable( false );
        _shadowNode.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
        _shadowNode.setStroke( null );

        // Add the children to the node in the appropriate order so that they appear as desired.
        addChild( _shadowNode );
        addChild( _buttonNode );
        _buttonNode.addChild( _htmlNode ); // HTML is a child of the button so we don't have to move it separately

        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        _buttonNode.addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventListener() {

            private boolean focus = false; // true if the button has focus

            public void setFocus( boolean focus ) {
                this.focus = focus;
                if ( _enabled ) {
                    _buttonNode.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                }
            }

            public void setArmed( boolean armed ) {
                if ( armed ) {
                    _buttonNode.setPaint( armedGradient );
                    _buttonNode.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
                }
                else {
                    _buttonNode.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                    _buttonNode.setOffset( 0, 0 );
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

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    private static HTMLNode createHtmlNode( String text, Font font, Color textColor ) {
        final HTMLNode _buttonText = new HTMLNode( text, textColor );
        _buttonText.setFont( font );
        _buttonText.setPickable( false );
        return _buttonText;
    }

    private double getHtmlWidth() {
        return _htmlNode.getFullBoundsReference().getWidth();
    }

    private double getHtmlHeight() {
        return _htmlNode.getFullBoundsReference().getHeight();
    }

    /*
     * When the mouse is not over the node, its specified background color is used.
     */
    protected Paint createMouseNotOverGradient() {
        return createGradient( createBrighterColor( _backgroundColor ), _backgroundColor );
    }

    /*
    * When the mouse is over the node but not pressed, the button gets brighter.
    */
    protected Paint createMouseOverGradient() {
        return createGradient( createBrighterColor( createBrighterColor( _backgroundColor ) ), createBrighterColor( _backgroundColor ) );
    }

    /*
    * When the button is armed (pressed), the color is similar to mouse-not-over, but the button is brighter at the bottom.
    */
    protected Paint createArmedGradient() {
        return createGradient( _backgroundColor, createBrighterColor( _backgroundColor ) );
    }

    /*
     * When the button is disabled, we use a standardized color to indicate disabled.
     */
    private Paint createDisabledGradient() {
        return createGradient( createBrighterColor( DISABLED_BACKGROUND_COLOR ), DISABLED_BACKGROUND_COLOR );
    }

    /*
    * Creates a gradient that vertically goes from topColor to bottomColor.
    * @param topColor
    * @param bottomColor
    * @return Paint
    */
    private Paint createGradient( Color topColor, Color bottomColor ) {
        if ( useGradient() ) {
            return new GradientPaint( (float) getHtmlWidth() / 2, 0f, topColor, (float) getHtmlWidth() * 0.5f, (float) getHtmlHeight(), bottomColor );
        }
        else {
            return bottomColor;
        }
    }

    /*
    * See Unfuddle Ticket #553, GradientPaint crashes Mac.
    */
    private boolean useGradient() {
        return !PhetUtilities.isMacintosh();
    }

    protected PPath getButton() {
        return _buttonNode;
    }

    protected int getShadowOffset() {
        return SHADOW_OFFSET;
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
                _buttonNode.setPaint( createMouseNotOverGradient() );
                _buttonNode.setStrokePaint( ENABLED_STROKE_COLOR );
                _htmlNode.setHTMLColor( _textColor );
                _shadowNode.setPaint( ENABLED_SHADOW_COLOR );
            }
            else {
                // Set the colors to make the button appear disabled.
                _buttonNode.setPaint( createDisabledGradient() );
                _buttonNode.setStrokePaint( DISABLED_STROKE_COLOR );
                _htmlNode.setHTMLColor( DISABLED_TEXT_COLOR );
                _shadowNode.setPaint( DISABLED_SHADOW_COLOR );
            }
            setPickable( enabled );
            setChildrenPickable( enabled );
        }
    }

    public boolean isEnabled() {
        return _enabled;
    }

    //------------------------------------------------------------------------
    // Test Harness
    //------------------------------------------------------------------------

    public static void main( String[] args ) {

        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
            }
        };

        ButtonNode testButton01 = new ButtonNode( "Test Me", 16, Color.GREEN );
        testButton01.setOffset( 5, 5 );
        testButton01.addActionListener( listener );

        ButtonNode testButton02 = new ButtonNode( "<html>Test <br> Me Too</html>", 24, new Color( 0x99cccc ) );
        testButton02.setOffset( 200, 5 );
        testButton02.addActionListener( listener );

        ButtonNode testButton03 = new ButtonNode( "<html><center>Default Color<br>and Font<center></html>" );
        testButton03.setOffset( 5, 200 );
        testButton03.addActionListener( listener );

        ButtonNode testButton04 = new ButtonNode( "Default Font Size", new Color( 0xcc3366 ) );
        testButton04.setOffset( 200, 200 );
        testButton04.addActionListener( listener );

        ButtonNode testButton05 = new ButtonNode( "Transparent", new Color( 255, 0, 0, 100 ) );
        testButton05.setOffset( 200, 100 );
        testButton05.addActionListener( listener );

        final ButtonNode testButton06 = new ButtonNode( "Test Enabled", new Color( 0, 200, 200 ) );
        testButton06.setOffset( 200, 300 );
        testButton06.addActionListener( listener );

        ButtonNode testButton07 = new ButtonNode( "Toggle Enabled ->", new PhetFont( Font.ITALIC, 16 ), Color.RED, new Color( 200, 200, 0 ) );
        testButton07.setOffset( 10, 300 );
        testButton07.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                System.out.println( "actionPerformed event= " + event );
                testButton06.setEnabled( !testButton06.isEnabled() );
            }
        } );

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testButton01 );
        canvas.addScreenChild( testButton02 );
        canvas.addScreenChild( testButton03 );
        canvas.addScreenChild( testButton04 );
        canvas.addScreenChild( testButton05 );
        canvas.addScreenChild( testButton06 );
        canvas.addScreenChild( testButton07 );

        JFrame frame = new JFrame( "ButtonNode.main" );
        frame.setContentPane( canvas );
        frame.setSize( 400, 450 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
