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
import edu.colorado.phet.solublesalts.model.ion.Silver;
import edu.colorado.phet.solublesalts.model.ion.Arsenate;

import java.util.ArrayList;

/**
 * CopperHydroxide
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SilverArsenate extends Salt {

    static private Lattice lattice = new ThreeToOneLattice( Arsenate.class,
                                                            Silver.class,
                                                            Silver.RADIUS + Arsenate.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        SilverArsenate.components.add( new Component( Arsenate.class, new Integer( 1 ) ) );
        SilverArsenate.components.add( new Component( Silver.class, new Integer( 3 ) ) );
    }

    public SilverArsenate() {
        super( components, lattice, Silver.class, Arsenate.class, 1E-22 );
    }
}
