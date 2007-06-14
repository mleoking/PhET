/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/math/ImmutableVector2D.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.13 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */

package edu.colorado.phet.common.math;

import java.awt.geom.Point2D;

/**
 * This Vector2D implementation is immutable.
 *
 * @author Ron LeMaster
 * @version $Revision: 1.13 $
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

    public class Float extends AbstractVector2D.Float implements ImmutableVector2D {

        public Float() {
        }

        public Float( float x, float y ) {
            super( x, y );
        }

        public Float( Vector2D v ) {
            super( v );
        }

        public Float( AbstractVector2D v ) {
            super( v );
        }

        public Float( Point2D p ) {
            super( p );
        }

        public Float( Point2D initialPt, Point2D finalPt ) {
            this( (float)( finalPt.getX() - initialPt.getX() ), (float)( finalPt.getY() - initialPt.getY() ) );
        }

    }
}
