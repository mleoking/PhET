/**
 * Class: PhysicalPanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 9:11:05 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.TxApparatusPanel;
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.NuclearModelElement;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class PhysicalPanel extends TxApparatusPanel {
    //public class PhysicalPanel extends ApparatusPanel {

    //    private HashMap modelElementToGraphicMap = new HashMap();
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
        final TxGraphic txg = new TxGraphic( ng, nucleonTx );
        NuclearModelElement.Listener listener = new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                PhysicalPanel.this.removeGraphic( txg );
            }
        };
        nucleus.addListener( listener );
        addGraphic( txg, nucleusLevel );
    }

    public void removeNucleus( Nucleus nucleus ) {
        //        removeGraphic( (Graphic)modelElementToGraphicMap.get( nucleus ) );
        //        modelElementToGraphicMap.remove( nucleus );
        return;
    }

    public synchronized void addGraphic( Graphic graphic ) {
        TxGraphic txg = new TxGraphic( graphic, nucleonTx );
        super.addGraphic( txg );
        //        addGraphic( graphic, nucleonTx );
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        origin.setLocation( this.getWidth() / 2, this.getHeight() / 2 );
        originTx.setToTranslation( origin.getX(), origin.getY() );

        nucleonTx.setToIdentity();
        nucleonTx.concatenate( originTx );
        nucleonTx.concatenate( scaleTx );

        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAlpha( (Graphics2D)graphics, 1 );
        super.paintComponent( g2 );
        gs.restoreGraphics();
    }

    public void clear() {
        //        Iterator it = modelElementToGraphicMap.keySet().iterator();
        //        while( it.hasNext() ) {
        //            removeGraphic( (Graphic)modelElementToGraphicMap.get( (Nucleus)it.next() ) );
        //        }
        //        modelElementToGraphicMap.clear();
    }

    //    protected void addGraphic( Graphic graphic, double level, AffineTransform atx ) {
    //        TxGraphic txg = new TxGraphic( graphic, atx );
    //        super.addGraphic( txg, level );
    //    }
    //
    //    protected void addGraphic( Graphic graphic, AffineTransform atx ) {
    //        TxGraphic txg = new TxGraphic( graphic, atx );
    //        super.addGraphic( txg );
    //    }

    //
    // Statics
    //
    protected static Color backgroundColor = new Color( 255, 255, 230 );

    //    public synchronized void addOriginCenteredGraphic( Graphic graphic, double level ) {
    //        this.addGraphic( graphic, level, originTx );
    //    }
    public void addOriginCenteredGraphic( Graphic graphic, double level ) {
        TxGraphic txg = new TxGraphic( graphic, this.originTx );
        addGraphic( txg, level );
    }
}
