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
import edu.colorado.phet.solublesalts.model.crystal.ThreeToTwoLattice;
import edu.colorado.phet.solublesalts.model.ion.Phosphate;
import edu.colorado.phet.solublesalts.model.ion.Strontium;

import java.util.ArrayList;

/**
 * StrontiumPhosphate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StrontiumPhosphate extends Salt {

    static private Lattice lattice = new ThreeToTwoLattice( Phosphate.class,
                                                            Strontium.class,
                                                            Strontium.RADIUS + Phosphate.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Component( Strontium.class, new Integer( 3 ) ) );
        components.add( new Component( Phosphate.class, new Integer( 2 ) ) );
    }

    public StrontiumPhosphate() {
        super( components, lattice, Strontium.class, Phosphate.class, 1E-31 );
    }
}
