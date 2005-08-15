/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: 
 * Branch : $Name:  
 * Modified by : $Author: 
 * Revision : $Revision: 
 * Date modified : $Date: 
 */

package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

/**
 * An electrode is a line between two endpoints. Its location is considered to be the
 * midpoint between the two endpoints
 */
public abstract class Electrode extends Particle {
    private double potential;

    // Physical dimensions
    private Point2D[] endpoints = new Point2D[2];

    protected Electrode( Point2D p1, Point2D p2 ) {
        setEndpoints( new Point2D[] {p1, p2 } );
    }

    public double getPotential() {
        return potential;
    }

    public void setPotential( double potential ) {
        this.potential = potential;
        stateChangeListenerProxy.stateChanged( new StateChangeEvent( this ) );
    }

    protected void setEndpoints( Point2D[] points) {
        endpoints = points;
        setPosition( (endpoints[0].getX() + endpoints[1].getX() )/ 2, (endpoints[0].getY() + endpoints[1].getY() )/ 2 );
    }
    
    public Point2D[] getEndpoints() {
        return endpoints;
    }

    //-----------------------------------------------------------------
    // Event and listener definitions
    //-----------------------------------------------------------------
    private EventChannel stateChangeEventChannel = new EventChannel( StateChangeListener.class );
    private StateChangeListener stateChangeListenerProxy = (StateChangeListener)stateChangeEventChannel.getListenerProxy();

    public void addStateChangeListener( StateChangeListener listener ) {
        stateChangeEventChannel.addListener( listener );
    }

    public void removeStateChangeListener( StateChangeListener listener ) {
        stateChangeEventChannel.removeListener( listener );
    }

    public class StateChangeEvent extends EventObject {
        public StateChangeEvent( Object source ) {
            super( source );
        }

        public Electrode getElectrode() {
            return (Electrode)getSource();
        }
    }

    public interface StateChangeListener extends EventListener {
        void stateChanged( StateChangeEvent event );
    }
}
