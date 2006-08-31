/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.tests.piccolo;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * TestPSwingQuality demonstrates the rendering quality of PSwing.
 * Two panels are shown; one is opaque and one is transparent.
 * On Macintosh (OS 10.3.9, JDK 1.4.2_09) the quality of the 
 * transparent panel is unacceptable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPSwingQuality extends JFrame {

    public static void main( String[] args ) {
        JFrame frame = new TestPSwingQuality();
        frame.show();
    }
    
    public TestPSwingQuality() {
        super( "TestPSwingQuality" );
        
        JPanel opaquePanel = new TestPanel( true /* opaque */ );
        JPanel transparentPanel = new TestPanel( false /* opaque */ );
        
        PSwingCanvas canvas = new PSwingCanvas();
        
        PNode opaqueNode = new PSwing( canvas, opaquePanel );
        canvas.getLayer().addChild( opaqueNode );
        
        PNode transparentNode = new PSwing( canvas, transparentPanel );
        canvas.getLayer().addChild( transparentNode );

        opaqueNode.setOffset( 50, 50 );
        transparentNode.setOffset( opaqueNode.getFullBounds().getX(), 
                opaqueNode.getFullBounds().getY() + opaqueNode.getFullBounds().getHeight() );
        
        getContentPane().add( canvas );
        setSize( 300, 300 );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    private static class TestPanel extends JPanel {

        public TestPanel( boolean opaque ) {
            
            JRadioButton rb1 = new JRadioButton( "ABC XYZ 123" );
            JRadioButton rb2 = new JRadioButton( "abc xyz 456" );
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( rb1 );
            buttonGroup.add( rb2 );
            rb1.setSelected( true );

            String title = opaque ? "Opaque" : "Transparent";
            setBorder( BorderFactory.createTitledBorder( title ) );
            add( rb1 );
            add( rb2 );

            // opacity
            setOpaque( opaque );
            rb1.setOpaque( opaque );
            rb2.setOpaque( opaque );
        }
    }
}
