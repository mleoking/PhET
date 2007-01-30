/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.model;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;

/**
 * Model provides basic functionality for models.
 * It manages model elements, and notifies interested parties when
 * model elements are added or removed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Model extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _modelElements; // array of ModelElement
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param clock
     */
    public Model( IClock clock ) {
        clock.addClockListener( this );
        _modelElements = new ArrayList();
        _listenerList = new EventListenerList();
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the clock ticks, call stepInTime for each model element.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
       
        if ( _modelElements.size() > 0 ) {
            Object[] modelElements = _modelElements.toArray(); // copy, this operation may change the list
            for ( int i = 0; i < modelElements.length; i++ ) {
                ModelElement modelElement = (ModelElement) modelElements[i];
                modelElement.stepInTime( dt );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Model Object management
    //----------------------------------------------------------------------------
    
    /**
     * Gets the complete set of model elements.
     * @return array of ModelElement
     */
    public ModelElement[] getModelElements() {
        return (ModelElement[]) _modelElements.toArray( new ModelElement[_modelElements.size()] );
    }
    
    /**
     * Adds a model element and notifies ModelListeners.
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        _modelElements.add( modelElement );
        fireModelObjectAdded( new ModelEvent( this, modelElement ) );
    }

    /**
     * Removes a model element and notifies ModelListeners.
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        _modelElements.remove( modelElement );
        fireModelObjectRemoved( new ModelEvent( this, modelElement ) );
    }
    
    //----------------------------------------------------------------------------
    // ModelEvent notification
    //----------------------------------------------------------------------------
    
    /**
     * ModelListener is the interface implemented by listener who 
     * wish to be notified of changes to the model.
     */
    public static interface ModelListener extends EventListener {
        public void modelElementAdded( ModelEvent event);
        public void modelElementRemoved( ModelEvent event);
    }
    
    /**
     * ModelEvent is the event used to indicate changes to the model.
     */
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
    
    /**
     * Adds a ModelListener.
     * @param listener
     */
    public void addModelListener( ModelListener listener ) {
        _listenerList.add( ModelListener.class, listener );
    }

    /**
     * Removes a ModelListener.
     * @param listener
     */
    public void removeModelListener( ModelListener listener ) {
        _listenerList.remove( ModelListener.class, listener );
    }

    /*
     * Calls modelElementAdded for all ModelListeners.
     */
    private void fireModelObjectAdded( ModelEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ModelListener.class ) {
                ( (ModelListener)listeners[i + 1] ).modelElementAdded( event );
            }
        }
    }
    
    /*
     * Calls modelElementRemoved for all ModelListeners.
     */
    private void fireModelObjectRemoved( ModelEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ModelListener.class ) {
                ( (ModelListener)listeners[i + 1] ).modelElementRemoved( event );
            }
        }
    }
}
