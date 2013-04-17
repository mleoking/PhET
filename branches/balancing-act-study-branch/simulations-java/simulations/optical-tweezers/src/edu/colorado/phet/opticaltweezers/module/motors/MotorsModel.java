// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.module.motors;

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.defaults.MotorsDefaults;
import edu.colorado.phet.opticaltweezers.model.*;
import edu.colorado.phet.opticaltweezers.module.OTAbstractModel;

/**
 * MotorsModel is the model for MotorsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsModel extends OTAbstractModel implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Fluid _fluid;
    private final MicroscopeSlide _microscopeSlide;
    private final Laser _laser;
    private final Bead _bead;
    private final Bead _invisibleBead;
    private final DNAStrand _dnaStrandBead; // DNA strand attached to visible bead
    private final DNAStrand _dnaStrandFree; // DNA strand with free end
    private final EnzymeA _enzymeA;
    private final EnzymeB _enzymeB;
    private final LaserPositionController _laserPositionController;
    
    private OTModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MotorsModel( OTClock clock ) {
        super( clock );
        
        _fluid = new Fluid( MotorsDefaults.FLUID_SPEED_RANGE,
                MotorsDefaults.FLUID_DIRECTION,
                MotorsDefaults.FLUID_VISCOSITY_RANGE, 
                MotorsDefaults.FLUID_TEMPERATURE_RANGE,
                MotorsDefaults.FLUID_APT_CONCENTRATION_RANGE);
        addModelElement( _fluid );
        
        _microscopeSlide = new MicroscopeSlide( MotorsDefaults.MICROSCOPE_SLIDE_POSITION,
                MotorsDefaults.MICROSCOPE_SLIDE_ORIENTATION,
                MotorsDefaults.MICROSCOPE_SLIDE_CENTER_HEIGHT,
                MotorsDefaults.MICROSCOPE_SLIDE_EDGE_HEIGHT );
        addModelElement( _microscopeSlide );
        
        _laser = new Laser( MotorsDefaults.LASER_POSITION, 
                MotorsDefaults.LASER_ORIENTATION, 
                MotorsDefaults.LASER_DIAMETER_AT_OBJECTIVE, 
                MotorsDefaults.LASER_DIAMETER_AT_WAIST,
                MotorsDefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST,
                MotorsDefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL,
                MotorsDefaults.LASER_WAVELENGTH,
                MotorsDefaults.LASER_VISIBLE_WAVELENGTH,
                MotorsDefaults.LASER_POWER_RANGE,
                MotorsDefaults.LASER_TRAP_FORCE_RATIO,
                MotorsDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE,
                clock );
        addModelElement( _laser );
        
        _bead = new Bead( MotorsDefaults.BEAD_POSITION, 
                MotorsDefaults.BEAD_ORIENTATION, 
                MotorsDefaults.BEAD_DIAMETER,
                MotorsDefaults.BEAD_DENSITY,
                _fluid,
                _microscopeSlide,
                _laser,
                MotorsDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE,
                MotorsDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE,
                MotorsDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                MotorsDefaults.BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE,
                MotorsDefaults.BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                MotorsDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE,
                MotorsDefaults.BEAD_VACUUM_FAST_THRESHOLD_RANGE,
                MotorsDefaults.BEAD_VACUUM_FAST_DT_RANGE,
                MotorsDefaults.BEAD_VACUUM_FAST_POWER_RANGE );
         addModelElement( _bead );
         
         _dnaStrandBead = new DNAStrand( MotorsDefaults.DNA_POSITION,
                 MotorsDefaults.DNA_BEAD_CONTOUR_LENGTH, 
                 MotorsDefaults.DNA_PERSISTENCE_LENGTH, 
                 MotorsDefaults.DNA_SPRING_LENGTH, 
                 MotorsDefaults.DNA_STRETCHINESS,
                 _bead,
                 _fluid,
                 clock,
                 MotorsDefaults.DNA_REFERENCE_CLOCK_STEP,
                 MotorsDefaults.DNA_SPRING_CONSTANT_RANGE, 
                 MotorsDefaults.DNA_DRAG_COEFFICIENT_RANGE, 
                 MotorsDefaults.DNA_KICK_CONSTANT_RANGE, 
                 MotorsDefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE,
                 MotorsDefaults.DNA_EVOLUTION_DT_RANGE,
                 MotorsDefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE );
         addModelElement( _dnaStrandBead );
         _bead.attachTo( _dnaStrandBead ); // attach bead to DNA strand
         
         _invisibleBead = new Bead( MotorsDefaults.INVISIBLE_BEAD_POSITION, 
                 MotorsDefaults.INVISIBLE_BEAD_ORIENTATION, 
                 MotorsDefaults.INVISIBLE_BEAD_DIAMETER,
                 MotorsDefaults.INVISIBLE_BEAD_DENSITY,
                 _fluid,
                 _microscopeSlide,
                 null, /* no laser influence */
                 MotorsDefaults.INVISIBLE_BEAD_BROWNIAN_MOTION_SCALE_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_DT_SUBDIVISION_THRESHOLD_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_VERLET_DT_SUBDIVISION_THRESHOLD_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_VERLET_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_VERLET_ACCELERATION_SCALE_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_VACUUM_FAST_THRESHOLD_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_VACUUM_FAST_DT_RANGE,
                 MotorsDefaults.INVISIBLE_BEAD_VACUUM_FAST_POWER_RANGE );
          addModelElement( _invisibleBead );
         
          _dnaStrandFree = new DNAStrand( MotorsDefaults.DNA_POSITION,
                  MotorsDefaults.DNA_FREE_CONTOUR_LENGTH, 
                  MotorsDefaults.DNA_PERSISTENCE_LENGTH, 
                  MotorsDefaults.DNA_SPRING_LENGTH, 
                  MotorsDefaults.DNA_STRETCHINESS,
                  _invisibleBead,
                  _fluid,
                  clock,
                  MotorsDefaults.DNA_REFERENCE_CLOCK_STEP,
                  MotorsDefaults.DNA_SPRING_CONSTANT_RANGE, 
                  MotorsDefaults.DNA_DRAG_COEFFICIENT_RANGE, 
                  MotorsDefaults.DNA_KICK_CONSTANT_RANGE, 
                  MotorsDefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE,
                  MotorsDefaults.DNA_EVOLUTION_DT_RANGE,
                  MotorsDefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE );
          addModelElement( _dnaStrandFree );
          _invisibleBead.attachTo( _dnaStrandFree ); // attach bead to DNA strand
          
         _enzymeA = new EnzymeA( MotorsDefaults.ENZYME_POSITION, 
                 MotorsDefaults.ENZYME_A_OUTER_DIAMETER, 
                 MotorsDefaults.ENZYME_A_INNER_DIAMETER,
                 _dnaStrandBead,
                 _dnaStrandFree,
                 _fluid,
                 clock.getFastRange().getMax() );
         _enzymeA.setEnabled( true );
         _enzymeA.addObserver( this );
         addModelElement( _enzymeA );
         
         _enzymeB = new EnzymeB( MotorsDefaults.ENZYME_POSITION, 
                 MotorsDefaults.ENZYME_B_OUTER_DIAMETER, 
                 MotorsDefaults.ENZYME_B_INNER_DIAMETER,
                 _dnaStrandBead,
                 _dnaStrandFree,
                 _fluid,
                 clock.getFastRange().getMax() );
         _enzymeB.setEnabled( !_enzymeA.isEnabled() );
         _enzymeB.addObserver( this );
         addModelElement( _enzymeB );

         _laserPositionController = new LaserPositionController( _laser, _dnaStrandBead );
         addModelElement( _laserPositionController );
         
         _modelViewTransform = new OTModelViewTransform( MotorsDefaults.MODEL_TO_VIEW_SCALE );
         
         resetDNA();
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
    
    public Bead getInvisibleBead() {
        return _invisibleBead;
    }
    
    public DNAStrand getDNAStrandBead() {
        return _dnaStrandBead;
    }
    
    public DNAStrand getDNAStrandFree() {
        return _dnaStrandFree;
    }
    
    public EnzymeA getEnzymeA() {
        return _enzymeA;
    }
    
    public EnzymeB getEnzymeB() {
        return _enzymeB;
    }
    
    public LaserPositionController getLaserPositionController() {
        return _laserPositionController;
    }
    
    public OTModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
    
    //----------------------------------------------------------------------------
    //
    //----------------------------------------------------------------------------
    
    public void resetDNA() {
        _dnaStrandBead.setContourLength( MotorsDefaults.DNA_BEAD_CONTOUR_LENGTH );
        _dnaStrandFree.setContourLength( MotorsDefaults.DNA_FREE_CONTOUR_LENGTH );
        _bead.setPosition( MotorsDefaults.BEAD_POSITION );
        _invisibleBead.setPosition( MotorsDefaults.INVISIBLE_BEAD_POSITION );
        if ( _enzymeA.isEnabled() ) {
            _dnaStrandBead.attachEnzyme( _enzymeA );
        }
        else if ( _enzymeB.isEnabled() ) {
            _dnaStrandBead.attachEnzyme( _enzymeB );
        }
        else {
            _dnaStrandBead.attachEnzyme( null );
        }
    }

    public void update( Observable o, Object arg ) {
        if ( o == _enzymeA || o == _enzymeB ) {
            if ( arg == AbstractEnzyme.PROPERTY_ENABLED ) {
                resetDNA();
            }
        }
    }
}
