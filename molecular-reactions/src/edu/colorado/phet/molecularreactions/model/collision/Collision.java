/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.collision;

import edu.colorado.phet.molecularreactions.model.Molecule;

/**
 * Collision
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface Collision {
    void collide( Molecule mA, Molecule mB, MoleculeMoleculeCollisionSpec collisionSpec );

    double getInteractionDistance();
}
