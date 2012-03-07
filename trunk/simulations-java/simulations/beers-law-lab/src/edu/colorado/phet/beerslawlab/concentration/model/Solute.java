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
 * Model of a solute, an immutable data structure.
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
    public final double saturatedConcentration; // M, in beaker
    public final double stockSolutionConcentration; // M, stock solution in dropper
    public final Property<SoluteColorScheme> colorScheme; // color scheme for mapping concentration to color (mutable for dev experimentation with colors)
    private Color particleColor; // color of solid particles
    public final double particleSize; // solid particles are square, this is the length of one side
    public final int particlesPerMole; // number of particles to show per mol of solute

    public Solute( String name, String formula, double saturatedConcentration, double stockSolutionConcentration,
                   SoluteColorScheme colorScheme, double particleSize, int particlesPerMole ) {

        assert( colorScheme.midConcentration <= saturatedConcentration );
        this.name = name;
        this.formula = formula;
        this.saturatedConcentration = saturatedConcentration;
        this.stockSolutionConcentration = stockSolutionConcentration;
        this.colorScheme = new Property<SoluteColorScheme>( colorScheme );
        this.particleColor = colorScheme.maxColor;
        this.particleSize = particleSize;
        this.particlesPerMole = particlesPerMole;

        this.colorScheme.addObserver( new SimpleObserver() {
            public void update() {
                updateParticleColor();
            }
        } );
    }

    // Override this for solutes that don't use the saturated color as the particle color.
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

    public static class DrinkMix extends Solute {
        public DrinkMix() {
            super( Strings.DRINK_MIX, BLLSymbols.DRINK_MIX, 5.96, 5.50, new DrinkMixColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
        }
    }

    public static class CobaltIINitrate extends Solute {
        public CobaltIINitrate() {
            super( Strings.COBALT_II_NITRATE, BLLSymbols.COBALT_II_NITRATE, 5.64, 5.0, new CobaltIINitrateColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
        }
    }

    public static class CobaltChloride extends Solute {
        public CobaltChloride() {
            super( Strings.COBALT_CHLORIDE, BLLSymbols.COBALT_CHLORIDE, 4.33, 4.0, new CobaltChlorideColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
        }
    }

    public static class PotassiumDichromate extends Solute {
        public PotassiumDichromate() {
            super( Strings.POTASSIUM_DICHROMATE, BLLSymbols.POTASSIUM_DICHROMATE, 0.51, 0.50, new PotassiumDichromateColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
        }
    }

    public static class PotassiumChromate extends Solute {
        public PotassiumChromate() {
            super( Strings.POTASSIUM_CHROMATE, BLLSymbols.POTASSIUM_CHROMATE, 3.35, 3.0, new PotassiumChromateColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
        }
    }

    public static class NickelIIChloride extends Solute {
        public NickelIIChloride() {
            super( Strings.NICKEL_II_CHLORIDE, BLLSymbols.NICKEL_II_CHLORIDE, 5.21, 5.0, new NickelIIChlorideColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
        }
    }

    public static class CopperSulfate extends Solute {
        public CopperSulfate() {
            super( Strings.COPPER_SULFATE, BLLSymbols.COPPER_SULFATE, 1.38, 1.0, new CopperSulfateColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
        }
    }

    // Potassium permanganate has different colors for solution and particles.
    public static class PotassiumPermanganate extends Solute {

        public PotassiumPermanganate() {
            super( Strings.POTASSIUM_PERMANGANATE, BLLSymbols.POTASSIUM_PERMANGANATE, 0.48, 0.4, new PotassiumPermanganateColorScheme(), PARTICLE_SIZE, PARTICLES_PER_MOLE );
            setParticleColor( Color.BLACK );
        }

        @Override protected void updateParticleColor() {
            // do nothing, particle color is constant
        }
    }
}
