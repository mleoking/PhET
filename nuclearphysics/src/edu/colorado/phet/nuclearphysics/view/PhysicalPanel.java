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
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;

public class PhysicalPanel extends ApparatusPanel {

    private HashMap modelElementToGraphicMap = new HashMap();
    protected Point2D.Double origin = new Point2D.Double();
    protected AffineTransform originTx = new AffineTransform();
    protected AffineTransform scaleTx = new AffineTransform();
    protected AffineTransform nucleonTx = new AffineTransform();
    private double nucleusLevel = Config.nucleusLevel;

    public PhysicalPanel() {
        this.setBackground( backgroundColor );
        setScale( 1 );
    }

    public double getScale() {
        return scaleTx.getScaleX();
    }

    public void setScale( double scale ) {
        scaleTx.setToScale( scale, scale );
    }

    public void addNucleus( Nucleus nucleus ) {
        NucleusGraphic ng = NucleusGraphicFactory.create( nucleus );
        // Register the graphic to the model element
        modelElementToGraphicMap.put( nucleus, ng );
        addGraphic( ng, nucleusLevel, nucleonTx );
    }

    public void removeNucleus( Nucleus nucleus ) {
        removeGraphic( (Graphic)modelElementToGraphicMap.get( nucleus ) );
        modelElementToGraphicMap.remove( nucleus );
    }

    public synchronized void addGraphic( Graphic graphic ) {
        super.addGraphic( graphic, nucleonTx );
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        origin.setLocation( this.getWidth() / 2, this.getHeight() / 2 );
        originTx.setToTranslation( origin.getX(), origin.getY() );

        nucleonTx.setToIdentity();
        nucleonTx.concatenate( originTx );
        nucleonTx.concatenate( scaleTx );

        GraphicsUtil.setAlpha( (Graphics2D)graphics, 1 );
        super.paintComponent( graphics );
    }

    public void clear() {
        Iterator it = modelElementToGraphicMap.keySet().iterator();
        while( it.hasNext() ) {
            removeGraphic( (Graphic)modelElementToGraphicMap.get( (Nucleus)it.next() ) );
        }
        modelElementToGraphicMap.clear();
    }

    //
    // Statics
    //
    protected static Color backgroundColor = new Color( 255, 255, 230 );

    public synchronized void addOriginCenteredGraphic( Graphic graphic, double level ) {
        this.addGraphic( graphic, level, originTx );
    }
}
