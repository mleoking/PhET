// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLResources.Symbols;
import edu.colorado.phet.common.phetcommon.util.ColorRange;

/**
 * Model of a solute, an immutable data structure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    private static final int PARTICLES_PER_MOLE = 200; // default

    // The form that the solute is delivered in, either a solid or in a stock solution.
    public static enum SoluteForm {
        SOLID, STOCK_SOLUTION
    }

    public final String name; // localized name
    public final String formula; // chemical formula, not localized
    public final double molarMass; // g/mol
    public final double saturatedConcentration; // M, in beaker
    public final double stockSolutionConcentration; // M, stock solution in dropper
    public final ColorRange solutionColor; // color range for a solution with non-zero concentration
    public final Color particleColor; // color of solid particles
    public final double particleSize; // solid particles are square, this is the length of one side
    public final int particlesPerMole; // number of particles to show per mol of solute

    // For most solutes, particles are the same as the color as the saturated solution.
    public Solute( String name, String formula, double molarMass, double saturatedConcentration, double stockSolutionConcentration, ColorRange solutionColor, double particleSize, int particlesPerMole ) {
        this( name, formula, molarMass, saturatedConcentration, stockSolutionConcentration, solutionColor, solutionColor.getMax(), particleSize, particlesPerMole );
    }

    public Solute( String name, String formula, double molarMass, double saturatedConcentration, double stockSolutionConcentration,
                   ColorRange solutionColor, Color particleColor, double particleSize, int particlesPerMole ) {
        this.name = name;
        this.formula = formula;
        this.molarMass = molarMass;
        this.saturatedConcentration = saturatedConcentration;
        this.stockSolutionConcentration = stockSolutionConcentration;
        this.solutionColor = solutionColor;
        this.particleColor = particleColor;
        this.particleSize = particleSize;
        this.particlesPerMole = particlesPerMole;
    }

    public static class KoolAid extends Solute {
        public KoolAid() {
            super( Strings.KOOL_AID, Symbols.KOOL_AID, 342.296, 5.96, 5.50, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), 5, PARTICLES_PER_MOLE );
        }
    }

    public static class ColbaltIINitrate extends Solute {
        public ColbaltIINitrate() {
            super( Strings.COBALT_II_NITRATE, Symbols.COBALT_II_NITRATE, 182.942, 5.64, 5.0, new ColorRange( new Color( 255, 225, 225 ), Color.RED ), 5, PARTICLES_PER_MOLE );
        }
    }

    public static class CobaltChloride extends Solute {
        public CobaltChloride() {
            super( Strings.COBALT_CHLORIDE, Symbols.COBALT_CHLORIDE, 129.839, 4.33, 4.0, new ColorRange( new Color( 255, 242, 242 ), new Color( 0xFF6A6A ) /* rose pink */ ), 5, PARTICLES_PER_MOLE );
        }
    }

    public static class PotassiumDichromate extends Solute {
        public PotassiumDichromate() {
            super( Strings.POTASSIUM_DICHROMATE, Symbols.POTASSIUM_DICHROMATE, 294.185, 0.51, 0.50, new ColorRange( new Color( 255, 232, 210 ), new Color( 0xFF7F00 ) /* orange */ ), 5, PARTICLES_PER_MOLE );
        }
    }

    public static class PotassiumChromate extends Solute {
        public PotassiumChromate() {
            super( Strings.POTASSIUM_CHROMATE, Symbols.POTASSIUM_CHROMATE, 194.191, 3.35, 3.0, new ColorRange( new Color( 255, 255, 199 ), Color.YELLOW ), 5, PARTICLES_PER_MOLE );
        }
    }

    public static class NickelIIChloride extends Solute {
        public NickelIIChloride() {
            super( Strings.NICKEL_II_CHLORIDE, Symbols.NICKEL_II_CHLORIDE, 129.599, 5.21, 5.0, new ColorRange( new Color( 234, 244, 234 ), new Color( 0x008000 ) /* green */ ), 5, PARTICLES_PER_MOLE );
        }
    }

    public static class CopperSulfate extends Solute {
        public CopperSulfate() {
            super( Strings.COPPER_SULFATE, Symbols.COPPER_SULFATE, 159.609, 1.38, 1.0, new ColorRange( new Color( 222, 238, 255 ), new Color( 0x1E90FF ) /* blue */ ), 5, PARTICLES_PER_MOLE );
        }
    }

    public static class PotassiumPermanganate extends Solute {
        public PotassiumPermanganate() {
            super( Strings.POTASSIUM_PERMANGANATE, Symbols.POTASSIUM_PERMANGANATE, 158.034, 0.48, 0.4, new ColorRange( new Color( 255, 0, 255 ), new Color( 0x8B008B ) /* purple */ ), Color.BLACK, 5, PARTICLES_PER_MOLE );
        }
    }
}
