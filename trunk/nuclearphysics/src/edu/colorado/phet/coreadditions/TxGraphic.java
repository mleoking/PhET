/**
 * Class: TxGraphic
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 5, 2004
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A decorator class for Graphic that applies associates an Affine Transform
 * with the graphic.
 */
public class TxGraphic extends PhetGraphic {
    private PhetGraphic graphic;
    private AffineTransform atx;

    public TxGraphic( PhetGraphic graphic, AffineTransform atx ) {
        this.graphic = graphic;
        this.atx = atx;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        g.transform( atx );
        graphic.paint( g );
        gs.restoreGraphics();
    }

    public PhetGraphic getWrappedGraphic() {
        return graphic;
    }

    public AffineTransform getTransform() {
        return atx;
    }

    public void setAtx( AffineTransform atx ) {
        this.atx = atx;
    }

    /**
     * Possible performance problem!!!!!
     * @return bounding rectangle
     */
    protected Rectangle determineBounds() {
        return atx.createTransformedShape(graphic.getBounds()).getBounds();
    }
}

