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

import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.PlainCubicLattice;
import edu.colorado.phet.solublesalts.model.Sodium;
import edu.colorado.phet.solublesalts.model.Chloride;

import java.util.HashMap;

/**
 * SodiumChloride
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SodiumChloride extends Salt {

    static private Lattice lattice = new PlainCubicLattice( Sodium.RADIUS + Chloride.RADIUS );
    static private HashMap components = new HashMap();

    static {
        components.put( Sodium.class, new Integer( 1 ) );
        components.put( Chloride.class, new Integer( 1 ) );
    }

    public SodiumChloride() {
        super( components, lattice, Sodium.class, Chloride.class );
    }
}
