// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.beerslawlab.common.BLLResources;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.CobaltIINitrate;
import edu.colorado.phet.beerslawlab.common.model.Solute.CopperSulfate;
import edu.colorado.phet.beerslawlab.common.model.Solute.KoolAid;
import edu.colorado.phet.beerslawlab.common.model.Solute.NickelIIChloride;
import edu.colorado.phet.beerslawlab.common.model.Solute.NullSolute;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumChromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumDichromate;
import edu.colorado.phet.beerslawlab.common.model.Solute.PotassiumPermanganate;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Solution model for the Beer's Law module.
 * This module has a set of selectable solutions.
 * Concentration is directly settable, solvent and solute are immutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawSolution extends Solution {

    public final Solute solute;
    public final Property<Double> concentration; // M
    public final int concentrationDisplayExponent;
    public final DoubleRange concentrationRange; // M
    public final CompositeProperty<Color> fluidColor; // derived from solute color and concentration

    //TODO add CompositeProperty for absorbance and %transmission

    public BeersLawSolution( final Solute solute, DoubleRange concentrationRange, int concentrationDisplayExponent ) {

        this.concentration = new Property<Double>( concentrationRange.getDefault() );
        this.solute = solute;
        this.concentrationDisplayExponent = concentrationDisplayExponent;
        this.concentrationRange = concentrationRange;

        // derive the solution color
        this.fluidColor = new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return createColor( solvent, solute, concentration.get() );
            }
        }, this.concentration );

        getDisplayUnits(); // call this to catch problems at instantiation
    }

    public String getDisplayName() {
        return MessageFormat.format( Strings.PATTERN_0FORMULA_1NAME, solute.formula, solute.name );
    }

    public Color getSaturatedColor() {
        return solute.getSaturatedColor();
    }

    public String getDisplayUnits() {
        if ( concentrationDisplayExponent == 1 ) {
            return Strings.UNITS_M;
        }
        if ( concentrationDisplayExponent == -3 ) {
            return Strings.UNITS_mM;
        }
        else if ( concentrationDisplayExponent == -6 ) {
            return Strings.UNITS_uM;
        }
        else {
            throw new IllegalStateException( "unsupported concentrationDisplayExponent=" + concentrationDisplayExponent );
        }
    }

    public static class KoolAidSolution extends BeersLawSolution {
        public KoolAidSolution() {
            super( new KoolAid(), new DoubleRange( 0, 0.400 ), -3 );
        }

        @Override public String getDisplayName() {
            return solute.name;
        }
    }

    public static class CobaltIINitrateSolution extends BeersLawSolution {
        public CobaltIINitrateSolution() {
            super( new CobaltIINitrate(), new DoubleRange( 0, 0.400 ), -3 );
        }
    }

    public static class CobaltChlorideSolution extends BeersLawSolution {
        public CobaltChlorideSolution() {
            super( new CobaltChloride(), new DoubleRange( 0, 0.250 ), -3 );
        }
    }

    public static class PotassiumDichromateSolution extends BeersLawSolution {
        public PotassiumDichromateSolution() {
            super( new PotassiumDichromate(), new DoubleRange( 0, 0.000500 ), -6 );
        }
    }

    public static class PotassiumChromateSolution extends BeersLawSolution {
        public PotassiumChromateSolution() {
            super( new PotassiumChromate(), new DoubleRange( 0, 0.000400 ), -6  );
        }
    }

    public static class NickelIIChlorideSolution extends BeersLawSolution {
        public NickelIIChlorideSolution() {
            super( new NickelIIChloride(), new DoubleRange( 0, 0.350 ), -3 );
        }
    }

    public static class CopperSulfateSolution extends BeersLawSolution {
        public CopperSulfateSolution() {
            super( new CopperSulfate(), new DoubleRange( 0, 0.200 ), -3 );
        }
    }

    public static class PotassiumPermanganateSolution extends BeersLawSolution {
        public PotassiumPermanganateSolution() {
            super( new PotassiumPermanganate(), new DoubleRange( 0, 0.000800 ), -6 );
        }
    }

    // Water is not technically a solution, but the design team wants it to act like a Solution in the model.
    public static class PureWater extends BeersLawSolution {

        public PureWater() {
            super( new NullSolute(), new DoubleRange( 0, 0, 0 ), 1 );
            assert( concentration.get() == 0d );
            concentration.addObserver( new SimpleObserver() {
                public void update() {
                    throw new IllegalStateException( "cannot set concentration of pure water" );
                }
            }, false );
        }

        public String getDisplayName() {
            return Strings.NONE_PURE_WATER;
        }

        public Color getSaturatedColor() {
            return solvent.getFluidColor();
        }
    }
}
