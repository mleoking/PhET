package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:49:22 PM
 *
 */

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.PlatformNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TestPlatformNode {
    private JFrame frame;
    private PlatformNode platformNode;
    private PhetPCanvas phetPCanvas;
    private RotationPlatform rotationPlatform = new RotationPlatform();

    public TestPlatformNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas();
        phetPCanvas.setSize( frame.getSize() );
        platformNode = new PlatformNode( new PlatformNode.RotationPlatformEnvironment() {
            public void setPositionDriven() {
            }
        }, rotationPlatform );
        phetPCanvas.addScreenChild( platformNode );

        final ModelSlider modelSlider = new ModelSlider( "angle", "radians", 0, Math.PI * 2 * 2, Math.PI * 2 );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rotationPlatform.setPosition( modelSlider.getValue() );
            }
        } );
        PSwing pSwing = new PSwing( modelSlider );
        phetPCanvas.addScreenChild( pSwing );
        pSwing.setOffset( 0, platformNode.getFullBounds().getMaxY() );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestPlatformNode().start();
    }

    public PlatformNode getPlatformNode() {
        return platformNode;
    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    void start() {
        frame.setVisible( true );
    }

    public RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }
}
