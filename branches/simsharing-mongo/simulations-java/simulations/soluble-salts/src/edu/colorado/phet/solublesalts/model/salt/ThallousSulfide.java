// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.salt;

import java.util.ArrayList;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Sulfur;
import edu.colorado.phet.solublesalts.model.ion.Thallium;

/**
 * CopperHydroxide
 *
 * @author Ron LeMaster
 */
public class ThallousSulfide extends Salt {

    static private Lattice lattice = new TwoToOneLattice( Sulfur.class,
                                                          Thallium.class,
                                                          Thallium.RADIUS + Sulfur.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        ThallousSulfide.components.add( new Component( Thallium.class, new Integer( 2 ) ) );
        ThallousSulfide.components.add( new Component( Sulfur.class, new Integer( 1 ) ) );
    }

    public ThallousSulfide() {
        super( ThallousSulfide.components, ThallousSulfide.lattice, Thallium.class, Sulfur.class, 6E-22 );
    }
}
