package edu.colorado.phet.nuclearphysics2.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
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

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    PPath _button;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    GradientButtonNode(String label, Point2D position){
        
        // Create the label node first, since its size will be the basis for
        // the other components of this button.
        HTMLNode buttonText = new HTMLNode(label);        
        buttonText.setFont(new PhetDefaultFont(Font.ITALIC, 14));
        buttonText.setOffset(position.getX() + HORIZONTAL_PADDING, position.getY() + VERTICAL_PADDING);
        buttonText.setPickable( false );

        // Create the button node.
        _button = new PPath(new RoundRectangle2D.Double(position.getX(), position.getY(), 
                buttonText.getFullBounds().width + 2 * HORIZONTAL_PADDING, 
                buttonText.getFullBounds().height + 2 * VERTICAL_PADDING,
                8, 8));
        _button.setPaint( Color.RED );

        // Create the button node.
        _button = new PPath(new RoundRectangle2D.Double(position.getX(), position.getY(), 
                buttonText.getFullBounds().width + 2 * HORIZONTAL_PADDING, 
                buttonText.getFullBounds().height + 2 * VERTICAL_PADDING,
                8, 8));
        _button.setPaint( Color.RED );

        // Register the button node for events.
        _button.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseEntered( PInputEvent event ) {
                _button.setPaint( Color.PINK );
            }
            public void mouseExited( PInputEvent event ) {
                _button.setPaint( Color.RED );
            }
            public void mousePressed( PInputEvent event ) {
                _button.setVisible( false );
            }
            public void mouseReleased( PInputEvent event ) {
                _button.setVisible( true );
            }
        } );


        // Add the children to the node in the appropriate order so that they
        // appear as desired.
        addChild( _button );
        addChild( buttonText );
    }
    
    public static void main( String[] args ) {
        
        PNode testButton01 = new GradientButtonNode("Test Me", new Point2D.Double(25, 24));
        PNode testButton02 = new GradientButtonNode("<html>Test <br> Me Too</html>", new Point2D.Double(60, 24));
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
//        canvas.addScreenChild( testButton01 );
        canvas.addScreenChild( testButton02 );
        canvas.setWorldScale( 200 );
        frame.setContentPane( canvas );
        frame.setSize( 400, 300 );
        frame.setVisible( true );
    }
}
