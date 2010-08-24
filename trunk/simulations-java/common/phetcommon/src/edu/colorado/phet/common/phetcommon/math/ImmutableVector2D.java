/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * This Vector2D implementation is immutable.
 *
 * @author Ron LeMaster
 * @author Sam Reid
 * @version $Revision$
 */
public interface ImmutableVector2D extends AbstractVector2D {

    public class Double extends AbstractVector2D.Double implements ImmutableVector2D {
        public Double() {
        }

        public Double( double x, double y ) {
            super( x, y );
        }

        public Double( Vector2D v ) {
            super( v );
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