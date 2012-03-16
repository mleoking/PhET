// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:49:22 PM
 *
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.RotationPlatformNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public class TestRotationPlatformNode {
    private JFrame frame;
    private RotationPlatformNode rotationPlatformNode;
    private PhetPCanvas phetPCanvas;
    private RotationPlatform rotationPlatform = new RotationPlatform();

    public TestRotationPlatformNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas();
        phetPCanvas.setSize( frame.getSize() );
        rotationPlatformNode = new RotationPlatformNode( rotationPlatform );
        phetPCanvas.addScreenChild( rotationPlatformNode );

        final ModelSlider modelSlider = new ModelSlider( "variable.angle", "units.radians", 0, Math.PI * 2 * 2, Math.PI * 2 );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rotationPlatform.setPosition( modelSlider.getValue() );
            }
        } );
        PSwing pSwing = new PSwing( modelSlider );
        phetPCanvas.addScreenChild( pSwing );
        pSwing.setOffset( 0, rotationPlatformNode.getFullBounds().getMaxY() );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestRotationPlatformNode().start();
    }

    public RotationPlatformNode getPlatformNode() {
        return rotationPlatformNode;
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
