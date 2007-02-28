/**
 * Class: CompositeTimeStep
 * Package: edu.colorado.phet.physics
 * Author: Another Guy
 * Date: Apr 22, 2003
 */
package edu.colorado.phet.physics;

import java.util.LinkedList;

public class CompositeTimeStep implements TimeStep {

    private LinkedList componentTimeSteps = new LinkedList();

    /**
     *
     * @param timeStep
     */
    public void add( TimeStep timeStep ) {
        componentTimeSteps.add( timeStep );
    }

    /**
     *
     * @param index
     * @param timeStep
     */
    public void add( int index, TimeStep timeStep ) {
        componentTimeSteps.add( index, timeStep );
    }

    /**
     *
     * @param timeStep
     */
    public void remove( TimeStep timeStep ) {
        componentTimeSteps.remove( timeStep );
    }

    /**
     *
     * @param timeStep
     */
    public void addFirst( TimeStep timeStep ) {
        componentTimeSteps.addFirst( timeStep );
    }

    /**
     *
     * @param timeStep
     */
    public void addLast( TimeStep timeStep ) {
        componentTimeSteps.addLast( timeStep );
    }

    public int indexOf( TimeStep timeStep ) {
        return componentTimeSteps.indexOf( timeStep );
    }

    /**
     *
     * @param dt
     */
    public void stepInTime( float dt ) {
        for( int i = 0; i < componentTimeSteps.size(); i++ ) {
            TimeStep timeStep = (TimeStep)componentTimeSteps.get( i );
            timeStep.stepInTime( dt );
        }
    }
}
