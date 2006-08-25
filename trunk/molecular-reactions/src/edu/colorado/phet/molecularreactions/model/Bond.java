/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

/**
 * Bond
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Bond {
    private SimpleMolecule[] participants;

    public Bond( SimpleMolecule m1, SimpleMolecule m2 ) {
        participants = new SimpleMolecule[] {m1, m2} ;
    }

    public SimpleMolecule[] getParticipants() {
        return participants;
    }
}
