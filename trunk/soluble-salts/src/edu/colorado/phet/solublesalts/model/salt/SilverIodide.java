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
import edu.colorado.phet.solublesalts.model.Iodine;
import edu.colorado.phet.solublesalts.model.Silver;

import java.util.HashMap;

/**
 * SilverIodide
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SilverIodide extends Salt {

    static private Lattice lattice = new PlainCubicLattice( Silver.RADIUS + Iodine.RADIUS );
    static private HashMap components = new HashMap();

    static {
        components.put( Silver.class, new Integer( 1 ) );
        components.put( Iodine.class, new Integer( 1 ) );
    }

    public SilverIodide() {
        super( components, lattice, Silver.class, Iodine.class );
    }
}
