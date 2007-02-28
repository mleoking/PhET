package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.io.IOException;

/**
 * awkward because of overall scale on parent node
 */

public class JetPackGraphic2 extends PhetPNode {
    private EnergySkateParkModule ec3Module;
    private Body body;
    private PImage imagePNode;

    public JetPackGraphic2( EnergySkateParkModule ec3Module, Body body ) {
        this.ec3Module = ec3Module;
        this.body = body;
        try {
            imagePNode = new PImage( ImageLoader.loadBufferedImage( "images/rocket5.png" ) );
            addChild( imagePNode );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        update();
        ec3Module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
//        ec3Module.getClock().addClockListener( new ClockAdapter() {
//            public void simulationTimeChanged( ClockEvent clockEvent ) {
//                update();
//            }
//        } );
//        scale( 1.0/17000.0*200);
    }

    private void update() {
        setVisible( body.getThrust().getMagnitude() > 0 );
        double x = body.getCenterOfMass().getX() - getFullBounds().getWidth() / 2;
        double y = body.getCenterOfMass().getY() - getFullBounds().getHeight() / 2;
        imagePNode.setRotation( body.getThrust().getAngle() + Math.PI / 2 );
        imagePNode.setOffset( x, y );
        double scale = 1.0 / 17000.0 * 200;
        imagePNode.setScale( scale );
    }

    public void setBody( Body body ) {
        if( this.body != body ) {
            this.body = body;
            update();
        }
    }
}
