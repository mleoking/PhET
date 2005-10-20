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

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * Ion
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Ion extends Atom {

    private IonProperties ionProperties;
    private Lattice bindingLattice;

    public Ion( IonProperties ionProperties ) {
        this( new Point2D.Double(),
              new Vector2D.Double(),
              new Vector2D.Double(),
              ionProperties );
    }

    public Ion( Point2D position, Vector2D velocity, Vector2D acceleration, IonProperties ionProperties ) {
        super( position, velocity, acceleration, ionProperties.getMass(), ionProperties.getRadius() );
        this.ionProperties = ionProperties;
    }

    public void stepInTime( double dt ) {
        if( !isBound() ) {
            super.stepInTime( dt );
        }
    }

    public void bindTo( Binder binder ) {
        if( binder instanceof Lattice ) {
            bindingLattice = (Lattice)binder;
        }
        super.bindTo( binder );
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public double getCharge() {
        return ionProperties.getCharge();
    }

    public Lattice getBindingLattice() {
        return bindingLattice;
    }
}
