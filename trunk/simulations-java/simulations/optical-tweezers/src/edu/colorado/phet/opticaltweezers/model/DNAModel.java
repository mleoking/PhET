/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;

/**
 * DNAModel is the model for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAModel extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _modelElements; // array of ModelElement
    
    private final OTClock _clock;
    private final Bead _bead;
    private final Laser _laser;
    private final Fluid _fluid;
    private final DNAStrand _dnaStrand;
    
    private ModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAModel( OTClock clock ) {
        super();
        
        _clock = clock;
        _clock.addClockListener( this );
        
        _modelElements = new ArrayList();
        
        _fluid = new Fluid( DNADefaults.FLUID_POSITION,
                DNADefaults.FLUID_ORIENTATION,
                DNADefaults.FLUID_HEIGHT,
                DNADefaults.FLUID_SPEED_RANGE, 
                DNADefaults.FLUID_VISCOSITY_RANGE, 
                DNADefaults.FLUID_TEMPERATURE_RANGE );
        _modelElements.add( _fluid );
        
        _laser = new Laser( DNADefaults.LASER_POSITION, 
                DNADefaults.LASER_ORIENTATION, 
                DNADefaults.LASER_DIAMETER_AT_OBJECTIVE, 
                DNADefaults.LASER_DIAMETER_AT_WAIST,
                DNADefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST,
                DNADefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL,
                DNADefaults.LASER_WAVELENGTH,
                DNADefaults.LASER_VISIBLE_WAVELENGTH,
                DNADefaults.LASER_POWER_RANGE );
        _modelElements.add( _laser );
        
        _bead = new Bead( DNADefaults.BEAD_POSITION, 
                DNADefaults.BEAD_ORIENTATION, 
                DNADefaults.BEAD_DIAMETER,
                DNADefaults.BEAD_DENSITY,
                DNADefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE,
                DNADefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                DNADefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE,
                _fluid,
                _laser );
         _modelElements.add( _bead );
         
         _dnaStrand = new DNAStrand( DNADefaults.DNA_CONTOUR_LENGTH, 
                 DNADefaults.DNA_PERSISTENCE_LENGTH, 
                 DNADefaults.DNA_NUMBER_OF_SPRINGS, 
                 DNADefaults.DNA_SPRING_CONSTANT_RANGE, 
                 DNADefaults.DNA_DRAG_COEFFICIENT_RANGE, 
                 DNADefaults.DNA_KICK_CONSTANT_RANGE, 
                 DNADefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE,
                 DNADefaults.DNA_EVOLUTION_DT_RANGE,
                 _clock,
                 _bead,
                 _fluid );
         _modelElements.add( _dnaStrand );
         _bead.attachTo( _dnaStrand ); // attach bead to DNA strand

         _modelViewTransform = new ModelViewTransform( DNADefaults.MODEL_TO_VIEW_SCALE );
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
    
    public DNAStrand getDNAStrand() {
        return _dnaStrand;
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
        Iterator i = _modelElements.iterator();
        while ( i.hasNext() ) {
            ModelElement modelElement = (ModelElement) i.next();
            modelElement.stepInTime( dt );
        }
    }
}
