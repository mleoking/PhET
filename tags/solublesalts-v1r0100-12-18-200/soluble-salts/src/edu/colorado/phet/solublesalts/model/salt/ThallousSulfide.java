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
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.model.ion.Copper;
import edu.colorado.phet.solublesalts.model.ion.Hydroxide;
import edu.colorado.phet.solublesalts.model.ion.Thallium;
import edu.colorado.phet.solublesalts.model.ion.Sulfur;

import java.util.ArrayList;

/**
 * CopperHydroxide
 *
 * @author Ron LeMaster
 * @version $Revision$
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
