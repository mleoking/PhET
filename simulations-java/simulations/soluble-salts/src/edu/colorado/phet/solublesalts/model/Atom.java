/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.collision.SphericalBody;
import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

/**
 * Atom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Atom extends SphericalBody {

    public Atom( double radius ) {
        super( radius );
    }

    protected Atom( Point2D center, Vector2DInterface velocity, Vector2DInterface acceleration, double mass, double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }
}
