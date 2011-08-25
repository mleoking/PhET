// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * List containing all pairs of particles that could be used to seed a crystal
 *
 * @author Sam Reid
 */
public class AllPairs extends ArrayList<IFormulaUnit> {
    public AllPairs( ItemList<Particle> freeParticles, Class<? extends Particle> typeA, Class<? extends Particle> typeB ) {
        ArrayList<Particle> aList = freeParticles.filter( typeA );
        ArrayList<Particle> bList = freeParticles.filter( typeB );
        for ( Particle a : bList ) {
            for ( Particle b : aList ) {

                //Check for equality in case typeA==typeB, as in the case of Sucrose
                if ( a != b ) {
                    add( new FormulaUnit( a, b ) );
                }
            }
        }
    }
}