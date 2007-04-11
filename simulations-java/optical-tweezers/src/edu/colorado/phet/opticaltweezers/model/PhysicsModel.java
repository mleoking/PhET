/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.util.ArrayList;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.view.ModelViewTransform;

/**
 * PhysicsModel is the model used in PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsModel extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _modelElements; // array of ModelElement
    
    private OTClock _clock;
    private Bead _bead;
    private Laser _laser;
    private Fluid _fluid;
    
    private ModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhysicsModel( OTClock clock ) {
        super();
        
        _clock = clock;
        _clock.addClockListener( this );
        
        _modelElements = new ArrayList();
        
        _fluid = new Fluid( PhysicsDefaults.FLUID_POSITION,
                PhysicsDefaults.FLUID_ORIENTATION,
                PhysicsDefaults.FLUID_HEIGHT,
                PhysicsDefaults.FLUID_SPEED_RANGE, 
                PhysicsDefaults.FLUID_VISCOSITY_RANGE, 
                PhysicsDefaults.FLUID_TEMPERATURE_RANGE );
        _modelElements.add( _fluid );
        
        _laser = new Laser( PhysicsDefaults.LASER_POSITION, 
                PhysicsDefaults.LASER_ORIENTATION, 
                PhysicsDefaults.LASER_DIAMETER_AT_OBJECTIVE, 
                PhysicsDefaults.LASER_DIAMETER_AT_WAIST,
                PhysicsDefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST,
                PhysicsDefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL,
                PhysicsDefaults.LASER_WAVELENGTH,
                PhysicsDefaults.LASER_VISIBLE_WAVELENGTH,
                PhysicsDefaults.LASER_POWER_RANGE );
        _modelElements.add( _laser );
        
         _bead = new Bead( PhysicsDefaults.BEAD_POSITION, 
                PhysicsDefaults.BEAD_ORIENTATION, 
                PhysicsDefaults.BEAD_DIAMETER,
                PhysicsDefaults.BEAD_DENSITY );
         _modelElements.add( _bead );

         _modelViewTransform = new ModelViewTransform( PhysicsDefaults.MODEL_TO_VIEW_SCALE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public OTClock getClock() {
        return _clock;
    }
    
    public Fluid getFluid() {
        return _fluid;
    }
    
    public Laser getLaser() {
        return _laser;
    }
    
    public Bead getBead() {
        return _bead;
    }
    
    public ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
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
       
        if ( _modelElements.size() > 0 ) {
            Object[] modelElements = _modelElements.toArray(); // copy, this operation may change the list
            for ( int i = 0; i < modelElements.length; i++ ) {
                ModelElement modelElement = (ModelElement) modelElements[i];
                modelElement.stepInTime( dt );
            }
        }
    }
}
