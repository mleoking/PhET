/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * TxGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TxGraphic extends PhetGraphic {
    private AffineTransform atx;
    private PhetGraphic graphic;

    public TxGraphic( Component component, PhetGraphic graphic, AffineTransform atx ) {
        super( component );
        this.atx = atx;
        this.graphic = graphic;
    }

    protected Rectangle determineBounds() {
        -
        return graphic.getBounds();
    }

    public void paint( Graphics2D g2 ) {
        saveGraphicsState( g2 );
        g2.transform( atx );
        graphic.paint( g2 );
        restoreGraphicsState();
    }
}
