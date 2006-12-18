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
import edu.colorado.phet.solublesalts.model.crystal.ThreeToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Chromium;
import edu.colorado.phet.solublesalts.model.ion.Hydroxide;

import java.util.ArrayList;

/**
 * CopperHydroxide
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ChromiumHydroxide extends Salt {

    static private Lattice lattice = new ThreeToOneLattice( Chromium.class,
                                                            Hydroxide.class,
                                                            Chromium.RADIUS + Hydroxide.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Salt.Component( Chromium.class, new Integer( 1 ) ) );
        components.add( new Salt.Component( Hydroxide.class, new Integer( 3 ) ) );
    }

    public ChromiumHydroxide() {
        super( components, lattice, Chromium.class, Hydroxide.class, 6.7E-31 );
    }
}
