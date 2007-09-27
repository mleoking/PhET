/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.GlaciersClock;

/**
 * GlaciersAbstractModel is the base class for all models.
 * Each module has one model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GlaciersAbstractModel extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final ArrayList _modelElements; // array of ModelElement
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlaciersAbstractModel( GlaciersClock clock ) {
        super();
        
        _clock = clock;
        _clock.addClockListener( this );
        
        _modelElements = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public GlaciersClock getClock() {
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
