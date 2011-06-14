// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.salt;

import java.util.ArrayList;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.OneToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Bromine;
import edu.colorado.phet.solublesalts.model.ion.Silver;

/**
 * SilverIodide
 *
 * @author Ron LeMaster
 */
public class SilverBromide extends Salt {

    static private Lattice lattice = new OneToOneLattice( Silver.RADIUS + Bromine.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        SilverBromide.components.add( new Component( Silver.class, new Integer( 1 ) ) );
        SilverBromide.components.add( new Component( Bromine.class, new Integer( 1 ) ) );
    }

    public SilverBromide() {
        super( SilverBromide.components, SilverBromide.lattice,
               Silver.class, Bromine.class, 5.25E-13 );
    }
}
