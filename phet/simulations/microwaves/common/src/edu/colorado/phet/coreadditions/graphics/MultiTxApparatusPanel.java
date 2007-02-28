/**
 * Class: MultiTxApparatusPanel
 * Class: edu.colorado.phet.coreadditions.graphics
 * User: Ron LeMaster
 * Date: Oct 17, 2003
 * Time: 8:00:10 PM
 */
package edu.colorado.phet.coreadditions.graphics;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.ModelViewTransform2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

public class MultiTxApparatusPanel extends ApparatusPanel {

    public static Graphics2D swingGraphics;
    public static ModelViewTransform2D modelViewTx;
    private AffineTransformFactory txFactory;
    public static AffineTransform aTx;
    public static AffineTransform invTx;
    private AffineTransform orgTx;
    private AffineTransform imageTx;
    private CompositeAffineTransform compositeTx = new CompositeAffineTransform();

    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        // Get the transform attached to the Graphics2D so we can get at its attributes
        orgTx = g2.getTransform();
        // Set up the transform that will be applied to the thermometer images
        imageTx = new AffineTransform();
        // Translate the transform to the model origin
        imageTx.translate( orgTx.getTranslateX() / orgTx.getScaleX(), orgTx.getTranslateY() / orgTx.getScaleY() );
        // Concatenate the inverse of the model-to-Swing coords transform
        try {
            imageTx.concatenate( g2.getTransform().createInverse() );
        }
        catch( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
        // Translate the transform so the bottom of the image is now at the model origin
//        imageTx.translate( 0, -image.getHeight() );
//        // Move the image to it's location in model coordinates
//        imageTx.translate( location.getX() * orgTx.getScaleX(),
//                           location.getY() * orgTx.getScaleY() );

//        swingGraphics = (Graphics2D)graphics;
//        aTx = txFactory.getTx( this.getBounds() );
//        try {
//            invTx = aTx.createInverse();
//        }
//        catch( NoninvertibleTransformException e ) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        }

        compositeTx.addTx( ShapeGraphicType.class, orgTx );
        compositeTx.addTx( ImageGraphicType.class, imageTx );
        g2.setTransform( compositeTx );
        super.paintComponent( graphics );
    }

    public MultiTxApparatusPanel( AffineTransformFactory tx ) {
        super( tx );
        txFactory = tx;
    }

}
