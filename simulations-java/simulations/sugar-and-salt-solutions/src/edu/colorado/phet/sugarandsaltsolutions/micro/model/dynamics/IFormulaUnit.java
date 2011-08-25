// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.ArrayList;

/**
 * Pair of matching particles that could potentially form a crystal together, if they are close enough together.  Order within the pair is irrelevant.
 *
 * @author Sam Reid
 */
public interface IFormulaUnit<T> {

    //Get the distance between the particles
    public double getDistance();

    //Move the particles closer together at the free particle speed
    public void moveTogether( double dt );

    //List all particles for purposes of iteration to add to a crystal
    public ArrayList<T> getParticles();
}