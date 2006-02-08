/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.salt;

import edu.colorado.phet.solublesalts.model.crystal.Lattice_new_new;
import edu.colorado.phet.solublesalts.model.crystal.PlainCubicLattice;
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Sodium;

import java.util.ArrayList;

/**
 * SodiumChloride
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SodiumChloride extends Salt {

    static private Lattice_new_new lattice = new PlainCubicLattice( Sodium.RADIUS + Chlorine.RADIUS );
//    static private Lattice lattice = new PlainCubicLattice( Sodium.RADIUS + Chlorine.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Salt.Component( Sodium.class, new Integer( 1 ) ) );
        components.add( new Salt.Component( Chlorine.class, new Integer( 1 ) ) );
    }

    public SodiumChloride() {
        super( components, lattice, Sodium.class, Chlorine.class, 1E-5 );
    }
}
