/**
 * Class: PhysicalPanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 9:11:05 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class PhysicalPanel extends ApparatusPanel {
    private AffineTransform atx = new AffineTransform();
    private HashMap modelElementToGraphicMap = new HashMap();
    private Point2D.Double origin = new Point2D.Double();
    private AffineTransform originTx = new AffineTransform();

    public PhysicalPanel() {
        this.setBackground( backgroundColor );
    }

    public void addNucleus( Nucleus nucleus ) {
        NucleusGraphic ng = new NucleusGraphic( nucleus );
        // Register the graphic to the model element
        modelElementToGraphicMap.put( nucleus, ng );
        addGraphic( ng, originTx );
//        addGraphic( ng );
    }

    public void removeNucleus( Nucleus nucleus ) {
        removeGraphic( (Graphic)modelElementToGraphicMap.get( nucleus ) );
        modelElementToGraphicMap.remove( nucleus );
    }

    public synchronized void addGraphic( Graphic graphic ) {
        super.addGraphic( graphic, originTx );
//        super.addGraphic( graphic );
    }

    public synchronized void removeGraphic( Graphic graphic ) {
        super.removeGraphic( graphic );    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        origin.setLocation( this.getWidth() / 2, this.getHeight() / 2 );

        originTx.setToTranslation( origin.getX(), origin.getY() );
        Graphics2D g2 = (Graphics2D)graphics;
        AffineTransform orgTx = g2.getTransform();
        atx.setToIdentity();
        atx.translate( -origin.getX(), -origin.getY() );
//        g2.setTransform( atx );
        super.paintComponent( graphics );    //To change body of overridden methods use File | Settings | File Templates.
//        g2.setTransform( orgTx );
    }

    public void clear() {
        super.removeAllGraphics();
        modelElementToGraphicMap.clear();
    }

    //
    // Statics
    //
    private static Color backgroundColor = new Color( 255, 255, 230 );
}
