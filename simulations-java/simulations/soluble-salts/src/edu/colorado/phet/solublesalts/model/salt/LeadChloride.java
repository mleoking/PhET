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
import edu.colorado.phet.solublesalts.model.ion.Chlorine;
import edu.colorado.phet.solublesalts.model.ion.Lead;

/**
 * SodiumChloride
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LeadChloride extends Salt {

    static private Lattice lattice = new TwoToOneLattice( Chlorine.class, Lead.class, Lead.RADIUS + Chlorine.RADIUS );
    //    static private Lattice lattice = new TwoToOneLattice( Chlorine.class, Lead.class, Lead.RADIUS + Chlorine.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Salt.Component( Lead.class, new Integer( 2 ) ) );
        components.add( new Salt.Component( Chlorine.class, new Integer( 1 ) ) );
    }

    public LeadChloride() {
        super( components, lattice, Lead.class, Chlorine.class, 1.6E-5 );
    }
}
