package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:22:14 PM
 */
public class TestMovingManNode {
    public static void main( String[] args ) throws IOException {
        JFrame frame = new JFrame( "Test Moving Man Node" );
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width, 300 );
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.setZoomEventHandler( new PZoomEventHandler() );
        frame.setContentPane( phetPCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        ConstantDtClock swingClock = new ConstantDtClock( 30, 1.0 );

        final MovingManMotionModel model = new MovingManMotionModel( swingClock );
        MovingManNode movingManNode = new MovingManNode( model );
        movingManNode.scale( 50 );
        movingManNode.translate( 10.5, 0 );
        phetPCanvas.addScreenChild( movingManNode );

        frame.setVisible( true );

//        swingClock.start();
        model.setVelocityDriven();
        model.setVelocity( 0.1 );
    }

}
