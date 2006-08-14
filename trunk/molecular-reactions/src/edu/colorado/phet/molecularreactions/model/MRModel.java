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

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.collision.Box2D;

/**
 * MRModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModel extends PublishingModel {

    public MRModel( IClock clock ) {
        super( clock );

        // Create the box
        Box2D box = new Box2D( /* specify the box */ );
        addModelElement( box );


        // Create collisions agents that will detect and handle collisions between molecules,
        // and between molecules and the box
    }


}
