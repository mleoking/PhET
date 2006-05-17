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

import edu.colorado.phet.boundstates.control.ZoomControl.ZoomSpec;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.util.AxisSpec;
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
    
    private static final boolean OFFSET_CONTROL_SUPPORTED = false;
    private static final boolean SUPERPOSITION_CONTROLS_SUPPORTED = false;
    private static final boolean PARTICLE_CONTROLS_SUPPORTED = false;
    private static final boolean MAGNIFYING_GLASS_SUPPORTED = true;
    private static final boolean MAGNIFYING_GLASS_SELECTED = true;

    // Particle ranges (min, max, default, significantDecimalPlaces)
    private static final DoubleRange MASS_MULTIPLIER_RANGE = new DoubleRange( 0.5, 1.1, 1, 1 );

    // Number of wells (min, max, default, significantDecimalPlaces)
    private static final IntegerRange NUMBER_OF_WELLS_RANGE = new IntegerRange( 1, 10, 5 );
    
    //----------------------------------------------------------------------------
    // 1D Coulomb ranges
    //----------------------------------------------------------------------------
    
    // Energy axis "zoomed in"
    private static final Range COULOMB_1D_ENERGY_RANGE1 = new Range( -20.5, 0.5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING1 = 5; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT1 = new DecimalFormat( "0.0" );
    
    // Energy axis "zoomed out"
    private static final Range COULOMB_1D_ENERGY_RANGE2 = new Range( -255, 5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING2 = 50; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT2 = new DecimalFormat( "##0" );
    
    // Potential attributes
    private static final DoubleRange COULOMB_1D_OFFSET_RANGE = new DoubleRange( 0, 0, 0, 1 ); // eV
    private static final DoubleRange COULOMB_1D_SPACING_RANGE = new DoubleRange( 0.05, 0.7, 0.1, 2 );
    
    //----------------------------------------------------------------------------
    // Square ranges
    //----------------------------------------------------------------------------
    
    // Energy axis
    private static final Range SQUARE_ENERGY_RANGE = new Range( -0.5, 20.5 ); // eV
    private static final double SQUARE_ENERGY_TICK_SPACING = 5; // eV
    private static final DecimalFormat SQUARE_ENERGY_TICK_FORMAT = new DecimalFormat( "0.0" );
    
    // Potential attributes
    private static final DoubleRange SQUARE_OFFSET_RANGE = new DoubleRange( 0, 0, 0, 1 ); // eV
    private static final DoubleRange SQUARE_WIDTH_RANGE = new DoubleRange( 0.1, 0.5, 0.5, 2 ); // nm
    private static final DoubleRange SQUARE_HEIGHT_RANGE = new DoubleRange( 0, 20, 10, 1 ); // eV
    private static final DoubleRange SQUARE_SEPARATION_RANGE = new DoubleRange( 0.05, 0.2, 0.1, 2 );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSManyWellsSpec() {
        super();
        
        setWellTypes( WELL_TYPES );
        setDefaultWellType( DEFAULT_WELL_TYPE );
        setNumberOfWellsRange( NUMBER_OF_WELLS_RANGE );
        
        setOffsetControlSupported( OFFSET_CONTROL_SUPPORTED );
        setSuperpositionControlsSupported( SUPERPOSITION_CONTROLS_SUPPORTED );
        setParticleControlsSupported( PARTICLE_CONTROLS_SUPPORTED );
        setMagnifyingGlassSupported( MAGNIFYING_GLASS_SUPPORTED );
        setMagnifyingGlassSelected( MAGNIFYING_GLASS_SELECTED );
        
        setMassMultiplierRange( MASS_MULTIPLIER_RANGE );

        // 1D Coulomb spec
        {
            AxisSpec axisSpec1 = new AxisSpec( COULOMB_1D_ENERGY_RANGE1, COULOMB_1D_ENERGY_TICK_SPACING1, COULOMB_1D_ENERGY_TICK_FORMAT1 );
            AxisSpec axisSpec2 = new AxisSpec( COULOMB_1D_ENERGY_RANGE2, COULOMB_1D_ENERGY_TICK_SPACING2, COULOMB_1D_ENERGY_TICK_FORMAT2 );
            AxisSpec[] axisSpecs = new AxisSpec[] { axisSpec1, axisSpec2 };
            ZoomSpec zoomSpec = new ZoomSpec( axisSpecs );
            BSWellSpec wellSpec = new BSWellSpec.Coulomb1D( zoomSpec, COULOMB_1D_OFFSET_RANGE, COULOMB_1D_SPACING_RANGE );
            setCoulomb1DSpec( wellSpec );
        }
        
        // Square spec
        {
            AxisSpec axisSpec = new AxisSpec( SQUARE_ENERGY_RANGE, SQUARE_ENERGY_TICK_SPACING, SQUARE_ENERGY_TICK_FORMAT );
            ZoomSpec zoomSpec = new ZoomSpec( axisSpec );
            BSWellSpec wellSpec = new BSWellSpec.Square( zoomSpec, 
                    SQUARE_OFFSET_RANGE, SQUARE_WIDTH_RANGE, SQUARE_HEIGHT_RANGE, SQUARE_SEPARATION_RANGE );
            setSquareSpec( wellSpec );
        }
    }
}
