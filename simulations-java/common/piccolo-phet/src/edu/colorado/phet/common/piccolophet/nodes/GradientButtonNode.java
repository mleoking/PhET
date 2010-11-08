/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventListener;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents a button that is a PNode and thus can be placed
 * within another PNode or on a PCanvas, and that is filled with a color
 * gradient to make it more "fun" looking (and thus suitable for adding to
 * the play area of the sims).
 *
 * @author John Blanco
 */
public class GradientButtonNode extends PhetPNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants that control the amount of padding (or space) around the text
    // on each side of the button.
    private static final double VERTICAL_PADDING_FACTOR = 1.1;
    private static final double HORIZONTAL_PADDING_FACTOR = 1.1;

    // Constant that controls where the shadow shows up and how far the button
    // translates when pushed.
    protected static final int SHADOW_OFFSET = 3;

    // Defaults for values that might not be specified at construction.
    private static final Color DEFAULT_COLOR = Color.GRAY;
    private static final int DEFAULT_FONT_SIZE = 14;
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;

    // Constants that control various visual aspects of the button.
    private static final double COLOR_SCALING_FACTOR = 0.5;
    private static final double BUTTON_CORNER_ROUNDEDNESS = 8;
    private static final Color SHADOW_COLOR = new Color( 0f, 0f, 0f, 0.2f );
    private static final Color DISABLED_BUTTON_COLOR = Color.GRAY;
    private static final Color DISABLED_TEXT_COLOR = Color.LIGHT_GRAY;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private final ArrayList<ActionListener> _actionListeners;
    private final Color _buttonColor;
    private final HTMLNode _htmlLabelNode;
    private final PPath _button;
    private boolean _enabled = true;
    private final Paint _mouseNotOverGradient;
    private Color _textColor = DEFAULT_TEXT_COLOR;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    /**
     * Construct a gradient button node.
     *
     * @param label - Text that will appear on the button.
     * @param fontSize - Size of font for the label text.
     * @param buttonColor - Overall color of button from which gradient will
     * be created.
     * @param textColor - Color of the text that will appear on the button.
     */
    public GradientButtonNode(String label, int fontSize, Color buttonColor, Color textColor){
        this( createHtmlLabelNode( label, fontSize, textColor ), buttonColor );
        _textColor = textColor;
    }

    /**
     * Construct a gradient button node.
     *
     * @param label - Text that will appear on the button.
     * @param fontSize - Size of font for the label text.
     * @param buttonColor - Overall color of button from which gradient will
     * be created.
     */
    public GradientButtonNode(String label, int fontSize, Color buttonColor){
        this( createHtmlLabelNode( label, fontSize, DEFAULT_TEXT_COLOR ), buttonColor );
    }

    //Assumes the PNode has an offset of (0,0)
    public GradientButtonNode(HTMLNode htmlLabelNode, Color buttonColor){
        this._htmlLabelNode=htmlLabelNode;
        _htmlLabelNode.setOffset((getIconWidth() * HORIZONTAL_PADDING_FACTOR - getIconWidth()) / 2,
        		(getIconHeight() * VERTICAL_PADDING_FACTOR - getIconHeight()) / 2);
        this._buttonColor=buttonColor;

        // Initialize local data.
        _actionListeners = new ArrayList<ActionListener>();

        _mouseNotOverGradient = getMouseNotOverGradient();
        // Gradient for when the mouse is over the button.
        final Paint mouseOverGradient = getMouseOverGradient();
        // Gradient for when the button is armed.
        final Paint armedGradient = getArmedGradient();

        // Create the button node.
        RoundRectangle2D buttonShape = new RoundRectangle2D.Double(0, 0,
                getIconWidth() * HORIZONTAL_PADDING_FACTOR,
                getIconHeight() * VERTICAL_PADDING_FACTOR,
                BUTTON_CORNER_ROUNDEDNESS, BUTTON_CORNER_ROUNDEDNESS);

        _button = new PPath(buttonShape);
        _button.setPaint( _mouseNotOverGradient );
        _button.addInputEventListener( new CursorHandler() ); // Does the finger pointer cursor thing.

        // Create the shadow node.
        PPath buttonShadow = new PPath(buttonShape);
        buttonShadow.setPaint( SHADOW_COLOR );
        buttonShadow.setPickable( false );
        buttonShadow.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
        buttonShadow.setStroke( null );

        // Add the children to the node in the appropriate order so that they
        // appear as desired.
        addChild( buttonShadow );
        addChild( _button );
        _button.addChild( _htmlLabelNode ); // icon is a child of the button so we don't have to move it separately

        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        _button.addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventListener() {
            private boolean focus = false; // true if the button has focus
            public void setFocus( boolean focus ) {
                this.focus = focus;
                _button.setPaint( focus ? mouseOverGradient : _mouseNotOverGradient);
            }
            public void setArmed( boolean armed ) {
                if ( armed ) {
                    _button.setPaint( armedGradient );
                    _button.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
                }
                else {
                    _button.setPaint( focus ? mouseOverGradient : _mouseNotOverGradient );
                    _button.setOffset( 0, 0 );
                }
            }
            public void fire() {
                ActionEvent event = new ActionEvent(this, 0, "BUTTON_FIRED");
                for (int i =0; i < _actionListeners.size(); i++){
                    (_actionListeners.get(i)).actionPerformed( event );
                }
            }
        } );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    private static HTMLNode createHtmlLabelNode( String label, int fontSize, Color textColor ) {
        final HTMLNode _buttonText = new HTMLNode(label, textColor);
        _buttonText.setFont(new PhetFont(Font.BOLD, fontSize));
        _buttonText.setPickable( false );
        return _buttonText;
    }

    private double getIconWidth() {
        return _htmlLabelNode.getFullBounds().getWidth();
    }

    private double getIconHeight() {
        return _htmlLabelNode.getFullBounds().getHeight();
    }

    protected Paint getArmedGradient() {
        return useGradient() ?
                new GradientPaint( (float) getIconWidth() / 2, 0f, _buttonColor,
                (float) getIconWidth() * 0.5f, (float) getIconHeight(),
                getBrighterColor( _buttonColor ) )
                :
                (Paint) getBrighterColor( getBrighterColor( _buttonColor ) );
    }

    // See Unfuddle Ticket #553
    // https://phet.unfuddle.com/projects/9404/tickets/by_number/553
    // Button Gradient Paint is disabled on Mac until this problem is resolved
    protected boolean useGradient() {
        return !PhetUtilities.isMacintosh();
    }

    protected Paint getMouseOverGradient() {
        return useGradient() ? new GradientPaint( (float) getIconWidth() / 2, 0f,
                getBrighterColor( getBrighterColor( _buttonColor ) ),
                (float) getIconWidth() * 0.5f, (float) getIconHeight(),
                getBrighterColor( _buttonColor ) )
                :
                (Paint) getBrighterColor( _buttonColor );
    }

    protected Paint getMouseNotOverGradient() {
        return useGradient() ? new GradientPaint( (float) getIconWidth() / 2, 0f,
                getBrighterColor( _buttonColor ),
                (float) getIconWidth() * 0.5f, (float) getIconHeight(),
                _buttonColor )
                :
                (Paint) _buttonColor;
    }

    private Paint getDisabledGradient() {
        return useGradient() ? new GradientPaint( (float) getIconWidth() / 2, 0f,
                getBrighterColor( DISABLED_BUTTON_COLOR ),
                (float) getIconWidth() * 0.5f, (float) getIconHeight(),
                DISABLED_BUTTON_COLOR )
                :
                (Paint) DISABLED_BUTTON_COLOR;
    }

    protected PPath getButton(){
    	return _button;
    }

    /**
     * Constructor for creating a default gradient button with only the label
     * specified.
     *
     * @param label - Text that will appear on button.
     */
    public GradientButtonNode(String label){
        this(label, DEFAULT_FONT_SIZE, DEFAULT_COLOR);
    }

    /**
     * Constructor for creating a button assuming the default font size & color.
     *
     * @param label
     * @param buttonColor
     */
    public GradientButtonNode(String label, Color buttonColor){
        this(label, DEFAULT_FONT_SIZE, buttonColor);
    }

    public void addActionListener( ActionListener listener ) {
        if (!_actionListeners.contains( listener )){
            _actionListeners.add( listener );
        }
    }

    public void removeActionListener( ActionListener listener ) {
        _actionListeners.remove( listener );
    }

    private static Color getBrighterColor(Color origColor){
        int red = origColor.getRed() + (int)Math.round( (255 - origColor.getRed()) * COLOR_SCALING_FACTOR);
        int green = origColor.getGreen() + (int)Math.round( (255 - origColor.getGreen()) * COLOR_SCALING_FACTOR);
        int blue = origColor.getBlue() + (int)Math.round( (255 - origColor.getBlue()) * COLOR_SCALING_FACTOR);
        int alpha = origColor.getAlpha(); // preserve transparency of original color, see #2123
        return new Color ( red, green, blue, alpha );
    }

    public void setEnabled( boolean enabled ) {
        if ( this._enabled != enabled ) {
            this._enabled = enabled;
            if ( enabled ) {
                // Restore original colors.
                _button.setPaint( getMouseNotOverGradient() );
                _htmlLabelNode.setHTMLColor( _textColor );
            }
            else {
                // Set the colors to make the button appear disabled.
                _button.setPaint( getDisabledGradient() );
                _htmlLabelNode.setHTMLColor( DISABLED_TEXT_COLOR );
            }
            setPickable( enabled );
            setChildrenPickable( enabled );
        }
    }

    public boolean isEnabled(){
        return _enabled;
    }

    //------------------------------------------------------------------------
    // Test Harness
    //------------------------------------------------------------------------

    public static void main( String[] args ) {

        ActionListener listener = new ActionListener(){
            public void actionPerformed(ActionEvent event){
                System.out.println("actionPerformed event= " + event);
            }
        };

        GradientButtonNode testButton01 = new GradientButtonNode("Test Me", 16, Color.GREEN);
        testButton01.setOffset( 5, 5 );
        testButton01.addActionListener( listener );

        GradientButtonNode testButton02 = new GradientButtonNode("<html>Test <br> Me Too</html>", 24, new Color(0x99cccc));
        testButton02.setOffset( 200, 5 );
        testButton02.addActionListener( listener );

        GradientButtonNode testButton03 = new GradientButtonNode("<html><center>Default Color<br>and Font<center></html>");
        testButton03.setOffset( 5, 200 );
        testButton03.addActionListener( listener );

        GradientButtonNode testButton04 = new GradientButtonNode("Default Font Size", new Color(0xcc3366));
        testButton04.setOffset( 200, 200 );
        testButton04.addActionListener( listener );

        GradientButtonNode testButton05 = new GradientButtonNode("Transparent", new Color( 255, 0, 0, 100 ) );
        testButton05.setOffset( 200, 100 );
        testButton05.addActionListener( listener );

        final GradientButtonNode testButton06 = new GradientButtonNode("Test Enabled", new Color( 0, 200, 200 ) );
        testButton06.setOffset( 200, 300 );
        testButton06.addActionListener( listener );

        GradientButtonNode testButton07 = new GradientButtonNode("Toggle Enabled ->", new Color( 200, 200, 0 ) );
        testButton07.setOffset( 10, 300 );
        testButton07.addActionListener( new ActionListener(){
            public void actionPerformed(ActionEvent event){
                System.out.println("actionPerformed event= " + event);
                testButton06.setEnabled( !testButton06.isEnabled() );
            }
        });

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testButton01 );
        canvas.addScreenChild( testButton02 );
        canvas.addScreenChild( testButton03 );
        canvas.addScreenChild( testButton04 );
        canvas.addScreenChild( testButton05 );
        canvas.addScreenChild( testButton06 );
        canvas.addScreenChild( testButton07 );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 400, 450 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
