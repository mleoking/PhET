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
 * BSManyWellsSpec contains the information needed to set up the "Many Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSManyWellsSpec extends BSAbstractModuleSpec {

    private static final String ID = "manyWellsModule";
    
    private static final BSWellType[] WELL_TYPES = { BSWellType.SQUARE, BSWellType.COULOMB_1D };
    private static final BSWellType DEFAULT_WELL_TYPE = BSWellType.SQUARE;
    
    private static final boolean OFFSET_CONTROL_SUPPORTED = false;
    private static final boolean SUPERPOSITION_CONTROLS_SUPPORTED = false;
    private static final boolean PARTICLE_CONTROLS_SUPPORTED = false;
    private static final boolean MAGNIFYING_GLASS_SUPPORTED = true;
    private static final boolean MAGNIFYING_GLASS_SELECTED = true;
    private static final boolean AVERAGE_PROBABILITY_DENSITY_SUPPORTED = true;
    
    // Particle ranges (min, max, default, significantDecimalPlaces)
    private static final DoubleRange MASS_MULTIPLIER_RANGE = new DoubleRange( 0.5, 1.1, 1, 2 );

    // Number of wells (min, max, default, significantDecimalPlaces)
    private static final IntegerRange NUMBER_OF_WELLS_RANGE = new IntegerRange( 1, 10, 5 );
    
    // Magnification power of the magnifying glass
    private static final double MAGNIFICATION = 10;
    
    // Field constant
    private static final DoubleRange FIELD_CONSTANT_RANGE = new DoubleRange( -1, 1, 0, 1 );
    
    //----------------------------------------------------------------------------
    // 1D Coulomb ranges
    //----------------------------------------------------------------------------
    
    // Energy axis zoom ranges
    private static final Range COULOMB_1D_ENERGY_RANGE1 = new Range( -20.5, 0.5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING1 = 5; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT1 = new DecimalFormat( "#0" );
    
    private static final Range COULOMB_1D_ENERGY_RANGE2 = new Range( -30.5, 0.5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING2 = 5; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT2 = new DecimalFormat( "#0" );
    
    private static final Range COULOMB_1D_ENERGY_RANGE3 = new Range( -51, 1 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING3 = 10; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT3 = new DecimalFormat( "#0" );
    
    private static final Range COULOMB_1D_ENERGY_RANGE4 = new Range( -101, 1 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING4 = 20; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT4 = new DecimalFormat( "##0" );
    
    private static final Range COULOMB_1D_ENERGY_RANGE5 = new Range( -255, 5 ); // eV
    private static final double COULOMB_1D_ENERGY_TICK_SPACING5 = 50; // eV
    private static final DecimalFormat COULOMB_1D_ENERGY_TICK_FORMAT5 = new DecimalFormat( "##0" );
    
    // Potential attributes
    private static final DoubleRange COULOMB_1D_OFFSET_RANGE = new DoubleRange( 0, 0, 0, 1 ); // eV
    private static final DoubleRange COULOMB_1D_SPACING_RANGE = new DoubleRange( 0.05, 0.7, 0.7, 2 );
    
    //----------------------------------------------------------------------------
    // Square ranges
    //----------------------------------------------------------------------------
    
    // Energy axis zoom ranges
    private static final Range SQUARE_ENERGY_RANGE1 = new Range( -0.5, 20.5 ); // eV
    private static final double SQUARE_ENERGY_TICK_SPACING1 = 5; // eV
    private static final DecimalFormat SQUARE_ENERGY_TICK_FORMAT1 = new DecimalFormat( "0" );
    
    private static final Range SQUARE_ENERGY_RANGE2 = new Range( -10.5, 30.5 ); // eV
    private static final double SQUARE_ENERGY_TICK_SPACING2 = 10; // eV
    private static final DecimalFormat SQUARE_ENERGY_TICK_FORMAT2 = new DecimalFormat( "0" );
    
    // Potential attributes
    private static final DoubleRange SQUARE_OFFSET_RANGE = new DoubleRange( 0, 0 ,0, 1 ); // eV
    private static final DoubleRange SQUARE_WIDTH_RANGE = new DoubleRange( 0.1, 0.5, 0.5, 2 ); // nm
    private static final DoubleRange SQUARE_HEIGHT_RANGE = new DoubleRange( 0, 20, 10, 1 ); // eV
    private static final DoubleRange SQUARE_SEPARATION_RANGE = new DoubleRange( 0.05, 0.2, 0.1, 2 );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSManyWellsSpec() {
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

        // 1D Coulomb spec
        {
            AxisSpec axisSpec1 = new AxisSpec( COULOMB_1D_ENERGY_RANGE1, COULOMB_1D_ENERGY_TICK_SPACING1, COULOMB_1D_ENERGY_TICK_FORMAT1 );
            AxisSpec axisSpec2 = new AxisSpec( COULOMB_1D_ENERGY_RANGE2, COULOMB_1D_ENERGY_TICK_SPACING2, COULOMB_1D_ENERGY_TICK_FORMAT2 );
            AxisSpec axisSpec3 = new AxisSpec( COULOMB_1D_ENERGY_RANGE3, COULOMB_1D_ENERGY_TICK_SPACING3, COULOMB_1D_ENERGY_TICK_FORMAT3 );
            AxisSpec axisSpec4 = new AxisSpec( COULOMB_1D_ENERGY_RANGE4, COULOMB_1D_ENERGY_TICK_SPACING4, COULOMB_1D_ENERGY_TICK_FORMAT4 );
            AxisSpec axisSpec5 = new AxisSpec( COULOMB_1D_ENERGY_RANGE5, COULOMB_1D_ENERGY_TICK_SPACING5, COULOMB_1D_ENERGY_TICK_FORMAT5 );
            AxisSpec[] axisSpecs = new AxisSpec[] { axisSpec1, axisSpec2, axisSpec3, axisSpec4, axisSpec5 };
            ZoomSpec zoomSpec = new ZoomSpec( axisSpecs );
            BSPotentialSpec wellSpec = new BSPotentialSpec.Coulomb1D( zoomSpec, COULOMB_1D_OFFSET_RANGE, COULOMB_1D_SPACING_RANGE );
            setCoulomb1DSpec( wellSpec );
        }
        
        // Square spec
        {
            AxisSpec axisSpec1 = new AxisSpec( SQUARE_ENERGY_RANGE1, SQUARE_ENERGY_TICK_SPACING1, SQUARE_ENERGY_TICK_FORMAT1 );
            AxisSpec axisSpec2 = new AxisSpec( SQUARE_ENERGY_RANGE2, SQUARE_ENERGY_TICK_SPACING2, SQUARE_ENERGY_TICK_FORMAT2 );
            AxisSpec[] axisSpecs = new AxisSpec[] { axisSpec1, axisSpec2 };
            ZoomSpec zoomSpec = new ZoomSpec( axisSpecs );
            BSPotentialSpec wellSpec = new BSPotentialSpec.Square( zoomSpec, 
                    SQUARE_OFFSET_RANGE, SQUARE_WIDTH_RANGE, SQUARE_HEIGHT_RANGE, SQUARE_SEPARATION_RANGE );
            setSquareSpec( wellSpec );
        }
    }
}
