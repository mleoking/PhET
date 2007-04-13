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

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;

import java.util.EventListener;

/**
 * ReleasingReactionSpring
 * <p>
 * A ReactionSpring that starts out completely compressed and releases the bodies attached to it
 * when the spring reaches its resting length.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ReleasingReactionSpring extends ReactionSpring {

    public ReleasingReactionSpring( double pe, double dl, double restingLength, SimpleMolecule[] bodies) {
        super( pe, dl, restingLength, bodies, true );
    }

    public void stepInTime( double dt ) {
        System.out.println( "getLength() = " + getLength() );
        System.out.println( "getRestingLength()instanceof = " + getRestingLength());
        if( getLength() < getRestingLength() ) {
            super.stepInTime( dt );
            System.out.println( "ReleasingReactionSpring.stepInTime:    push" );
        }
        else {
            changeListenerProxy.bodiesReleased( this );
            System.out.println( "ReleasingReactionSpring.stepInTime:    release" );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public static interface ChangeListener extends EventListener {
        void bodiesReleased( ReleasingReactionSpring spring );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
