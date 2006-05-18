/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * SampleTarget
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleTarget extends Point2D.Double {

    //--------------------------------------------------------------------------------------------------
    // Class fields, methods, and inner classes
    //--------------------------------------------------------------------------------------------------

    public static class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public SampleTarget getSampleTarget() {
            return (SampleTarget)getSource();
        }
    }

    public static interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    Point2D location = new Double();

    public void setLocation( double x, double y ) {
        super.setLocation( x, y );
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public void setLocation( Point2D p ) {
        super.setLocation( p );
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public Point2D getLocation() {
        location.setLocation( getX(), getY() );
        return location;
    }

}
