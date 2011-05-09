// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module.dna;

import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.OTAbstractModel;

/**
 * DNAModel is the model for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAModel extends OTAbstractModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Fluid _fluid;
    private final MicroscopeSlide _microscopeSlide;
    private final Laser _laser;
    private final Bead _bead;
    private final DNAStrand _dnaStrand;
    
    private OTModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAModel( OTClock clock ) {
        super( clock );
        
        _fluid = new Fluid( DNADefaults.FLUID_SPEED_RANGE,
                DNADefaults.FLUID_DIRECTION,
                DNADefaults.FLUID_VISCOSITY_RANGE, 
                DNADefaults.FLUID_TEMPERATURE_RANGE,
                DNADefaults.FLUID_APT_CONCENTRATION_RANGE );
        addModelElement( _fluid );
        
        _microscopeSlide = new MicroscopeSlide( DNADefaults.MICROSCOPE_SLIDE_POSITION,
                DNADefaults.MICROSCOPE_SLIDE_ORIENTATION,
                DNADefaults.MICROSCOPE_SLIDE_CENTER_HEIGHT,
                DNADefaults.MICROSCOPE_SLIDE_EDGE_HEIGHT );
        addModelElement( _microscopeSlide );
        
        _laser = new Laser( DNADefaults.LASER_POSITION, 
                DNADefaults.LASER_ORIENTATION, 
                DNADefaults.LASER_DIAMETER_AT_OBJECTIVE, 
                DNADefaults.LASER_DIAMETER_AT_WAIST,
                DNADefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST,
                DNADefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL,
                DNADefaults.LASER_WAVELENGTH,
                DNADefaults.LASER_VISIBLE_WAVELENGTH,
                DNADefaults.LASER_POWER_RANGE,
                DNADefaults.LASER_TRAP_FORCE_RATIO,
                DNADefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE,
                clock );
        addModelElement( _laser );
        
        _bead = new Bead( DNADefaults.BEAD_POSITION, 
                DNADefaults.BEAD_ORIENTATION, 
                DNADefaults.BEAD_DIAMETER,
                DNADefaults.BEAD_DENSITY,
                _fluid,
                _microscopeSlide,
                _laser,
                DNADefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE,
                DNADefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE,
                DNADefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                DNADefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE,
                DNADefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                DNADefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE,
                DNADefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE,
                DNADefaults.BEAD_VACUUM_FAST_DT_RANGE,
                DNADefaults.BEAD_VACUUM_FAST_POWER_RANGE );
         addModelElement( _bead );
         
         _dnaStrand = new DNAStrand( DNADefaults.DNA_POSITION,
                 DNADefaults.DNA_CONTOUR_LENGTH, 
                 DNADefaults.DNA_PERSISTENCE_LENGTH, 
                 DNADefaults.DNA_SPRING_LENGTH,
                 DNADefaults.DNA_STRETCHINESS,
                 _bead,
                 _fluid,
                 clock,
                 DNADefaults.DNA_REFERENCE_CLOCK_STEP,
                 DNADefaults.DNA_SPRING_CONSTANT_RANGE, 
                 DNADefaults.DNA_DRAG_COEFFICIENT_RANGE, 
                 DNADefaults.DNA_KICK_CONSTANT_RANGE, 
                 DNADefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE,
                 DNADefaults.DNA_EVOLUTION_DT_RANGE,
                 DNADefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE );
         addModelElement( _dnaStrand );
         _bead.attachTo( _dnaStrand ); // attach bead to DNA strand

         _modelViewTransform = new OTModelViewTransform( DNADefaults.MODEL_TO_VIEW_SCALE );
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
    
    public DNAStrand getDNAStrand() {
        return _dnaStrand;
    }
    
    public OTModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
}
