// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module.physics;

import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.OTAbstractModel;

/**
 * PhysicsModel is the model used in PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsModel extends OTAbstractModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Fluid _fluid;
    private final MicroscopeSlide _microscopeSlide;
    private final Laser _laser;
    private final Bead _bead;
    
    private final OTModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PhysicsModel( OTClock clock ) {
        super( clock );
        
        _fluid = new Fluid( PhysicsDefaults.FLUID_SPEED_RANGE,
                PhysicsDefaults.FLUID_DIRECTION,
                PhysicsDefaults.FLUID_VISCOSITY_RANGE, 
                PhysicsDefaults.FLUID_TEMPERATURE_RANGE,
                PhysicsDefaults.FLUID_APT_CONCENTRATION_RANGE );
        addModelElement( _fluid );
        
        _microscopeSlide = new MicroscopeSlide( PhysicsDefaults.MICROSCOPE_SLIDE_POSITION,
                PhysicsDefaults.MICROSCOPE_SLIDE_ORIENTATION,
                PhysicsDefaults.MICROSCOPE_SLIDE_CENTER_HEIGHT,
                PhysicsDefaults.MICROSCOPE_SLIDE_EDGE_HEIGHT );
        addModelElement( _microscopeSlide );
        
        _laser = new Laser( PhysicsDefaults.LASER_POSITION, 
                PhysicsDefaults.LASER_ORIENTATION, 
                PhysicsDefaults.LASER_DIAMETER_AT_OBJECTIVE, 
                PhysicsDefaults.LASER_DIAMETER_AT_WAIST,
                PhysicsDefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST,
                PhysicsDefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL,
                PhysicsDefaults.LASER_WAVELENGTH,
                PhysicsDefaults.LASER_VISIBLE_WAVELENGTH,
                PhysicsDefaults.LASER_POWER_RANGE,
                PhysicsDefaults.LASER_TRAP_FORCE_RATIO,
                PhysicsDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE,
                clock );
        addModelElement( _laser );
        
         _bead = new Bead( PhysicsDefaults.BEAD_POSITION, 
                 PhysicsDefaults.BEAD_ORIENTATION, 
                 PhysicsDefaults.BEAD_DIAMETER,
                 PhysicsDefaults.BEAD_DENSITY,
                 _fluid,
                 _microscopeSlide,
                 _laser,
                 PhysicsDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE,
                 PhysicsDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE,
                 PhysicsDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                 PhysicsDefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE,
                 PhysicsDefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                 PhysicsDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE,
                 PhysicsDefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE,
                 PhysicsDefaults.BEAD_VACUUM_FAST_DT_RANGE,
                 PhysicsDefaults.BEAD_VACUUM_FAST_POWER_RANGE );
         addModelElement( _bead );

         _modelViewTransform = new OTModelViewTransform( PhysicsDefaults.MODEL_TO_VIEW_SCALE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public Fluid getFluid() {
        return _fluid;
    }
   
    public MicroscopeSlide getMicroscopeSlide() {
        return _microscopeSlide;
    }
    
    public Laser getLaser() {
        return _laser;
    }
    
    public Bead getBead() {
        return _bead;
    }
    
    public OTModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
}
