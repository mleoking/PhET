package edu.colorado.phet.physics.observation;

import edu.colorado.phet.physics.body.Particle;

/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 11, 2003
 * Time: 2:52:58 PM
 * To change this template use Options | File Templates.
 */
public interface ParticleObserver {

    public void update( Particle particle, Object arg );
    public void leavingSystem( Particle particle );
}
