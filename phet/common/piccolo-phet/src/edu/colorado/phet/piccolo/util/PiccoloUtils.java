/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo.util;

import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;

/**
 * PiccoloUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PiccoloUtils {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    // Identifiers for border locations
    public static final int NORTH = 1, NORTH_EAST = 2, EAST = 3, SOUTH_EAST = 4, SOUTH = 5, SOUTH_WEST = 6, WEST = 7, NORTH_WEST = 8;

    /**
     * Returns the location of a specified compass point on the bounds of a PNode.
     * This is useful for locating one PNode relative to another.
     * @param pnode
     * @param borderPointID
     * @return
     */
    public static Point2D getBorderPoint( PNode pnode, int borderPointID ) {
        double x = pnode.getOffset().getX();
        double y = pnode.getOffset().getY();
        double height = pnode.getFullBounds().getHeight();
        double width = pnode.getFullBounds().getWidth();
        switch( borderPointID ) {
            case NORTH:
                x += width / 2;
                break;
            case NORTH_EAST:
                x += width;
                break;
            case EAST:
                x += width;
                y += height / 2;
                break;
            case SOUTH_EAST:
                x += width;
                y += height;
                break;
            case SOUTH:
                x += width / 2;
                y += height;
                break;
            case SOUTH_WEST:
                y += height;
                break;
            case WEST:
                y += height / 2;
                break;
            case NORTH_WEST:
                break;
            default:
                throw new RuntimeException( "invalid borderPointID");
        }
        return new Point2D.Double( x, y );
    }
}
