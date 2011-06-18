// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Unfuddle #689.
 * <p/>
 * This program demonstrates a bug with using a JSlider with PSwing.
 * This problem seems to occur only on newer Intel Macs running OS 10.5
 * and Java 1.5.  (This program was written on Mac OS 10.5.3 with Java 1.5.0_13).
 * <p/>
 * If the JSlider is inside a JPanel, and the JPanel is wrapped by a PSwing,
 * then the JSlider's knob doesn't render properly.  The rendering is
 * unacceptable when the JPanel has a titled border, and just a little off
 * if the JPanel has no titled border.  The rendering is also OK if the
 * slider has no ticks.
 * <p/>
 * This program creates 3 instances of the JPanel. Two of them are wrapped
 * with PSwing (one with a titled border, one without), while the third is
 * rendered entirely via Swing. It also creates a slider that is not
 * contained in a JPanel, but is wrapped by a PSwing directly.
 * This demonstrates what the JSlider should look like.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPSwingJSlider {

    public static void main( String[] args ) {

        double xOffset = 10;
        double yOffset = 10;

        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 600, 300 ) );

        // OK, demonstrated workaround
        {
            PSwing pswing = new PSwing( new SliderWithTicks() );
            canvas.getLayer().addChild( pswing );
            pswing.setOffset( xOffset, yOffset );
            xOffset += 100;
        }

        // Very broken, border
        {
            JPanel panel = new JPanel();
            panel.setBorder( new TitledBorder( "broken" ) );
            panel.add( new SliderWithTicks() );
            PSwing pswing = new PSwing( panel );
            canvas.getLayer().addChild( pswing );
            pswing.setOffset( xOffset, yOffset );
            xOffset += 100;
        }

        // Less broken, no border
        {
            JPanel panel = new JPanel();
            panel.add( new SliderWithTicks() );
            PSwing pswing = new PSwing( panel );
            canvas.getLayer().addChild( pswing );
            pswing.setOffset( xOffset, yOffset );
            xOffset += 100;
        }

        // OK, no ticks
        {
            JPanel panel = new JPanel();
            panel.setBorder( new TitledBorder( "OK" ) );
            panel.add( new SliderNoTicks() );
            PSwing pswing = new PSwing( panel );
            canvas.getLayer().addChild( pswing );
            pswing.setOffset( xOffset, yOffset );
            xOffset += 100;
        }

        // OK
        JPanel swingControlPanel = new JPanel();
        swingControlPanel.setBorder( new TitledBorder( "OK" ) );
        swingControlPanel.add( new SliderWithTicks() );

        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( swingControlPanel, BorderLayout.EAST );
        mainPanel.add( canvas, BorderLayout.CENTER );

        JFrame frame = new JFrame();
        frame.setContentPane( mainPanel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

    private static class SliderNoTicks extends JSlider {

        public SliderNoTicks() {
            setOrientation( JSlider.VERTICAL );
            setMinimum( 0 );
            setMaximum( 100 );
            setValue( 0 );
        }
    }

    private static class SliderWithTicks extends SliderNoTicks {

        public SliderWithTicks() {
            setMajorTickSpacing( 50 );
            setMinorTickSpacing( 10 );
            setPaintTicks( true );
            setPaintLabels( true );
        }
    }
}
