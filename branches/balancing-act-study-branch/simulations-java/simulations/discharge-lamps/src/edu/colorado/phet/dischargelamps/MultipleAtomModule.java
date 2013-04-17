// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * MultipleAtomModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MultipleAtomModule extends DischargeLampModule {

    /**
     * Constructor
     *
     * @param clock
     */
    protected MultipleAtomModule( String name, IClock clock, int numAtoms, double maxAtomSpeed ) {
        super( name, clock );
        setLogoPanel( null );
        addAtoms( getTube(), numAtoms, maxAtomSpeed );
    }
}
