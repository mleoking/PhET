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
 * This Vector2D implementation supports mutators & in-place operations.
 *
 * @author Ron LeMaster
 * @author Sam Reid
 * @version $Revision$
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