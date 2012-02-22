// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSymbols;
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ColorRange;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Solution model for the Beer's Law module.
 * This module has a set of selectable solutions.
 * Concentration is directly settable, solvent and solute are immutable.
 * <p>
 * The numeric values for specific solutions were arrived at by running lab experiments,
 * and are documented in doc/Beers-Law-Lab-design.pdf and doc/BeersLawLabData.xlsx.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawSolution {

    public final Solvent solvent;
    public final String name;
    public final String formula;
    public final Property<Double> concentration; // M
    public final DoubleRange concentrationRange; // M
    public final ConcentrationTransform concentrationTransform;
    public final ColorRange colorRange;
    public final Color saturatedColor;
    public final CompositeProperty<Color> fluidColor; // derived
    public final double lambdaMax; // wavelength for maximum absorbance, nm
    public final double molarAbsorptionMax; // corresponds to lambdaMax, 1/(cm*M)
    public final double molarConversionFactor; // this is needed primarily for DrinkMixSolution, see class doc

    // Most solutions have a molarConversionFactor of 1.
    public BeersLawSolution( String name, String formula,
                             DoubleRange concentrationRange, int concentrationViewExponent,
                             ColorRange colorRange, Color saturatedColor,
                             double lambdaMax, double molarAbsorptionMax ) {
        this( name, formula, concentrationRange, concentrationViewExponent, colorRange, saturatedColor, lambdaMax, molarAbsorptionMax, 1 );
    }

    public BeersLawSolution( String name, String formula,
                             DoubleRange concentrationRange, int concentrationViewExponent,
                             ColorRange colorRange, Color saturatedColor,
                             double lambdaMax, double molarAbsorptionMax, double molarConversionFactor ) {

        this.solvent = new Water();
        this.name = name;
        this.formula = formula;
        this.concentration = new Property<Double>( concentrationRange.getDefault() );
        this.concentrationRange = concentrationRange;
        this.concentrationTransform = new ConcentrationTransform( concentrationViewExponent );
        this.colorRange = colorRange;
        this.saturatedColor = saturatedColor;
        this.lambdaMax = lambdaMax;
        this.molarAbsorptionMax = molarAbsorptionMax;
        this.molarConversionFactor = molarConversionFactor;

        // derive the solution color
        this.fluidColor = new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return createColor( concentration.get(), BeersLawSolution.this.concentrationRange, BeersLawSolution.this.colorRange, solvent );
            }
        }, this.concentration );
    }

    public String getDisplayName() {
        return MessageFormat.format( Strings.PATTERN_0FORMULA_1NAME, formula, name );
    }

    public String getViewUnits() {
        return concentrationTransform.getViewUnits();
    }

    // Creates a color that corresponds to the solution's concentration.
    private static final Color createColor( double concentration, DoubleRange concentrationRange, ColorRange colorRange, Solvent solvent ) {
        Color color = solvent.color;
        if ( concentration > 0 ) {
            LinearFunction f = new LinearFunction( concentrationRange.getMin(), concentrationRange.getMax(), 0, 1 );
            color = colorRange.interpolateLinear( f.evaluate( concentration ) );
        }
        return color;
    }

    /*
     * This solution corresponds to a popular powdered drink mix whose name is a registered trademark.
     * It's a bit tricky because the molar absorptivity is for Red 40, but the concentration range is for sugar.
     * So we need to specify a "molar conversion factor" to adjust its concentration when computing
     * absorbance (A) and transmittance (T).
     */
    public static class DrinkMixSolution extends BeersLawSolution {
        public DrinkMixSolution() {
            super( Strings.DRINK_MIX, BLLSymbols.DRINK_MIX,
                   new DoubleRange( 0, 0.400 ), -3,
                   new ColorRange( new Color( 255, 225, 225 ), Color.RED ), Color.RED,
                   511, 1270.18, 0.000003999268 );
        }

        // This solution's solute doesn't have a formula, so use just its name.
        @Override public String getDisplayName() {
            return name;
        }
    }

    public static class CobaltIINitrateSolution extends BeersLawSolution {
        public CobaltIINitrateSolution() {
            super( Strings.COBALT_II_NITRATE, BLLSymbols.COBALT_II_NITRATE,
                   new DoubleRange( 0, 0.400 ), -3,
                   new ColorRange( new Color( 255, 225, 225 ), Color.RED ), Color.RED,
                   550, 4.72 );
        }
    }

    public static class CobaltChlorideSolution extends BeersLawSolution {
        public CobaltChlorideSolution() {
            super( Strings.COBALT_CHLORIDE, BLLSymbols.COBALT_CHLORIDE,
                   new DoubleRange( 0, 0.250 ), -3,
                   new ColorRange( new Color( 255, 242, 242 ), new Color( 0xFF6A6A ) ), new Color( 0xFF6A6A ),
                   549, 7.23 );
        }
    }

    public static class PotassiumDichromateSolution extends BeersLawSolution {
        public PotassiumDichromateSolution() {
            super( Strings.POTASSIUM_DICHROMATE, BLLSymbols.POTASSIUM_DICHROMATE,
                   new DoubleRange( 0, 0.000500 ), -6,
                   new ColorRange( new Color( 255, 232, 210 ), new Color( 0xFF7F00 ) ), new Color( 0xFF7F00 ),
                   394, 3696.53 );
        }
    }

    public static class PotassiumChromateSolution extends BeersLawSolution {
        public PotassiumChromateSolution() {
            super( Strings.POTASSIUM_CHROMATE, BLLSymbols.POTASSIUM_CHROMATE,
                   new DoubleRange( 0, 0.000400 ), -6, new ColorRange( new Color( 255, 255, 199 ), Color.YELLOW ), Color.YELLOW,
                   413, 4770.07 );
        }
    }

    public static class NickelIIChlorideSolution extends BeersLawSolution {
        public NickelIIChlorideSolution() {
            super( Strings.NICKEL_II_CHLORIDE, BLLSymbols.NICKEL_II_CHLORIDE,
                   new DoubleRange( 0, 0.350 ), -3, new ColorRange( new Color( 234, 244, 234 ), new Color( 0x008000 ) ), new Color( 0x008000 ),
                   435, 5.31 );
        }
    }

    public static class CopperSulfateSolution extends BeersLawSolution {
        public CopperSulfateSolution() {
            super( Strings.COPPER_SULFATE, BLLSymbols.COPPER_SULFATE,
                   new DoubleRange( 0, 0.200 ), -3, new ColorRange( new Color( 222, 238, 255 ), new Color( 0x1E90FF ) ), new Color( 0x1E90FF ),
                   780, 9.67 );
        }
    }

    public static class PotassiumPermanganateSolution extends BeersLawSolution {
        public PotassiumPermanganateSolution() {
            super( Strings.POTASSIUM_PERMANGANATE, BLLSymbols.POTASSIUM_PERMANGANATE,
                   new DoubleRange( 0, 0.000800 ), -6,
                   new ColorRange( new Color( 255, 235, 255 ), new Color( 255, 0, 255 ) ), new Color( 0x8B008B ),
                   566, 2389.61 );
        }
    }
}
