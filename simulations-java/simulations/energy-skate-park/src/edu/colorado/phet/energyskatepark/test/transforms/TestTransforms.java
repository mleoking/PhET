// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.test.transforms;

import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.energyskatepark.util.EnergySkateParkLogging;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 6:31:14 PM
 */

public class TestTransforms {
    private PCanvas contentPane;
    private JFrame frame;
    private ModelNode worldNode4;

    public TestTransforms() {
        frame = new JFrame();
        contentPane = new PCanvas();
        contentPane.setPanEventHandler( null );
        frame.setContentPane( contentPane );

        WorldNode worldNode = new WorldNode( contentPane, 6, 6 );//the node will fill the contentpanel, min model width of 6,
        // max model width of 6

        //Just create the path directly in model units.
        PPath path = new PPath();
        path.setStroke( new PFixedWidthStroke( 1 ) );
        path.moveTo( 0, 0 );
        path.lineTo( 3, 0 );
        path.lineTo( 3, 3 );
        path.closePath();
        worldNode.addChild( path );

        //One way to set size in ModelNode
        PImage image = PImageFactory.create( "energy-skate-park/images/skater3.png" );
        double aspectRatio = image.getWidth() / image.getHeight();
        image.setWidth( 3 );//model width
        image.setHeight( 3 / aspectRatio );//model height
        image.setOffset( 1, 0 );
        worldNode.addChild( image );

        //Another way to set size in model node.
        ModelNode modelNode = new ModelNode( PImageFactory.create( "energy-skate-park/images/skater3.png" ), 2.0 );
        worldNode.addChild( modelNode );

        ModelNode modelNode2 = new ModelNode( new PText( "Hello" ), 1.0 );
        worldNode.addChild( modelNode2 );

        worldNode4 = new ModelNode( PImageFactory.create( "energy-skate-park/images/skater3.png" ), 1.0 );
        worldNode4.setOffset( 1, 0 );
        worldNode4.rotateInPlace( Math.PI );
        worldNode4.addInputEventListener( new PDragEventHandler() );
        worldNode.addChild( worldNode4 );

        PPath measuringStick = new PPath();
        measuringStick.setStrokePaint( Color.red );
        measuringStick.moveTo( 0, 0 );
        measuringStick.lineTo( 100, 0 );
        measuringStick.lineTo( 100, 10 );
        measuringStick.lineTo( 200, 0 );
        measuringStick.lineTo( 200, 10 );
        measuringStick.lineTo( 300, 0 );
        ModelNode worldNode3 = new ModelNode( measuringStick, 3.0 );
        worldNode.addChild( worldNode3 );

        contentPane.getLayer().addChild( worldNode );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        new TestTransforms().start();
    }

    private void start() {
        frame.setVisible( true );
        EnergySkateParkLogging.println( "worldNode4.getFullBounds() = " + worldNode4.getGlobalFullBounds() );
    }
}
