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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;


public class Model extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _modelElements; // array of ModelElement
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Model( IClock clock ) {
        clock.addClockListener( this );
        _modelElements = new ArrayList();
        _listenerList = new EventListenerList();
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    public void clockTicked( ClockEvent clockEvent ) {
        double dt = clockEvent.getSimulationTimeChange();
       
        // Iterator through a copy of the list, in case the list changes.
        ArrayList modelElements = new ArrayList( _modelElements );
        Iterator i = modelElements.iterator();
        while ( i.hasNext() ) {
            ModelElement modelElement = (ModelElement)i.next();
            modelElement.stepInTime( dt );
        }
    }
    
    //----------------------------------------------------------------------------
    // Model Object management
    //----------------------------------------------------------------------------
    
    public ArrayList getModelElements() {
        return _modelElements;
    }
    
    public void addModelElement( ModelElement modelElement ) {
        _modelElements.add( modelElement );
        fireModelObjectAdded( new ModelEvent( this, modelElement ) );
    }

    public void removeModelElement( ModelElement modelElement ) {
        _modelElements.remove( modelElement );
        fireModelObjectRemoved( new ModelEvent( this, modelElement ) );
    }
    
    //----------------------------------------------------------------------------
    // ModelEvent notification
    //----------------------------------------------------------------------------
    
    public static interface ModelListener extends EventListener {
        public void modelElementAdded( ModelEvent event);
        public void modelElementRemoved( ModelEvent event);
    }

    public static class ModelListenerAdapter implements ModelListener {
        public void modelElementAdded( ModelEvent event ) {}
        public void modelElementRemoved( ModelEvent event ) {}
    }
    
    public class ModelEvent extends EventObject {

        private ModelElement _modelElement;

        public ModelEvent( Object source, ModelElement modelElement ) {
            super( source );
            _modelElement = modelElement;
        }

        public ModelElement getModelElement() {
            return _modelElement;
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
                ( (ModelListener)listeners[i + 1] ).modelElementAdded( event );
            }
        }
    }
    
    private void fireModelObjectRemoved( ModelEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ModelListener.class ) {
                ( (ModelListener)listeners[i + 1] ).modelElementRemoved( event );
            }
        }
    }
}
