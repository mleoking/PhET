/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics.mousecontrols;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * TxTranslatable
 *
 * @author ?
 * @version $Revision$
 */
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
