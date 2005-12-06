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
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.model.Sodium;
import edu.colorado.phet.solublesalts.model.Chloride;
import edu.colorado.phet.solublesalts.model.Lead;

import java.util.HashMap;

/**
 * SodiumChloride
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LeadChloride extends Salt {

    static private Lattice lattice = new TwoToOneLattice( Chloride.class, Lead.class, Lead.RADIUS + Chloride.RADIUS );
    static private HashMap components = new HashMap();

    static {
        components.put( Lead.class, new Integer( 2 ) );
        components.put( Chloride.class, new Integer( 1 ) );
    }

    public LeadChloride() {
        super( components, lattice );
    }
}
