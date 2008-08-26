/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Unfuddle #689.
 * 
 * This program demonstrates a bug with using a JSlider with PSwing.
 * This problem seems to occur only on newer Intel Macs running OS 10.5 
 * and Java 1.5.  (This program was written on Mac OS 10.5.3 with Java 1.5.0_13).
 * <p>
 * If the JSlider is inside a JPanel, and the JPanel is wrapped by a PSwing,
 * then the JSlider's knob doesn't render properly.  The rendering is 
 * unacceptable when the JPanel has a titled border, and just a little off 
 * if the JPanel has no titled border.
 * <p>
 * This program creates 3 instances of the JPanel. Two of them are wrapper
 * with PSwing (one with a titled border, or without), while the third is 
 * rendered entirely via Swing. It also creates a slider that is not 
 * contained in a JPanel, but is wrapped by a PSwing directly.
 * This demonstrates what the JSlider should look like.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingJSlider {

    public static void main( String[] args ) {
        
        JPanel swingControlPanel = new TestPanel( "title" );
        
        PSwing titledControlPanelWrapper = new PSwing( new TestPanel( "title" ) );
        titledControlPanelWrapper.setOffset( 100, 10 );
        
        PSwing untitledControlPanelWrapper = new PSwing( new TestPanel() );
        untitledControlPanelWrapper.setOffset( 200, 10 );
        
        JSlider slider = new TestSlider();
        PSwing sliderWrapper = new PSwing( slider );
        sliderWrapper.setOffset( 300, 10 );
        
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.getLayer().addChild( titledControlPanelWrapper );
        canvas.getLayer().addChild( untitledControlPanelWrapper );
        canvas.getLayer().addChild( sliderWrapper );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( swingControlPanel, BorderLayout.EAST );
        mainPanel.add( canvas, BorderLayout.CENTER );
        
        JFrame frame = new JFrame();
        frame.setContentPane( mainPanel );
        frame.setSize( 500, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

    private static class TestPanel extends JPanel {

        public TestPanel() {
            this( null );
        }
        
        public TestPanel( String title) {
            if ( title != null ) {
                TitledBorder border = new TitledBorder( title );
                setBorder( border );
            }
            add( new TestSlider() );
        }
    }
    
    private static class TestSlider extends JSlider {
        
        public TestSlider() {
            setOrientation( JSlider.VERTICAL );
            setMinimum( 0 );
            setMaximum( 100 );
            setValue( 0 );
            setMajorTickSpacing( 50 );
            setMinorTickSpacing( 10 );
            setPaintTicks( true );
            setPaintLabels( true );
        }
    }

}
