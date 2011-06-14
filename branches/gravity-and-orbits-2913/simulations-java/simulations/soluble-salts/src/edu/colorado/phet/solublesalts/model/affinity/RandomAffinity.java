// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.affinity;

import java.util.Random;

import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.util.InvalidParameterTypeException;

/**
 * RandomAffinity
 *
 * @author Ron LeMaster
 */
public class RandomAffinity implements Affinity {
    Random random = new Random( System.currentTimeMillis() );
    double affinityLikelihood;

    public RandomAffinity( double affinityLikelihood ) {
        this.affinityLikelihood = affinityLikelihood;
    }

    public boolean stick( Object obj1, Object obj2 ) {
        if ( !( obj1 instanceof Ion && obj2 instanceof Vessel ) ) {
            throw new InvalidParameterTypeException();
        }
        Ion ion = (Ion) obj1;
        Vessel vessel = (Vessel) obj2;
        return random.nextDouble() <= affinityLikelihood
               && !( ion.getPosition().getY() - ion.getRadius() <= vessel.getWater().getMinY() );
    }
}
