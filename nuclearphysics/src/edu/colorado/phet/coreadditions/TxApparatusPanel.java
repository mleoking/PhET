/**
 * Class: TxApparatusPanel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.util.HashMap;

public class TxApparatusPanel extends ApparatusPanel {
    private HashMap txGraphicMap = new HashMap();

    public void addGraphic( Graphic graphic, double level ) {
        super.addGraphic( graphic, level );
        if( graphic instanceof TxGraphic ) {
            TxGraphic txg = (TxGraphic)graphic;
            txGraphicMap.put( txg.getWrappedGraphic(), txg );
        }
    }

    public void removeGraphic( Graphic graphic ) {
        if( !( graphic instanceof TxGraphic ) ) {
            super.removeGraphic( (Graphic)txGraphicMap.get( graphic ) );
            txGraphicMap.remove( graphic );
        }
        else {
            super.removeGraphic( graphic );
        }
    }

    public void removeAllGraphics() {
        super.removeAllGraphics();
        txGraphicMap.clear();
    }
}
