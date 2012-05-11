// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.CobaltChlorideData;
import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.CobaltIINitrateData;
import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.CopperSulfateData;
import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.DrinkMixData;
import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.NickelIIChlorideData;
import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.PotassiumChromateData;
import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.PotassiumDichromateData;
import edu.colorado.phet.beerslawlab.beerslaw.model.MolarAbsorptivityData.PotassiumPermanganateData;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSymbols;
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ColorRange;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Solution model for the Beer's Law module.
 * <p/>
 * The numeric values for specific solutions were arrived at by running lab experiments,
 * and are documented in doc/Beers-Law-Lab-design.pdf and doc/BeersLawLabData.xlsx.
 * <p/>
 * Note that this model does not use the Solute model from the Concentration module, because
 * we have very different needs wrt color scheme, properties, etc.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class BeersLawSolution implements Resettable {

    public final Solvent solvent;
    public final String name;
    public final String formula;
    public final Property<Double> concentration; // M
    public final DoubleRange concentrationRange; // M
    public final ConcentrationTransform concentrationTransform; // transform between M and other units (eg, mM, uM)
    public final ColorRange colorRange; // colors for the range of values that we're interested in. colorRange.maxColor is not necessarily the saturated color
    public final Color saturatedColor;
    public final CompositeProperty<Color> fluidColor; // derived
    public final MolarAbsorptivityData molarAbsorptivityData; // experimental data that maps wavelength to molar absorptivity

    // Constructor for solutions whose colorRange.maxColor is the same as the saturated color.
    public BeersLawSolution( String name, String formula,
                             DoubleRange concentrationRange, int concentrationViewScale,
                             ColorRange colorRange,
                             MolarAbsorptivityData molarAbsorptivityData ) {
        this( name, formula, concentrationRange, concentrationViewScale, colorRange, colorRange.getMax(), molarAbsorptivityData );
    }

    public BeersLawSolution( String name, String formula,
                             DoubleRange concentrationRange, int concentrationViewScale,
                             ColorRange colorRange, Color saturatedColor,
                             MolarAbsorptivityData molarAbsorptivityData ) {

        this.solvent = new Water();
        this.name = name;
        this.formula = formula;
        this.concentration = new Property<Double>( concentrationRange.getDefault() );
        this.concentrationRange = concentrationRange;
        this.concentrationTransform = new ConcentrationTransform( concentrationViewScale );
        this.colorRange = colorRange;
        this.saturatedColor = saturatedColor;
        this.molarAbsorptivityData = molarAbsorptivityData;

        // derive the solution color
        this.fluidColor = new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return createColor( concentration.get(), BeersLawSolution.this.concentrationRange, BeersLawSolution.this.colorRange, solvent );
            }
        }, this.concentration );
    }

    public void reset() {
        concentration.reset();
    }

    public String getDisplayName() {
        return MessageFormat.format( Strings.PATTERN_0FORMULA_1NAME, formula, name );
    }

    public String getViewUnits() {
        return concentrationTransform.getUnits();
    }

    // Creates a color that corresponds to the solution's concentration.
    private static final Color createColor( double concentration, DoubleRange concentrationRange, ColorRange colorRange, Solvent solvent ) {
        Color color = solvent.color.get();
        if ( concentration > 0 ) {
            LinearFunction f = new LinearFunction( concentrationRange.getMin(), concentrationRange.getMax(), 0, 1 );
            color = colorRange.interpolateLinear( f.evaluate( concentration ) );
        }
        return color;
    }

    // This solution corresponds to a popular powdered drink mix whose name is a registered trademark.
    public static class DrinkMixSolution extends BeersLawSolution {
        public DrinkMixSolution() {
            super( Strings.DRINK_MIX, BLLSymbols.DRINK_MIX,
                   new DoubleRange( 0, 0.400, 0.100 ), 1000,
                   new ColorRange( new Color( 255, 225, 225 ), Color.RED ),
                   new DrinkMixData() );
        }

        // This solution's solute doesn't have a formula, so use just its name.
        @Override public String getDisplayName() {
            return name;
        }
    }

    public static class CobaltIINitrateSolution extends BeersLawSolution {
        public CobaltIINitrateSolution() {
            super( Strings.COBALT_II_NITRATE, BLLSymbols.COBALT_II_NITRATE,
                   new DoubleRange( 0, 0.400, 0.100 ), 1000,
                   new ColorRange( new Color( 255, 225, 225 ), Color.RED ),
                   new CobaltIINitrateData() );
        }
    }

    public static class CobaltChlorideSolution extends BeersLawSolution {
        public CobaltChlorideSolution() {
            super( Strings.COBALT_CHLORIDE, BLLSymbols.COBALT_CHLORIDE,
                   new DoubleRange( 0, 0.250, 0.100 ), 1000,
                   new ColorRange( new Color( 255, 242, 242 ), new Color( 255, 106, 106 ) ),
                   new CobaltChlorideData() );
        }
    }

    public static class PotassiumDichromateSolution extends BeersLawSolution {
        public PotassiumDichromateSolution() {
            super( Strings.POTASSIUM_DICHROMATE, BLLSymbols.POTASSIUM_DICHROMATE,
                   new DoubleRange( 0, 0.000500, 0.000100 ), 1000000,
                   new ColorRange( new Color( 255, 232, 210 ), new Color( 255, 127, 0 ) ),
                   new PotassiumDichromateData() );
        }
    }

    public static class PotassiumChromateSolution extends BeersLawSolution {
        public PotassiumChromateSolution() {
            super( Strings.POTASSIUM_CHROMATE, BLLSymbols.POTASSIUM_CHROMATE,
                   new DoubleRange( 0, 0.000400, 0.000100 ), 1000000,
                   new ColorRange( new Color( 255, 255, 199 ), new Color( 255, 255, 0 ) ),
                   new PotassiumChromateData() );
        }
    }

    public static class NickelIIChlorideSolution extends BeersLawSolution {
        public NickelIIChlorideSolution() {
            super( Strings.NICKEL_II_CHLORIDE, BLLSymbols.NICKEL_II_CHLORIDE,
                   new DoubleRange( 0, 0.350, 0.100 ), 1000,
                   new ColorRange( new Color( 234, 244, 234 ), new Color( 0, 128, 0 ) ),
                   new NickelIIChlorideData() );
        }
    }

    public static class CopperSulfateSolution extends BeersLawSolution {
        public CopperSulfateSolution() {
            super( Strings.COPPER_SULFATE, BLLSymbols.COPPER_SULFATE,
                   new DoubleRange( 0, 0.200, 0.100 ), 1000,
                   new ColorRange( new Color( 222, 238, 255 ), new Color( 30, 144, 255 ) ),
                   new CopperSulfateData() );
        }
    }

    // Color range does not extend to the saturated color.
    public static class PotassiumPermanganateSolution extends BeersLawSolution {
        public PotassiumPermanganateSolution() {
            super( Strings.POTASSIUM_PERMANGANATE, BLLSymbols.POTASSIUM_PERMANGANATE,
                   new DoubleRange( 0, 0.000800, 0.000100 ), 1000000,
                   new ColorRange( new Color( 255, 235, 255 ), new Color( 255, 0, 255 ) ), new Color( 80, 0, 120 ),
                   new PotassiumPermanganateData() );
        }
    }
}
