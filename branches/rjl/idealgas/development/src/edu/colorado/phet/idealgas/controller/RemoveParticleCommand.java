package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.physics.body.Body;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Mar 7, 2003
 * Time: 12:52:28 PM
 * To change this template use Options | File Templates.
 */
public class RemoveParticleCommand implements Command {

    private Particle particle;

    public RemoveParticleCommand() {
    }

    public RemoveParticleCommand( Particle particle ) {
        this.particle = particle;
    }

    public Object doIt() {

        // If no particle was specified in the constructor, then
        // get a reference to one
        if( particle == null ) {
            List bodies = PhetApplication.instance().getPhysicalSystem().getBodies();
            for( int i = 0; particle == null && i < bodies.size(); i++ ) {
                Body body = (Body)bodies.get( i );
                if( body instanceof Particle ) {
                    particle = (Particle)body;
                }
            }
        }
        particle.removeFromSystem();
        return null;
    }
}
