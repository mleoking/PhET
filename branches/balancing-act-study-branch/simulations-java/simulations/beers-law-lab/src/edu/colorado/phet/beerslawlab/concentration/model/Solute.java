// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSymbols;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.CobaltChlorideColorScheme;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.CobaltIINitrateColorScheme;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.CopperSulfateColorScheme;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.DrinkMixColorScheme;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.NickelIIChlorideColorScheme;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.PotassiumChromateColorScheme;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.PotassiumDichromateColorScheme;
import edu.colorado.phet.beerslawlab.concentration.model.SoluteColorScheme.PotassiumPermanganateColorScheme;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Solute {

    // defaults
    private static final int PARTICLE_SIZE = 5;
    private static final int PARTICLES_PER_MOLE = 200;

    // The form that the solute is delivered in, either a solid or in a stock solution.
    public static enum SoluteForm {
        SOLID, STOCK_SOLUTION
    }

    public final String name; // localized name
    public final String formula; // chemical formula, not localized
    public final Property<SoluteColorScheme> colorScheme; // color scheme for mapping concentration to color (mutable for dev experimentation with colors)
    public final double stockSolutionConcentration; // M, stock solution in dropper
    private Color particleColor; // color of solid particles
    public final double particleSize; // solid particles are square, this is the length of one side
    public final int particlesPerMole; // number of particles to show per mole of solute

    // Solute with standard particle size and particles per mole.
    public Solute( String name, String formula, SoluteColorScheme colorScheme, double stockSolutionConcentration ) {
        this( name, formula, colorScheme, stockSolutionConcentration, PARTICLE_SIZE, PARTICLES_PER_MOLE );
    }

    public Solute( String name, String formula, SoluteColorScheme colorScheme,
                   double stockSolutionConcentration, double particleSize, int particlesPerMole ) {

        this.name = name;
        this.formula = formula;
        this.colorScheme = new Property<SoluteColorScheme>( colorScheme );
        this.stockSolutionConcentration = stockSolutionConcentration;
        this.particleColor = colorScheme.maxColor;
        this.particleSize = particleSize;
        this.particlesPerMole = particlesPerMole;

        this.colorScheme.addObserver( new SimpleObserver() {
            public void update() {
                updateParticleColor();
            }
        } );
    }

    /*
     * Particle color is typically the same as the color of the saturated solution,
     * and changing the color scheme updates the particle color.
     * Override this where different behavior is desired.
     */
    protected void updateParticleColor() {
        particleColor = colorScheme.get().maxColor;
    }

    public String getDisplayName() {
        return name;
    }

    public Color getParticleColor() {
        return particleColor;
    }

    protected void setParticleColor( Color color ) {
        particleColor = color;
    }

    public double getSaturatedConcentration() {
        return colorScheme.get().maxConcentration;
    }

    public static class DrinkMix extends Solute {
        public DrinkMix() {
            super( Strings.DRINK_MIX, BLLSymbols.DRINK_MIX, new DrinkMixColorScheme(), 5.50 );
        }
    }

    public static class CobaltIINitrate extends Solute {
        public CobaltIINitrate() {
            super( Strings.COBALT_II_NITRATE, BLLSymbols.COBALT_II_NITRATE, new CobaltIINitrateColorScheme(), 5.0 );
        }
    }

    public static class CobaltChloride extends Solute {
        public CobaltChloride() {
            super( Strings.COBALT_CHLORIDE, BLLSymbols.COBALT_CHLORIDE, new CobaltChlorideColorScheme(), 4.0 );
        }
    }

    public static class PotassiumDichromate extends Solute {
        public PotassiumDichromate() {
            super( Strings.POTASSIUM_DICHROMATE, BLLSymbols.POTASSIUM_DICHROMATE, new PotassiumDichromateColorScheme(), 0.50 );
        }
    }

    public static class PotassiumChromate extends Solute {
        public PotassiumChromate() {
            super( Strings.POTASSIUM_CHROMATE, BLLSymbols.POTASSIUM_CHROMATE, new PotassiumChromateColorScheme(), 3.0 );
        }
    }

    public static class NickelIIChloride extends Solute {
        public NickelIIChloride() {
            super( Strings.NICKEL_II_CHLORIDE, BLLSymbols.NICKEL_II_CHLORIDE, new NickelIIChlorideColorScheme(), 5.0 );
        }
    }

    public static class CopperSulfate extends Solute {
        public CopperSulfate() {
            super( Strings.COPPER_SULFATE, BLLSymbols.COPPER_SULFATE, new CopperSulfateColorScheme(), 1.0 );
        }
    }

    // Potassium permanganate has different colors for solution and particles.
    public static class PotassiumPermanganate extends Solute {

        public PotassiumPermanganate() {
            super( Strings.POTASSIUM_PERMANGANATE, BLLSymbols.POTASSIUM_PERMANGANATE, new PotassiumPermanganateColorScheme(), 0.4 );
            setParticleColor( Color.BLACK );
        }

        @Override protected void updateParticleColor() {
            // Do nothing, particle color is constant and unrelated to color scheme.
        }
    }
}
