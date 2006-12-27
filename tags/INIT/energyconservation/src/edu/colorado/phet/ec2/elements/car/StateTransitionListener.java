package edu.colorado.phet.ec2.elements.car;

/**
 * User: Sam Reid
 * Date: Oct 6, 2003
 * Time: 7:27:33 AM
 * Copyright (c) Oct 6, 2003 by Sam Reid
 */
public interface StateTransitionListener {
//    public void transitionChanged();

    void transitionChanged( CarMode targetState );
}
