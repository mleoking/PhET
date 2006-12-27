package edu.colorado.phet.bernoulli.pipe;

import edu.colorado.phet.common.model.ModelElement;

/**
 * Represents an entity a certain distance along the pipe.
 */
public class PipeParticle extends ModelElement {
    double x;
    Pipe pipe;

    public PipeParticle( Pipe pipe ) {
        this.pipe = pipe;
    }

    public void stepInTime( double dt ) {
//        double speed = pipe.speedAt(scalarDist);
        x += .05;
        updateObservers();
    }

    public Pipe getPipe() {
        return pipe;
    }

    public double getPosition() {
        return x;
    }

    public void setPosition( double x ) {
        this.x = x;
        updateObservers();
    }

}
