// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.model.salt;

import java.util.ArrayList;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.OneToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Copper;
import edu.colorado.phet.solublesalts.model.ion.Iodide;
import edu.colorado.phet.solublesalts.model.ion.Silver;

/**
 * SilverIodide
 *
 * @author Ron LeMaster
 */
public class CuprousIodide extends Salt {

    static private Lattice lattice = new OneToOneLattice( Silver.RADIUS + Iodide.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        CuprousIodide.components.add( new Component( Copper.class, new Integer( 1 ) ) );
        CuprousIodide.components.add( new Component( Iodide.class, new Integer( 1 ) ) );
    }

    public CuprousIodide() {
        super( CuprousIodide.components, CuprousIodide.lattice, Copper.class, Iodide.class, 1.10E-12 );
    }
}
