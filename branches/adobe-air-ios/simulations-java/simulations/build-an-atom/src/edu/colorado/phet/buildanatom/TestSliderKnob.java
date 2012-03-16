// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Class for testing issues with the size of slider knobs when using
 * LinearValueControls, see #2979.  The problem only occurs on Win Vista and
 * Win 7 (so far).
 *
 * @author John Blanco
 */
public class TestSliderKnob {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                // Set the look and feel to the value used for PhET sims.
                // This is important, because the problem does not show up
                // when using the "Metal" (which is the default cross-
                // platform) look and feel.
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName() );
                }
                catch ( UnsupportedLookAndFeelException e ) {
                    // handle exception
                }
                catch ( ClassNotFoundException e ) {
                    // handle exception
                }
                catch ( InstantiationException e ) {
                    // handle exception
                }
                catch ( IllegalAccessException e ) {
                    // handle exception
                }

                PhetPCanvas canvas = new PhetPCanvas();

                final LinearValueControl linearValueControl = new LinearValueControl( 0, 100, 0, "Test Label", "###", "sec" ) {{
                    setMajorTicksVisible( false );
                }};
                PSwing linearValueControlNode = new PSwing( linearValueControl );
                canvas.addWorldChild( linearValueControlNode );

                JFrame frame = new JFrame( "LinearValueControl Test" );
                frame.setContentPane( canvas );
                frame.setSize( 475, 425 );
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                frame.setVisible( true );
            }
        } );
    }

    // Non-Piccolo version, in case it is needed.
    public static void main2( String[] args ) {
        try {
            // Set the look and feel to the value used for PhET sims.
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName() );
        }
        catch ( UnsupportedLookAndFeelException e ) {
            // handle exception
        }
        catch ( ClassNotFoundException e ) {
            // handle exception
        }
        catch ( InstantiationException e ) {
            // handle exception
        }
        catch ( IllegalAccessException e ) {
            // handle exception
        }

        JPanel mainPanel = new JPanel();
        final LinearValueControl linearValueControl = new LinearValueControl( 0, 100, 0, "Test Label", "###", "sec" ) {{
            setMajorTicksVisible( false );
            setMinorTicksVisible( false );
        }};
        mainPanel.add( linearValueControl );
        JFrame frame = new JFrame( "LinearValueControl Test" );
        frame.setContentPane( mainPanel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
