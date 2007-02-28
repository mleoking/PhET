/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import java.text.DecimalFormat;

import org.jfree.data.Range;

import edu.colorado.phet.boundstates.control.ZoomControl.ZoomSpec;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.util.AxisSpec;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.boundstates.util.IntegerRange;

/**
 * BSOneWellSpec contains the information needed to set up the "One Well" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSOneWellSpec extends BSAbstractModuleSpec {

    private static final String ID = "oneWellModule";
    
    private static final BSWellType[] WELL_TYPES = { BSWellType.SQUARE, BSWellType.ASYMMETRIC, BSWellType.COULOMB_1D, BSWellType.COULOMB_3D, BSWellType.HARMONIC_OSCILLATOR };
    private static final BSWellType DEFAULT_WELL_TYPE = BSWellType.SQUARE;
    
    private static final boolean OFFSET_CONTROL_SUPPORTED = true;
    private static final boolean SUPERPOSITION_CONTROLS_SUPPORTED = true;
    private static final boolean PARTICLE_CONTROLS_SUPPORTED = true;
    private static final boolean MAGNIFYING_GLASS_SUPPORTED = true;
    private static final boolean MAGNIFYING_GLASS_SELECTED = false;
    private static final boolean AVERAGE_PROBABILITY_DENSITY_SUPPORTED = false;

    // Particle ranges (min, max, default, significantDecimalPlaces)
    private static final DoubleRange MASS_MULTIPLIER_RANGE = new DoubleRange( 0.5, 1.1, 1, 2 );
    
    // Number of wells (min, max, default, significantDecimalPlaces)
    private static final IntegerRange NUMBER_OF_WELLS_RANGE = new IntegerRange( 1, 1, 1 );
    
    // Magnification power of the magnifying glass
    private static final double MAGNIFICATION = 10;
    
    // Field constant
    private static final DoubleRange FIELD_CONSTANT_RANGE = new DoubleRange( 0, 0, 0, 0 );
    
    //----------------------------------------------------------------------------
    // Asymmetric ranges
    //----------------------------------------------------------------------------
    
    // Energy axis
    private static final Range ASYMMETRIC_ENERGY_RANGE = new Range( -5.5, +15.5 ); // eV
    private static final double ASYMMETRIC_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat ASYMMETRIC_ENERGY_TICK_FORMAT = new DecimalFormat( "#0" );
    
    // Potential attributes
    private static final DoubleRange ASYMMETRIC_OFFSET_RANGE = new DoubleRange( -5, 15, 0, 1 ); // eV
    private static final DoubleRange ASYMMETRIC_WIDTH_RANGE = new DoubleRange( 0.1, 6, 1, 1 ); // nm
    private static final DoubleRange ASYMMETRIC_HEIGHT_RANGE = new DoubleRange( 0, 20, 10, 1 ); // eV
    
    //----------------------------------------------------------------------------
    // 1D Coulomb ranges
    //----------------------------------------------------------------------------
    
    // Energy axis
    private static final Range COULOMB_1D_ENERGY_RANGE = new Range( -15.5, +5.5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT = new DecimalFormat( "#0" );
    
    // Potential attributes
    private static final DoubleRange COULOMB_1D_OFFSET_RANGE = new DoubleRange( -15, 5, 0, 1 ); // eV
    private static final DoubleRange COULOMB_1D_SPACING_RANGE = new DoubleRange( 0, 0, 0, 0 ); // nm (don't care for one well)
    
    //----------------------------------------------------------------------------
    // 3D Coulomb ranges
    //----------------------------------------------------------------------------
    
    // Energy axis
    private static final Range COULOMB_3D_ENERGY_RANGE = new Range( -15.5, +5.5 ); // eV
    private static final double COULOMB_3D_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat COULOMB_3D_ENERGY_TICK_FORMAT = new DecimalFormat( "#0" );

    // Potential attributes
    private static final DoubleRange COULOMB_3D_OFFSET_RANGE = new DoubleRange( -15, 5, 0, 1 ); // eV
    
    //----------------------------------------------------------------------------
    // Harmonic Oscillator ranges
    //----------------------------------------------------------------------------
    
    // Energy axis
    private static final Range HARMONIC_OSCILLATOR_ENERGY_RANGE = new Range( -5.5, +15.5 ); // eV
    private static final double HARMONIC_OSCILLATOR_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat HARMONIC_OSCILLATOR_ENERGY_TICK_FORMAT = new DecimalFormat( "#0" );
    
    // Potential attributes
    private static final DoubleRange HARMONIC_OSCILLATOR_OFFSET_RANGE = new DoubleRange( -5, 15, 0, 1 ); // eV
    private static final DoubleRange HARMONIC_OSCILLATOR_ANGULAR_FREQUENCY_RANGE = new DoubleRange( 1, 10, 1, 1 ); // fs^-1
  
    //----------------------------------------------------------------------------
    // Square ranges
    //----------------------------------------------------------------------------
    
    // Energy axis
    private static final Range SQUARE_ENERGY_RANGE = new Range( -5.5, +15.5 ); // eV
    private static final double SQUARE_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat SQUARE_ENERGY_TICK_FORMAT = new DecimalFormat( "#0" );
    
    // Potential attributes
    private static final DoubleRange SQUARE_OFFSET_RANGE = new DoubleRange( -5, 15, 0, 1 ); // eV
    private static final DoubleRange SQUARE_WIDTH_RANGE = new DoubleRange( 0.1, 6, 1, 1 ); // nm
    private static final DoubleRange SQUARE_HEIGHT_RANGE = new DoubleRange( 0, 20, 10, 1 ); // eV
    private static final DoubleRange SQUARE_SEPARATION_RANGE = new DoubleRange( 0, 0, 0, 0 ); // nm (don't care for one well)
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSOneWellSpec() {
        super();

        setId( ID );
        
        setWellTypes( WELL_TYPES );
        setDefaultWellType( DEFAULT_WELL_TYPE );
        setNumberOfWellsRange( NUMBER_OF_WELLS_RANGE );
        setFieldConstantRange( FIELD_CONSTANT_RANGE );
        
        setOffsetControlSupported( OFFSET_CONTROL_SUPPORTED );
        setSuperpositionControlsSupported( SUPERPOSITION_CONTROLS_SUPPORTED );
        setParticleControlsSupported( PARTICLE_CONTROLS_SUPPORTED );
        setMagnifyingGlassSupported( MAGNIFYING_GLASS_SUPPORTED );
        setMagnifyingGlassSelected( MAGNIFYING_GLASS_SELECTED );
        setAverageProbabilityDensityIsSupported( AVERAGE_PROBABILITY_DENSITY_SUPPORTED );
        
        setMassMultiplierRange( MASS_MULTIPLIER_RANGE );
        
        setMagnification( MAGNIFICATION );
        
        // Asymmetric spec
        {
            AxisSpec axisSpec = new AxisSpec( ASYMMETRIC_ENERGY_RANGE, ASYMMETRIC_ENERGY_TICK_SPACING, ASYMMETRIC_ENERGY_TICK_FORMAT );
            ZoomSpec zoomSpec = new ZoomSpec( axisSpec );
            BSPotentialSpec wellSpec = new BSPotentialSpec.Asymmetric( zoomSpec, 
                    ASYMMETRIC_OFFSET_RANGE, ASYMMETRIC_WIDTH_RANGE, ASYMMETRIC_HEIGHT_RANGE  );
            setAsymmetricSpec( wellSpec );
        }
        
        // 1D Coulomb spec
        {
            AxisSpec axisSpec = new AxisSpec( COULOMB_1D_ENERGY_RANGE, COULOMB_1D_ENERGY_TICK_SPACING, COULOMB_1D_ENERGY_TICK_FORMAT );
            ZoomSpec zoomSpec = new ZoomSpec( axisSpec );
            BSPotentialSpec wellSpec = new BSPotentialSpec.Coulomb1D( zoomSpec, COULOMB_1D_OFFSET_RANGE, COULOMB_1D_SPACING_RANGE  );
            setCoulomb1DSpec( wellSpec );
        }
        
        // 3D Coulomb spec
        {
            AxisSpec axisSpec = new AxisSpec( COULOMB_3D_ENERGY_RANGE, COULOMB_3D_ENERGY_TICK_SPACING, COULOMB_3D_ENERGY_TICK_FORMAT );
            ZoomSpec zoomSpec = new ZoomSpec( axisSpec );
            BSPotentialSpec wellSpec = new BSPotentialSpec.Coulomb3D( zoomSpec, COULOMB_3D_OFFSET_RANGE );
            setCoulomb3DSpec( wellSpec );
        }
        
        // Harmonic Oscillator spec
        {
            AxisSpec axisSpec = new AxisSpec( HARMONIC_OSCILLATOR_ENERGY_RANGE, HARMONIC_OSCILLATOR_ENERGY_TICK_SPACING, HARMONIC_OSCILLATOR_ENERGY_TICK_FORMAT );
            ZoomSpec zoomSpec = new ZoomSpec( axisSpec );
            BSPotentialSpec wellSpec = new BSPotentialSpec.HarmonicOscillator( zoomSpec, 
                    HARMONIC_OSCILLATOR_OFFSET_RANGE, HARMONIC_OSCILLATOR_ANGULAR_FREQUENCY_RANGE  );
            setHarmonicOscillatorSpec( wellSpec );
        }
        
        // Square spec
        {
            AxisSpec axisSpec = new AxisSpec( SQUARE_ENERGY_RANGE, SQUARE_ENERGY_TICK_SPACING, SQUARE_ENERGY_TICK_FORMAT );
            ZoomSpec zoomSpec = new ZoomSpec( axisSpec );
            BSPotentialSpec wellSpec = new BSPotentialSpec.Square( zoomSpec, 
                    SQUARE_OFFSET_RANGE, SQUARE_WIDTH_RANGE, SQUARE_HEIGHT_RANGE, SQUARE_SEPARATION_RANGE );
            setSquareSpec( wellSpec );
        }
    }
}
