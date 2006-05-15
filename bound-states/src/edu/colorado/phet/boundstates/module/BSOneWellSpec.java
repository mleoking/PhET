/* Copyright 2005, University of Colorado */

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

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.boundstates.util.IntegerRange;

/**
 * BSOneWellSpec contains the information needed to set up the "One Well" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSOneWellSpec extends BSAbstractModuleSpec {

    private static final BSWellType[] WELL_TYPES = { BSWellType.SQUARE, BSWellType.ASYMMETRIC, BSWellType.COULOMB_1D, BSWellType.COULOMB_3D, BSWellType.HARMONIC_OSCILLATOR };
    private static final BSWellType DEFAULT_WELL_TYPE = BSWellType.SQUARE;
    
    private static final boolean SUPERPOSITION_CONTROLS_SUPPORTED = true;
    private static final boolean PARTICLE_CONTROLS_SUPPORTED = true;
    private static final boolean MAGNIFYING_GLASS_SUPPORTED = true;
    private static final boolean MAGNIFYING_GLASS_SELECTED = false;

    // Particle ranges (min, max, default, significantDecimalPlaces)
    private static final DoubleRange MASS_MULTIPLIER_RANGE = new DoubleRange( 0.5, 1.1, 1, 1 );
    
    // Number of wells (min, max, default, significantDecimalPlaces)
    private static final IntegerRange NUMBER_OF_WELLS_RANGE = new IntegerRange( 1, 1, 1 );
    
    // Asymmetric ranges (min, max, default, significantDecimalPlaces)
    private static final Range ASYMMETRIC_ENERGY_RANGE = new Range( -5, +15 ); // eV
    private static final double ASYMMETRIC_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat ASYMMETRIC_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    private static final BSAxisSpec ASYMMETRIC_ENERGY_AXIS_SPEC = 
        new BSAxisSpec( ASYMMETRIC_ENERGY_RANGE, ASYMMETRIC_ENERGY_TICK_SPACING, ASYMMETRIC_ENERGY_TICK_FORMAT );
    private static final DoubleRange ASYMMETRIC_OFFSET_RANGE = new DoubleRange( -5, 15, 0, 1 ); // eV
    private static final DoubleRange ASYMMETRIC_WIDTH_RANGE = new DoubleRange( 0.1, 8, 1, 1 ); // nm
    private static final DoubleRange ASYMMETRIC_HEIGHT_RANGE = new DoubleRange( 0, 20, 10, 1 ); // eV
    private static final BSWellRangeSpec ASYMMETRIC_RANGE_SPEC = 
            new BSWellRangeSpec.Asymmetric( ASYMMETRIC_ENERGY_AXIS_SPEC, ASYMMETRIC_OFFSET_RANGE, ASYMMETRIC_WIDTH_RANGE, ASYMMETRIC_HEIGHT_RANGE );
    
    // 1D Coulomb ranges (min, max, default, significantDecimalPlaces)
    private static final Range COULOMB_1D_ENERGY_RANGE = new Range( -15, +5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    private static final BSAxisSpec COULOMB_1D_ENERGY_AXIS_SPEC = 
        new BSAxisSpec( COULOMB_1D_ENERGY_RANGE, COULOMB_1D_ENERGY_TICK_SPACING, COULOMB_1D_ENERGY_TICK_FORMAT );
    private static final DoubleRange COULOMB_1D_OFFSET_RANGE = new DoubleRange( -15, 5, 0, 1 ); // eV
    private static final DoubleRange COULOMB_1D_SPACING_RANGE = new DoubleRange( 0, 0, 0, 0 ); // nm (don't care for one well)
    private static final BSWellRangeSpec COULOMB_1D_RANGE_SPEC = 
        new BSWellRangeSpec.Coulomb1D( COULOMB_1D_ENERGY_AXIS_SPEC, COULOMB_1D_OFFSET_RANGE, COULOMB_1D_SPACING_RANGE );
    
    // 3D Coulomb ranges (min, max, default, significantDecimalPlaces)
    private static final Range COULOMB_3D_ENERGY_RANGE = new Range( -15, +5 ); // eV
    private static final double COULOMB_3D_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat COULOMB_3D_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    private static final BSAxisSpec COULOMB_3D_ENERGY_AXIS_SPEC = 
        new BSAxisSpec( COULOMB_3D_ENERGY_RANGE, COULOMB_3D_ENERGY_TICK_SPACING, COULOMB_3D_ENERGY_TICK_FORMAT );
    private static final DoubleRange COULOMB_3D_OFFSET_RANGE = new DoubleRange( -15, 5, 0, 1 ); // eV
    private static final BSWellRangeSpec COULOMB_3D_RANGE_SPEC = 
        new BSWellRangeSpec.Coulomb3D( COULOMB_3D_ENERGY_AXIS_SPEC, COULOMB_3D_OFFSET_RANGE );
    
    // Harmonic Oscillator ranges (min, max, default, significantDecimalPlaces)
    private static final Range HARMONIC_OSCILLATOR_ENERGY_RANGE = new Range( -5, +15 ); // eV
    private static final double HARMONIC_OSCILLATOR_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat HARMONIC_OSCILLATOR_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    private static final BSAxisSpec HARMONIC_OSCILLATOR_ENERGY_AXIS_SPEC = 
        new BSAxisSpec( HARMONIC_OSCILLATOR_ENERGY_RANGE, HARMONIC_OSCILLATOR_ENERGY_TICK_SPACING, HARMONIC_OSCILLATOR_ENERGY_TICK_FORMAT );
    private static final DoubleRange HARMONIC_OSCILLATOR_OFFSET_RANGE = new DoubleRange( -5, 15, 0, 1 ); // eV
    private static final DoubleRange HARMONIC_OSCILLATOR_ANGULAR_FREQUENCY_RANGE = new DoubleRange( 1, 10, 1, 1 ); // fs^-1
    private static final BSWellRangeSpec HARMONIC_OSCILLATOR_RANGE_SPEC =
        new BSWellRangeSpec.HarmonicOscillator( HARMONIC_OSCILLATOR_ENERGY_AXIS_SPEC, HARMONIC_OSCILLATOR_OFFSET_RANGE, HARMONIC_OSCILLATOR_ANGULAR_FREQUENCY_RANGE );
    
    // Square ranges (min, max, default, significantDecimalPlaces)
    private static final Range SQUARE_ENERGY_RANGE = new Range( -5, +15 ); // eV
    private static final double SQUARE_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat SQUARE_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    private static final BSAxisSpec SQUARE_ENERGY_AXIS_SPEC = 
        new BSAxisSpec( SQUARE_ENERGY_RANGE, SQUARE_ENERGY_TICK_SPACING, SQUARE_ENERGY_TICK_FORMAT );
    private static final DoubleRange SQUARE_OFFSET_RANGE = new DoubleRange( -5, 15, 0, 1 ); // eV
    private static final DoubleRange SQUARE_WIDTH_RANGE = new DoubleRange( 0.1, 8, 1, 1 ); // nm
    private static final DoubleRange SQUARE_HEIGHT_RANGE = new DoubleRange( 0, 20, 10, 1 ); // eV
    private static final DoubleRange SQUARE_SEPARATION_RANGE = new DoubleRange( 0, 0, 0, 0 ); // nm (don't care for one well)
    private static final BSWellRangeSpec SQUARE_RANGE_SPEC =
            new BSWellRangeSpec.Square( SQUARE_ENERGY_AXIS_SPEC, SQUARE_OFFSET_RANGE, SQUARE_WIDTH_RANGE, SQUARE_HEIGHT_RANGE, SQUARE_SEPARATION_RANGE );
    
    public BSOneWellSpec() {
        super();

        setWellTypes( WELL_TYPES );
        setDefaultWellType( DEFAULT_WELL_TYPE );
        setNumberOfWellsRange( NUMBER_OF_WELLS_RANGE );
        
        setSuperpositionControlsSupported( SUPERPOSITION_CONTROLS_SUPPORTED );
        setParticleControlsSupported( PARTICLE_CONTROLS_SUPPORTED );
        setMagnifyingGlassSupported( MAGNIFYING_GLASS_SUPPORTED );
        setMagnifyingGlassSelected( MAGNIFYING_GLASS_SELECTED );

        setMassMultiplierRange( MASS_MULTIPLIER_RANGE );
        
        setAsymmetricRangeSpec( ASYMMETRIC_RANGE_SPEC );
        setCoulomb1DRangeSpec( COULOMB_1D_RANGE_SPEC );
        setCoulomb3DRangeSpec( COULOMB_3D_RANGE_SPEC );
        setHarmonicOscillatorRangeSpec( HARMONIC_OSCILLATOR_RANGE_SPEC );
        setSquareRangeSpec( SQUARE_RANGE_SPEC );
    }
}
