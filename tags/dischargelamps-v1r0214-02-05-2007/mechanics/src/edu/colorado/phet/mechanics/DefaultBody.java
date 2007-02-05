/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mechanics;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * DefaultBody
 * <p>
 * A concrete extension of Body that provides default implementations
 * of teh abstract methods in Body.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DefaultBody extends Body {

    protected DefaultBody() {
        super();
    }

    protected DefaultBody( Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
    }

    public Point2D getCM() {
        return null;
    }

    public double getMomentOfInertia() {
        return 0;
    }
}
