/* Copyright 2004-2010, University of Colorado */

package edu.colorado.phet.common.phetcommon.math;

/**
 * This Vector2D implementation supports mutators & in-place operations.
 *
 * @author Ron LeMaster
 * @author Sam Reid
 */
public interface Vector2DInterface extends AbstractVector2DInterface {

    Vector2DInterface add( AbstractVector2DInterface v );

    Vector2DInterface scale( double scale );

    Vector2DInterface subtract( AbstractVector2DInterface that );

    Vector2DInterface rotate( double theta );

    void setX( double x );

    void setY( double y );

    void setComponents( double x, double y );

    Vector2DInterface normalize();

}