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

import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.mechanics.Body;

/**
 * MRCollisionExpert
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface MRCollisionAgent {
    boolean detectAndDoCollision( MRModel model, Body bodyA, Body bodyB );
}
