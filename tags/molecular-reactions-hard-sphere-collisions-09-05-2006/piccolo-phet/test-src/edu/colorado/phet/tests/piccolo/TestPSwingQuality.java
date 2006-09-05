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

import java.awt.Color;

import javax.swing.*;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * TestPSwingQuality demonstrates the rendering quality of PSwing.
 * Three panels are shown; one is opaque and the other two are transparent.
 * On Macintosh (OS 10.3.9, JDK 1.4.2_09) the quality of the 
 * transparent panels is unacceptable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPSwingQuality extends JFrame {

    /* Use opaque JComponents */
    private static int SET_OPAQUE_TRUE = 1;
    /* Use transparent JComponents using setOpaque(false) */
    private static int SET_OPAQUE_FALSE = 2;
    /* Use transparent JComponents using setBackground(new Color(0,0,0,0)) */
    private static int SET_BACKGROUND_TRANSPARENT = 3;
    
    public static void main( String[] args ) {
        JFrame frame = new TestPSwingQuality();
        frame.show();
    }
    
    public TestPSwingQuality() {
        super( "TestPSwingQuality" );
        
        JPanel opaquePanel = new TestPanel( "setOpaque(true)", SET_OPAQUE_TRUE );
        JPanel transparentPanel = new TestPanel( "setOpaque(false)", SET_OPAQUE_FALSE );
        JPanel backgroundPanel = new TestPanel( "setBackground(transparent)", SET_BACKGROUND_TRANSPARENT );
        
        PSwingCanvas canvas = new PSwingCanvas();
        
        PNode opaqueNode = new PSwing( canvas, opaquePanel );
        canvas.getLayer().addChild( opaqueNode );
        
        PNode transparentNode = new PSwing( canvas, transparentPanel );
        canvas.getLayer().addChild( transparentNode );

        PNode backgroundNode = new PSwing( canvas, backgroundPanel );
        canvas.getLayer().addChild( backgroundNode );
        
        opaqueNode.setOffset( 50, 50 );
        transparentNode.setOffset( opaqueNode.getFullBounds().getX(), 
                opaqueNode.getFullBounds().getY() + opaqueNode.getFullBounds().getHeight() );
        backgroundNode.setOffset( transparentNode.getFullBounds().getX(), 
                transparentNode.getFullBounds().getY() + transparentNode.getFullBounds().getHeight() );
        
        getContentPane().add( canvas );
        setSize( 300, 300 );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    private static class TestPanel extends JPanel {

        public TestPanel( String title, int type ) {
            
            JRadioButton rb1 = new JRadioButton( "ABC XYZ 123" );
            JRadioButton rb2 = new JRadioButton( "abc xyz 456" );
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( rb1 );
            buttonGroup.add( rb2 );
            rb1.setSelected( true );

            setBorder( BorderFactory.createTitledBorder( title ) );
            add( rb1 );
            add( rb2 );
            
            if ( type == SET_OPAQUE_TRUE ) {
                // do nothing
            }
            else if ( type == SET_OPAQUE_FALSE ) {
                setOpaque( false );
                rb1.setOpaque( false );
                rb2.setOpaque( false );
            }
            else if ( type == SET_BACKGROUND_TRANSPARENT ) {
                Color transparentColor = new Color( 0f, 0f, 0f, 0f );
                setBackground( transparentColor );
                rb1.setBackground( transparentColor );
                rb2.setBackground( transparentColor );
            }
        }
    }
}
