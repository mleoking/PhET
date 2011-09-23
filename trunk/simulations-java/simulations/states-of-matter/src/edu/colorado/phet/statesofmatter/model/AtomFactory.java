// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.ConfigurableStatesOfMatterAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

/**
 * This class is used to create an instance of the desired type of atom.
 *
 * @author John Blanco
 */
public class AtomFactory {

    public static StatesOfMatterAtom createAtom( AtomType atomType ) {
        StatesOfMatterAtom atom = null;

        if ( atomType == AtomType.ADJUSTABLE ) {
            atom = new ConfigurableStatesOfMatterAtom( 0, 0 );
        }
        else if ( atomType == AtomType.ARGON ) {
            atom = new ArgonAtom( 0, 0 );
        }
        else if ( atomType == AtomType.NEON ) {
            atom = new NeonAtom( 0, 0 );
        }
        else if ( atomType == AtomType.OXYGEN ) {
            atom = new OxygenAtom( 0, 0 );
        }

        return atom;
    }
}
