// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.Color;
import java.text.MessageFormat;

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
import edu.colorado.phet.beerslawlab.common.model.Solvent;
import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationSolution;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Solution model for the Beer's Law module.
 * This module has a set of selectable solutions.
 * Concentration is directly settable, solvent and solute are immutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawSolution {

    public final Property<Double> concentration;
    public final Solvent solvent;
    public final Solute solute;
    public final CompositeProperty<Color> fluidColor; // derived from solute color and concentration
    public final String concentrationUnits;
    //TODO add CompositeProperty for absorbance and %transmission

    public BeersLawSolution( final Solute solute, String concentrationUnits ) {
        this( solute, 0d, concentrationUnits );
    }

    public BeersLawSolution( final Solute solute, double initialConcentration, String concentrationUnits ) {

        this.concentration = new Property<Double>( initialConcentration );
        this.solvent = new Water();
        this.solute = solute;
        this.concentrationUnits = concentrationUnits;

        // derive the solution color
        this.fluidColor = new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return ConcentrationSolution.createColor( solvent, solute, concentration.get() ); //TODO undesirable dependency on Solution here
            }
        }, this.concentration );
    }

    public String getDisplayName() {
        return MessageFormat.format( Strings.PATTERN_0FORMULA_1NAME, solute.formula, solute.name );
    }

    public Color getSaturatedColor() {
        return solute.solutionColor.getMax();
    }

    public static class KoolAidSolution extends BeersLawSolution {
        public KoolAidSolution() {
            super( new KoolAid(), Strings.UNITS_mM );
        }

        @Override public String getDisplayName() {
            return solute.name;
        }
    }

    public static class CobaltIINitrateSolution extends BeersLawSolution {
        public CobaltIINitrateSolution() {
            super( new CobaltIINitrate(), Strings.UNITS_mM );
        }
    }

    public static class CobaltChlorideSolution extends BeersLawSolution {
        public CobaltChlorideSolution() {
            super( new CobaltChloride(), Strings.UNITS_mM );
        }
    }

    public static class PotassiumDichromateSolution extends BeersLawSolution {
        public PotassiumDichromateSolution() {
            super( new PotassiumDichromate(), Strings.UNITS_uM );
        }
    }

    public static class PotassiumChromateSolution extends BeersLawSolution {
        public PotassiumChromateSolution() {
            super( new PotassiumChromate(), Strings.UNITS_uM );
        }
    }

    public static class NickelIIChlorideSolution extends BeersLawSolution {
        public NickelIIChlorideSolution() {
            super( new NickelIIChloride(), Strings.UNITS_mM );
        }
    }

    public static class CopperSulfateSolution extends BeersLawSolution {
        public CopperSulfateSolution() {
            super( new CopperSulfate(), Strings.UNITS_mM );
        }
    }

    public static class PotassiumPermanganateSolution extends BeersLawSolution {
        public PotassiumPermanganateSolution() {
            super( new PotassiumPermanganate(), Strings.UNITS_uM );
        }
    }

    // Water is not technically a solution, but the design team wants it to act like a Solution in the model.
    public static class PureWater extends BeersLawSolution {

        public PureWater() {
            super( new NullSolute(), 0d, "" );
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
