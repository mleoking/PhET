// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.salt;

import java.util.ArrayList;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Bromine;
import edu.colorado.phet.solublesalts.model.ion.Mercury;

/**
 * MercuryBromine
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MercuryBromide extends Salt {

    static private Lattice lattice = new TwoToOneLattice( Mercury.class,
                                                          Bromine.class,
                                                          Mercury.RADIUS + Bromine.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Component( Mercury.class, new Integer( 1 ) ) );
        components.add( new Component( Bromine.class, new Integer( 2 ) ) );
    }

    public MercuryBromide() {
        super( components, lattice, Mercury.class, Bromine.class, 6.2E-20 );
    }
}
