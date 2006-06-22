/**
 * Class: TxTranslatable
 * Package: edu.colorado.phet.common.view.graphics.mousecontrols
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.common_cck.view.graphics.mousecontrols;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class TxTranslatable implements Translatable {
    AffineTransform viewToModelTx;
    Translatable translatable;

    public TxTranslatable( Translatable translatable, AffineTransform viewToModelTx ) {
        this.viewToModelTx = viewToModelTx;
        this.translatable = translatable;
    }

    public void translate( double dx, double dy ) {
        Point2D.Double orig = new Point2D.Double( dx, dy );
        Point2D out = viewToModelTx.deltaTransform( orig, null );
        translatable.translate( out.getX(), out.getY() );
    }
}
