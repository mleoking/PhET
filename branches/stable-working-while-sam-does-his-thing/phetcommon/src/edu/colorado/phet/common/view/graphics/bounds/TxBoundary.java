/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.graphics.bounds;

import edu.colorado.phet.common.view.graphics.Boundary;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * @deprecated
 * @author ?
 * @version $Revision$
 */
public class TxBoundary implements Boundary {
    AffineTransform transform;
    ModelBoundary modelBounds;
    /**
     * @deprecated 
     * @param modelBounds
     * @param transform
     */
    public TxBoundary( ModelBoundary modelBounds, AffineTransform transform ) {
        this.transform = transform;
        this.modelBounds = modelBounds;
    }

    public boolean contains( int x, int y ) {
        Point2D modelCoordinate = transform.transform( new Point( x, y ), null );
        return modelBounds.contains( modelCoordinate.getX(), modelCoordinate.getY() );
    }

}
