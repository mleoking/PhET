package edu.colorado.phet.nuclearphysics2.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;



public class GradientButtonNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Constants that control the amount of padding (or space) around the text
    // on each side of the button.
    private static final int VERTICAL_PADDING = 3;
    private static final int HORIZONTAL_PADDING = 3;
    
    // Constant that controls where the shadow shows up and how far the button
    // translates when pushed.
    private static final int SHADOW_OFFSET = 2;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    PPath _button;
    HTMLNode _buttonText;
    Color _mainButtonColor;
    ArrayList _actionListeners = new ArrayList();
    GradientPaint _unpressedGradient;
    GradientPaint _pressedGradient;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public GradientButtonNode(String label, int fontSize, Color buttonColor){
        
        _mainButtonColor = buttonColor;
  
        // Create the label node first, since its size will be the basis for
        // the other components of this button.
        _buttonText = new HTMLNode(label);        
        _buttonText.setFont(new PhetDefaultFont(Font.BOLD, fontSize));
        _buttonText.setOffset(HORIZONTAL_PADDING, VERTICAL_PADDING);
        _buttonText.setPickable( false );

        // Create the gradients that will be used to color the buttons.
        _unpressedGradient = new GradientPaint((float)_buttonText.getFullBounds().width * 0.5f, 0f,
                getBrighterColor( buttonColor ), 
                (float)_buttonText.getFullBounds().width * 0.5f, (float)_buttonText.getFullBounds().height, 
                buttonColor);
        _pressedGradient = new GradientPaint((float)_buttonText.getFullBounds().width * 0.5f, 0f,
                getBrighterColor(getBrighterColor( buttonColor )), 
                (float)_buttonText.getFullBounds().width * 0.5f, (float)_buttonText.getFullBounds().height, 
                getBrighterColor( buttonColor ));

        // Create the button node.
        _button = new PPath(new RoundRectangle2D.Double(0, 0, 
        _buttonText.getFullBounds().width + 2 * HORIZONTAL_PADDING, 
        _buttonText.getFullBounds().height + 2 * VERTICAL_PADDING,
        8, 8));
        _button.setPaint( _unpressedGradient );
        _button.addInputEventListener( new CursorHandler() ); // Does the finger pointer cursor thing.

        // Create the shadow node.
        PNode buttonShadow = new PPath(new RoundRectangle2D.Double(SHADOW_OFFSET,
                SHADOW_OFFSET, _buttonText.getFullBounds().width + 2 * HORIZONTAL_PADDING,
                _buttonText.getFullBounds().height + 2 * VERTICAL_PADDING,
                8, 8));
        buttonShadow.setPaint( Color.BLACK );
        buttonShadow.setPickable( false );
        buttonShadow.setTransparency( 0.3f );

        // Register the button node for events.
        _button.addInputEventListener( new PBasicInputEventHandler() {
            boolean _mouseInside = false;

            public void mouseEntered( PInputEvent event ) {
                _mouseInside = true;
                _button.setPaint( _pressedGradient );
            }
            public void mouseExited( PInputEvent event ) {
                _mouseInside = false;
                _button.setPaint( _unpressedGradient );
            }
            public void mousePressed( PInputEvent event ) {
                _button.setOffset( SHADOW_OFFSET, SHADOW_OFFSET );
                _buttonText.setOffset(HORIZONTAL_PADDING + SHADOW_OFFSET, VERTICAL_PADDING + SHADOW_OFFSET);
            }
            public void mouseReleased( PInputEvent event ) {
                _button.setOffset( 0, 0 );
                _buttonText.setOffset(HORIZONTAL_PADDING, VERTICAL_PADDING);
                if ( _mouseInside ) {
                    fireEvent(new ActionEvent(this, 0, "button released") );
                }
            }
            
            private void fireEvent( ActionEvent event ) {
                for (int i =0; i < _actionListeners.size(); i++){
                    ((ActionListener)_actionListeners.get(i)).actionPerformed( event );
                }
            }
        } );


        // Add the children to the node in the appropriate order so that they
        // appear as desired.
        addChild( buttonShadow );
        addChild( _button );
        addChild( _buttonText );
    }
    
    private Color getBrighterColor(Color origColor){
        final double COLOR_SCALING_FACTOR = 2.0;
        int red = origColor.getRed() + (int)Math.round( (double)(255 - origColor.getRed()) * 0.5); 
        int green = origColor.getGreen() + (int)Math.round( (double)(255 - origColor.getGreen()) * 0.5); 
        int blue = origColor.getBlue() + (int)Math.round( (double)(255 - origColor.getBlue()) * 0.5); 
        return new Color ( red, green, blue );
    }
    
    public void addActionListener( ActionListener listener ) {
        if (!_actionListeners.contains( listener )){
            _actionListeners.add( listener );
        }
    }

    public void removeActionListener( ActionListener listener ) {
        _actionListeners.remove( listener );
    }

    
    public static void main( String[] args ) {
        
        PNode testButton01 = new GradientButtonNode("Test Me", 16, Color.GREEN);
        PNode testButton02 = new GradientButtonNode("<html>Test <br> Me Too</html>", 14,
                Color.BLUE);
        testButton02.setOffset( 100, 100 );
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( testButton01 );
        canvas.addScreenChild( testButton02 );
        canvas.setWorldScale( 100 );
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setVisible( true );
    }
}
