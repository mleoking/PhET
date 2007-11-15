/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.defaults.ExampleDefaults;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.model.GlaciersClock;

/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersModel extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final ArrayList _modelElements; // array of ModelElement
    private final ExampleModelElement _exampleModelElement;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlaciersModel( GlaciersClock clock ) {
        super();
        
        _clock = clock;
        _clock.addClockListener( this );
        
        _modelElements = new ArrayList();
        
        _exampleModelElement = new ExampleModelElement( 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_SIZE,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION, 
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        addModelElement( _exampleModelElement  );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GlaciersClock getClock() {
        return _clock;
    }
    
    protected void addModelElement( ModelElement element ) {
        _modelElements.add( element );
    }
    
    public ExampleModelElement getExampleModelElement() {
        return _exampleModelElement;
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
