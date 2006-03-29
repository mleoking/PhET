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
import edu.colorado.phet.solublesalts.model.ion.Chloride;
import edu.colorado.phet.solublesalts.model.ion.Lead;

import java.util.ArrayList;

/**
 * SodiumChloride
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LeadChloride extends Salt {

    static private Lattice lattice = new TwoToOneLattice( Chloride.class, Lead.class, Lead.RADIUS + Chloride.RADIUS );
//    static private Lattice lattice = new TwoToOneLattice( Chlorine.class, Lead.class, Lead.RADIUS + Chlorine.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Salt.Component( Lead.class, new Integer( 2 ) ) );
        components.add( new Salt.Component( Chloride.class, new Integer( 1 ) ) );
    }

    public LeadChloride() {
        super( components, lattice, Lead.class, Chloride.class, 1.6E-5 );
    }
}
