// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Model provides basic functionality for models.
 * It manages model elements, and notifies interested parties when
 * model elements are added or removed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Model extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList<ModelElement> _modelElements;
    private ArrayList<ModelListener> _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param clock
     */
    public Model( IClock clock ) {
        clock.addClockListener( this );
        _modelElements = new ArrayList<ModelElement>();
        _listeners = new ArrayList<ModelListener>();
    }
    
    //----------------------------------------------------------------------------
    // ClockListener implementation
    //----------------------------------------------------------------------------
    
    // When the clock ticks, call stepInTime for each model element.
    public void clockTicked( ClockEvent event ) {
        final double dt = event.getSimulationTimeChange();
        for ( ModelElement modelElement : new ArrayList<ModelElement>( _modelElements ) ) {
             modelElement.stepInTime( dt );
        }
    }
    
    //----------------------------------------------------------------------------
    // Model Object management
    //----------------------------------------------------------------------------
    
    protected void addModelElement( ModelElement modelElement ) {
        _modelElements.add( modelElement );
        fireModelElementAdded( modelElement );
    }

    protected void removeModelElement( ModelElement modelElement ) {
        _modelElements.remove( modelElement );
        fireModelElementRemoved( modelElement );
    }
    
    //----------------------------------------------------------------------------
    // ModelListener interface
    //----------------------------------------------------------------------------
    
    public interface ModelListener {
        public void modelElementAdded( ModelElement modelElement );
        public void modelElementRemoved( ModelElement modelElement );
    }
    
    public void addModelListener( ModelListener listener ) {
        _listeners.add( listener );
    }

    public void removeModelListener( ModelListener listener ) {
        _listeners.remove( listener );
    }

    private void fireModelElementAdded( ModelElement modelElement ) {
        for ( ModelListener listener : new ArrayList<ModelListener>( _listeners ) ) {
            listener.modelElementAdded( modelElement );
        }
    }
    
    private void fireModelElementRemoved( ModelElement modelElement ) {
        for ( ModelListener listener : new ArrayList<ModelListener>( _listeners ) ) {
            listener.modelElementRemoved( modelElement );
        }
    }
}
