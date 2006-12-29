package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 2:49:22 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.view.PlatformNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TestPlatformNode {
    private JFrame frame;

    public TestPlatformNode() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.setSize( frame.getSize() );
        final PlatformNode platformNode = new PlatformNode();
        platformNode.setOffset( 100, 100 );
        phetPCanvas.addScreenChild( platformNode );

        final ModelSlider modelSlider = new ModelSlider( "angle", "radians", 0, Math.PI * 2 * 2, Math.PI * 2 );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                platformNode.setAngle( modelSlider.getValue() );
            }
        } );
        PSwing pSwing = new PSwing( phetPCanvas, modelSlider );
        phetPCanvas.addScreenChild( pSwing );
        pSwing.setOffset( 0, platformNode.getFullBounds().getMaxY() );
        frame.setContentPane( phetPCanvas );
    }

    public static void main( String[] args ) {
        new TestPlatformNode().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
