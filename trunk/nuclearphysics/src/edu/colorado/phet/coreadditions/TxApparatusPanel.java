/**
 * Class: TxApparatusPanel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.geom.AffineTransform;
import java.util.HashMap;

/**
 * An ApparatusPanel that handles TxGraphics
 */
public class TxApparatusPanel extends ApparatusPanel2 {
    //    public class TxApparatusPanel extends ApparatusPanel {
    private HashMap txGraphicMap = new HashMap();
    private AffineTransform atx = new AffineTransform();

    public TxApparatusPanel( BaseModel model ) {
        super( model );
    }

    public AffineTransform getTransform() {
        return atx;
    }

    public void setTransform( AffineTransform atx ) {
        this.atx = atx;
    }

    public void addGraphic( Graphic graphic, double level ) {
        super.addGraphic( graphic, level );
        if( graphic instanceof TxGraphic ) {
            TxGraphic txg = (TxGraphic)graphic;
            txGraphicMap.put( txg.getWrappedGraphic(), txg );
        }
    }

    public void removeGraphic( Graphic graphic ) {
        super.removeGraphic( graphic );
        if( !( graphic instanceof TxGraphic ) ) {
            super.removeGraphic( (Graphic)txGraphicMap.get( graphic ) );
            txGraphicMap.remove( graphic );
        }
    }

    public void removeAllGraphics() {
        super.removeAllGraphics();
        txGraphicMap.clear();
    }
}
