/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.affinity;

import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.util.InvalidParameterTypeException;

import java.util.Random;

/**
 * RandomAffinity
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RandomAffinity implements Affinity {
    Random random = new Random( System.currentTimeMillis() );
    double affinityLikelihood;

    public RandomAffinity( double affinityLikelihood ) {
        this.affinityLikelihood = affinityLikelihood;
    }

    public boolean stick( Object obj1, Object obj2 ) {
        if( !( obj1 instanceof Ion && obj2 instanceof Vessel ) ) {
            throw new InvalidParameterTypeException();
        }
        Ion ion = (Ion)obj1;
        Vessel vessel = (Vessel)obj2;
        return random.nextDouble() <= affinityLikelihood
               && !( ion.getPosition().getY() - ion.getRadius() <= vessel.getWater().getMinY() );
    }
}
