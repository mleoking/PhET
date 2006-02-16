/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;


/**
 * This class represents partially reflecting mirror.
 */
public class PartialMirror extends Mirror {

    private Partial partialStrategy;

    public PartialMirror( Point2D end1, Point2D end2 ) {
        super( end1, end2 );
        partialStrategy = new Partial( 1.0f );
        this.addReflectionStrategy( partialStrategy );
    }

    public double getReflectivity() {
        return partialStrategy.getReflectivity();
    }

    public void setReflectivity( double reflectivity ) {
        partialStrategy.setReflectivity( reflectivity );
        listenerProxy.reflectivityChanged( new ReflectivityChangedEvent( this ) );
    }

    public void addReflectionStrategy( ReflectionStrategy reflectionStrategy ) {
        // If the strategy being added is a reflecting strategy, remove the old one
        if( reflectionStrategy instanceof Partial ) {
            partialStrategy = (Partial)reflectionStrategy;
            for( int i = 0; i < reflectionStrategies.size(); i++ ) {
                ReflectionStrategy strategy = (ReflectionStrategy)reflectionStrategies.get( i );
                if( strategy instanceof Partial ) {
                    reflectionStrategies.remove( strategy );
                    break;
                }
            }
        }
        this.reflectionStrategies.add( reflectionStrategy );
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------

    private EventChannel eventChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)eventChannel.getListenerProxy();

    public interface Listener extends EventListener {
        void reflectivityChanged( ReflectivityChangedEvent event );
    }

    public class ReflectivityChangedEvent extends EventObject {
        public ReflectivityChangedEvent( Object source ) {
            super( source );
        }

        public double getReflectivity() {
            return ( (PartialMirror)source ).getReflectivity();
        }
    }

    public void addListener( Listener listener ) {
        eventChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        eventChannel.removeListener( listener );
    }
}
