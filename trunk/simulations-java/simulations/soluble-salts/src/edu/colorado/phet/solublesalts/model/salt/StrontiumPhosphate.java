// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.salt;

import java.util.ArrayList;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.ThreeToTwoLattice;
import edu.colorado.phet.solublesalts.model.ion.Phosphate;
import edu.colorado.phet.solublesalts.model.ion.Strontium;

/**
 * StrontiumPhosphate
 *
 * @author Ron LeMaster
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
