/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;


public class Model {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock;
    private ArrayList _modelObjects; // array of IModelObject
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Model( IClock clock ) {
        _clock = clock;
        _modelObjects = new ArrayList();
        _listenerList = new EventListenerList();
    }
    
    //----------------------------------------------------------------------------
    // Model Object management
    //----------------------------------------------------------------------------
    
    public ArrayList getModelObjects() {
        return _modelObjects;
    }
    
    public void addModelObject( IModelObject modelObject ) {
        _modelObjects.add( modelObject );
        if ( modelObject instanceof ClockListener ) {
            _clock.addClockListener( (ClockListener) modelObject );
        }
        fireModelObjectAdded( new ModelEvent( this, modelObject ) );
    }

    public void removeModelObject( IModelObject modelObject ) {
        _modelObjects.remove( modelObject );
        if ( modelObject instanceof ClockListener ) {
            _clock.removeClockListener( (ClockListener) modelObject );
        }
        fireModelObjectRemoved( new ModelEvent( this, modelObject ) );
    }
    
    //----------------------------------------------------------------------------
    // ModelEvent notification
    //----------------------------------------------------------------------------
    
    public static interface ModelListener extends EventListener {
        public void modelObjectAdded( ModelEvent event);
        public void modelObjectRemoved( ModelEvent event);
    }

    public static class ModelListenerAdapter implements ModelListener {
        public void modelObjectAdded( ModelEvent event ) {}
        public void modelObjectRemoved( ModelEvent event ) {}
    }
    
    public class ModelEvent extends EventObject {

        private IModelObject _modelObject;

        public ModelEvent( Object source, IModelObject modelObject ) {
            super( source );
            _modelObject = modelObject;
        }

        public IModelObject getModelObject() {
            return _modelObject;
        }
    }
    
    public void addModelListener( ModelListener listener ) {
        _listenerList.add( ModelListener.class, listener );
    }

    public void removeModelListener( ModelListener listener ) {
        _listenerList.remove( ModelListener.class, listener );
    }

    private void fireModelObjectAdded( ModelEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ModelListener.class ) {
                ( (ModelListener)listeners[i + 1] ).modelObjectAdded( event );
            }
        }
    }
    
    private void fireModelObjectRemoved( ModelEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ModelListener.class ) {
                ( (ModelListener)listeners[i + 1] ).modelObjectRemoved( event );
            }
        }
    }
}
