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
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.model.*;

import java.util.HashMap;

/**
 * CopperHydroxide
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CopperHydroxide extends Salt {

    static private Lattice lattice = new TwoToOneLattice( Copper.class,
                                                          Hydroxide.class,
                                                          Copper.RADIUS + Hydroxide.RADIUS );
    static private HashMap components = new HashMap();

    static {
        components.put( Copper.class, new Integer( 1 ) );
        components.put( Hydroxide.class, new Integer( 2 ) );
    }

    public CopperHydroxide() {
        super( components, lattice, Copper.class, Hydroxide.class );
    }
}
