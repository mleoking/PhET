// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * Bond
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Bond extends SimpleObservable implements ModelElement {
    private SimpleMolecule[] participants;

    public Bond( SimpleMolecule m1, SimpleMolecule m2 ) {
        participants = new SimpleMolecule[]{m1, m2};
    }

    public SimpleMolecule[] getParticipants() {
        return participants;
    }

    /**
     * No time-dependent behavior
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        notifyObservers();
    }

}
