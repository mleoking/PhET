/* Copyright 2008, University of Colorado */

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

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

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
    private static final int VERTICAL_PADDING = 3;
    private static final int HORIZONTAL_PADDING = 3;
    
    // Constant that controls where the shadow shows up and how far the button
    // translates when pushed.
    private static final int SHADOW_OFFSET = 3;
    
    // Defaults for values that might not be specified at construction.
    private static final Color DEFAULT_COLOR = Color.GRAY;
    private static final int DEFAULT_FONT_SIZE = 14;
    
    // Constants that control various visual aspects of the button.
    private static final double COLOR_SCALING_FACTOR = 0.5;
    private static final double BUTTON_CORNER_ROUNDEDNESS = 8;
    private static final float SHADOW_TRANSPARENCY = 0.2f;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private ArrayList _actionListeners;
    private Color _buttonColor;
    private PNode _icon;

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
     */
    public GradientButtonNode(String label, int fontSize, Color buttonColor){
        this( createTextIcon( label, fontSize ), buttonColor );
    }

    //Assumes the PNode has an offset of (0,0)
    public GradientButtonNode(PNode icon,Color buttonColor){
        this._icon=icon;
        this._buttonColor=buttonColor;

        // Initialize local data.
        _actionListeners = new ArrayList();



        // Gradient for when the mouse is not over the button.
        final Paint mouseNotOverGradient = getMouseNotOverGradient();
        // Gradient for when the mouse is over the button.
        final Paint mouseOverGradient = getMouseOverGradient();
        // Gradient for when the button is armed.
        final Paint armedGradient = getArmedGradient();

        // Create the button node.
        RoundRectangle2D buttonShape = new RoundRectangle2D.Double(0, 0,
                getIconWidth() + 2 * HORIZONTAL_PADDING,
                getIconHeight() + 2 * VERTICAL_PADDING,
                BUTTON_CORNER_ROUNDEDNESS, BUTTON_CORNER_ROUNDEDNESS);

        final PPath button = new PPath(buttonShape);
        button.setPaint( mouseNotOverGradient );
        button.addInputEventListener( new CursorHandler() ); // Does the finger pointer cursor thing.

        // Create the shadow node.
        PNode buttonShadow = new PPath(buttonShape);
        buttonShadow.setPaint( Color.BLACK );
        buttonShadow.setPickable( false );
        buttonShadow.setTransparency( SHADOW_TRANSPARENCY );
        buttonShadow.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );

        // Add the children to the node in the appropriate order so that they
        // appear as desired.
        addChild( buttonShadow );
        addChild( button );
        button.addChild( _icon ); // icon is a child of the button so we don't have to move it separately

        // Register a handler to watch for button state changes.
        ButtonEventHandler handler = new ButtonEventHandler();
        button.addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventListener() {
            private boolean focus = false; // true if the button has focus
            public void setFocus( boolean focus ) {
                this.focus = focus;
                button.setPaint( focus ? mouseOverGradient : mouseNotOverGradient);
            }
            public void setArmed( boolean armed ) {
                if ( armed ) {
                    button.setPaint( armedGradient );
                    button.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
                }
                else {
                    button.setPaint( focus ? mouseOverGradient : mouseNotOverGradient );
                    button.setOffset( 0, 0 );
                }
            }
            public void fire() {
                ActionEvent event = new ActionEvent(this, 0, "BUTTON_FIRED");
                for (int i =0; i < _actionListeners.size(); i++){
                    ((ActionListener)_actionListeners.get(i)).actionPerformed( event );
                }
            }
        } );
    }

    private static PNode createTextIcon( String label, int fontSize ) {
        // Create the label node first, since its size will be the basis for
        // the other components of this button.
        final HTMLNode _buttonText = new HTMLNode(label);
        _buttonText.setFont(new PhetFont(Font.BOLD, fontSize));
        _buttonText.setOffset(HORIZONTAL_PADDING, VERTICAL_PADDING);
        _buttonText.setPickable( false );
        return _buttonText;
    }

    private double getIconWidth() {
        return _icon.getFullBounds().getWidth();
    }

    private double getIconHeight() {
        return _icon.getFullBounds().getHeight();
    }

    private Paint getArmedGradient() {
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
    private boolean useGradient() {
        return !PhetUtilities.isMacintosh();
    }

    private Paint getMouseOverGradient() {
        return useGradient()?new GradientPaint((float) getIconWidth() / 2, 0f,
                getBrighterColor(getBrighterColor( _buttonColor )),
                (float) getIconWidth() * 0.5f, (float) getIconHeight(),
                getBrighterColor( _buttonColor ))
               :(Paint) getBrighterColor( _buttonColor );
    }

    private Paint getMouseNotOverGradient() {
        return useGradient()?new GradientPaint((float) getIconWidth() / 2, 0f,
                getBrighterColor( _buttonColor ),
                (float) getIconWidth() * 0.5f, (float) getIconHeight(),
                _buttonColor)
               :(Paint)_buttonColor;
    }

    /**
     * Constructor for creating a default gradient button with only the label
     * specified.
     * 
     * @param label - Text that will appear on button.
     */
    GradientButtonNode(String label){
        this(label, DEFAULT_FONT_SIZE, DEFAULT_COLOR);
    }
    
    /**
     * Constructor for creating a button assuming the default font size.
     * 
     * @param label
     * @param color
     */
    GradientButtonNode(String label, Color color){
        this(label, DEFAULT_FONT_SIZE, color);
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------

    public void addActionListener( ActionListener listener ) {
        if (!_actionListeners.contains( listener )){
            _actionListeners.add( listener );
        }
    }

    public void removeActionListener( ActionListener listener ) {
        _actionListeners.remove( listener );
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private static Color getBrighterColor(Color origColor){
        int red = origColor.getRed() + (int)Math.round( (double)(255 - origColor.getRed()) * COLOR_SCALING_FACTOR); 
        int green = origColor.getGreen() + (int)Math.round( (double)(255 - origColor.getGreen()) * COLOR_SCALING_FACTOR); 
        int blue = origColor.getBlue() + (int)Math.round( (double)(255 - origColor.getBlue()) * COLOR_SCALING_FACTOR); 
        return new Color ( red, green, blue );
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
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testButton01 );
        canvas.addScreenChild( testButton02 );
        canvas.addScreenChild( testButton03 );
        canvas.addScreenChild( testButton04 );
        
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
        frame.setVisible( true );
    }
}
