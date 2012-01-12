// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.opticaltweezers.model.OTClock;

/**
 * OTAbstractModel is the base class for all models.
 * Each module has one model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTAbstractModel extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final OTClock _clock;
    private final ArrayList _modelElements; // array of ModelElement
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public OTAbstractModel( OTClock clock ) {
        super();
        
        _clock = clock;
        _clock.addClockListener( this );
        
        _modelElements = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public OTClock getClock() {
        return _clock;
    }
    
    protected void addModelElement( ModelElement element ) {
        _modelElements.add( element );
    }
    
    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    /**
     * When the clock ticks, call stepInTime for each model element.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        Iterator i = _modelElements.iterator();
        while ( i.hasNext() ) {
            ModelElement modelElement = (ModelElement) i.next();
            modelElement.stepInTime( dt );
        }
    }
}
