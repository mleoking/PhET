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
import edu.colorado.phet.solublesalts.model.crystal.OneToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Silver;
import edu.colorado.phet.solublesalts.model.ion.Iodide;
import edu.colorado.phet.solublesalts.model.ion.Copper;

import java.util.ArrayList;

/**
 * SilverIodide
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CuprousIodide extends Salt {

    static private Lattice lattice = new OneToOneLattice( Silver.RADIUS + Iodide.RADIUS );
//    static private Lattice lattice = new PlainCubicLattice( Silver.RADIUS + Iodine.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        CuprousIodide.components.add( new Component( Copper.class, new Integer( 1 ) ) );
        CuprousIodide.components.add( new Component( Iodide.class, new Integer( 1 ) ) );
    }

    public CuprousIodide() {
        super( CuprousIodide.components, CuprousIodide.lattice, Copper.class, Iodide.class, 5.1E-12 );
    }
}
