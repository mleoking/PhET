package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:37:54 PM
 */
public class MovingManNode extends PNode {
    public MovingManNode( final RotationModel rotationModel ) {
        PText text = new PText( "hello" );
        addChild( text );
        for( int i = -10; i <= 10; i++ ) {
            PText tickMark = new PText( "" + i );
            Font aFont = new Font( "Lucida Sans", Font.PLAIN, 2 );
            aFont = aFont.deriveFont( 0.5f );
            tickMark.setFont( aFont );
            tickMark.setOffset( i, 0 );
            addChild( tickMark );
        }
        final PPath object = new PhetPPath( new Rectangle2D.Double( -1, -1, 2, 2 ), Color.blue );
        object.translate( 0, 1 );
        addChild( object );
        rotationModel.addListener( new RotationModel.Listener() {
            public void steppedInTime() {
                updateObject( object, rotationModel );
            }
        } );
        updateObject( object, rotationModel );
    }

    private void updateObject( PPath object, RotationModel rotationModel ) {
        object.setOffset( rotationModel.getAngle(), 1.5 );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Moving Man Node" );
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width, 300 );
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        final RotationModel rotationModel = new RotationModel();

        MovingManNode movingManNode = new MovingManNode( rotationModel );
        movingManNode.scale( 50 );
        movingManNode.translate( 10.5, 0 );
        phetPCanvas.addScreenChild( movingManNode );

        frame.setVisible( true );
        SwingClock swingClock = new SwingClock( 30, 1.0 );
        swingClock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                rotationModel.stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
        swingClock.start();
        rotationModel.setVelocityDriven();
        rotationModel.setAngularVelocity( 0.1 );
    }
}
