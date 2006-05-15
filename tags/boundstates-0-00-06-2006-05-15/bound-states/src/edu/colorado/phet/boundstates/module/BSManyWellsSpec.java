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
 * BSManyWellsSpec contains the information needed to set up the "Many Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSManyWellsSpec extends BSAbstractModuleSpec {

    private static final BSWellType[] WELL_TYPES = { BSWellType.SQUARE, BSWellType.COULOMB_1D };
    private static final BSWellType DEFAULT_WELL_TYPE = BSWellType.SQUARE;
    
    private static final boolean SUPERPOSITION_CONTROLS_SUPPORTED = false;
    private static final boolean PARTICLE_CONTROLS_SUPPORTED = false;
    private static final boolean MAGNIFYING_GLASS_SUPPORTED = true;
    private static final boolean MAGNIFYING_GLASS_SELECTED = true;

    // Particle ranges (min, max, default, significantDecimalPlaces)
    private static final DoubleRange MASS_MULTIPLIER_RANGE = new DoubleRange( 0.5, 1.1, 1, 1 );

    // Number of wells (min, max, default, significantDecimalPlaces)
    private static final IntegerRange NUMBER_OF_WELLS_RANGE = new IntegerRange( 1, 10, 5 );
    
    // 1D Coulomb ranges (min, max, default, significantDecimalPlaces)
    private static final Range COULOMB_1D_ENERGY_RANGE = new Range( -15, +5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    private static final BSAxisSpec COLUOMB_1D_ENERGY_AXIS_SPEC = 
        new BSAxisSpec( COULOMB_1D_ENERGY_RANGE, COULOMB_1D_ENERGY_TICK_SPACING, COULOMB_1D_ENERGY_TICK_FORMAT );
    private static final DoubleRange COULOMB_1D_OFFSET_RANGE = new DoubleRange( -15, 5, 0, 1 ); // eV
    private static final DoubleRange COULOMB_1D_SPACING_RANGE = new DoubleRange( 0.3, 3, 1, 1 );
    private static final BSWellRangeSpec COULOMB_1D_RANGE_SPEC = 
        new BSWellRangeSpec.Coulomb1D( COLUOMB_1D_ENERGY_AXIS_SPEC, COULOMB_1D_OFFSET_RANGE, COULOMB_1D_SPACING_RANGE );
    
    // Square ranges (min, max, default, significantDecimalPlaces)
    private static final Range SQUARE_ENERGY_RANGE = new Range( -5, +15 ); // eV
    private static final double SQUARE_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat SQUARE_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    private static final BSAxisSpec SQUARE_ENERGY_AXIS_SPEC = 
        new BSAxisSpec( SQUARE_ENERGY_RANGE, SQUARE_ENERGY_TICK_SPACING, SQUARE_ENERGY_TICK_FORMAT );
    private static final DoubleRange SQUARE_OFFSET_RANGE = new DoubleRange( -5, 15, 0, 1 ); // eV
    private static final DoubleRange SQUARE_WIDTH_RANGE = new DoubleRange( 0.1, 0.5, 0.5, 2 ); // nm
    private static final DoubleRange SQUARE_HEIGHT_RANGE = new DoubleRange( 0, 20, 10, 1 ); // eV
    private static final DoubleRange SQUARE_SEPARATION_RANGE = new DoubleRange( 0.05, 0.2, 0.1, 2 );
    private static final BSWellRangeSpec SQUARE_RANGE_SPEC =
            new BSWellRangeSpec.Square( SQUARE_ENERGY_AXIS_SPEC, SQUARE_OFFSET_RANGE, SQUARE_WIDTH_RANGE, SQUARE_HEIGHT_RANGE, SQUARE_SEPARATION_RANGE );
    
    public BSManyWellsSpec() {
        super();
        
        setWellTypes( WELL_TYPES );
        setDefaultWellType( DEFAULT_WELL_TYPE );
        setNumberOfWellsRange( NUMBER_OF_WELLS_RANGE );
        
        setSuperpositionControlsSupported( SUPERPOSITION_CONTROLS_SUPPORTED );
        setParticleControlsSupported( PARTICLE_CONTROLS_SUPPORTED );
        setMagnifyingGlassSupported( MAGNIFYING_GLASS_SUPPORTED );
        setMagnifyingGlassSelected( MAGNIFYING_GLASS_SELECTED );
        
        setMassMultiplierRange( MASS_MULTIPLIER_RANGE );

        setCoulomb1DRangeSpec( COULOMB_1D_RANGE_SPEC );
        setSquareRangeSpec( SQUARE_RANGE_SPEC );
    }
}
