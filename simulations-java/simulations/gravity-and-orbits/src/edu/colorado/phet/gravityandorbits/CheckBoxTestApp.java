// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits;

import java.awt.*;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Little app for testing check box behavior on and off of Piccolo canvases.
 */
public class CheckBoxTestApp {

    public static void main( String[] args ) {

        final JCheckBox playAreaCheckBox = new JCheckBox("Play area check box"){{
            setBackground( Color.black );//make the background black to demonstrate that the checkbox still looks checkable, even when disabled
        }};
        final JCheckBox controlAreaCheckBox = new JCheckBox("Control area check box"){{
            setBackground( Color.black );//make the background black to demonstrate that the checkbox still looks checkable, even when disabled
        }};
        JCheckBox enableCheckBoxes = new JCheckBox("CheckBoxesEnabled"){{
            setSelected( true );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    playAreaCheckBox.setEnabled( isSelected() );
                    controlAreaCheckBox.setEnabled( isSelected() );
                }
            });
        }};

        // Control panel
        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        controlPanel.add( enableCheckBoxes );
        controlPanel.add( controlAreaCheckBox );

        // Canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 400, 400 ) );
        canvas.addWorldChild( new PSwing( playAreaCheckBox ) );

        // App panel
        JPanel appPanel = new JPanel();
        appPanel.add( canvas );
        appPanel.add( controlPanel );

        // Frame
        JFrame frame = new JFrame();
        frame.setContentPane( appPanel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
