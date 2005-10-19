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

import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Atom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Atom extends SphericalBody {
    private static Random random = new Random();

    private Set binders = new HashSet();

    public Atom( double radius ) {
        super( radius );
    }

    protected Atom( Point2D center, Vector2D velocity, Vector2D acceleration, double mass, double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }

//    public void setIsBound( boolean isBound ) {
//        this.isBound = isBound;
//        notifyObservers();
//    }

    public void bindTo( Binder binder ) {
        binders.add( binder );
        notifyObservers();
    }

    public void unbindFrom( Binder binder ) {
        binders.remove( binder );

        // Choose a new velocity
        if( binders.isEmpty() ) {
            double theta = random.nextDouble() * Math.PI * 2;
        }

        notifyObservers();
    }

    public boolean isBound() {
        return !binders.isEmpty();
    }
}
