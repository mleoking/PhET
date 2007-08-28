/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */

package edu.colorado.phet.colorvision.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * ImmutableVector2D
 *
 * @author Ron LeMaster
 * @version $Revision:14443 $
 */
public interface ImmutableVector2D extends AbstractVector2D {

    public class Double extends AbstractVector2D.Double implements ImmutableVector2D {
        public Double() {
        }

        public Double( double x, double y ) {
            super( x, y );
        }

        public Double( AbstractVector2D v ) {
            super( v );
        }

        public Double( Point2D p ) {
            super( p );
        }

        public Double( Point2D initialPt, Point2D finalPt ) {
            super( initialPt, finalPt );
        }

    }

}
