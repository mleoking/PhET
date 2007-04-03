package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetcomponents.PhetZoomControl;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;

public class TestPSwingPhetZoomControl {
    private JFrame frame;

    public TestPSwingPhetZoomControl() {
        frame = new JFrame( "TestPSwingPhetZoomControl" );

        PhetPCanvas pCanvas = new PhetPCanvas();

        frame.setContentPane( pCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );

        ApparatusPanel ap = new ApparatusPanel();
        PhetZoomControl zc = new PhetZoomControl( ap, PhetZoomControl.HORIZONTAL );
        zc.setLocation( 200, 300 );
        PhetZoomControl zc2 = new PhetZoomControl( ap, PhetZoomControl.VERTICAL );
        zc2.setLocation( 300, 300 );
        ap.addGraphic( zc );
        ap.addGraphic( zc2 );

        pCanvas.addScreenChild( new PSwing( ap ) );
    }

    public static void main( String[] args ) {
        new TestPSwingPhetZoomControl().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
