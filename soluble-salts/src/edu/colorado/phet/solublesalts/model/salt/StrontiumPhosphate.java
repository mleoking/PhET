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

import edu.colorado.phet.solublesalts.model.crystal.*;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.model.ion.*;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * StrontiumPhosphate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StrontiumPhosphate extends Salt {

    static private Lattice lattice = new ThreeToTwoLattice( Strontium.class,
                                                            Phosphate.class,
                                                            Strontium.RADIUS + Phosphate.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Component( Strontium.class, new Integer( 2 ) ) );
        components.add( new Component( Phosphate.class, new Integer( 3 ) ) );
    }

    public StrontiumPhosphate() {
        super( components, lattice, Strontium.class, Phosphate.class, 1E-31 );
    }
}
