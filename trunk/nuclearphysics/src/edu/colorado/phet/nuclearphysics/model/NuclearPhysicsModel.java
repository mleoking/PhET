/**
 * Class: NuclearPhysicsModel
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Oct 14, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;

public class NuclearPhysicsModel extends BaseModel {

    public void removeModelElement( ModelElement m ) {
        super.removeModelElement( m );
        if( m instanceof NuclearModelElement ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)m;
            nuclearModelElement.leaveSystem();
        }
    }
}
