/**
 * Class: TxGraphic
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 5, 2004
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A decorator class for Graphic that applies associates an Affine Transform
 * with the graphic.
 */
public class TxGraphic implements Graphic {
    private Graphic graphic;
    private AffineTransform atx;

    public TxGraphic( Graphic graphic, AffineTransform atx ) {
        this.graphic = graphic;
        this.atx = atx;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.transform( atx );
        graphic.paint( g );
        gs.restoreGraphics();
    }

    public Graphic getWrappedGraphic() {
        return graphic;
    }

    public AffineTransform getTransform() {
        return atx;
    }

    public void setAtx( AffineTransform atx ) {
        this.atx = atx;
    }
}

