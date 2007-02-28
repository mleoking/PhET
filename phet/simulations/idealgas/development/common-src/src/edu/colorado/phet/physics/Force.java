/*
 * Class: Force
 * Package: edu.colorado.phet.physics
 *
 * Created by: Ron LeMaster
 * Date: Oct 21, 2002
 */
package edu.colorado.phet.physics;

import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.body.PhysicalEntity;

public abstract class Force extends PhysicalEntity {

    public abstract void act( Particle p );
}
