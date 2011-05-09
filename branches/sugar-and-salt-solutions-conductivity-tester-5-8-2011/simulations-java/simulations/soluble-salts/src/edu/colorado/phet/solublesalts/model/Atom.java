// Copyright 2002-2011, University of Colorado

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
import edu.colorado.phet.common.phetcommon.math.Vector2D;

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

    protected Atom( Point2D center, Vector2D velocity, Vector2D acceleration, double mass, double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }
}
