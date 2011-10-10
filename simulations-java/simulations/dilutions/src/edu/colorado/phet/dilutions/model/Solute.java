// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.DilutionsResources.Symbols;

/**
 * Model of a solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Solute {

    public final String name;
    public final String formula;
    public final double maxConcentration;
    public final Color solutionColor;
    public final Color precipitateColor;

    // For most solutes, the color of the precipitate is the same as the color in solution.
    public Solute( String name, String formula, double maxConcentration, Color solutionColor ) {
        this( name, formula, maxConcentration, solutionColor, solutionColor );
    }

    public Solute( String name, String formula, double maxConcentration, Color solutionColor, Color precipitateColor ) {
        this.name = name;
        this.formula = formula;
        this.maxConcentration = maxConcentration;
        this.solutionColor = solutionColor;
        this.precipitateColor = precipitateColor;
    }

    public static class KoolAid extends Solute {
        public KoolAid() {
            super( Strings.KOOL_AID, Symbols.KOOL_AID, 5.0, PhetColorScheme.RED_COLORBLIND );
        }
    }

    public static class CobaltIINitrate extends Solute {
        public CobaltIINitrate() {
            super( Strings.COBALT_II_NITRATE, Symbols.COBALT_II_NITRATE, 5.0, PhetColorScheme.RED_COLORBLIND );
        }
    }

    public static class NickelChloride extends Solute {
        public NickelChloride() {
            super( Strings.NICKEL_II_CHLORIDE, Symbols.NICKEL_II_CHLORIDE, 5.0, Color.GREEN );
        }
    }

    public static class CobaltChloride extends Solute {
        public CobaltChloride() {
            super( Strings.COBALT_CHLORIDE, Symbols.COBALT_CHLORIDE, 4.35, new Color( 255, 147, 158 ) /* rose pink */ );
        }
    }

    public static class PotassiumChromate extends Solute {
        public PotassiumChromate() {
            super( Strings.POTASSIUM_CHROMATE, Symbols.POTASSIUM_CHROMATE, 3.35, Color.YELLOW );
        }
    }

    public static class GoldIIIChloride extends Solute {
        public GoldIIIChloride() {
            super( Strings.GOLD_III_CHLORIDE, Symbols.GOLD_III_CHLORIDE, 2.25, Color.YELLOW );
        }
    }

    public static class CopperSulfate extends Solute {
        public CopperSulfate() {
            super( Strings.COPPER_SULFATE, Symbols.COPPER_SULFATE, 1.40, Color.BLUE );
        }
    }

    public static class PotassiumDichromate extends Solute {
        public PotassiumDichromate() {
            super( Strings.POTASSIUM_DICHROMATE, Symbols.POTASSIUM_DICHROMATE, 0.50, Color.ORANGE );
        }
    }

    public static class PotassiumPermanganate extends Solute {
        public PotassiumPermanganate() {
            super( Strings.POTASSIUM_PERMANGANATE, Symbols.POTASSIUM_PERMANGANATE, 0.50, new Color( 117, 57, 255 ) /* purple */, Color.BLACK );
        }
    }
}
