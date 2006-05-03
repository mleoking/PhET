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
import edu.colorado.phet.solublesalts.model.ion.Bromine;

import java.util.ArrayList;

/**
 * SilverIodide
 *
 * @author Ron LeMaster
 * @version $Revision$
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
