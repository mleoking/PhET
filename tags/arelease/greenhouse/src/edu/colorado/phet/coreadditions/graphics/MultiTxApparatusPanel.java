/**
 * Class: MultiTxApparatusPanel
 * Class: edu.colorado.phet.coreadditions.graphics
 * User: Ron LeMaster
 * Date: Oct 17, 2003
 * Time: 8:00:10 PM
 */
package edu.colorado.phet.coreadditions.graphics;

import edu.colorado.phet.common.view.ApparatusPanel;
//import edu.colorado.phet.common.view.AffineTransformFactory;
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

    protected void paintComponent( Graphics graphics ) {
        swingGraphics = (Graphics2D)graphics;
        aTx = txFactory.getTx( this.getBounds() );
        try {
            invTx = aTx.createInverse();
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        super.paintComponent( graphics );
    }

    public MultiTxApparatusPanel( AffineTransformFactory tx ) {
        super( tx );
        txFactory = tx;
    }


    //
    // Interfaces implemented
    //
    
    //
    // Static fields and methods
    //
    
    //
    // Inner classes
    //
}
