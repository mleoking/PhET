// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class TestLinearValueControlPSwing {
    private final JFrame frame;

    public TestLinearValueControlPSwing() {
        frame = new JFrame( getClass().getName() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final PhetPCanvas contentPane = new PhetPCanvas();
        contentPane.setWorldTransformStrategy( new PhetPCanvas.CenteringBoxStrategy( contentPane, new PDimension( 800, 600 ) ) );

        LinearValueControl control = new LinearValueControl( 0, 10, "label", "0.00", "units" );
        final PSwing swing = new PSwing( control );

//        final PSwing swing = new PSwing(new JLabel("label"));
//        final JPanel mypanel = new JPanel();
//        mypanel.add(new JLabel("hello"));
//        mypanel.add(new JTextField("secondtime"));
//        mypanel.add(new JLabel("secondtime"));
//        final PSwing swing = new PSwing(mypanel);

        swing.setOffset( 400, 300 );
        contentPane.addWorldChild( swing );
        frame.setContentPane( contentPane );
        frame.setSize( 800, 600 );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new TestLinearValueControlPSwing().start();
            }
        } );
    }

    private void start() {
        frame.setVisible( true );
    }
}
